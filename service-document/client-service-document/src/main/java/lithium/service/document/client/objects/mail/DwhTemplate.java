package lithium.service.document.client.objects.mail;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum DwhTemplate {
    UPLOADED_DOCUMENT_TEMPLATE("uploaded.document.dwh"),
    MATCH_DOCUMENT_ADDRESS_MANUAL_TEMPLATE("match.address.dwh");

    @Getter
    private String templateName;
}
