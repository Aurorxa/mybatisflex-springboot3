package com.github.domain;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Table("tb_order")
@Accessors(chain = true)
public class Order {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String orderKey;

    private String orderName;

    private Date createTime;

    private Date updateTime;

    private Long accountId;

}
