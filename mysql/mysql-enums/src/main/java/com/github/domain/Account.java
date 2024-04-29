package com.github.domain;

import com.github.enums.AccountGradeEnum;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.RelationManyToMany;
import com.mybatisflex.annotation.Table;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Table(value = "tb_account")
@Accessors(chain = true)
public class Account {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String userName;

    private String password;

    private Integer age;

    private Date birthday;

    private AccountGradeEnum grade;

    private Date createTime;

    private Date updateTime;

    private BigDecimal money;

    @RelationManyToMany(
            joinTable = "tb_account_role", // 中间表
            selfField = "id",
            joinSelfColumn = "account_id",
            targetField = "id",
            joinTargetColumn = "role_id")
    private List<Role> role;
}
