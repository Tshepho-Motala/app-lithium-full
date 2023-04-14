package lithium.service.limit.data.entities;

import java.sql.Timestamp;

import lithium.service.limit.enums.RealityCheckStatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import javax.persistence.Enumerated;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.EnumType;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Table(name = "reality_check_track_data")
public class RealityCheckTrackData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String guid;
    @Column
    @Enumerated(EnumType.STRING)
    private RealityCheckStatusType action;
    @Column
    private Timestamp date;
}
