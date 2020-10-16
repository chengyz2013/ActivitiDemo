-- ----------------------------
-- Table structure for alarm_details
-- ----------------------------
CREATE TABLE IF NOT EXISTS `alarm_details` (
  `id`                     BIGINT(20)      NOT NULL  AUTO_INCREMENT COMMENT '自增ID',
  `serialNo`                   VARCHAR(64)     NOT NULL  COMMENT  '报警编号',
  `contractNo`                   VARCHAR(64)     NOT NULL  COMMENT  '合约号',
  `product`                   VARCHAR(64)     NOT NULL  COMMENT  '品种',
  `alarmDesc`                   VARCHAR(64)     COMMENT  '报警描述',
  `alarmType`                   VARCHAR(64)     COMMENT  '报警类型',
  `alarmDate`                   VARCHAR(64)     COMMENT  '报警发生日期（yyyyMMdd）',
  `alarmTime`                   VARCHAR(64)     COMMENT  '报警发生时间（hh:mm:ss.SSS）',
  `handleState`                   VARCHAR(64)   COMMENT  '处理状态',
  `instanceId`                   VARCHAR(64)    COMMENT  '流程实例ID',
  `execId`                   VARCHAR(64)    COMMENT  '流程执行ID',
  `nodeState`                   VARCHAR(64)     COMMENT  '流程节点状态',
  `createTime`                   TIMESTAMP    NOT NULL  COMMENT  '创建时间',
  `updateTime`                   TIMESTAMP    NOT NULL  COMMENT  '更新时间',
  `createUser`                   VARCHAR(64)    COMMENT  '创建者',
  `updateUser`                   VARCHAR(64)    COMMENT  '更新者',
  PRIMARY KEY (`id`),
  UNIQUE KEY `serialNo` (`serialNo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='alarm_details';