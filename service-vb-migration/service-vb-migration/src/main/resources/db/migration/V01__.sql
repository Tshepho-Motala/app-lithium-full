create table migration_exception_record
(
  id bigint (20) not null auto_increment,
  exception_message varchar(5000) null,
  customer_id varchar(255) null,
  migration_type varchar(255) null,
  request_json varchar(5000) null,
  PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table migration_credential
(
  id bigint not null auto_increment,
  hashed_password varchar(255) null,
  hashing_algorithm smallint not null,
  salt varchar(255) null,
  security_question varchar(255) null,
  security_question_answer varchar(255) null,
  username varchar(255) null,
  player_guid varchar(255) default null,
  customer_id varchar(255) default null,
  PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table progress
(
  `id` bigint (255) not null auto_increment,
  `chunk_size` int not null,
  `created_date` datetime not null,
  `updated_time` datetime default null,
  `migration_type` varchar(255) default null,
  `running` bit(1) DEFAULT 0,
  `last_row_processed` bigint not null,
  `customer_id` varchar(255) default null,
  `total_number_of_rows` bigint not null,
  PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
