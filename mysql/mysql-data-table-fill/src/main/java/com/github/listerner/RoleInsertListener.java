package com.github.listerner;

import com.github.domain.Role;
import com.mybatisflex.annotation.AbstractInsertListener;
import java.util.Date;

public class RoleInsertListener extends AbstractInsertListener<Role> {
    @Override
    public void doInsert(Role entity) {
        entity.setCreateTime(new Date());
    }
}
