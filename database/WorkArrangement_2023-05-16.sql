ALTER TABLE Rule ADD COLUMN `balance` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '均衡排班' AFTER closingPosition;
ALTER TABLE Rule ADD COLUMN `minimumWorkingHourPerMonth` INT NOT NULL DEFAULT 120 COMMENT '最小月工作时长' AFTER balance;
ALTER TABLE Rule ADD COLUMN `maximumContinuousWorkingDays` INT NOT NULL DEFAULT 5 COMMENT '最大连续工作天数' AFTER minimumWorkingHourPerMonth;