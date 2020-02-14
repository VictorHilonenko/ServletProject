package beauty.scheduler.web.myspring;

import beauty.scheduler.entity.enums.Role;

import java.util.Optional;

//QUESTION:
//is it normal name?
public class UserPrincipal {
    private Optional<Long> id;
    private String email;
    private Role role;
    private String currentLang;

    public UserPrincipal() {
    }

    public UserPrincipal(Optional<Long> id, String email, Role role, String currentLang) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.currentLang = currentLang;
    }

    public boolean isAuthenticated() {
        return !"".equals(email);
    }

    public boolean hasRoleTag(String tag) {
        return role.hasTag(tag);
    }

    public Optional<Long> getId() {
        return id;
    }

    public void setId(Optional<Long> id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getCurrentLang() {
        return currentLang;
    }

    public void setCurrentLang(String currentLang) {
        this.currentLang = currentLang;
    }
}