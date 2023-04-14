INSERT INTO `status` (`deleted`,`description`,`name`,`user_enabled`,`version`) VALUES
(false,'The user may login, but may have other restrictions in place to prevent logging in and/or performing other actions.','OPEN',true,0),
(false,'The user may not login right now, but in due time the system will automatically change the status to open.','FROZEN',false,0),
(false,'The user may not login.','BLOCKED',false,0);

CREATE TABLE `status_reason` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `status_reason` VALUES
(1,'SELF_EXCLUSION','Self-Exclusion',0),
(2,'GAMSTOP_SELF_EXCLUSION','Gamstop Self-Exclusion',0),
(3,'COOLING_OFF','Cooling Off',0),
(4,'PLAYER_REQUEST','Player Request',0),
(5,'RESPONSIBLE_GAMING','Responsible Gaming',0),
(6,'AML','Anti Money Laundering',0),
(7,'FRAUD','Fraud',0),
(8,'DUPLICATED_ACCOUNT','Duplicated Account',0),
(9,'OTHER','Other',0);

ALTER TABLE `user`
ADD COLUMN `status_reason_id` bigint(20) DEFAULT NULL,
ADD FOREIGN KEY `fk_status_reason` (`status_reason_id`) REFERENCES `status_reason` (`id`);

UPDATE `user` u
INNER JOIN `status` s ON u.status_id = s.id
SET `status_reason_id` =
CASE
	WHEN `s`.`name` = 'DISABLED' THEN 9
	WHEN `s`.`name` = 'DISABLED_UNDERAGE' THEN 9
	WHEN `s`.`name` = 'DISABLED_FRAUD' THEN 7
	WHEN `s`.`name` = 'SUSPEND_FRAUD' THEN 7
	WHEN `s`.`name` = 'DISABLED TEST ACCOUNT' THEN 9
	WHEN `s`.`name` = 'CONSENT_REMOVED' THEN 4
	WHEN `s`.`name` = 'DISABLED_ACCOUNT_CLOSURE' THEN 4
ELSE NULL
END;

UPDATE `user` u
INNER JOIN `status` s ON `u`.`status_id` = `s`.`id`
SET `status_id` =
CASE
	WHEN `s`.`user_enabled` = true THEN (SELECT `id` FROM `status` WHERE `name` = 'OPEN')
	ELSE (SELECT `id` FROM `status` WHERE `name` = 'BLOCKED')
END;

DELETE FROM `status` WHERE `name` NOT IN ('OPEN','FROZEN','BLOCKED');

CREATE TABLE `status_status_reason_association` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `reason_id` bigint(20) NOT NULL,
  `status_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_status_status_reason` (`status_id`,`reason_id`),
  KEY `FKaua8dlsb6ud3o7jx5u4ydasl8` (`reason_id`),
  CONSTRAINT `FKaua8dlsb6ud3o7jx5u4ydasl8` FOREIGN KEY (`reason_id`) REFERENCES `status_reason` (`id`),
  CONSTRAINT `FKwfx66e9hdig7tk1pvvp29mtd` FOREIGN KEY (`status_id`) REFERENCES `status` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
