package lithium.service.limit.data.dto;

import lithium.service.limit.data.entities.Domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DomainRestrictionSetDto {

    private Long id;
    private int version;
    private Domain domain;
    private String name;
    private boolean systemRestriction;
    private boolean enabled;
    private boolean dwhVisible;
    private boolean deleted;
    private List<DomainRestrictionDto> restrictions;
    private String errorMessage;
    private int altMessageCount;
    private boolean communicateToPlayer;
    private List<RestrictionOutcomePlaceActionDto> placeActions;
    private List<RestrictionOutcomeLiftActionDto> liftActions;
    private Long excludeTagId;
    private String placeMailTemplate;
    private String liftMailTemplate;
}
