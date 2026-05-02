package kfupm.clinic.model;

public record Patient(String id, String name, String phone) {
    @Override public String toString() {
        return id + " | " + name + " | " + phone;
    }
}
