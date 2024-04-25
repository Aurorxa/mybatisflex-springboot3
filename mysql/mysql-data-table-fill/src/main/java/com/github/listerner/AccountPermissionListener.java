package com.github.listerner;

import com.mybatisflex.annotation.SetListener;

public class AccountPermissionListener implements SetListener {
    @Override
    public Object onSet(Object entity, String property, Object value) {
        if (isPassword(property)) {
            // 当前用户
            String currentUser = "lisi";

            // 如果当前用户有权限，则返回属性值
            if (hasPermission(currentUser)) {
                return value;
            }

            // 否则不返回数据
            return null;
        }
        // 其它的属性值直接返回
        return value;
    }

    /**
     * 判断属性是否是密码字段
     *
     * @param property 属性
     * @return true 或 false
     */
    public boolean isPassword(String property) {
        return "password".equals(property);
    }

    /**
     * 判断当前用户是否具有权限
     *
     * @param currentUser 当前用户
     * @return true 或 false
     */
    public boolean hasPermission(String currentUser) {
        return "admin".equals(currentUser);
    }
}
