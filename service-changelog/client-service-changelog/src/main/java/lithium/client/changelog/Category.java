package lithium.client.changelog;

import lombok.Getter;

@Getter
public enum Category {
	ACCOUNT("Account"),
	ACCESS("Access"),
	SUPPORT("Support"),
	RETENTION("Retention"),
	BONUSES("Bonuses"),
	SALES("Sales"),
	RESPONSIBLE_GAMING("Responsible Gaming"),
	FINANCE("Finance"),
	DOCUMENTS("Documents"),
	REWARDS("Rewards"),
	PROMOTIONS("Promotions");

	private final String name;

	Category(String name) {
		this.name = name;
	}

	public static Category fromName(String name) {

		for (Category s: Category.values()) {
			if (s.name.equalsIgnoreCase(name)) {
				return s;
			}
		}
		return null;
	}
}
