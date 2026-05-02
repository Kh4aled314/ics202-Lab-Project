package kfupm.clinic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import kfupm.clinic.api.Result;
import kfupm.clinic.parser.CommandDispatcher;
import kfupm.clinic.parser.CommandTokenizer;
import kfupm.clinic.service.ClinicService;
import kfupm.clinic.service.ClinicServiceImpl;

public class ClinicSystem {
    public static void main(String[] args) throws Exception {
        ClinicService service = new ClinicServiceImpl();
        CommandDispatcher dispatcher = new CommandDispatcher(service);

        System.out.println("KFUPM Clinic System (CLI)");
        System.out.println("Type HELP to see commands. Type EXIT to quit.");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("> ");
            String line = br.readLine();
            if (line == null) break;
            line = line.trim();
            if (line.isEmpty()) continue;
            if (line.equalsIgnoreCase("EXIT")) break;

            try {
                List<String> tokens = CommandTokenizer.tokenize(line);
                Result<?> r = dispatcher.dispatch(tokens);

                if (r == null) continue;

                if (!r.message().isBlank()) {
                    if (r.ok()) {
                        System.out.println("[OK] " + r.message());
                    } else if (r.message().startsWith("NOT_SUPPORTED:")) {
                        System.out.println("[NOT SUPPORTED] " + r.message().substring("NOT_SUPPORTED:".length()).trim());
                    } else {
                        System.out.println("[ERROR] " + r.message());
                    }
                }

                if (r.data() != null) {
                    printData(r.data());
                }
            } catch (Exception ex) {
                // Last-resort safety net: never crash the REPL.
                System.out.println("[ERROR] Unexpected error: " + safeMsg(ex));
            }
        }
        System.out.println("Bye.");
    }

    private static String safeMsg(Throwable t) {
        String m = t.getMessage();
        return (m == null || m.isBlank()) ? t.getClass().getSimpleName() : m;
    }

    private static void printData(Object data) {
        if (data instanceof List<?> list) {
            if (list.isEmpty()) {
                System.out.println("(empty)");
                return;
            }
            for (Object o : list) System.out.println(o);
        } else {
            System.out.println(data);
        }
    }
}
