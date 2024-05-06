package com.github.tenant.domain.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class AccountRoleTableDef extends TableDef {

    public static final AccountRoleTableDef ACCOUNT_ROLE = new AccountRoleTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn ROLE_ID = new QueryColumn(this, "role_id");

    public final QueryColumn ACCOUNT_ID = new QueryColumn(this, "account_id");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, ROLE_ID, ACCOUNT_ID};

    public AccountRoleTableDef() {
        super("", "tb_account_role");
    }

    private AccountRoleTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public AccountRoleTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new AccountRoleTableDef("", "tb_account_role", alias));
    }

}
