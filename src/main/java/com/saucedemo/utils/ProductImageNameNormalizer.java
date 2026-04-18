package com.saucedemo.utils;

import java.util.regex.Pattern;

public final class ProductImageNameNormalizer {

    private static final String STATIC_MEDIA_PREFIX = "/static/media/";

    private static final Pattern HASH_BEFORE_EXT = Pattern.compile(
            "(?i)^(.+?)\\.([a-f0-9]{8,})\\.(jpg|jpeg|png|webp)$");

    private ProductImageNameNormalizer() {
    }

    public static String normalize(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        String s = raw.trim();
        s = s.replaceFirst("(?i)^src\\s*=\\s*[\"']?", "");
        s = s.replaceFirst("[\"']\\s*$", "");
        s = s.trim();

        int media = s.indexOf(STATIC_MEDIA_PREFIX);
        if (media >= 0) {
            s = s.substring(media + STATIC_MEDIA_PREFIX.length());
        }
        int slash = s.lastIndexOf('/');
        if (slash >= 0) {
            s = s.substring(slash + 1);
        }
        int q = s.indexOf('?');
        if (q >= 0) {
            s = s.substring(0, q);
        }

        var m = HASH_BEFORE_EXT.matcher(s);
        if (m.matches()) {
            s = m.group(1) + "." + m.group(3).toLowerCase();
        }
        return s;
    }
}
