package com.github.domain;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Table("tb_good")
@Accessors(chain = true)
public class Good {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String goodName;

    private Date createTime;

    private Date updateTime;

    private Long accountId;

}
