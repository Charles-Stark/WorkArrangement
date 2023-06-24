/*
 Navicat Premium Data Transfer

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 80033
 Source Host           : localhost:3306
 Source Schema         : workarrangement

 Target Server Type    : MySQL
 Target Server Version : 80033
 File Encoding         : 65001

 Date: 24/06/2023 18:21:16
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for absence
-- ----------------------------
DROP TABLE IF EXISTS `absence`;
CREATE TABLE `absence`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id, 自增主键',
  `employeeId` bigint NOT NULL COMMENT '请假员工id',
  `managerId` bigint NOT NULL COMMENT '管理员id',
  `shopId` bigint NOT NULL COMMENT '店铺id',
  `reason` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请假原因',
  `attachmentPhoto` mediumblob NULL COMMENT '图片附件',
  `photoType` char(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片类型',
  `isApproved` tinyint(1) NULL DEFAULT NULL COMMENT '请假是否通过',
  `absenceDate` date NOT NULL COMMENT '请假日期',
  `createAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for employee
-- ----------------------------
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '员工id，与User中id相符',
  `uid` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '员工uid，店铺代码+编号，类似学号',
  `position` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '员工职位，可选：门店经理，副经理，小组长，店员',
  `shop` bigint NOT NULL COMMENT '员工所属门店',
  `salary` double NOT NULL COMMENT '员工薪资',
  `time` double NOT NULL COMMENT '员工工作时长',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uid`(`uid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for employeetoschedule
-- ----------------------------
DROP TABLE IF EXISTS `employeetoschedule`;
CREATE TABLE `employeetoschedule`  (
  `employeeId` bigint UNSIGNED NOT NULL COMMENT '员工id',
  `scheduleId` bigint NOT NULL COMMENT '该员工所处的最新排班表id',
  PRIMARY KEY (`employeeId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for flow
-- ----------------------------
DROP TABLE IF EXISTS `flow`;
CREATE TABLE `flow`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '客流量数据id，自增主键',
  `shop` bigint NOT NULL COMMENT '门店id，与shop中id相符',
  `date` date NOT NULL COMMENT '当天的日期',
  `flowUnits` json NOT NULL COMMENT '当天客流量数据，json',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 420 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '通知id，自增主键',
  `isRead` tinyint(1) NOT NULL COMMENT '是否已读，已读为1，未读为0',
  `fromUser` bigint NOT NULL COMMENT '发送者id',
  `toUser` bigint NOT NULL COMMENT '接收者id',
  `type` int NOT NULL COMMENT '通知类型',
  `text` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '通知文本',
  `createAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '通知生成时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 340 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for preference
-- ----------------------------
DROP TABLE IF EXISTS `preference`;
CREATE TABLE `preference`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '员工id，与User中id相符',
  `workingDay` char(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '工作日偏好，数字表示星期几，英文逗号分隔，例：1,3,4',
  `workingHours` varchar(156) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '工作时间偏好，例：08:00-12:00,18:00-22:00',
  `durationOfShift` int NULL DEFAULT NULL COMMENT '班次时长偏好，每天时长不超过多少小时，例：4',
  `durationOfWeek` int NULL DEFAULT NULL COMMENT '每周最多工作时间，每周时长不超过多少小时，例：20',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for rule
-- ----------------------------
DROP TABLE IF EXISTS `rule`;
CREATE TABLE `rule`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '排班规则id，自增主键',
  `shop` bigint NOT NULL COMMENT '对应的门店id，与Shop中id相符',
  `prepareTime` double NOT NULL DEFAULT 1 COMMENT '开店前准备时间',
  `prepareWorkloadPerPerson` double NOT NULL DEFAULT 100 COMMENT '人均工作量u，门店面积/u=人数',
  `preparePosition` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `maxServiceNumber` double NOT NULL DEFAULT 3.8 COMMENT '最大服务人数u，预测客流/u=店员需求数',
  `servicePosition` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `numberOnDuty` int NOT NULL DEFAULT 1 COMMENT '值班人数，即无客流时所需最小人数',
  `closingTime` double NOT NULL DEFAULT 2 COMMENT '收尾时间',
  `closingWorkloadPerPersonU` double NOT NULL DEFAULT 80 COMMENT '人均收尾工作量u，门店面积/u+v=人数',
  `closingWorkloadPerPersonV` double NOT NULL DEFAULT 1 COMMENT '人均收尾工作量v，门店面积/u+v=人数',
  `closingPosition` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `balance` tinyint(1) NOT NULL DEFAULT 0 COMMENT '均衡排班',
  `minimumWorkingHourPerMonth` int NOT NULL DEFAULT 120 COMMENT '最小月工作时长',
  `maximumContinuousWorkingDays` int NOT NULL DEFAULT 5 COMMENT '最大连续工作天数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 581 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for schedule
-- ----------------------------
DROP TABLE IF EXISTS `schedule`;
CREATE TABLE `schedule`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '排班表id，自增主键',
  `shop` bigint NOT NULL COMMENT '对应门店id，与Shop中id相符',
  `manager` bigint NOT NULL COMMENT '对应管理员id，与User中id相符',
  `createAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '排班表生成时间',
  `isActive` tinyint(1) NOT NULL COMMENT '当前是否生效',
  `useRule` bigint NOT NULL COMMENT '使用的规则id，与Rule中id相符',
  `startAt` date NOT NULL COMMENT '开始日期',
  `endAt` date NOT NULL COMMENT '结束日期',
  `weeks` json NOT NULL COMMENT '排班表内容，json',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for shop
-- ----------------------------
DROP TABLE IF EXISTS `shop`;
CREATE TABLE `shop`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '门店id，自增主键',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '门店名称',
  `address` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '门店地址',
  `size` double NOT NULL COMMENT '门店面积，单位为平方米',
  `manager` bigint NOT NULL COMMENT '门店管理员id，与User中id相符',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 52 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for survey
-- ----------------------------
DROP TABLE IF EXISTS `survey`;
CREATE TABLE `survey`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `shop` bigint NOT NULL,
  `satisfaction` double NOT NULL,
  `nps` int NOT NULL,
  `q1` double NOT NULL,
  `q2` double NOT NULL,
  `q3` double NOT NULL,
  `q4` double NOT NULL,
  `q5` double NOT NULL,
  `q6` double NOT NULL,
  `optimizedValue` double NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户id，自增主键',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户邮箱，唯一，不为空',
  `password` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户密码，不为空，存储SHA256值',
  `username` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名，不为空',
  `photo` mediumblob NULL COMMENT '用户头像',
  `photoType` char(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户头像格式',
  `isManager` tinyint(1) NOT NULL COMMENT '用户是否为管理员',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `telephone`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 206 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
