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

