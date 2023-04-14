package lithium.service.cashier.client.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum DoProcessorResponseStatus {
	
	NEXTSTAGE(1, true),
	NOOP(2, true),
	IFRAMEPOST(3, true),
	REDIRECT_NEXTSTAGE(4, true),
	REDIRECT(5, true),
	INPUTERROR(-1, true),
	FATALERROR(-2, false),
	SUCCESS(10, false),
	DECLINED(11, false),
	NEXTSTAGE_NOPROCESS(12, false),
	REVERSAL_NEXTSTAGE(13, false), //Reversal received via callback path. Machine needs to post to reverse method in processor.
	IFRAMEPOST_NEXTSTAGE(14, true),
	EXPIRED(15, false),
	PLAYER_CANCEL(16, false),
	REMOTE_FAILURE_AUTO_RETRY(17, true),
	PENDING_AUTO_RETRY(18, true),
	NEXTSTAGE_NOPROCESS_WITH_RETRY(19, false);


	@Getter Integer code;
	@Getter boolean active;

	public static DoProcessorResponseStatus fromCode(Integer code) {
		for (DoProcessorResponseStatus r: DoProcessorResponseStatus.values()) {
			if (r.getCode() == code) return r;
		}
		return null;
	}
	
}
