package lithium.service.casino.service.util;

public enum EActiveBonusWorkflowStatus {
	COMPLETED_BONUS, // We just completed this bonus
	CANCELLED_BONUS, // We just cancelled the bonus
	EXPIRED_BONUS, // We just expired the bonus
	ACTIVE_BONUS, // No action taken on bonus
	NO_BONUS
}
