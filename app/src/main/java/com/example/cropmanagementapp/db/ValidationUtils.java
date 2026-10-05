package com.example.cropmanagementapp.db;

/**
 * Small validation helpers shared across the "add"/"edit" forms so a
 * text field meant for a name/label can't be saved as pure digits.
 */
public class ValidationUtils {

    /** True if the text contains at least one letter (a-z, A-Z). */
    public static boolean containsLetter(String text) {
        if (text == null) return false;
        for (int i = 0; i < text.length(); i++) {
            if (Character.isLetter(text.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}