CREATE TABLE `fivepk_account` (
  `account_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `password` varchar(25) COLLATE utf8_bin DEFAULT NULL,
  `seoid` varchar(25) COLLATE utf8_bin DEFAULT NULL,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`account_id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1000000 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `fivepk_player_info` (
  `account_id` bigint(20) NOT NULL,
  `name` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `nick_name` varchar(25) COLLATE utf8_bin NOT NULL,
  `pic` smallint(6) DEFAULT '0',
  `score` bigint(20) DEFAULT '0',
  PRIMARY KEY (`account_id`),
  UNIQUE KEY `nick_name` (`nick_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

alter table `fivepk_player_info` add COLUMN compare_history_cards VARCHAR(15);

alter table `fivepk_player_info` add UNIQUE KEY `name` (name);

alter table fivepk_player_info modify compare_history_cards varchar(50) COLLATE utf8_bin default null;

alter table fivepk_player_info modify score int(20) default 200000;

alter table fivepk_player_info add COLUMN coin int(20) default 0 AFTER pic;

//2016-11-07
alter table `fivepk_account` add column account_type smallint(1) default 0 COMMENT '0-游客1-普通玩家' after seoid;

update fivepk_account set account_type = 1 where name is not null;

alter table fivepk_player_info drop column name;

//2016-11-10
CREATE TABLE `fivepk_seo` (
  `auto_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `seoid` varchar(50) NOT NULL,
  `seo_machine_id` int(5) NOT NULL,
  `seo_machine_type` smallint(1) DEFAULT 0,
  `account_id` bigint(20) DEFAULT '0',
  `seo_machine_stay_time` timestamp NULL,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`auto_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000000 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

//2016-11-11
alter table `fivepk_player_info` add column nick_name_count int(5) DEFAULT 0;

alter table `fivepk_player_info` change score score int(20) DEFAULT 20000;

//2016-11-18
alter table fivepk_seo modify seo_machine_id varchar(50) not null;