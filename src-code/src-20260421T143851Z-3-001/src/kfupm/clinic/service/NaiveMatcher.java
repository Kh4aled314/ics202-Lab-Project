package kfupm.clinic.service;

public class NaiveMatcher implements StringMatcher {
    @Override
    public boolean contains(String text, String pattern) {
        if (pattern == null) {
            return false;
        }

        if (pattern.length() == 0) {
            return true;
        }

        if (text == null) {
            return false;
        }

        text = text.toLowerCase();
        pattern = pattern.toLowerCase();

        if (pattern.length() > text.length()) {
            return false;
        }

        for (int i = 0; i <= text.length() - pattern.length(); i++) {
            int j = 0;

            while (j < pattern.length() &&
                    text.charAt(i + j) == pattern.charAt(j)) {
                j++;
            }

            if (j == pattern.length()) {
                return true;
            }
        }

        return false;
    }
}
