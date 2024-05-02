package com.github.domain.extra;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Table("tb_account_role")
@Accessors(chain = true)
public class AccountRole {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long accountId;

    private Long roleId;

    private Date createTime;

    private Date updateTime;
}
