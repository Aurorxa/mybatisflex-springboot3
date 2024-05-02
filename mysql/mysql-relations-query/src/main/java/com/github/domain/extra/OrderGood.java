package com.github.domain.extra;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Table("tb_order_good")
@Accessors(chain = true)
public class OrderGood {
    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long goodId;

    private Long orderId;

    private Date createTime;

    private Date updateTime;
}
