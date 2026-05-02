package kfupm.clinic.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import kfupm.clinic.api.Result;
import kfupm.clinic.ds.*;
import kfupm.clinic.model.*;

/**
 * Students implement the system logic here.
 *
 * Rules:
 * - Use the provided custom data structures.
 * - Do NOT use Java built-in maps/trees/priority queues for storage.
 */
public class ClinicServiceImpl implements ClinicService {

    // Hash tables
    private final HashTable<String, Patient> patientsById = new HashTable<>();
    private final HashTable<String, Appointment> apptsById = new HashTable<>();

    // Appointment schedule index (AVL)
    private final AVLTree<AppointmentKey, Appointment> apptsByTime = new AVLTree<>();

    // Walk-ins and urgent
    private final LinkedQueue<Patient> walkIns = new LinkedQueue<>();
    private final MaxHeap<UrgentPatient> urgentHeap = new MaxHeap<>((a, b) -> {
        // Higher severity first; tie-break earlier arrival first.
        if (a.severity() != b.severity()) return Integer.compare(a.severity(), b.severity());
        // earlier arrival should win => invert compare so earlier is "greater"
        return Long.compare(b.arrivalEpochMillis(), a.arrivalEpochMillis());
    });

    // Undo + log
    private final LinkedStack<Action> undo = new LinkedStack<>();
    private final SinglyLinkedList<VisitLogEntry> log = new SinglyLinkedList<>();

    private final StringMatcher naive = new NaiveMatcher();
    private final StringMatcher kmp = new KMPMatcher();

    private int nextApptId = 1;

    @Override
    public Result<Void> addPatient(String id, String name, String phone) {
        if (id == null || id.isBlank()) {
            return Result.fail("Patient id is required.");
        }

        if (name == null || name.isBlank()) {
            return Result.fail("Patient name is required.");
        }

        if (phone == null || phone.isBlank()) {
            return Result.fail("Patient phone is required.");
        }

        Patient existingPatient = patientsById.get(id);

        if (existingPatient != null) {
            return Result.fail("Patient already exists.");
        }

        Patient patient = new Patient(id, name, phone);

        patientsById.put(id, patient);

        undo.push(new Action(ActionType.ADD_PATIENT, patient));

        return Result.ok(null, "Patient added.");
    }

    @Override
    public Result<Patient> findPatient(String id) {
        if (id == null || id.isBlank()) {
            return Result.fail("Patient id is required.");
        }

        Patient patient = patientsById.get(id);

        if (patient == null) {
            return Result.fail("Patient not found.");
        }

        return Result.ok(patient, "Patient found.");
    }

    @Override
    public Result<Void> deletePatient(String id) {
        if (id == null || id.isBlank()) {
            return Result.fail("Patient id is required.");
        }

        Patient removedPatient = patientsById.remove(id);

        if (removedPatient == null) {
            return Result.fail("Patient not found.");
        }

        undo.push(new Action(ActionType.DELETE_PATIENT, removedPatient));

        return Result.ok(null, "Patient deleted.");
    }

    @Override
    public Result<String> addAppointment(String patientId, LocalDate date, LocalTime time, String doctor) {
        if (patientId == null || patientId.isBlank()) {
            return Result.fail("Patient id is required.");
        }

        if (date == null) {
            return Result.fail("Date is required.");
        }

        if (time == null) {
            return Result.fail("Time is required.");
        }

        if (doctor == null || doctor.isBlank()) {
            return Result.fail("Doctor is required.");
        }

        Patient patient = patientsById.get(patientId);

        if (patient == null) {
            return Result.fail("Patient not found.");
        }

        if (hasAppointmentAt(date, time)) {
            return Result.fail("Appointment slot is already taken.");
        }

        String appointmentId = newAppointmentId();

        Appointment appointment = new Appointment(
                appointmentId,
                patient.id(),
                patient.name(),
                patient.phone(),
                date,
                time,
                doctor
        );

        AppointmentKey key = appointmentKey(appointment);

        apptsById.put(appointmentId, appointment);
        apptsByTime.put(key, appointment);

        undo.push(new Action(ActionType.ADD_APPT, appointment));

        return Result.ok(appointmentId, "Appointment added.");
    }

