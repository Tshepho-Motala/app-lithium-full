package lithium.service.user.provider.sphonic.idin.objects;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class IDINWorkflowTwoRequest {
    RequestDetails requestDetails;
    RequestDataWorkFlowTwo requestData;
}
