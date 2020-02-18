package beauty.scheduler.web.myspring.form;

import beauty.scheduler.web.myspring.annotation.Regex;

import static beauty.scheduler.util.AppConstants.REGEX_EMAIL;
import static beauty.scheduler.util.AppConstants.REGEX_NOT_LT_2_ENGLISH;

public class LoginForm {
    @Regex(pattern = REGEX_EMAIL, errorMessage = "warning.emailWrong")
    private String email;

    @Regex(pattern = REGEX_NOT_LT_2_ENGLISH, errorMessage = "warning.latinNotLessThen2")
    private String password;

    public LoginForm() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}