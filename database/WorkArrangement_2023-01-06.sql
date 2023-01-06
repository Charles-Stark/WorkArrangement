# ************************************************************
# Sequel Ace SQL dump
# 版本号： 20044
#
# https://sequel-ace.com/
# https://github.com/Sequel-Ace/Sequel-Ace
#
# 主机: localhost (MySQL 8.0.27)
# 数据库: WorkArrangement
# 生成时间: 2023-01-06 05:36:01 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
SET NAMES utf8mb4;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE='NO_AUTO_VALUE_ON_ZERO', SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# 转储表 Employee
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Employee`;

CREATE TABLE `Employee` (
  `id` bigint unsigned NOT NULL COMMENT '员工id，与User中id相符',
  `uid` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '员工uid，店铺代码+编号，类似学号',
  `position` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '员工职位，可选：门店经理，副经理，小组长，店员',
  `shop` bigint NOT NULL COMMENT '员工所属门店',
  `salary` double NOT NULL COMMENT '员工薪资',
  `time` double NOT NULL COMMENT '员工工作时长',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uid` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 Flow
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Flow`;

CREATE TABLE `Flow` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '客流量数据id，自增主键',
  `shop` bigint NOT NULL COMMENT '门店id，与shop中id相符',
  `date` date NOT NULL COMMENT '当天的日期',
  `flowUnits` json NOT NULL COMMENT '当天客流量数据，json',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 Notification
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Notification`;

CREATE TABLE `Notification` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '通知id，自增主键',
  `isRead` tinyint(1) NOT NULL COMMENT '是否已读，已读为1，未读为0',
  `fromUser` bigint NOT NULL COMMENT '发送者id',
  `toUser` bigint NOT NULL COMMENT '接收者id',
  `type` int NOT NULL COMMENT '通知类型',
  `text` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '通知文本',
  `createAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '通知生成时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 Preference
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Preference`;

CREATE TABLE `Preference` (
  `id` bigint unsigned NOT NULL COMMENT '员工id，与User中id相符',
  `workingDay` char(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '工作日偏好，数字表示星期几，英文逗号分隔，例：1,3,4',
  `workingHours` varchar(156) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '工作时间偏好，例：08:00-12:00,18:00-22:00',
  `durationOfShift` int DEFAULT NULL COMMENT '班次时长偏好，每天时长不超过多少小时，例：4',
  `durationOfWeek` int DEFAULT NULL COMMENT '每周最多工作时间，每周时长不超过多少小时，例：20',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 Rule
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Rule`;

CREATE TABLE `Rule` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '排班规则id，自增主键',
  `shop` bigint NOT NULL COMMENT '对应的门店id，与Shop中id相符',
  `prepareTime` double NOT NULL DEFAULT '1' COMMENT '开店前准备时间',
  `prepareWorkloadPerPerson` double NOT NULL DEFAULT '100' COMMENT '人均工作量u，门店面积/u=人数',
  `preparePosition` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '开店前准备时限制职位，英文逗号隔开，无限制则为空',
  `maxServiceNumber` double NOT NULL DEFAULT '3.8' COMMENT '最大服务人数u，预测客流/u=店员需求数',
  `servicePosition` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '班次安排时限制职位，英文逗号隔开，无限制则为空',
  `numberOnDuty` int NOT NULL DEFAULT '1' COMMENT '值班人数，即无客流时所需最小人数',
  `closingTime` double NOT NULL DEFAULT '2' COMMENT '收尾时间',
  `closingWorkloadPerPersonU` double NOT NULL DEFAULT '80' COMMENT '人均收尾工作量u，门店面积/u+v=人数',
  `closingWorkloadPerPersonV` double NOT NULL DEFAULT '1' COMMENT '人均收尾工作量v，门店面积/u+v=人数',
  `closingPosition` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '收尾时限制职位，英文逗号隔开，无限制则为空',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 Schedule
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Schedule`;

CREATE TABLE `Schedule` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '排班表id，自增主键',
  `shop` bigint NOT NULL COMMENT '对应门店id，与Shop中id相符',
  `manager` bigint NOT NULL COMMENT '对应管理员id，与User中id相符',
  `createAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '排班表生成时间',
  `isActive` tinyint(1) NOT NULL COMMENT '当前是否生效',
  `useRule` bigint NOT NULL COMMENT '使用的规则id，与Rule中id相符',
  `startAt` date NOT NULL COMMENT '开始日期',
  `endAt` date NOT NULL COMMENT '结束日期',
  `weeks` json NOT NULL COMMENT '排班表内容，json',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 Shop
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Shop`;

CREATE TABLE `Shop` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '门店id，自增主键',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '门店名称',
  `address` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '门店地址',
  `size` double NOT NULL COMMENT '门店面积，单位为平方米',
  `manager` bigint NOT NULL COMMENT '门店管理员id，与User中id相符',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 User
# ------------------------------------------------------------

DROP TABLE IF EXISTS `User`;

CREATE TABLE `User` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '用户id，自增主键',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户邮箱，唯一，不为空',
  `password` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户密码，不为空，存储SHA256值',
  `username` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名，不为空',
  `photo` mediumblob COMMENT '用户头像',
  `photoType` char(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户头像格式',
  `isManager` tinyint(1) NOT NULL COMMENT '用户是否为管理员',
  PRIMARY KEY (`id`),
  UNIQUE KEY `telephone` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
