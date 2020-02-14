package beauty.scheduler.entity;

import beauty.scheduler.dao.core.annotations.DBColumn;
import beauty.scheduler.dao.core.annotations.DBTable;

import static beauty.scheduler.util.AppConstants.ID_FIELD;

//NOTE: ready for review
@DBTable(name = "feedbacks")
public class Feedback {
    @DBColumn(name = ID_FIELD)
    private Long id;

    @DBColumn(name = "appointment_id")
    private Appointment appointment;

    @DBColumn(name = "rating")
    private Byte rating;

    @DBColumn(name = "text_message")
    private String textMessage;

    public Feedback() {
    }

    public Feedback(Long id, Appointment appointment, Byte rating, String textMessage) {
        this.id = id;
        this.appointment = appointment;
        this.rating = rating;
        this.textMessage = textMessage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public Byte getRating() {
        return rating;
    }

    public void setRating(Byte rating) {
        this.rating = rating;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }
}