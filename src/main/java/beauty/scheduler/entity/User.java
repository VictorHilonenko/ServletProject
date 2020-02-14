package beauty.scheduler.entity;

import beauty.scheduler.dao.core.annotations.DBColumn;
import beauty.scheduler.dao.core.annotations.DBTable;
import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.entity.enums.ServiceType;

import static beauty.scheduler.util.AppConstants.ID_FIELD;

//NOTE: ready for review
@DBTable(name = "users")
public class User {
    @DBColumn(name = ID_FIELD)
    private Long id;

    @DBColumn(name = "first_name_en")
    private String firstNameEn;

    @DBColumn(name = "last_name_en")
    private String lastNameEn;

    @DBColumn(name = "first_name_uk")
    private String firstNameUk;

    @DBColumn(name = "last_name_uk")
    private String lastNameUk;

    @DBColumn(name = "email")
    private String email;

    @DBColumn(name = "tel_number")
    private String telNumber;

    @DBColumn(name = "password")
    private String password;

    @DBColumn(name = "role")
    private Role role;

    @DBColumn(name = "service_type")
    private ServiceType serviceType;

    public User() {
    }

    public User(Long id, String firstNameEn, String lastNameEn, String firstNameUk, String lastNameUk, String email, String telNumber, String password, Role role, ServiceType serviceType) {
        this.id = id;
        this.firstNameEn = firstNameEn;
        this.lastNameEn = lastNameEn;
        this.firstNameUk = firstNameUk;
        this.lastNameUk = lastNameUk;
        this.email = email;
        this.telNumber = telNumber;
        this.password = password;
        this.role = role;
        this.serviceType = serviceType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstNameEn() {
        return firstNameEn;
    }

    public void setFirstNameEn(String firstNameEn) {
        this.firstNameEn = firstNameEn;
    }

    public String getLastNameEn() {
        return lastNameEn;
    }

    public void setLastNameEn(String lastNameEn) {
        this.lastNameEn = lastNameEn;
    }

    public String getFirstNameUk() {
        return firstNameUk;
    }

    public void setFirstNameUk(String firstNameUk) {
        this.firstNameUk = firstNameUk;
    }

    public String getLastNameUk() {
        return lastNameUk;
    }

    public void setLastNameUk(String lastNameUk) {
        this.lastNameUk = lastNameUk;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelNumber() {
        return telNumber;
    }

    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    //TODO toString()
}