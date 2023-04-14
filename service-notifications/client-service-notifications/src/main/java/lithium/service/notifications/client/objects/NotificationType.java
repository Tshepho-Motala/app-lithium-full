package lithium.service.notifications.client.objects;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Version;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class NotificationType {
    private Long id;
    private String name;

    @Version
    private int version;
}
