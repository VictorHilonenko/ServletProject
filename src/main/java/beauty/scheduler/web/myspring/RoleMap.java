package beauty.scheduler.web.myspring;

import beauty.scheduler.entity.enums.Role;

import java.util.Map;

//NOTE: ready for review
public class RoleMap<T> {
    private Map<Role, T> roleMap;

    public RoleMap(Map<Role, T> roleMap) {
        this.roleMap = roleMap;
    }

    public RoleMap addValueForRole(Role roleTag, T value) {
        Role.getAllByRoleTag(roleTag).forEach(role -> {
            this.roleMap.put(role, value);
        });

        return this;
    }

    public T getForRole(Role role) {
        return roleMap.get(role);
    }

    public Map<Role, T> getRoleMap() {
        return this.roleMap;
    }

    public void setRoleMap(Map<Role, T> roleMap) {
        this.roleMap = roleMap;
    }
}