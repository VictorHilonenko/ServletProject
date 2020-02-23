package beauty.scheduler.web.myspring.core;

import beauty.scheduler.entity.enums.Role;

import java.util.HashMap;
import java.util.Map;

class RoleMap<T> {
    private Map<Role, T> roleMap = new HashMap<>();

    public RoleMap() {
    }

    RoleMap addValueForRole(Role roleTag, T value) {
        Role.getAllByRoleTag(roleTag).forEach(role -> {
            this.roleMap.put(role, value);
        });

        return this;
    }

    T getForRole(Role role) {
        return roleMap.get(role);
    }
}