package beauty.scheduler.web.form;

import static beauty.scheduler.util.AppConstants.*;

//NOTE: ready for review
public class RegistrationForm {
    @Regex(pattern = REGEX_NOT_EMPTY_ENGLISH, errorMessage = "warning.notEmptyEnglishLetters")
    private String firstNameEn;

    @Regex(pattern = REGEX_NOT_EMPTY_ENGLISH, errorMessage = "warning.notEmptyEnglishLetters")
    private String lastNameEn;

    @Regex(pattern = REGEX_URAINIAN, errorMessage = "warning.UkrainianLetters")
    private String firstNameUk;

    @Regex(pattern = REGEX_URAINIAN, errorMessage = "warning.UkrainianLetters")
    private String lastNameUk;

    @Regex(pattern = REGEX_EMAIL, errorMessage = "warning.emailWrong")
    private String email;

    @Regex(pattern = REGEX_TEL_FORMAT, errorMessage = "warning.wrongTelNumberFormat")
    private String telNumber;

    @Regex(pattern = REGEX_NOT_LT_2_ENGLISH, errorMessage = "warning.latinNotLessThen2")
    private String password;

    public RegistrationForm() {
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
}