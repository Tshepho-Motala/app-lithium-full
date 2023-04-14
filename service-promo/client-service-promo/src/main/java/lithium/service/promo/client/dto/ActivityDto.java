package lithium.service.promo.client.dto;

/**
 * @author Rivalani
 * Created this to help deserialize from IActivity when transfered over the wire
 */
public class ActivityDto implements IActivity{

    private String activity;

    public ActivityDto(String activity) {
        this.activity = activity;
    }

    @Override
    public String getActivity() {
        return activity;
    }
}