    @Override
    public Result<Void> cancelAppointment(String appointmentId) {
        if (appointmentId == null || appointmentId.isBlank()) {
            return Result.fail("Appointment id is required.");
        }

        Appointment appointment = apptsById.remove(appointmentId);

        if (appointment == null) {
            return Result.fail("Appointment not found.");
        }

        apptsByTime.remove(appointmentKey(appointment));

        undo.push(new Action(ActionType.CANCEL_APPT, appointment));

        return Result.ok(null, "Appointment cancelled.");
    }

    @Override
    public Result<Appointment> findAppointment(String appointmentId) {
        if (appointmentId == null || appointmentId.isBlank()) {
            return Result.fail("Appointment id is required.");
        }

        Appointment appointment = apptsById.get(appointmentId);

        if (appointment == null) {
            return Result.fail("Appointment not found.");
        }

        return Result.ok(appointment, "Appointment found.");
    }

    @Override
    public List<Appointment> viewDay(LocalDate date) {
        List<Appointment> result = new ArrayList<>();

        if (date == null) {
            return result;
        }

        apptsByTime.inOrder((key, appointment) -> {
            if (appointment.date().equals(date)) {
                result.add(appointment);
            }
        });

        return result;
    }

    @Override
    public List<Appointment> viewRange(LocalDate date, LocalTime start, LocalTime end) {
        List<Appointment> result = new ArrayList<>();

        if (date == null || start == null || end == null) {
            return result;
        }

        if (start.isAfter(end)) {
            return result;
        }

        apptsByTime.inOrder((key, appointment) -> {
            LocalTime appointmentTime = appointment.time();

            if (appointment.date().equals(date)
                    && !appointmentTime.isBefore(start)
                    && !appointmentTime.isAfter(end)) {
                result.add(appointment);
            }
        });

        return result;
    }

    @Override
    public Result<Void> addWalkIn(String patientId) {
        if (patientId == null || patientId.isBlank()) {
            return Result.fail("Patient id is required.");
        }

        Patient patient = patientsById.get(patientId);

        if (patient == null) {
            return Result.fail("Patient not found.");
        }

        walkIns.enqueue(patient);

        undo.push(new Action(ActionType.ADD_WALKIN, patient));

        return Result.ok(null, "Walk-in added.");
    }

    @Override
    public List<Patient> viewWalkIns() {
        // Non-destructive view
        return walkIns.toList();
    }

    @Override
    public Result<Void> addUrgent(String patientId, int severity) {
        if (patientId == null || patientId.isBlank()) {
            return Result.fail("Patient id is required.");
        }

        if (severity < 1 || severity > 5) {
            return Result.fail("Severity must be between 1 and 5.");
        }

        Patient patient = patientsById.get(patientId);

        if (patient == null) {
            return Result.fail("Patient not found.");
        }

        UrgentPatient urgentPatient = new UrgentPatient(
                patient,
                severity,
                System.currentTimeMillis()
        );

        urgentHeap.push(urgentPatient);

        undo.push(new Action(ActionType.ADD_URGENT, urgentPatient));

        return Result.ok(null, "Urgent patient added.");
    }

    @Override
    public Result<UrgentPatient> peekUrgent() {
        UrgentPatient up = urgentHeap.peek();
        if (up == null) return Result.fail("No urgent patients.");
        return Result.ok(up, "Most urgent patient.");
    }

    @Override
    public List<UrgentPatient> viewUrgentsSnapshot() {
        return urgentHeap.toListSnapshot();
    }

    @Override
    public Result<VisitLogEntry> serveNext(String doctor, String note) {
        if (doctor == null || doctor.isBlank()) {
            return Result.fail("Doctor is required.");
        }

        if (note == null) {
            note = "";
        }

        String type;
        String patientId;
        String patientName;
        Object servedObject;

        if (!urgentHeap.isEmpty()) {
            UrgentPatient urgentPatient = urgentHeap.pop();

            type = "URGENT";
            patientId = urgentPatient.patient().id();
            patientName = urgentPatient.patient().name();
            servedObject = urgentPatient;

        } else if (!walkIns.isEmpty()) {
            Patient patient = walkIns.dequeue();

            type = "WALKIN";
            patientId = patient.id();
            patientName = patient.name();
            servedObject = patient;

        } else {
            AVLTree.Entry<AppointmentKey, Appointment> earliest = apptsByTime.minEntry();

            if (earliest == null) {
                return Result.fail("No patients to serve.");
            }

            Appointment appointment = earliest.value();

            apptsByTime.remove(earliest.key());
            apptsById.remove(appointment.appointmentId());

            type = "APPOINTMENT";
            patientId = appointment.patientId();
            patientName = appointment.patientName();
            servedObject = appointment;
        }

        VisitLogEntry entry = new VisitLogEntry(
                System.currentTimeMillis(),
                patientId,
                patientName,
                type,
                doctor,
                note
        );

        log.addLast(entry);

        undo.push(new Action(ActionType.SERVE, new ServedInfo(type, servedObject, entry)));

        return Result.ok(entry, "Patient served.");
    }

