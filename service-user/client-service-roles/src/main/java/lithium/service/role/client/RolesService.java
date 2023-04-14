package lithium.service.role.client;

import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;
import lithium.service.translate.client.objects.Domain;
import lithium.service.translate.client.stream.TranslationsStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import java.net.URLEncoder;
import java.util.List;

@Slf4j
@Service
public class RolesService {
	@Autowired private TranslationsStream translationsStream;
	
	public void registerRole(RoleClient roleClient, String name, String roleString, String description, String catName, String catDescription) throws Exception {
		log.trace("registerRole()");
		Response<Category> category = roleClient.addCategory(catName, catDescription);
		if (category.isSuccessful()) {
			registerTranslations(category.getData().getNameCode(), catName);
			registerTranslations(category.getData().getDescriptionCode(), catDescription);
		} else {
			log.error("Category not registered, not attempting translations. "+category);
		}
		log.info("Created Category :: "+category);
		Response<Role> role = roleClient.addRole(name, roleString, description, category.getData().getName());
		if (role.isSuccessful()) {
			registerTranslations(role.getData().getNameCode(), name);
			registerTranslations(role.getData().getDescriptionCode(), description);
		} else {
			log.error("Role not registered, not attempting translations. "+role);
		}
		log.info("Created Role :: "+role);
	}

	@Retryable(backoff=@Backoff(delay=10000),maxAttempts=30)
	public void registerRoles(List<Role> roles) throws Exception {
//		RoleClient roleClient = services.target(RoleClient.class, true);
//		TranslationClient translationClient = services.target(TranslationClient.class, true);
//		roles.forEach(r -> {
//			log.info("Sending Role : "+r+" to be registered.");
//			try {
//				registerRole(roleClient, translationClient, r.getName(), r.getRole(), r.getDescription(), r.getCategory().getName(), r.getCategory().getDescription());
//			} catch (Exception e) {
//				throw new RuntimeException(e);
//			}
//		});
	}
	
	private void registerTranslations(String code, String value) {
		log.info("Register Translation : " + code + " value :" + value);
		try {
			translationsStream.registerTranslation(new Domain("default"), "en", code, value);
		} catch (Exception e) {
			log.error("Could not register translation for : " + code + " value :" + value, e);
		}
	}
}
