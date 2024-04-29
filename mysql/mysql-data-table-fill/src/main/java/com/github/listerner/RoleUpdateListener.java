package com.github.listerner;

import com.github.domain.Role;
import com.mybatisflex.annotation.AbstractUpdateListener;
import java.util.Date;

public class RoleUpdateListener extends AbstractUpdateListener<Role> {

    @Override
    public void doUpdate(Role entity) {
        entity.setUpdateTime(new Date());
    }
}
