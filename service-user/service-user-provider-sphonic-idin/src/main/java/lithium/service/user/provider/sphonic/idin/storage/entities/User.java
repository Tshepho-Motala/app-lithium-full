package lithium.service.user.provider.sphonic.idin.storage.entities;

import java.io.Serializable;
import javax.persistence.*;

import lithium.jpa.entity.EntityWithUniqueGuid;
import lithium.service.access.provider.sphonic.data.entities.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table
@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable, EntityWithUniqueGuid {
    private static final long serialVersionUID = -97208492760039004L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column
    public String guid; // User guid in idin will be the domainName/applicantHash

    @Column
    @Version
    public Integer version;

    @Column
    private boolean addressVerified;

    @Column
    private boolean cellphoneValidated;

    @Column
    private boolean emailValidated;

    @Column
    private String cellphoneNumber;

    @Column
    private String email;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Domain domain;

    public boolean setAddressVerified(boolean addressVerified) {
        return this.addressVerified = addressVerified;
    }

    public boolean getAddressVerified() {
        return addressVerified;
    }

    public void setEmailValidated(boolean emailValidated) {
        this.emailValidated = emailValidated;
    }

    public boolean isEmailValidated() {
        return emailValidated;
    }

    public void setCellphoneValidated(boolean cellphoneValidated) {
        this.cellphoneValidated = cellphoneValidated;
    }

    public boolean isCellphoneValidated() {
        return cellphoneValidated;
    }
}
