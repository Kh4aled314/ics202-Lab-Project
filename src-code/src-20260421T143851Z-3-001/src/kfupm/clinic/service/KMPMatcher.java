package kfupm.clinic.service;

public class KMPMatcher implements StringMatcher {
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

        int[] lps = buildLps(pattern);

        int i = 0; // index for text
        int j = 0; // index for pattern

        while (i < text.length()) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;

                if (j == pattern.length()) {
                    return true;
                }
            } else {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        return false;
    }

    private int[] buildLps(String pattern) {
        int[] lps = new int[pattern.length()];

        int length = 0;
        int i = 1;

        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(length)) {
                length++;
                lps[i] = length;
                i++;
            } else {
                if (length != 0) {
                    length = lps[length - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps;
    }
}
