package beauty.scheduler.dto;

public class FeedbackDTO {
    private int id;

    private String appointmentDate;

    private byte appointmentTime;

    private String serviceType;

    private String customerName;

    private String masterName;

    private byte rating;

    private String textMessage;

    public FeedbackDTO() {
    }

    public FeedbackDTO(int id, String appointmentDate, byte appointmentTime, String serviceType, String customerName, String masterName, byte rating, String textMessage) {
        this.id = id;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.serviceType = serviceType;
        this.customerName = customerName;
        this.masterName = masterName;
        this.rating = rating;
        this.textMessage = textMessage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public byte getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(byte appointmentTime) {
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
}