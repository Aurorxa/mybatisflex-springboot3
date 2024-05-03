package com.github.domain;

import com.mybatisflex.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

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

    @RelationManyToOne(selfField = "accountId", targetField = "id")
    private Account account;

    @RelationManyToMany(
            joinTable = "tb_order_good", // 中间表
            selfField = "id",
            joinSelfColumn = "order_id",
            targetField = "id",
            joinTargetColumn = "good_id")
    private List<Good> goodList;


}
