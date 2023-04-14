package lithium.service.mail.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;


@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProviderType implements Serializable {
    private static final long serialVersionUID = 4209518602834948453L;
    private long id;
    private String name;
}
