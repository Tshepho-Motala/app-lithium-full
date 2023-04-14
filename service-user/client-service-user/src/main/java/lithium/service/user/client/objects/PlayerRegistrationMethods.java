package lithium.service.user.client.objects;

import lombok.Getter;

@Getter
public enum PlayerRegistrationMethods {
    IDIN("idin");
    PlayerRegistrationMethods(String method){
        this.method = method;
    }

    private String method;
}
