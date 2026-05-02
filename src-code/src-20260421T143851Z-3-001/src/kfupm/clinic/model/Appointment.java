package kfupm.clinic.model;

import java.time.LocalDate;
import java.time.LocalTime;

public record Appointment(String appointmentId, String patientId, String patientName,
                          String phone, LocalDate date, LocalTime time, String doctor) {
    @Override public String toString() {
        return appointmentId + " | " + patientId + " | " + patientName + " | " + doctor + " | " + date + " " + time;
    }
}
