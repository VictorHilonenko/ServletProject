package beauty.scheduler.web.form;

import static beauty.scheduler.util.AppConstants.*;

//NOTE: ready for review
public class FeedbackForm {

    @Regex(pattern = REGEX_NUMBERS, errorMessage = "warning.onlyNumbers")
    private String id;

    @Regex(pattern = REGEX_1_10_RANGE, errorMessage = "warning.wrongRange1_10")
    private String rating;

    @Regex(pattern = REGEX_NOT_EMPTY_GENERAL, errorMessage = "warning.notEmptyGeneral")
    private String textMessage;

    public FeedbackForm() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }
}