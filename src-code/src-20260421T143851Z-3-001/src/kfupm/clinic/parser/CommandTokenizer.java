package kfupm.clinic.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Tokenizes a command line.
 * - Splits by whitespace
 * - Supports quoted strings: "Dr. Sara" or "note with spaces"
 * - Supports escaping inside quotes: \" 
 */
public final class CommandTokenizer {
    private CommandTokenizer() {}

    public static List<String> tokenize(String line) {
        ArrayList<String> out = new ArrayList<>();
        if (line == null) return out;

        int n = line.length();
        int i = 0;
        while (i < n) {
            while (i < n && Character.isWhitespace(line.charAt(i))) i++;
            if (i >= n) break;

            char c = line.charAt(i);
            if (c == '"') {
                i++;
                StringBuilder sb = new StringBuilder();
                while (i < n) {
                    char ch = line.charAt(i);
                    if (ch == '\\' && i + 1 < n) {
                        char next = line.charAt(i + 1);
                        if (next == '"' || next == '\\') {
                            sb.append(next);
                            i += 2;
                            continue;
                        }
                    }
                    if (ch == '"') {
                        i++;
                        break;
                    }
                    sb.append(ch);
                    i++;
                }
                out.add(sb.toString());
            } else {
                int j = i;
                while (j < n && !Character.isWhitespace(line.charAt(j))) j++;
                out.add(line.substring(i, j));
                i = j;
            }
        }
        return out;
    }
}
