package com.github.tenant.domain.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class AccountTableDef extends TableDef {

    public static final AccountTableDef ACCOUNT = new AccountTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn AGE = new QueryColumn(this, "age");

    public final QueryColumn MONEY = new QueryColumn(this, "money");

    public final QueryColumn GENDER = new QueryColumn(this, "gender");

    public final QueryColumn BIRTHDAY = new QueryColumn(this, "birthday");

    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    public final QueryColumn USER_NAME = new QueryColumn(this, "user_name");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, AGE, MONEY, GENDER, BIRTHDAY, TENANT_ID, USER_NAME, CREATE_TIME, UPDATE_TIME};

    public AccountTableDef() {
        super("", "tb_account");
    }

    private AccountTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public AccountTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new AccountTableDef("", "tb_account", alias));
    }

}
