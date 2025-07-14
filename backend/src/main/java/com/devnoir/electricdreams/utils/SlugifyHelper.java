package com.devnoir.electricdreams.utils;

import java.text.Normalizer;

public class SlugifyHelper {

    public static String toSlug(String input) {
        if (input == null) return "";

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String slug = normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        slug = slug.toLowerCase()
                   .replaceAll("[^a-z0-9\\s-]", "")
                   .replaceAll("\\s+", "-")
                   .replaceAll("-{2,}", "-")
                   .replaceAll("^-|-$", "");

        return slug.length() > 70 ? slug.substring(0, 70) : slug;
    }
}
