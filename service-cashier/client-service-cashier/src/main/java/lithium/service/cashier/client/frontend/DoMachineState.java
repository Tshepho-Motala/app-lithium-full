package lithium.service.cashier.client.frontend;

import lithium.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public enum DoMachineState {
	START(0, true),
	NEEDINPUT(1, true),
	VALIDATEINPUT(2, true),
	WAITFORPROCESSOR(3, true),
	FATALERROR(4, false),
	SUCCESS(5, false),
	DECLINED(6, false),
	WAITFORAPPROVAL(7, true),
	APPROVED(8, true),
	REJECTED(9, false),
	IFRAMEPOST(10, true),
	CANCEL(11, false),
	EXPIRED(12, false),
	REQUIREDFIELDS(13, true),
	CHECKREQUIREDFIELDS(14, true),
	REVERSALPENDING(15, true),
	REVERSALAPPROVED(16, false),
	REVERSALREJECTED(17, false),
	PLAYER_CANCEL(18, false),
	RESERVED_FUNDS_REVERSAL_FAILURE(19, false),
	AUTO_APPROVED(20, true),
	PENDING_CANCEL(21, true),
	ON_HOLD(22, true),
	AUTO_APPROVED_DELAYED(23, true),
	ON_HOLD_REPROCESS(24, true),
	APPROVED_DELAYED(25, true);

	@Getter
	Integer code;
	@Getter
	boolean active;
	private static final List<String> ACTIVE_STATE_CODES = activeStateCodes();
    private static final List<String> FINAL_STATE_CODES = finalStateCodes();

	public static DoMachineState fromCode(Integer code) {
		for (DoMachineState r : DoMachineState.values()) {
			if (r.getCode() == code) return r;
		}
		return null;
	}

	public static DoMachineState fromName(String name) {
		if (StringUtil.isEmpty(name)) {
			return null;
		}
		for (DoMachineState r : DoMachineState.values()) {
			if (name.equals(r.name())) return r;
		}
		return null;
	}

	private static List<String> activeStateCodes() {
		List<String> finalStateCodes = Arrays.asList(DoMachineState.values())
				.stream()
				.filter(state -> state.isActive())
				.map(state -> state.name())
				.collect(Collectors.toList());
		return finalStateCodes;
	}

    private static List<String> finalStateCodes() {
        List<String> finalStateCodes = Arrays.asList(DoMachineState.values())
                .stream()
                .filter(state -> !state.isActive())
                .map(state -> state.name())
                .collect(Collectors.toList());
        return finalStateCodes;
    }

	public static List<String> getActiveStateCodes() {
		return ACTIVE_STATE_CODES;
	}

    public static List<String> getFinalStateCodes() {
        return FINAL_STATE_CODES;
    }
}
