package lithium.service.user.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.transaction.Transactional;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.user.data.entities.Label;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserRevision;
import lithium.service.user.data.entities.UserRevisionLabelValue;
import lithium.service.user.data.repositories.UserRevisionLabelValueRepository;
import lithium.service.user.data.repositories.UserRevisionRepository;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserLabelValueService {
  final LabelValueService labelValueService;
  final LabelService labelService;
  final UserRevisionLabelValueRepository userRevisionLabelValueRepository;
  final UserRevisionRepository userRevisionRepository;
  final UserService userService;
  final ChangeLogService changeLogService;
  final TokenStore tokenStore;
  public UserLabelValueService(
      LabelValueService labelValueService,
      LabelService labelService,
      UserRevisionLabelValueRepository userRevisionLabelValueRepository,
      UserRevisionRepository userRevisionRepository,
      UserService userService,
      ChangeLogService changeLogService,
      TokenStore tokenStore) {
    this.labelValueService = labelValueService;
    this.labelService = labelService;
    this.userRevisionLabelValueRepository = userRevisionLabelValueRepository;
    this.userRevisionRepository = userRevisionRepository;
    this.userService = userService;
    this.changeLogService =  changeLogService;
    this.tokenStore = tokenStore;
  }

  @Transactional
  public void save(long userId, Map<String,String> labelAndValueMap) {
    User user = userService.findOne(userId);
    UserRevision rv = userRevisionRepository.save(UserRevision.builder().user(user).build());

    labelAndValueMap.forEach((label,value) -> {
      userRevisionLabelValueRepository.save(
          UserRevisionLabelValue.builder()
              .userRevision(rv)
              .labelValue(labelValueService.findOrCreate(label, value))
              .build()
      );

    });

    user.setCurrent(rv);
    userService.save(user);
  }

  @Transactional
  public User updateOrAddUserLabelValues(long userId, Map<String,String> labelAndValueMap) {
    User user = userService.findOne(userId);// Makes sure that we are working from the latest changed user entity
    UserRevision oldRv = user.getCurrent();

    String author = user.guid();
    LithiumTokenUtil util = null; // /frontend uses basic authentication, hence the check below. We can safely assume that when util = null, that it is a create and not edit changelog
    if (SecurityContextHolder.getContext().getAuthentication() instanceof OAuth2Authentication) {
      util = LithiumTokenUtil.builder(tokenStore, SecurityContextHolder.getContext().getAuthentication()).build();
    }
    SubCategory subCategory = util == null ? SubCategory.ACCOUNT_CREATION : SubCategory.EDIT_DETAILS;
    String comment = null;

    List<UserRevisionLabelValue> oldRvLabelValueList = oldRv == null || oldRv.getLabelValueList() == null ? new ArrayList<>() : oldRv.getLabelValueList();

    List<ChangeLogFieldChange> clfc = new ArrayList<>();

    String note = "reason_for_change";

    if(labelAndValueMap.containsKey(note)) {
      comment = labelAndValueMap.get(note);
      labelAndValueMap.remove(note);
    }

    UserRevision rv = userRevisionRepository.save(UserRevision.builder().user(user).build());
    for (Entry<String, String> entry : labelAndValueMap.entrySet()) {
      String label = entry.getKey();
      String value = entry.getValue();
      userRevisionLabelValueRepository.save(
          UserRevisionLabelValue.builder()
              .userRevision(rv)
              .labelValue(labelValueService.findOrCreate(label, value))
              .build()
      );
      clfc.add(ChangeLogFieldChange.builder().field(label).fromValue("").toValue(value).build());
    }

    for (UserRevisionLabelValue userRevisionLabelValue : oldRvLabelValueList) {
      AtomicBoolean hasLabel = new AtomicBoolean(false);
      labelAndValueMap.forEach((label, value) -> {
        if (label.equals(userRevisionLabelValue.getLabelValue().getLabel().getName())) {
          hasLabel.set(true);
          clfc.stream().forEach(changeLogFieldChange -> {
            if (changeLogFieldChange.getField().equals(label))
              changeLogFieldChange.setFromValue(userRevisionLabelValue.getLabelValue().getValue());
          });
        }
      });

      if (!hasLabel.get()) {
        userRevisionLabelValueRepository.save(
            UserRevisionLabelValue.builder()
                .userRevision(rv)
                .labelValue(userRevisionLabelValue.getLabelValue())
                .build()
        );
      }
    }

    try {

      if(comment != null) {
        ChangeLogFieldChange change =  clfc.get(0);

        String toText =  StringUtil.isEmpty(change.getFromValue()) ? "set to" : " to";
        comment = String.format("%s%s %s. %s", change.getFromValue(), toText,change.getToValue(), comment);
        author = util != null ? util.getJwtUser().getGuid() : author;
      }
      changeLogService.registerChangesForNotesWithFullNameAndDomain("user", util == null ? "create" : "edit", user.getId(), author, util, comment,
          null, clfc, Category.ACCOUNT, subCategory, 0, user.domainName());
    } catch (Exception e) {
      log.error("Failed to create changelog while adding additionalData. (" + user + ")", e);
    }

    user.setCurrent(rv);
    return userService.save(user);
  }

//	public UserRevisionLabelValue save(User user, String label, String value) {
//		LabelValue labelValue = labelValueService.findOrCreate(label, value);
//		return repository.save(
//				UserRevisionLabelValue.builder()
//				.user(user)
//				.labelValue(labelValue)
//				.build()
//			);
//	}

	public UserRevisionLabelValue findByUserAndLabel(User user, String labelName) {
    List<Label> labelsByNames = labelService.getLabelsByNames(Arrays.asList(labelName));
    if (labelsByNames != null) {
      Label label = labelsByNames.get(0);
      if(user.getCurrent() != null) {
        return userRevisionLabelValueRepository.findByUserRevisionAndLabelValueLabel(user.getCurrent(), label);
      }
    }
		return null;
	}

//	public void delete(UserRevisionLabelValue ulv) {
//		repository.delete(ulv);
//	}
}
