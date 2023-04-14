package lithium.service.promo.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lithium.service.promo.client.dto.PromoActivity;
import lithium.service.promo.client.dto.PromoProviderRegistration;
import lithium.service.promo.data.entities.Activity;
import lithium.service.promo.data.entities.Category;
import lithium.service.promo.data.entities.PromoProvider;
import lithium.service.promo.data.entities.ActivityExtraField;
import lithium.service.promo.data.repositories.ActivityRepository;
import lithium.service.promo.data.repositories.ActivityExtraFieldRepository;
import lithium.service.promo.data.repositories.CategoryRepository;
import lithium.service.promo.data.repositories.PromoProviderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PromoProviderRegistrationService {

  @Autowired
  PromoProviderRepository promoProviderRepository;
  @Autowired
  ActivityExtraFieldRepository activityExtraFieldRepository;
  @Autowired
  ActivityRepository activityRepository;

  @Autowired
  CategoryRepository categoryRepository;


  private Category findOrCreateCategory(String name) {
    Category category = categoryRepository.findByName(name);

    if (category == null) {
      category = categoryRepository.save(Category.builder().name(name).build());
    }

    return category;
  }

  private PromoProvider findOrCreate(String url, String category) {
    PromoProvider promoProvider = promoProviderRepository.findByUrlAndCategoryName(url, category);
    if (promoProvider == null) {

      promoProvider = promoProviderRepository.save(PromoProvider.builder().category(findOrCreateCategory(category)).url(url).build());
    }
    return promoProvider;
  }

  private ActivityExtraField findOrCreateExtraField(Activity activity, String name) {
    ActivityExtraField activityExtraField = activityExtraFieldRepository.findByActivityAndName(activity, name);
    if (activityExtraField == null) {
      activityExtraField = activityExtraFieldRepository.save(ActivityExtraField.builder().name(name).activity(activity)
              .build());
    }
    return activityExtraField;
  }

  private Activity findOrCreateActivity(PromoProvider promoProvider, PromoActivity promoActivity) throws IOException {
    String activityName = promoActivity.getActivity().getActivity();

    Activity activity = activityRepository.findByPromoProviderAndName(promoProvider, activityName);
    if (activity == null) {
      activity = activityRepository.save(Activity.builder()
              .name(activityName)
              .promoProvider(promoProvider).build());
    }
    return activity;
  }

  public void register(PromoProviderRegistration request) throws IOException {
    log.debug("Registering PromoProvider: " + request);

    PromoProvider promoProvider = findOrCreate(request.getUrl(), request.getCategory().getCategory());
    promoProvider.setName(request.getName());

    List<Activity> activities = new ArrayList<>();
    for (PromoActivity promoActivity: request.getActivities()) {
      Activity newActivity = findOrCreateActivity(promoProvider, promoActivity);
      newActivity.setRequiresValue(promoActivity.getRequiresValue());
      activities.add(newActivity);

      List<ActivityExtraField> providerExtraFields = new ArrayList<>();
      for (lithium.service.promo.client.dto.ActivityExtraField rtf: promoActivity.getExtraFields()) {
        ActivityExtraField activityExtraField = findOrCreateExtraField(newActivity, rtf.getName());
        activityExtraField.setDataType(rtf.getType());
        activityExtraField.setFieldType(rtf.getFieldType());
        activityExtraField.setDescription(rtf.getDescription());
        activityExtraField.setFetchExternalData(rtf.getFetchExternalData());
        activityExtraField.setRequired(rtf.getRequired());
        providerExtraFields.add(activityExtraField);
      }
      newActivity.setExtraFields(providerExtraFields);
      activityRepository.save(newActivity);
    }

    promoProvider.setActivities(activities);
    promoProviderRepository.save(promoProvider);
  }
}