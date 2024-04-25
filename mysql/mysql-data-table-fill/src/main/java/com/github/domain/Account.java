package com.github.domain;

import com.github.listerner.AccountListener;
import com.github.listerner.AccountPermissionListener;
import com.github.listerner.LoggerListener;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.RelationManyToMany;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Table(value = "tb_account", onInsert = {AccountListener.class, LoggerListener.class}, onUpdate = {AccountListener.class, LoggerListener.class}, onSet = AccountPermissionListener.class)
@Accessors(chain = true)
public class Account {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String userName;

    private String password;

    private Integer age;

    private Date birthday;

    private Integer gender;

    private Date createTime;

    private Date updateTime;

    private BigDecimal money;

    @RelationManyToMany(joinTable = "tb_account_role", // 中间表
            selfField = "id", joinSelfColumn = "account_id", targetField = "id", joinTargetColumn = "role_id")
    private List<Role> role;
}