package lithium.service.document.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;
    private String fileName;
    private Date uploadDate;
    private String reviewStatus;
    private boolean sensitive;
	private Long documentFileId;
    private Long typeId;
    private Long reviewReasonId;
    private String fileLink;
    private String documentType;

}
