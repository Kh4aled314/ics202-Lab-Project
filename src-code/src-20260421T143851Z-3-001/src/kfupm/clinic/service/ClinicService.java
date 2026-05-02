package kfupm.clinic.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import kfupm.clinic.api.Result;
import kfupm.clinic.model.*;

public interface ClinicService {

    // Patients (Hash)
    Result<Void> addPatient(String id, String name, String phone);
    Result<Patient> findPatient(String id);
    Result<Void> deletePatient(String id);

    // Appointments (AVL + Hash)
    Result<String> addAppointment(String patientId, LocalDate date, LocalTime time, String doctor);
    Result<Void> cancelAppointment(String appointmentId);
    Result<Appointment> findAppointment(String appointmentId);
    List<Appointment> viewDay(LocalDate date);
    List<Appointment> viewRange(LocalDate date, LocalTime start, LocalTime end);

    // Walk-ins (Queue)
    Result<Void> addWalkIn(String patientId);
    List<Patient> viewWalkIns();

    // Urgent (Heap)
    Result<Void> addUrgent(String patientId, int severity);
    Result<UrgentPatient> peekUrgent();
    List<UrgentPatient> viewUrgentsSnapshot();

    // Serve + Log
    Result<VisitLogEntry> serveNext(String doctor, String note);
    List<VisitLogEntry> printLog();

    // Search in log notes
    List<VisitLogEntry> searchLogNaive(String pattern);
    List<VisitLogEntry> searchLogKmp(String pattern);

    // Undo
    Result<Action> undo();
}
