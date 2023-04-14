package lithium.service.cashier.client.objects.transaction.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lithium.service.cashier.client.objects.Fees;
import lithium.service.cashier.client.objects.Limits;
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
public class Processor implements Serializable {
    private static final long serialVersionUID = -969339311761532415L;

    private Long id;
    private Boolean enabled;
    private Boolean deposit;
    private Boolean withdraw;
    private String name;
    private String code;
    private String url;
    private List<Method> methods;
    private Fees fees;
    private Limits limits;
    private String[] methodNames;
    @Default
    private List<ProcessorProperty> properties = new ArrayList<>();
}