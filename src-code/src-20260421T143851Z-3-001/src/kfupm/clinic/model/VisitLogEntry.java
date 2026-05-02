package kfupm.clinic.model;

public record VisitLogEntry(long servedEpochMillis,
                            String patientId,
                            String patientName,
                            String type,
                            String doctor,
                            String notes) {
    @Override public String toString() {
        String n = (notes == null) ? "" : notes;
        return servedEpochMillis + " | " + type + " | " + patientId + " " + patientName + " | " + doctor + " | " + n;
    }
}
