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

#2016-12-10

alter table fivepk_seo add machine_auto int(2) default '0';

#2016-12-12
CREATE TABLE `fivepk_prefab_random` (
	`prefab_cards` INT (10) NOT NULL,
	`prefab_0` VARCHAR(10) default '0,0',
	`prefab_1` VARCHAR(10) default '0,0',
	`prefab_2` VARCHAR(10) default '0,0',
	`prefab_3` VARCHAR(10) default '0,0',
	`prefab_4` VARCHAR(10) default '0,0',
	`prefab_5` VARCHAR(10) default '0,0',
	`prefab_6` VARCHAR(10) default '0,0',
	`prefab_7` VARCHAR(10) default '0,0',
	PRIMARY KEY (`prefab_cards`)
) ENGINE = INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

alter table fivepk_seo add COLUMN prefab_four_of_a_kind_ja int(20) DEFAULT 4 after prefab_four_of_a_kind_joker_count;
alter table fivepk_seo add COLUMN prefab_four_of_a_kind_two_ten int(20) DEFAULT 4 after prefab_four_of_a_kind_ja;
alter table fivepk_seo add COLUMN prefab_full_house int(20) DEFAULT 4 after prefab_four_of_a_kind_two_ten;
alter table fivepk_seo add COLUMN prefab_flush int(20) DEFAULT 4 after prefab_full_house;
alter table fivepk_seo add COLUMN prefab_straight int(20) DEFAULT 4 after prefab_flush;
alter table fivepk_seo add COLUMN prefab_three_of_a_kind int(20) DEFAULT 4 after prefab_straight;
alter table fivepk_seo add COLUMN prefab_two_pairs int(20) DEFAULT 4 after prefab_three_of_a_kind;
alter table fivepk_seo add COLUMN prefab_seven_better int(20) DEFAULT 4 after prefab_two_pairs;
alter table fivepk_seo add COLUMN prefab_four_flush int(20) DEFAULT 4 after prefab_seven_better;
alter table fivepk_seo add COLUMN prefab_four_straight int(20) DEFAULT 4 after prefab_four_flush;
alter table fivepk_seo add COLUMN prefab_seven_better_keep int(20) DEFAULT 4 after prefab_four_straight;


#2016-12-14

alter table fivepk_seo add COLUMN prefab_five_of_a_kind_compare int(20) DEFAULT 1 after prefab_five_of_a_kind_count;
alter table fivepk_prefab modify prefab_0 VARCHAR(255) DEFAULT '0' NOT NULL;
alter table fivepk_prefab modify prefab_1 VARCHAR(255) DEFAULT '0' NOT NULL;
alter table fivepk_prefab modify prefab_2 VARCHAR(255) DEFAULT '0' NOT NULL;
alter table fivepk_prefab modify prefab_3 VARCHAR(255) DEFAULT '0' NOT NULL;
alter table fivepk_prefab modify prefab_4 VARCHAR(255) DEFAULT '0' NOT NULL;
alter table fivepk_prefab modify prefab_5 VARCHAR(255) DEFAULT '0' NOT NULL;
alter table fivepk_prefab modify prefab_6 VARCHAR(255) DEFAULT '0' NOT NULL;
alter table fivepk_prefab modify prefab_7 VARCHAR(255) DEFAULT '0' NOT NULL;

alter table fivepk_seo modify prefab_five_bars_count double DEFAULT 0;
alter table fivepk_seo modify prefab_royal_flush_count double DEFAULT 0;
alter table fivepk_seo modify prefab_five_of_a_kind_count double DEFAULT 0;
alter table fivepk_seo modify prefab_straight_flush_count double DEFAULT 0;
alter table fivepk_seo modify prefab_four_of_a_kind_Joker_count double DEFAULT 0;

alter table fivepk_seo add COLUMN prefab_four_of_a_kind_Joker_two_fourteen int(20) DEFAULT 0 after prefab_four_of_a_kind_Joker_count;
#2016-12-15
alter table fivepk_seo add COLUMN prefab_four_of_a_kind_J_A int(20) DEFAULT 6 after prefab_four_of_a_kind_Joker_two_fourteen;
alter table fivepk_seo add COLUMN prefab_four_of_a_kind_T_T int(20) DEFAULT 6 after prefab_four_of_a_kind_JA;
alter table fivepk_seo add COLUMN prefab_four_of_a_kind_J_A_count int(20) DEFAULT 0 after prefab_four_of_a_kind_J_A;
alter table fivepk_seo add COLUMN prefab_four_of_a_kind_T_T_count int(20) DEFAULT 0 after prefab_four_of_a_kind_T_T;

