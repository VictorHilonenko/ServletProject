package beauty.scheduler.dto;

import java.time.LocalDate;

public class FeedbackDTO {
    private Long id;

    private LocalDate appointmentDate;

    private Byte appointmentTime;

    private String serviceType;

    private String customerName;

    private String masterName;

    private Byte rating;

    private String textMessage;

    public FeedbackDTO() {
    }

    public FeedbackDTO(Long id, LocalDate appointmentDate, Byte appointmentTime, String serviceType, String customerName, String masterName, Byte rating, String textMessage) {
        this.id = id;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.serviceType = serviceType;
        this.customerName = customerName;
        this.masterName = masterName;
        this.rating = rating;
        this.textMessage = textMessage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public Byte getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(Byte appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
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