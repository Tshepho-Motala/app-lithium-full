package lithium.service.document;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ServiceDocumentModuleInfo extends ModuleInfoAdapter {
	public ServiceDocumentModuleInfo() {
		Category documentTypeCategory = Category.builder().name("Document Type Operations").description("These are all the roles relevant to managing document types.").build();
		addRole(Role.builder().category(documentTypeCategory).name("Document Type List").role("DOCUMENT_TYPES_VIEW").description("View list of all document types.").build());
		addRole(Role.builder().category(documentTypeCategory).name("Document Type Edit").role("DOCUMENT_TYPES_EDIT").description("Edit a document type.").build());
		Category documentType = Category.builder().name("Document Operations").description("These are all the roles relevant to managing documents.").build();
		addRole(Role.builder().category(documentType).name("View regular documents").role("DOCUMENT_REGULAR_VIEW").description("User will have access to the tab and be able to view regular documents (no download, no upload, no sensitive docs)").build());
		addRole(Role.builder().category(documentType).name("View sensitive documents").role("DOCUMENT_SENSITIVE_VIEW").description("User will be able to view documents flagged as sensitive.").build());
		addRole(Role.builder().category(documentType).name("Upload/edit regular documents").role("DOCUMENT_REGULAR_EDIT").description("User will have access to upload regular documents.").build());
		addRole(Role.builder().category(documentType).name("Upload/edit sensitive documents").role("DOCUMENT_SENSITIVE_EDIT").description("Users will have the option to flag a file as sensitive during upload or edit.").build());
		addRole(Role.builder().category(documentType).name("Delete documents").role("DOCUMENT_DELETE").description("User will have the ability to delete already uploaded documents.").build());
		addRole(Role.builder().category(documentType).name("Download documents").role("DOCUMENT_DOWNLOAD").description("User will have the ability to download already uploaded documents.").build());
	}

	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/frontend/document/**").authenticated()
				.antMatchers(HttpMethod.GET, "/backoffice/document-type/{domain}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domain, 'DOCUMENT_TYPES_VIEW')")
				.antMatchers(HttpMethod.PUT, "/backoffice/document-type/{domain}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domain, 'DOCUMENT_TYPES_EDIT')")
				.antMatchers(HttpMethod.GET, "/public/**").permitAll()
				.antMatchers("/backoffice/document/**").authenticated()
				.antMatchers(HttpMethod.GET, "/backoffice/document/{domain}/per-user").access("@lithiumSecurity.hasDomainRole(authentication, #domain, 'DOCUMENT_REGULAR_VIEW')")
				.antMatchers(HttpMethod.GET, "/backoffice/document/{domain}/per-user-sensitive").access("@lithiumSecurity.hasDomainRole(authentication, #domain, 'DOCUMENT_SENSITIVE_VIEW')")
				.antMatchers(HttpMethod.POST, "/backoffice/document/{domain}/regular/**").access("@lithiumSecurity.hasDomainRole(authentication, #domain, 'DOCUMENT_REGULAR_EDIT', 'DOCUMENT_SENSITIVE_EDIT')")
				.antMatchers(HttpMethod.POST, "/backoffice/document/{domain}/sensitive/**").access("@lithiumSecurity.hasDomainRole(authentication, #domain, 'DOCUMENT_SENSITIVE_EDIT')")
				.antMatchers(HttpMethod.DELETE, "/backoffice/document/{domain}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domain, 'DOCUMENT_DELETE')")
				.antMatchers(HttpMethod.GET, "/backoffice/document/{domain}/regular/get-document-file").access("@lithiumSecurity.hasDomainRole(authentication, #domain, 'DOCUMENT_DOWNLOAD')")
				.antMatchers("/document/**").access("@lithiumSecurity.authenticatedSystem(authentication)")
				.antMatchers(HttpMethod.GET, "/data-migration-job/**").access("@lithiumSecurity.hasRole(authentication, 'ADMIN')");

	}
}
