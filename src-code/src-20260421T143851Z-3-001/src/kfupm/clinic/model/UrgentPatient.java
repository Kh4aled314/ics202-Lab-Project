package kfupm.clinic.model;

public record UrgentPatient(Patient patient, int severity, long arrivalEpochMillis) {
    @Override public String toString() {
        return patient.id() + " | " + patient.name() + " | severity=" + severity;
    }
}
