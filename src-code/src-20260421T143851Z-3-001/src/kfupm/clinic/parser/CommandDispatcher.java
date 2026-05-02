package kfupm.clinic.parser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import kfupm.clinic.api.Result;
import kfupm.clinic.model.*;
import kfupm.clinic.service.ClinicService;

/**
 * Parses tokens and calls the service.
 *
 * This file is intentionally simple and "dumb":
 * - Students should not store state here.
 */
public class CommandDispatcher {

    private final ClinicService service;

    public CommandDispatcher(ClinicService service) {
        this.service = service;
    }

    public Result<?> dispatch(List<String> t) {
        if (t.isEmpty()) return Result.ok(null, "");
        String cmd = t.get(0).toUpperCase();

        try {
            return switch (cmd) {
                case "HELP" -> Result.ok(null, helpText());
                case "ADD_PATIENT" -> {
                    requireArgs(t, 4);
                    yield service.addPatient(t.get(1), t.get(2), t.get(3));
                }
                case "FIND_PATIENT" -> {
                    requireArgs(t, 2);
                    yield service.findPatient(t.get(1));
                }
                case "DELETE_PATIENT" -> {
                    requireArgs(t, 2);
                    yield service.deletePatient(t.get(1));
                }
                case "ADD_APPT" -> {
                    requireArgs(t, 5);
                    LocalDate d = LocalDate.parse(t.get(2));
                    LocalTime tm = LocalTime.parse(t.get(3));
                    yield service.addAppointment(t.get(1), d, tm, t.get(4));
                }
                case "CANCEL_APPT" -> {
                    requireArgs(t, 2);
                    yield service.cancelAppointment(t.get(1));
                }
                case "FIND_APPT" -> {
                    requireArgs(t, 2);
                    yield service.findAppointment(t.get(1));
                }
                case "VIEW_DAY" -> {
                    requireArgs(t, 2);
                    LocalDate d = LocalDate.parse(t.get(1));
                    yield Result.ok(service.viewDay(d), "Day schedule");
                }
                case "VIEW_RANGE" -> {
                    requireArgs(t, 4);
                    LocalDate d = LocalDate.parse(t.get(1));
                    LocalTime s = LocalTime.parse(t.get(2));
                    LocalTime e = LocalTime.parse(t.get(3));
                    yield Result.ok(service.viewRange(d, s, e), "Range schedule");
                }
                case "ADD_WALKIN" -> {
                    requireArgs(t, 2);
                    yield service.addWalkIn(t.get(1));
                }
                case "VIEW_WALKINS" -> Result.ok(service.viewWalkIns(), "Walk-in queue");
                case "ADD_URGENT" -> {
                    requireArgs(t, 3);
                    int sev = Integer.parseInt(t.get(2));
                    yield service.addUrgent(t.get(1), sev);
                }
                case "PEEK_URGENT" -> service.peekUrgent();
                case "VIEW_URGENTS" -> Result.ok(service.viewUrgentsSnapshot(), "Urgent heap snapshot");
                case "SERVE_NEXT" -> {
                    requireArgs(t, 3);
                    yield service.serveNext(t.get(1), t.get(2));
                }
                case "PRINT_LOG" -> Result.ok(service.printLog(), "Visit log");
                case "SEARCH_LOG_NAIVE" -> {
                    requireArgs(t, 2);
                    yield Result.ok(service.searchLogNaive(t.get(1)), "Matches (naive)");
                }
                case "SEARCH_LOG_KMP" -> {
                    requireArgs(t, 2);
                    yield Result.ok(service.searchLogKmp(t.get(1)), "Matches (kmp)");
                }
                case "UNDO" -> service.undo();
                default -> Result.fail("Unknown command. Type HELP.");
            };
        } catch (UnsupportedOperationException ex) {
            // Used by starter code / partial student implementations.
            String msg = ex.getMessage();
            if (msg == null || msg.isBlank()) msg = "This feature is not implemented yet.";
            return Result.fail("NOT_SUPPORTED: " + msg);
        } catch (IllegalArgumentException ex) {
            return Result.fail("Bad arguments: " + ex.getMessage());
        } catch (Exception ex) {
            return Result.fail("Error: " + ex.getMessage());
        }
    }

    private static void requireArgs(List<String> t, int n) {
        if (t.size() < n) throw new IllegalArgumentException("Expected at least " + (n - 1) + " arguments.");
    }

    private static String helpText() {
        return String.join("\n",
                "Commands:",
                "  HELP",
                "  EXIT",
                "Patients:",
                "  ADD_PATIENT <id> <name> <phone>",
                "  FIND_PATIENT <id>",
                "  DELETE_PATIENT <id>",
                "Appointments:",
                "  ADD_APPT <patientId> <date YYYY-MM-DD> <time HH:MM> <doctor>",
                "  CANCEL_APPT <appointmentId>",
                "  FIND_APPT <appointmentId>",
                "  VIEW_DAY <date>",
                "  VIEW_RANGE <date> <startTime> <endTime>",
                "Walk-ins:",
                "  ADD_WALKIN <patientId>",
                "  VIEW_WALKINS",
                "Urgent:",
                "  ADD_URGENT <patientId> <severity 1..5>",
                "  PEEK_URGENT",
                "  VIEW_URGENTS",
                "Serve + Log:",
                "  SERVE_NEXT <doctor> <note>",
                "  PRINT_LOG",
                "Search notes:",
                "  SEARCH_LOG_NAIVE <pattern>",
                "  SEARCH_LOG_KMP <pattern>",
                "Undo:",
                "  UNDO"
        );
    }
}
