package lithium.service.casino.cms.storage.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Table
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BannerTag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, columnDefinition = "BIT(1) DEFAULT 1")
    private Boolean enabled;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "banner_id")
    private Banner banner;
}
