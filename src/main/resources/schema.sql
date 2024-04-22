CREATE TABLE IF NOT EXISTS `customer` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `tb_account`(
    `id`        INTEGER PRIMARY KEY AUTO_INCREMENT,
    `user_name` VARCHAR(100),
    `age`       INTEGER,
    `birthday`  DATETIME
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;