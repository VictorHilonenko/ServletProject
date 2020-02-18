package beauty.scheduler.entity;

import beauty.scheduler.dao.core.annotations.DBColumn;
import beauty.scheduler.dao.core.annotations.DBTable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDate;

import static beauty.scheduler.util.AppConstants.ID_FIELD;

//NOTE: ready for review
@DBTable(name = "email_messages")
public class EmailMessage {
    @DBColumn(name = ID_FIELD)
    private Long id;

    @DBColumn(name = "email")
    private String email;

    @DBColumn(name = "subject")
    private String subject;

    @DBColumn(name = "text_message")
    private String textMessage;

    @DBColumn(name = "date_created")
    private LocalDate dateCreated;

    @DBColumn(name = "date_sent")
    private LocalDate dateSent;

    @DBColumn(name = "date_link_opened")
    private LocalDate dateLinkOpened;

    @DBColumn(name = "quick_access_code")
    private String quickAccessCode;

    public EmailMessage() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public LocalDate getDateSent() {
        return dateSent;
    }

    public void setDateSent(LocalDate dateSent) {
        this.dateSent = dateSent;
    }

    public LocalDate getDateLinkOpened() {
        return dateLinkOpened;
    }

    public void setDateLinkOpened(LocalDate dateLinkOpened) {
        this.dateLinkOpened = dateLinkOpened;
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
                .append(dateSent, that.dateSent)
                .append(dateLinkOpened, that.dateLinkOpened)
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
                .append(dateSent)
                .append(dateLinkOpened)
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
                .append("dateSent", dateSent)
                .append("dateLinkOpened", dateLinkOpened)
                .append("quickAccessCode", quickAccessCode)
                .toString();
    }
}