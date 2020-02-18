package beauty.scheduler.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class UserDTO {
    private String firstNameEn;

    private String lastNameEn;

    private String firstNameUk;

    private String lastNameUk;

    private String email;

    private String telNumber;

    private String role;

    private String serviceType;

    public UserDTO() {
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("firstNameEn", firstNameEn)
                .append("lastNameEn", lastNameEn)
                .append("firstNameUk", firstNameUk)
                .append("lastNameUk", lastNameUk)
                .append("email", email)
                .append("telNumber", telNumber)
                .append("role", role)
                .append("serviceType", serviceType)
                .toString();
    }
}