package com.izapolsky.paintshop;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Enum that defines possible paint types and how to obtain them from string representation
 */
public enum PaintType {

    GLOSS("G"),
    MATTE("M");

    final String letter;

    PaintType(String letter) {
        this.letter = letter;
    }

    private final static Map<String, PaintType> lookup;

    static {
        HashMap<String, PaintType> map = new HashMap<>();
        for (PaintType t : PaintType.values()) {
            map.put(t.letter, t);
        }
        lookup = Collections.unmodifiableMap(map);
    }

    public static Optional<PaintType> fromString(String c) {
        return lookup.get(c) == null ? Optional.empty() : Optional.of(lookup.get(c));
    }
}
