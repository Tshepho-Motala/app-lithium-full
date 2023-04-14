CREATE TABLE `fail_login_attempt`
(
    ip VARCHAR(255),
    domain_name VARCHAR(255),
    failure_amount int(11),
    version int(11) not null,
    date_added datetime,
    PRIMARY KEY (`ip`),
    KEY `idx_domain` (`domain_name`),
    KEY `idx_date_added` (`date_added`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
