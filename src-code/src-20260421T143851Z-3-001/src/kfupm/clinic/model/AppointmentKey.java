package kfupm.clinic.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Composite key for scheduling order.
 * Sorted by date, then time, then appointmentId.
 */
public record AppointmentKey(LocalDate date, LocalTime time, String appointmentId) implements Comparable<AppointmentKey> {
    @Override
    public int compareTo(AppointmentKey o) {
        int c = date.compareTo(o.date);
        if (c != 0) return c;
        c = time.compareTo(o.time);
        if (c != 0) return c;
        return appointmentId.compareTo(o.appointmentId);
    }

    public String slotKey() {
        return date + "|" + time;
    }
}
