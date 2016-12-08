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

update fivepk_account set account_type = 1 where name is not null;

alter table fivepk_player_info drop column name;

#2016-11-07
alter table `fivepk_account` add column account_type smallint(1) default 0 COMMENT '0-游客1-普通玩家' after seoid;

#2016-11-10
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

#2016-11-11
alter table `fivepk_player_info` add column nick_name_count int(5) DEFAULT 0;

alter table `fivepk_player_info` change score score int(20) DEFAULT 20000;

#2016-11-18
alter table fivepk_seo modify seo_machine_id varchar(50) not null;

#2016-11-19
alter table fivepk_account add account_info int(2) default 0;

alter table fivepk_player_info add total_online_time int(50) default 0;

alter table fivepk_player_info add end_login_time timestamp default current_timestamp on update current_timestamp ;

#2016-11-28
CREATE TABLE `fivepk_point` (
  `id` int(200) NOT NULL AUTO_INCREMENT,
  `accound_id` int(20) DEFAULT NULL,
  `seoid` varchar(50) DEFAULT NULL,
  `operate_math` int(20) DEFAULT NULL,
  `operate_up` int(20) DEFAULT NULL,
  `operate_down` int(20) DEFAULT NULL,
  `operate_type` varchar(10) DEFAULT NULL,
  `operate_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8;

CREATE TABLE `fivepk_service` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `in_ip` varchar(20) DEFAULT NULL,
  `out_ip` varchar(20) DEFAULT NULL,
  `ports` varchar(8) DEFAULT NULL,
  `enabled` varchar(2) DEFAULT NULL,
  `service_name` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;


#2016-11-29
CREATE TABLE `access_points` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `nick_name` varchar(20) DEFAULT NULL,
  `seoid` varchar(20) DEFAULT NULL,
  `on_score` int(20) DEFAULT '0',
  `on_coin` int(20) DEFAULT '0',
  `up_score` int(20) DEFAULT '0',
  `up_coin` int(20) DEFAULT '0',
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#2016-11-30
CREATE TABLE `machine_default` (
  `id` int(50) NOT NULL AUTO_INCREMENT,
  `seo_machine_id` varchar(50) NOT NULL,
  `seoid` varchar(50) DEFAULT NULL,
  `seven_better` int(20) DEFAULT '0',
  `two_pairs` int(20) DEFAULT '0',
  `three_kind` int(20) DEFAULT '0',
  `straight` int(20) DEFAULT '0',
  `flush` int(20) DEFAULT '0',
  `full_house` int(20) DEFAULT '0',
  `little_four_kind` int(20) DEFAULT '0',
  `big_four_kind` int(20) DEFAULT '0',
  `str_flush` int(20) DEFAULT '0',
  `five_kind` int(20) DEFAULT '0',
  `royal_flush` int(20) DEFAULT '0',
  `five_bars` int(20) DEFAULT '0',
  `win_number` int(50) DEFAULT '0',
  `play_number` int(50) DEFAULT '0',
  `play_sum_point` int(50) DEFAULT '0',
  `win_sum_point` int(50) DEFAULT '0',
  `oneday` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

CREATE TABLE `machine_match` (
  `id` int(50) NOT NULL AUTO_INCREMENT,
  `seo_machine_id` varchar(50) DEFAULT NULL,
  `seoid` varchar(50) DEFAULT NULL,
  `win_number` int(50) DEFAULT '0',
  `play_number` int(50) DEFAULT '0',
  `win_point` int(50) DEFAULT '0',
  `play_point` int(50) DEFAULT '0',
  `three` int(50) DEFAULT '0',
  `four` int(50) DEFAULT '0',
  `five` int(50) DEFAULT '0',
  `pass_number` int(50) DEFAULT '0',
  `pass_money` int(50) DEFAULT '0',
  `orider_machine_money` int(50) DEFAULT '0',
  `oneday` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE `machine_gain` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `seo_machine_id` varchar(50) DEFAULT NULL,
  `seoid` varchar(50) DEFAULT NULL,
  `add_win_number` int(50) DEFAULT '0',
  `add_play_number` int(50) DEFAULT '0',
  `add_win_point` int(50) DEFAULT '0',
  `add_play_point` int(50) DEFAULT '0',
  `gain` int(50) DEFAULT '0',
  `oneday` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

#2016-11-29
alter table `fivepk_seo` add COLUMN prefab_five_bars TINYINT(1) DEFAULT 7;
alter table `fivepk_seo` add COLUMN prefab_royal_flush TINYINT(1) DEFAULT 6;
alter table `fivepk_seo` add COLUMN prefab_five_of_a_kind TINYINT(1) DEFAULT 6;
alter table `fivepk_seo` add COLUMN prefab_straight_flush TINYINT(1) DEFAULT 4;
alter table `fivepk_seo` add COLUMN prefab_four_of_a_kind_Joker TINYINT(1) DEFAULT 5;

CREATE TABLE `fivepk_prefab` (
	`prefab_cards` INT (10) NOT NULL,
	`prefab_0` INT (10) default 0,
	`prefab_1` INT (10) default 0,
	`prefab_2` INT (10) default 0,
	`prefab_3` INT (10) default 0,
	`prefab_4` INT (10) default 0,
	`prefab_5` INT (10) default 0,
	`prefab_6` INT (10) default 0,
	`prefab_7` INT (10) default 0,
	PRIMARY KEY (`prefab_cards`)
) ENGINE = INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

alter table `fivepk_seo` add COLUMN seo_machine_play_count BIGINT DEFAULT 1;

alter table fivepk_prefab add column win_pool VARCHAR(255) NOT NULL

#2016-12-1
alter table fivepk_seo add COLUMN prefab_five_bars_count int(20) DEFAULT 0 after prefab_five_bars;
alter table fivepk_seo add COLUMN prefab_royal_flush_count int(20) DEFAULT 0 after prefab_royal_flush;
alter table fivepk_seo add COLUMN prefab_five_of_a_kind_count int(20) DEFAULT 0 after prefab_five_of_a_kind;
alter table fivepk_seo add COLUMN prefab_straight_flush_count int(20) DEFAULT 0 after prefab_straight_flush;
alter table fivepk_seo add COLUMN prefab_four_of_a_kind_Joker_count int(20) DEFAULT 0 after prefab_four_of_a_kind_Joker;

#2016-12-2
alter table fivepk_seo CHANGE prefab_four_of_a_kind_Joker_count prefab_four_of_a_kind_joker_count int(20) DEFAULT '0';
alter table fivepk_seo CHANGE prefab_four_of_a_kind_Joker prefab_four_of_a_kind_joker int(20) DEFAULT '5';

alter table machine_default change play_sum_point play_sum_point int(20) default '1';

alter table machine_default change play_number play_number int(20) default '1';

alter table machine_match change play_number play_number int(20) default '1';

alter table machine_match change play_point play_point int(20) default '1';

alter table machine_gain change add_play_point add_play_point int(20) default '1';

alter table fivepk_player_info modify `score` int(20) DEFAULT 0;

#2016-12-6
CREATE TABLE `fivepk_diamond` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `seoid` varchar(10) DEFAULT NULL,
  `diamond_math` int(20) DEFAULT NULL,
  `diamond_up` int(20) DEFAULT NULL,
  `diamond_down` int(20) DEFAULT NULL,
  `diamond_type` varchar(10) DEFAULT NULL,
  `login_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

CREATE TABLE `seoid_diamond` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `seoid` varchar(10) DEFAULT NULL,
  `diamond` int(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

alter table seoid_diamond modify diamond int(50) default '0';

insert seoid_diamond(seoid,diamond) value('CE',0);

#2016-12-7

alter table fivepk_account add account_ip varchar(20) default '0.0.0.0';

alter table machine_match add orider_machine_number int(20) default '0';

alter table fivepk_seo modify create_date timestamp default current_timestamp on update current_timestamp ;