    @Override
    public List<VisitLogEntry> printLog() {
        return log.toList();
    }

    @Override
    public List<VisitLogEntry> searchLogNaive(String pattern) {
        List<VisitLogEntry> result = new ArrayList<>();

        if (pattern == null) {
            return result;
        }

        List<VisitLogEntry> entries = log.toList();

        for (VisitLogEntry entry : entries) {
            String notes = entry.notes();

            if (naive.contains(notes, pattern)) {
                result.add(entry);
            }
        }

        return result;
    }

    @Override
    public List<VisitLogEntry> searchLogKmp(String pattern) {
        List<VisitLogEntry> result = new ArrayList<>();

        if (pattern == null) {
            return result;
        }

        List<VisitLogEntry> entries = log.toList();

        for (VisitLogEntry entry : entries) {
            String notes = entry.notes();

            if (kmp.contains(notes, pattern)) {
                result.add(entry);
            }
        }

        return result;
    }

    @Override
    public Result<Action> undo() {
        if (undo.isEmpty()) {
            return Result.fail("Nothing to undo.");
        }

        Action action = undo.pop();

        if (action.type() == ActionType.ADD_PATIENT) {
            Patient patient = (Patient) action.payload();
            patientsById.remove(patient.id());

        } else if (action.type() == ActionType.DELETE_PATIENT) {
            Patient patient = (Patient) action.payload();
            patientsById.put(patient.id(), patient);

        } else if (action.type() == ActionType.ADD_APPT) {
            Appointment appointment = (Appointment) action.payload();

            apptsById.remove(appointment.appointmentId());
            apptsByTime.remove(appointmentKey(appointment));

        } else if (action.type() == ActionType.CANCEL_APPT) {
            Appointment appointment = (Appointment) action.payload();

            apptsById.put(appointment.appointmentId(), appointment);
            apptsByTime.put(appointmentKey(appointment), appointment);

        } else if (action.type() == ActionType.ADD_WALKIN) {
            walkIns.removeLast();

        } else if (action.type() == ActionType.ADD_URGENT) {
            UrgentPatient urgentPatient = (UrgentPatient) action.payload();
            urgentHeap.remove(urgentPatient);

        } else if (action.type() == ActionType.SERVE) {
            ServedInfo servedInfo = (ServedInfo) action.payload();

            log.removeLast();

            if (servedInfo.type().equals("URGENT")) {
                UrgentPatient urgentPatient = (UrgentPatient) servedInfo.servedObject();
                urgentHeap.push(urgentPatient);

            } else if (servedInfo.type().equals("WALKIN")) {
                Patient patient = (Patient) servedInfo.servedObject();
                walkIns.addFirst(patient);

            } else if (servedInfo.type().equals("APPOINTMENT")) {
                Appointment appointment = (Appointment) servedInfo.servedObject();

                apptsById.put(appointment.appointmentId(), appointment);
                apptsByTime.put(appointmentKey(appointment), appointment);
            }
        }

        return Result.ok(action, "Undo completed.");
    }

    private AppointmentKey appointmentKey(Appointment appointment) {
        return new AppointmentKey(
                appointment.date(),
                appointment.time(),
                appointment.appointmentId()
        );
    }

    private boolean hasAppointmentAt(LocalDate date, LocalTime time) {
        boolean[] found = {false};

        apptsByTime.inOrder((key, appointment) -> {
            if (appointment.date().equals(date) && appointment.time().equals(time)) {
                found[0] = true;
            }
        });

        return found[0];
    }

    // Helpers you may want
    private String newAppointmentId() {
        return "A" + (nextApptId++);
    }

    private record ServedInfo(String type, Object servedObject, VisitLogEntry logEntry) {}
}
