package beauty.scheduler.entity;

import beauty.scheduler.dao.annotation.DBColumn;
import beauty.scheduler.dao.annotation.DBTable;
import beauty.scheduler.entity.enums.ServiceType;
import beauty.scheduler.util.LocaleUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static beauty.scheduler.util.AppConstants.ID_FIELD;

@DBTable(name = "appointments")
public class Appointment {
    @DBColumn(name = ID_FIELD)
    private int id;

    @DBColumn(name = "appointment_date")
    private LocalDate appointmentDate;

    @DBColumn(name = "appointment_time")
    private byte appointmentTime;

    @DBColumn(name = "service_type")
    private ServiceType serviceType;

    @DBColumn(name = "customer_id")
    private User customer;

    @DBColumn(name = "master_id")
    private User master;

    @DBColumn(name = "service_provided")
    private boolean serviceProvided;

    public Appointment() {
    }

    public Appointment(int id, LocalDate appointmentDate, byte appointmentTime, ServiceType serviceType, User customer, User master, boolean serviceProvided) {
        this.id = id;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.serviceType = serviceType;
        this.customer = customer;
        this.master = master;
        this.serviceProvided = serviceProvided;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public String getFormattedAppointmentDate(String lang) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(LocaleUtils.getLocalizedMessage("localdate.format.long", lang));
        return formatter.format(appointmentDate);
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public byte getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(byte appointmentTime) {
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

    public boolean getServiceProvided() {
        return serviceProvided;
    }

    public void setServiceProvided(boolean serviceProvided) {
        this.serviceProvided = serviceProvided;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Appointment that = (Appointment) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(appointmentDate, that.appointmentDate)
                .append(appointmentTime, that.appointmentTime)
                .append(serviceType, that.serviceType)
                .append(customer, that.customer)
                .append(master, that.master)
                .append(serviceProvided, that.serviceProvided)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(appointmentDate)
                .append(appointmentTime)
                .append(serviceType)
                .append(customer)
                .append(master)
                .append(serviceProvided)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("appointmentDate", appointmentDate)
                .append("appointmentTime", appointmentTime)
                .append("serviceType", serviceType)
                .append("customer", customer)
                .append("master", master)
                .append("serviceProvided", serviceProvided)
                .toString();
    }
}