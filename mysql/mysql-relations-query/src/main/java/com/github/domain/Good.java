package com.github.domain;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.RelationManyToMany;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@Table("tb_good")
@Accessors(chain = true)
public class Good {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String goodName;

    private Date createTime;

    private Date updateTime;

    @RelationManyToMany(
            joinTable = "tb_order_good", // 中间表
            selfField = "id",
            joinSelfColumn = "good_id",
            targetField = "id",
            joinTargetColumn = "order_id")
    private List<Order> orderList;

}
