package lithium.service.cashier.client.objects.transaction.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lithium.service.cashier.client.objects.Domain;
import lithium.service.cashier.client.objects.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class DomainMethod implements Serializable {
    private static final long serialVersionUID = 6386377983884071315L;

    private Long id;
    private int version;

    private String name;

    private Image image;

    private Method method;

    private Domain domain;

    private Boolean enabled;

    private Boolean deleted;

    private Boolean deposit;

    private Integer priority;

    private String accessRule;
}