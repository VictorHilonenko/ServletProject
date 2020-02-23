package beauty.scheduler.entity;

import beauty.scheduler.dao.annotation.DBColumn;
import beauty.scheduler.dao.annotation.DBTable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static beauty.scheduler.util.AppConstants.ID_FIELD;

@DBTable(name = "feedbacks")
public class Feedback {
    @DBColumn(name = ID_FIELD)
    private int id;

    @DBColumn(name = "appointment_id")
    private Appointment appointment;

    @DBColumn(name = "rating")
    private byte rating;

    @DBColumn(name = "text_message")
    private String textMessage;

    public Feedback() {
    }

    public Feedback(int id, Appointment appointment, byte rating, String textMessage) {
        this.id = id;
        this.appointment = appointment;
        this.rating = rating;
        this.textMessage = textMessage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public byte getRating() {
        return rating;
    }

    public void setRating(byte rating) {
        this.rating = rating;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Feedback feedback = (Feedback) o;

        return new EqualsBuilder()
                .append(id, feedback.id)
                .append(appointment, feedback.appointment)
                .append(rating, feedback.rating)
                .append(textMessage, feedback.textMessage)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(appointment)
                .append(rating)
                .append(textMessage)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("appointment", appointment)
                .append("rating", rating)
                .append("textMessage", textMessage)
                .toString();
    }
}