CREATE TABLE `fivepk_default` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) DEFAULT NULL,
  `machine_id` varchar(20) DEFAULT NULL,
  `credit` int(10) DEFAULT '0',
  `bet` int(10) DEFAULT '0',
  `win` int(10) DEFAULT '0',
  `one_card` varchar(50) DEFAULT NULL,
  `guard_card` varchar(30) DEFAULT "",
  `two_card` varchar(50) DEFAULT NULL,
  `card_type` int(5) DEFAULT NULL,
  `guess_point` int(10) DEFAULT '0',
  `guess_type` varchar(200) DEFAULT "",
  `fivepk_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8;

#2016-12-16
alter table fivepk_seo add COLUMN prefab_joker int(20) DEFAULT 4 after prefab_seven_better_keep;
alter table fivepk_seo add COLUMN prefab_four_of_a_kind_two_ten_continue varchar(255) DEFAULT null after prefab_four_of_a_kind_two_ten;
alter table fivepk_seo add COLUMN prefab_four_of_a_kind_two_ten_two int(5) DEFAULT 7 after prefab_four_of_a_kind_two_ten;

alter table machine_default add four2 int(10) default '0';
alter table machine_default add four3 int(10) default '0';
alter table machine_default add four4 int(10) default '0';
alter table machine_default add four5 int(10) default '0';
alter table machine_default add four6 int(10) default '0';
alter table machine_default add four7 int(10) default '0';
alter table machine_default add four8 int(10) default '0';
alter table machine_default add four9 int(10) default '0';
alter table machine_default add four10 int(10) default '0';
alter table machine_default add four11 int(10) default '0';
alter table machine_default add four12 int(10) default '0';
alter table machine_default add four13 int(10) default '0';
alter table machine_default add four14 int(10) default '0';
alter table machine_default add five1 int(10) default '0';
alter table machine_default add five2 int(10) default '0';

CREATE TABLE `fivepk_path` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(10) DEFAULT NULL,
  `machine_id` varchar(10) DEFAULT NULL,
  `win_point` int(20) DEFAULT '0',
  `play_point` int(20) DEFAULT '0',
  `win_number` int(20) DEFAULT '0',
  `play_number` int(20) DEFAULT '0',
  `begin_point` int(20) DEFAULT '0',
  `end_point` int(20) DEFAULT '0',
  `access_point` int(10) DEFAULT '0',
  `begin_time` timestamp NULL DEFAULT NULL,
  `end_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `login_ip` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#2016-12-20
alter table fivepk_seo add compare_history_cards varchar(50) default null;

#2016-12-21
alter table fivepk_seo modify prefab_four_of_a_kind_ja int(20) DEFAULT '3';
alter table fivepk_seo modify prefab_four_of_a_kind_two_ten int(20) DEFAULT '3';

#2016-12-22
update role set name='管理员' where id=1;
update role set name='开分员' where id=2;

#2016-12-27
alter table fivepk_seo add prefab_force_seven_better TINYINT default 4;
alter table fivepk_seo add prefab_force_seven_better_count SMALLINT default 0;

alter table fivepk_default modify guess_point varchar(200) default '';

#2016-12-28
alter table fivepk_seo add prefab_compare_buff INT default 60000;
alter table fivepk_seo add prefab_compare_cut_down SMALLINT default 4;
alter table fivepk_seo add prefab_compare_cut_down_count SMALLINT default 0;
alter table fivepk_seo add prefab_compare_seven_joker SMALLINT default 0;

#2016-12-29
alter table fivepk_seo modify prefab_four_of_a_kind_Joker_two_fourteen int(20) DEFAULT '2'

#2016-12-30
alter table fivepk_seo modify prefab_four_of_a_kind_joker int(20) DEFAULT '7';
alter table fivepk_seo modify prefab_four_of_a_kind_Joker_two_fourteen int(20) DEFAULT '0';

#2017-1-3
insert role(id,name) value(3,'超级管理员');

#2017-1-10
alter table fivepk_seo modify prefab_four_of_a_kind_J_A_count double DEFAULT 0;
alter table fivepk_seo modify prefab_four_of_a_kind_T_T_count double DEFAULT 0;

#2017-1-12
alter table seoid_diamond modify seoid varchar(100);

#2017-1-13
create table seopath(
id int(50) auto_increment primary key,
machine varchar(20) default null,
sname varchar(20) default null,
str varchar(200) default null,
nowtime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)
insert role(id,name) value(4,'财务员');

#2017-1-17
alter table fivepk_default add one_card_type int(10) DEFAULT '0';

#2017-1-19
alter table fivepk_service add coin int(10) default '0';
alter table fivepk_service change ports score int(10) default '0';

#2017-1-20
alter table `fivepk_default` add index index_machine_id(machine_id);