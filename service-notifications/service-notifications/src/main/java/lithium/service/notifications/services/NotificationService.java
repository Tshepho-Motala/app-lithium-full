package lithium.service.notifications.services;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import lithium.service.notifications.data.entities.NotificationType;
import lithium.service.notifications.data.repositories.NotificationTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import lithium.service.notifications.data.entities.Domain;
import lithium.service.notifications.data.entities.Notification;
import lithium.service.notifications.data.entities.NotificationChannel;
import lithium.service.notifications.data.repositories.NotificationChannelRepository;
import lithium.service.notifications.data.repositories.NotificationRepository;
import lithium.service.notifications.data.specifications.NotificationSpecifications;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationService {
	@Autowired ChannelService channelService;
	@Autowired DomainService domainService;
	@Autowired NotificationChannelRepository notificationChannelRepository;
	@Autowired NotificationRepository repository;
	@Autowired NotificationTypeRepository notificationTypeRepository;

	public List<Notification> findByDomainName(String domainName) {
		return repository.findByDomainName(domainName);
	}

	public Notification findByDomainNameAndName(String domainName, String name) {
		return repository.findByDomainNameAndName(domainName, name);
	}

	public Page<Notification> findByDomains(List<String> domains, String searchValue, Pageable pageable) {
		Specification<Notification> spec = Specification.where(NotificationSpecifications.domains(domains));
		if ((searchValue != null) && (searchValue.length() > 0)) {
			Specification<Notification> s = Specification.where(NotificationSpecifications.any(searchValue));
			spec = (spec == null)? s: spec.and(s);
		}
		Page<Notification> result = repository.findAll(spec, pageable);
		return result;
	}

	@Transactional(rollbackOn=Exception.class)
	@Retryable(backoff=@Backoff(delay=500), maxAttempts=10)
	public Notification create(lithium.service.notifications.client.objects.Notification notificationPost) throws Exception {
		log.info("Creating notification: " + notificationPost);
		Notification notification = repository.findByDomainNameAndName(notificationPost.getDomain().getName(), notificationPost.getName());
		if (notification != null) throw new Exception("A notification with this name already exists");
		Domain domain = domainService.findOrCreate(notificationPost.getDomain().getName());
		notification = repository.save(
				Notification.builder()
						.domain(domain)
						.name(notificationPost.getName())
						.displayName(notificationPost.getDisplayName())
						.description(notificationPost.getDescription())
						.message(notificationPost.getMessage())
						.build()
		);
		List<NotificationChannel> channels = new ArrayList<>();
		for (lithium.service.notifications.client.objects.NotificationChannel nc: notificationPost.getChannels()) {
			NotificationChannel notificationChannel = notificationChannelRepository.save(
					NotificationChannel.builder()
							.channel(channelService.findOrCreate(nc.getChannel().getName()))
							.notification(notification)
							.forced(nc.getForced())
							.templateName(nc.getTemplateName())
							.templateLang(nc.getTemplateLang())
							.build()
			);
			channels.add(notificationChannel);
		}

		if (notificationPost.getNotificationType() != null) {
			NotificationType notificationType = notificationTypeRepository.findById(notificationPost.getNotificationType().getId()).get();
			notification.setNotificationType(notificationType);
			repository.save(notification);
		}
		log.info("Notification: " + notification);
		return notification;
	}

	@Transactional(rollbackOn=Exception.class)
	@Retryable(backoff=@Backoff(delay=500), maxAttempts=10)
	public Notification createOrUpdate(lithium.service.notifications.client.objects.Notification notificationPost) throws Exception {
		log.info("Creating notification: " + notificationPost);
		Notification notification = repository.findByDomainNameAndName(notificationPost.getDomain().getName(), notificationPost.getName());
		if (notification != null) {
			return modify(notification, notificationPost);
		}

		NotificationType notificationType = null;
		if (notificationPost.getNotificationType() != null) {
			notificationType = notificationTypeRepository.findFirstByName(notificationPost.getNotificationType().getName()).get();
		} else {
			//Fallback, all notifications without a type will have the default type
			notificationType = notificationTypeRepository.findFirstByName(lithium.service.notifications.enums.NotificationType.DEFAULT.getType()).get();
		}

		Domain domain = domainService.findOrCreate(notificationPost.getDomain().getName());
		notification = repository.save(
				Notification.builder()
						.domain(domain)
						.name(notificationPost.getName())
						.displayName(notificationPost.getDisplayName())
						.description(notificationPost.getDescription())
						.message(notificationPost.getMessage())
						.systemNotification(notificationPost.isSystemNotification())
						.notificationType(notificationType)
						.build()
		);
		List<NotificationChannel> channels = new ArrayList<>();
		for (lithium.service.notifications.client.objects.NotificationChannel nc: notificationPost.getChannels()) {
			NotificationChannel notificationChannel = notificationChannelRepository.save(
					NotificationChannel.builder()
							.channel(channelService.findOrCreate(nc.getChannel().getName()))
							.notification(notification)
							.forced(nc.getForced())
							.templateName(nc.getTemplateName())
							.templateLang(nc.getTemplateLang())
							.build()
			);
			channels.add(notificationChannel);
		}
		log.info("Notification: " + notification);
		return notification;
	}
	public Notification modify(Notification notification, lithium.service.notifications.client.objects.Notification notificationPost) throws Exception {
		notification.setDisplayName(notificationPost.getDisplayName());
		notification.setDescription(notificationPost.getDescription());
		notification.setMessage(notificationPost.getMessage());
		return repository.save(notification);
	}

	@Transactional(rollbackOn=Exception.class)
	@Retryable(backoff=@Backoff(delay=500), maxAttempts=10)
	public Notification addChannel(Notification notification,
								   lithium.service.notifications.client.objects.NotificationChannel notificationChannelPost
	) throws Exception {
		NotificationChannel notificationChannel = NotificationChannel.builder()
				.notification(notification)
				.channel(channelService.findOrCreate(notificationChannelPost.getChannel().getName()))
				.forced((notificationChannelPost.getForced() != null)? notificationChannelPost.getForced(): false)
				.templateName(notificationChannelPost.getTemplateName())
				.templateLang(notificationChannelPost.getTemplateLang())
				.build();
		notificationChannel = notificationChannelRepository.save(notificationChannel);
		notification.getChannels().add(notificationChannel);
		return repository.save(notification);
	}

	@Transactional(rollbackOn=Exception.class)
	@Retryable(backoff=@Backoff(delay=500), maxAttempts=10)
	public Notification removeChannel(Notification notification, NotificationChannel notificationChannel) throws Exception {
		notification.getChannels().remove(notificationChannel);
		notification = repository.save(notification);
		notificationChannelRepository.delete(notificationChannel);
		return repository.findOne(notification.getId());
	}

	@Transactional(rollbackOn=Exception.class)
	@Retryable(backoff=@Backoff(delay=500), maxAttempts=10)
	public Notification modifyChannel(Notification notification,
									  NotificationChannel notificationChannel, lithium.service.notifications.client.objects.NotificationChannel notificationChannelPost) throws Exception {
		notificationChannel.setChannel(channelService.findOrCreate(notificationChannelPost.getChannel().getName()));
		notificationChannel.setForced((notificationChannelPost.getForced() != null)? notificationChannelPost.getForced(): false);
		notificationChannel.setTemplateName(notificationChannelPost.getTemplateName());
		notificationChannel.setTemplateLang(notificationChannelPost.getTemplateLang());
		notificationChannel = notificationChannelRepository.save(notificationChannel);
		return repository.findOne(notification.getId());
	}
}
