package com.github.domain;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.RelationManyToMany;
import com.mybatisflex.annotation.Table;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Table(value = "tb_role")
@Accessors(chain = true)
public class Role {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String roleName;

    private Date createTime;

    private Date updateTime;

    @RelationManyToMany(
            joinTable = "tb_account_role", // 中间表
            selfField = "id", // 当前实体类的属性
            joinSelfColumn = "role_id", // 当前表和中间表的关系字段
            targetField = "id", // 目标实体类的属性
            joinTargetColumn = "account_id" // 目标表和中间表的关系字段
            )
    private List<Account> accounts;
}
