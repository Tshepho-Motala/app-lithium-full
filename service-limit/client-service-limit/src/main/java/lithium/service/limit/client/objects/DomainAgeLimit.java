package lithium.service.limit.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class DomainAgeLimit implements Serializable {
    private static final long serialVersionUID = -1;

    private Long id;

    private int version;

    private String domainName;

    private int granularity;

    private int ageMax;

    private int ageMin;

    private long amount;

    private int type;

    private Date createdDate = new Date();

    private Date modifiedDate = new Date();

    private String createdByGuid;

    private String modifiedByGuid;

}
