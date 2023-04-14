package lithium.service.notifications.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.util.LocaleContextProcessor;
import lithium.service.notifications.client.enums.SystemNotification;
import lithium.service.notifications.client.exceptions.Status404InboxItemNotFoundException;
import lithium.service.notifications.client.objects.InboxMessagePlaceholderReplacement;
import lithium.service.notifications.data.entities.Domain;
import lithium.service.notifications.data.entities.Inbox;
import lithium.service.notifications.data.entities.Notification;
import lithium.service.notifications.data.entities.User;
import lithium.service.notifications.data.objects.UserInboxQueryParams;
import lithium.service.notifications.data.repositories.InboxMessagePlaceholderReplacementRepository;
import lithium.service.notifications.data.repositories.InboxRepository;
import lithium.service.notifications.data.specifications.InboxSpecifications;
import lithium.service.user.client.UserApiInternalClient;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InboxService {
	@Autowired InboxRepository repository;
	@Autowired InboxMessagePlaceholderReplacementRepository phrRepo;
	@Autowired UserService userService;
	@Autowired LithiumServiceClientFactory services;
	@Autowired ModelMapper modelMapper;
	@Autowired private ChangeLogService changeLogService;
	@Autowired private InboxUserService inboxUserService;
	@Autowired private MessageSource messageSource;
	@Autowired LocaleContextProcessor localeContextProcessor;

	public Inbox findById(Long id) {
		Inbox inbox = repository.findOne(id);
		lithium.service.user.client.objects.User fullUser = null;
		try {
			fullUser = services.target(UserApiInternalClient.class, "service-user", true).getUser(inbox.getUser().getGuid()).getData();
		} catch (LithiumServiceClientFactoryException e) {
		}
		inbox.setFullUser(fullUser);
		return inbox;
	}

	public Inbox findByNotificationAndUser(Notification notification, User user) {
		return null;
	}

	public Inbox addToInbox(Domain domain, String userGuid, Notification notification, String message, List<InboxMessagePlaceholderReplacement> phReplacements) {
		return addToInbox(domain, userGuid, notification, message, phReplacements, false);
	}

	public Inbox addToInbox(Domain domain, String userGuid, Notification notification, String message, List<InboxMessagePlaceholderReplacement> phReplacements, boolean isCta) {
		Inbox inbox = Inbox.builder()
				.domain(domain)
				.user(userService.findOrCreate(userGuid))
				.notification(notification)
				.message(message)
				.cta(isCta)
				.build();
		inbox = repository.save(inbox);
		if (phReplacements != null) {
			List<lithium.service.notifications.data.entities.InboxMessagePlaceholderReplacement> prList = new ArrayList<>();
			for (InboxMessagePlaceholderReplacement phReplacement: phReplacements) {
				lithium.service.notifications.data.entities.InboxMessagePlaceholderReplacement pr =
						modelMapper.map(phReplacement, lithium.service.notifications.data.entities.InboxMessagePlaceholderReplacement.class);
				pr.setInbox(inbox);
				pr = phrRepo.save(pr);
				prList.add(pr);
			}
			inbox.setPhReplacements(prList);
		}
		log.debug(String.format("Added InboxItem for user %s, item:%s", userGuid, inbox));
		return inbox;
	}

	public Inbox markRead(Long id) {
		Inbox inbox = repository.findOne(id);
		inbox.setRead(true);
		if (inbox.getReadDate() == null) inbox.setReadDate(new Date());
		inbox.setLastReadDate(new Date());
		return repository.save(inbox);

	}

	public Page<Inbox> findByDomains(List<String> domains, Boolean showRead, Boolean showUnread, String userGuid, String searchValue, Pageable pageable) {
		Specification<Inbox> spec = Specification.where(InboxSpecifications.domains(domains));
		if (!showRead) {
			spec = spec.and(InboxSpecifications.read(false));
		} else if (!showUnread) {
			spec = spec.and(InboxSpecifications.read(true));
		}
		if (userGuid != null) {
			spec = spec.and(InboxSpecifications.user(userGuid));
		}
		if ((searchValue != null) && (searchValue.length() > 0)) {
			Specification<Inbox> s = Specification.where(InboxSpecifications.any(searchValue));
			spec = (spec == null)? s: spec.and(s);
		}
		Page<Inbox> result = repository.findAll(spec, pageable);
		return result;
	}

	@Cacheable(value = "lithium.service.notifications.services.inbox-service.find-user-inbox", key = "#params.userGuid")
	public List<Inbox> findUserInbox(@NotNull UserInboxQueryParams params) {

		String state =  params.isRead() ? "read":"unread";

		User user = userService.findOrCreate(params.getUserGuid());

		log.debug(String.format("Querying user's inbox for %s notification items, user:%s, locale:%s, channels:%s"
				, state, params.getUserGuid(), params.getLocale(), params.getChannels()));

		String[] channels = params.getChannels().split(",");
		channels = channels.length > 0 ? channels: new String[]{params.getChannels()};

		List<String> channelList = Arrays.stream(channels).map(String::trim).collect(Collectors.toList());


		Specification<Inbox> spec = Specification.where(InboxSpecifications.user(user))
				.and(InboxSpecifications.read(params.isRead()))
				.and(InboxSpecifications.withChannels(channelList));

		if(params.getCta() != null) {
			spec = spec.and(InboxSpecifications.cta(params.getCta()));
		}

		if(!StringUtil.isEmpty(params.getType())) {
			spec = spec.and(InboxSpecifications.withType(params.getType()));
		}

		List<Inbox> inboxItems = repository.findAll(spec)
				.stream()
				.map(inbox -> setInboxMessage(inbox, params.getLocale()))
				.collect(Collectors.toList());

		log.debug(String.format("%s notification items for user:%s, locale:%s, channels:%s, inboxItems:%s", state, params.getUserGuid(), params.getLocale(), params.getChannels(), inboxItems), inboxItems);

		return inboxItems;
	}

	public Inbox read(Long id, String replyMessage, String locale, LithiumTokenUtil util) throws Status404InboxItemNotFoundException {
		User user = userService.findOrCreate(util.guid());
		Inbox inbox = repository.findOneByIdAndUser(id, user);

		if(inbox == null) {
			log.error(String.format("Inbox with Id %s could not be found for user %s", id, util.guid()));
			throw new Status404InboxItemNotFoundException(String.format("Inbox with Id %s could not be found", id));
		}

		if(inbox.getRead()) {
			log.debug(String.format("Inbox with id %s already marked as read", id), inbox);
			return setInboxMessage(inbox,  locale);
		}

		inbox.setRead(true);
		inbox.setLastReadDate(new Date());

		if (inbox.getReadDate() == null)  {
			inbox.setReadDate(new Date());
		}

		repository.save(inbox);
		inboxUserService.updateSummaryFromInbox(inbox, true);

		if(isInterventionInbox(inbox)) {
			List<ChangeLogFieldChange> cls = new ArrayList<>();

			cls.add(ChangeLogFieldChange.builder()
					.field(inbox.getNotification().getName())
					.fromValue("unread")
					.toValue("read")
					.build()
			);

			changeLogService.registerChangesForNotesWithFullNameAndDomain("user.notifications.intervention", "edit", util.id(), util.guid(), util,
					replyMessage, null, cls, Category.RESPONSIBLE_GAMING, SubCategory.RESTRICTION, 100, util.domainName());
		}

		log.debug(String.format("Inbox with id %s read status now set to true, inbox: %s", id, inbox), inbox);

		return setInboxMessage(inbox, locale);
	}

	public boolean isInterventionInbox(Inbox inbox) {
		if(Objects.isNull(inbox) || Objects.isNull(inbox.getNotification()) || !inbox.getNotification().getSystemNotification())  {
			return false;
		}

		Notification notification = inbox.getNotification();

		SystemNotification systemNotification = SystemNotification.fromNotificationName(notification.getName());

		return !Objects.isNull(systemNotification) && systemNotification.notificationName().toUpperCase().startsWith("INTERVENTION_MESSAGE");
	}

	public Inbox setInboxMessage(Inbox inbox, String locale) {
		if(Objects.isNull(inbox)) {
			throw new IllegalArgumentException("A valid inbox object is required  when calling InboxService#setInboxMessage");
		}

		String message = inbox.getMessage();
		localeContextProcessor.setLocaleContextHolder(locale);

		if(!Objects.isNull(inbox.getPhReplacements()) && !Objects.isNull(message)) {
			for(lithium.service.notifications.data.entities.InboxMessagePlaceholderReplacement imp: inbox.getPhReplacements()) {
				if(imp.getKey().equalsIgnoreCase("%translationKey%")) {
					String translated = messageSource.getMessage(imp.getValue(), new Object[]{ new lithium.service.translate.client.objects.Domain(inbox.getDomain().getName())},
							imp.getValue(), LocaleContextHolder.getLocale());

					message = message.replace("%translationKey%", translated);
				}
				else {
					String value = imp.getValue();
					if (value != null && value.contains(".")) {
						value = messageSource.getMessage(value, new Object[]{ new lithium.service.translate.client.objects.Domain(inbox.getDomain().getName())},
								value, LocaleContextHolder.getLocale());
					}
					message = message.replace(imp.getKey(), value);
				}
			}
		}

		inbox.setMessage(message);
		return inbox;
	}
}
