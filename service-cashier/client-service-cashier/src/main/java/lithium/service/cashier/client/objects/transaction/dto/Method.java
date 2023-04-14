package lithium.service.cashier.client.objects.transaction.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lithium.service.cashier.client.objects.Image;
import lithium.service.cashier.client.objects.ProcessorProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Method implements Serializable {
    private static final long serialVersionUID = -5791186162662490522L;

    private Long id;
    private int version;

    @NotNull
    private String code;

    private Boolean enabled;
    private Image image;
    private String name;

    @Default
    private Boolean inApp = false;

    private String platform;

    private MethodStage[] depositStages;
    private MethodStage[] withdrawalStages;
    private MethodStage[] reversalStages;

    @Default
    private List<ProcessorProperty> properties = new ArrayList<>();
}
