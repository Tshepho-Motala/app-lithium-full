package lithium.service.casino.provider.iforium.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BalanceResponse extends Response {

    @Valid
    @NotNull
    @JsonProperty("Balance")
    private Balance balance;

    @Builder
    public BalanceResponse(Integer errorCode, Balance balance) {
        super(errorCode);
        this.balance = balance;
    }
}
