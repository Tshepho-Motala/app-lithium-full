package lithium.service.document.client.objects;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class DocumentFile implements Serializable {

	private static final long serialVersionUID = 1L;

	private String documentUuid;

	private File file;

	private boolean deleted;

	private int documentPage;
	
	@DateTimeFormat(iso=ISO.DATE_TIME)
	private Date uploadDate;
}
