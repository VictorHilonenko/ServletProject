package beauty.scheduler.entity;

import beauty.scheduler.dao.annotation.DBColumn;
import beauty.scheduler.dao.annotation.DBTable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDate;

import static beauty.scheduler.util.AppConstants.ID_FIELD;

@DBTable(name = "email_messages")
public class EmailMessage {
    @DBColumn(name = ID_FIELD)
    private int id;

    @DBColumn(name = "email")
    private String email;

    @DBColumn(name = "subject")
    private String subject;

    @DBColumn(name = "text_message")
    private String textMessage;

    @DBColumn(name = "date_created")
    private LocalDate dateCreated;

    @DBColumn(name = "sent")
    private Boolean sent;

    @DBColumn(name = "quick_access_code")
    private String quickAccessCode;

    public EmailMessage() {
    }

    public EmailMessage(int id, String email, String subject, String textMessage, LocalDate dateCreated, Boolean sent, String quickAccessCode) {
        this.id = id;
        this.email = email;
        this.subject = subject;
        this.textMessage = textMessage;
        this.dateCreated = dateCreated;
        this.sent = sent;
        this.quickAccessCode = quickAccessCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Boolean getSent() {
        return sent;
    }

    public void setSent(Boolean sent) {
        this.sent = sent;
    }

    public String getQuickAccessCode() {
        return quickAccessCode;
    }

    public void setQuickAccessCode(String quickAccessCode) {
        this.quickAccessCode = quickAccessCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        EmailMessage that = (EmailMessage) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(email, that.email)
                .append(subject, that.subject)
                .append(textMessage, that.textMessage)
                .append(dateCreated, that.dateCreated)
                .append(sent, that.sent)
                .append(quickAccessCode, that.quickAccessCode)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(email)
                .append(subject)
                .append(textMessage)
                .append(dateCreated)
                .append(sent)
                .append(quickAccessCode)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("email", email)
                .append("subject", subject)
                .append("textMessage", textMessage)
                .append("dateCreated", dateCreated)
                .append("sent", sent)
                .append("quickAccessCode", quickAccessCode)
                .toString();
    }
}