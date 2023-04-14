package lithium.service.document.client.objects;

import org.springframework.beans.factory.annotation.Value;

import java.util.Date;


public interface DocumentFileProjection {

	Long getDocumentId();

	Long getId();

	@Value("#{target.file?.name}")
	String getFileName();

	@Value("#{target.file?.id}")
	Long getFileId();

	Integer getDocumentPage();

	Date getUploadDate();
}
