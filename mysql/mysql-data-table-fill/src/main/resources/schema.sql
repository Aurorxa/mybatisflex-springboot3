CREATE TABLE IF NOT EXISTS `customer` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `tb_account` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户名称',
    `password` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户密码',
    `age` int DEFAULT NULL COMMENT '年龄',
    `birthday` datetime DEFAULT NULL COMMENT '出生日期',
    `gender` int DEFAULT NULL COMMENT '等级',
    `money`   DECIMAL(10, 7) DEFAULT NULL COMMENT '余额',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

CREATE TABLE IF NOT EXISTS `tb_role` (
   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
   `role_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '角色名称',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色表';


CREATE TABLE IF NOT EXISTS `tb_account_role` (
   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
   `account_id` bigint DEFAULT NULL COMMENT '用户的id，外键',
   `role_id` bigint DEFAULT NULL COMMENT '角色的id，外键',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户和角色关联表';