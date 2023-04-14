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
public class MethodStage implements Serializable {
    private static final long serialVersionUID = -5791186162662490522L;

    private Long id;
    private int version;


    private int number;
    private String title;
    private String description;

    private MethodStageField[] inputFields;
    private MethodStageField[] outputFields;

}