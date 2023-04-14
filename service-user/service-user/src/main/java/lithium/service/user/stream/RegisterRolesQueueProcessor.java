package lithium.service.user.stream;

import lithium.service.translate.client.objects.Domain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import lithium.service.Response;
import lithium.service.translate.client.stream.TranslationsStream;
import lithium.service.user.controllers.RolesController;
import lithium.service.user.data.entities.Category;
import lithium.service.user.data.entities.Role;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableBinding(RegisterRolesQueueSink.class)
@Slf4j
public class RegisterRolesQueueProcessor {
	
	@Autowired RolesController rolesController;
	@Autowired TranslationsStream translationsStream;
	
	@StreamListener(RegisterRolesQueueSink.INPUT) 
	void handle(lithium.service.role.client.objects.Role clientRole) throws Exception {
		
		log.info("Received a role from the queue for processing: " + clientRole);
		Response<Category> category = rolesController.addCategory(clientRole.getCategory().getName(), clientRole.getCategory().getDescription());
		translationsStream.registerTranslation(new Domain("default"), "en", category.getData().getNameCode(), clientRole.getCategory().getName());
		Response<Role> role  = rolesController.addRole(clientRole.getName(), clientRole.getRole(), clientRole.getDescription(), clientRole.getCategory().getName());
		translationsStream.registerTranslation(new Domain("default"), "en", role.getData().getNameCode(), clientRole.getName());
		translationsStream.registerTranslation(new Domain("default"), "en", role.getData().getDescriptionCode(), clientRole.getDescription());
	}
	
}
