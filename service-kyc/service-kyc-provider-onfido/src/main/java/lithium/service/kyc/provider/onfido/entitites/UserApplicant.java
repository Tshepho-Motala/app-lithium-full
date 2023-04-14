package lithium.service.kyc.provider.onfido.entitites;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(indexes = {@Index(name = "idx_user_guid", columnList = "userGuid", unique = true)})
public class UserApplicant {

    @Id
    private String applicantId;
    private String userGuid;
    private String domainName;
}
