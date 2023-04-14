package lithium.service.document.client.objects.mail;

import lithium.service.client.objects.placeholders.Placeholder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String domainName;
    private String userGuid;
    private DwhTemplate template;
    private Set<Placeholder> placeholders;
}

