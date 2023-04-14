package lithium.service.casino.service.util;

public enum EPendingBonusWorkflowStatus {
	ACTIVATED_PENDING_BONUS, // Used when a pending bonus is activated
	NO_PENDING_BONUS_LEFT, // Used when there are no more pending bonuses to activate
	NO_PENDING_ACTIVATION_PERFORMED, // Used when current active bonus is still valid and pending bonus did not get activated
	ERROR_ACTIVATING_PENDING_BONUS
}
