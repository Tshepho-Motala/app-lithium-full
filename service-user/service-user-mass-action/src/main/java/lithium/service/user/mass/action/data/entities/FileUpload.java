package lithium.service.user.mass.action.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "file_upload_meta")
public class FileUpload {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    private UploadStatus uploadStatus;

    @Column(nullable=false)
    private Date uploadDate;

    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    private UploadType uploadType;

    @Column(nullable=false)
    private int recordsFound;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(name = "fileUploadData", inverseJoinColumns = {@JoinColumn(name = "file_upload_meta_id")}, joinColumns = {@JoinColumn(name = "id")})
    @JsonManagedReference
    @JsonIgnore
    private List<FileData> fileData = new ArrayList<>();

    @OneToOne(mappedBy = "fileUploadMeta", fetch = FetchType.LAZY)
    @JoinTable(name = "file", inverseJoinColumns = {@JoinColumn(name = "file_upload_meta_id")}, joinColumns = {@JoinColumn(name = "id")})
    @JsonManagedReference
    private DBFile file;

    @OneToOne(mappedBy = "fileUploadMeta", fetch = FetchType.EAGER)
    @JoinTable(name = "massActionMeta", inverseJoinColumns = {@JoinColumn(name = "file_upload_meta_id")}, joinColumns = {@JoinColumn(name = "id")})
    @JsonManagedReference
    private FileMeta massActionMeta;

    @JoinColumn(nullable=false)
    @ManyToOne(fetch=FetchType.EAGER)
    private Domain domain;

    @JoinColumn(nullable=false)
    @ManyToOne(fetch=FetchType.EAGER)
    private User author;

    public String getAuthorGuid() {
        if (this.author == null) { return null; }
        return this.author.guid();
    }
}