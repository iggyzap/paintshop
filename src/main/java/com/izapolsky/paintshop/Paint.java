package com.izapolsky.paintshop;

/**
 * Paint definition for parsing purposes
 */
public class Paint {
    final int number;
    PaintType type;

    Paint(int number, PaintType type) {
        this.number = number;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Paint{" +
                "number=" + number +
                ", type=" + type +
                '}';
    }
}
