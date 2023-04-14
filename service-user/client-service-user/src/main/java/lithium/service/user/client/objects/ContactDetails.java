package lithium.service.user.client.objects;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ContactDetails {
    private String userGuid;
    private Address address;
    private Email emailValidate;
    private CellphoneNumber cellphoneNumberValidate;
    private String contactVerifiedType;
    private String category;
    private String subCategory;
}
