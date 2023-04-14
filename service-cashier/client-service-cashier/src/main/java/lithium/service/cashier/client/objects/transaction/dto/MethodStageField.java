package lithium.service.cashier.client.objects.transaction.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class MethodStageField implements Serializable {
    private static final long serialVersionUID = -5791186162662490522L;

    private Long id;
    private int version;
    private String code;
    private String type;
    private String name;
    private String description;
    private Integer sizeXs;
    private Integer sizeMd;
    private Integer displayOrder;
    private Boolean required;
    private MethodStage stage;
}