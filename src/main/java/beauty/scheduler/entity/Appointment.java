package beauty.scheduler.entity;

import beauty.scheduler.dao.core.annotations.DBColumn;
import beauty.scheduler.dao.core.annotations.DBTable;
import beauty.scheduler.entity.enums.ServiceType;

import java.time.LocalDate;

import static beauty.scheduler.util.AppConstants.ID_FIELD;

//NOTE: ready for review
@DBTable(name = "appointments")
public class Appointment {
    @DBColumn(name = ID_FIELD)
    private Long id;

    @DBColumn(name = "appointment_date")
    private LocalDate appointmentDate;

    @DBColumn(name = "appointment_time")
    private Byte appointmentTime; //in this Project logic scope time will be only integer, like 8...20

    @DBColumn(name = "service_type")
    private ServiceType serviceType;

    @DBColumn(name = "customer_id")
    private User customer;

    @DBColumn(name = "master_id")
    private User master;

    @DBColumn(name = "service_provided")
    private Boolean serviceProvided;

    public Appointment() {
    }

    public Appointment(Long id, LocalDate appointmentDate, Byte appointmentTime, ServiceType serviceType, User customer, User master, Boolean serviceProvided) {
        this.id = id;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.serviceType = serviceType;
        this.customer = customer;
        this.master = master;
        this.serviceProvided = serviceProvided;
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

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public User getMaster() {
        return master;
    }

    public void setMaster(User master) {
        this.master = master;
    }

    public Boolean getServiceProvided() {
        return serviceProvided;
    }

    public void setServiceProvided(Boolean serviceProvided) {
        this.serviceProvided = serviceProvided;
    }

    //TODO write your own methods
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}