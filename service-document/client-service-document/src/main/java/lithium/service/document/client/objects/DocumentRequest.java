package lithium.service.document.client.objects;

import lithium.service.document.client.objects.enums.DocumentReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fileName;
    private byte[] content;
    private String mimeType;
    private int docPage;
    private DocumentPurpose documentPurpose;
    private String documentType;
    private String domainName;
    private String userGuid;
    private DocumentReviewStatus reviewStatus;

}

