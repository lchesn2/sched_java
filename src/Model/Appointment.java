package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;

public class Appointment {
    private int appointmentId;
    private int customerId;
    private int userId;
    private String type;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String createdBy;

    public Appointment(int appointmentId, int customerId, int userId, String title, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, String createdBy) {
        this.appointmentId = appointmentId;
        this.customerId = customerId;
        this.userId = userId;
        this.type = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdBy = createdBy;
    }

    public static Appointment findAppointmentObject(int id) {
        for (Appointment a : appointments) {
            if (a.getAppointmentId() == id)
                return a;
        }
        return null;
    }

    public static ObservableList<Appointment> appointments = FXCollections.observableArrayList();

    public static void addAppointment(Appointment a) {
        appointments.add(a);
    }

    public static ObservableList<Appointment> getAppointments() {
        return appointments;
    }

    public static ObservableList<Month> months = FXCollections.observableArrayList();

    public static void addMonth(Month m) {
        months.add(m);
    }

    public static ObservableList<Month> getMonths() {
        return months;
    }

    public static boolean deleteAppointment(int id) {
        for (Appointment a : appointments) {
            if (a.getAppointmentId() == id) {
                Appointment.getAppointments().remove(a);
                return true;
            }
        }
        return false;
    }

    public static ObservableList<Appointment> appointmentsThisWeek = FXCollections.observableArrayList();

    public static void addAppointmentThisWeek(Appointment appt) {
        appointmentsThisWeek.add(appt);
    }

    public static ObservableList<Appointment> getAppointmentsThisWeek() {
        return appointmentsThisWeek;
    }

    public static ObservableList<Appointment> appointmentsThisMonth = FXCollections.observableArrayList();

    public static void addAppointmentsThisMonth(Appointment appt) {
        appointmentsThisMonth.add(appt);
    }

    public static ObservableList<Appointment> getAppointmentsThisMonth() {
        return appointmentsThisMonth;
    }


    public static ObservableList<String> types = FXCollections.observableArrayList("Initial", "Follow-Up", "Other");

    public static ObservableList<String> getTypes() {
        return types;
    }

    public void setUserId(int id) {
        this.userId = id;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }


    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }


    public String getCreatedBy() {
        return createdBy;
    }
}