package com.izapolsky.paintshop;


/**
 * Paint definition for parsing purposes
 */
public class Paint {
    public final int number;
    public final PaintType type;

    public Paint(int number, PaintType type) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Paint paint = (Paint) o;
        return number == paint.number &&
                type == paint.type;
    }

}
