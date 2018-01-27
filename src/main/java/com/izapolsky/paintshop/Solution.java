package com.izapolsky.paintshop;

import java.util.BitSet;
import java.util.Optional;

class Solution {
    final BitSet seenCustomers = new BitSet();
    final int maxCost;
    final int currentCost;
    final Main.PaintType[] paints;

    Solution(BitSet toClone, int extraCustomer, int maxCost, int currentCost, Main.PaintType[] paints) {
        this.seenCustomers.or(toClone);
        this.seenCustomers.set(extraCustomer);
        this.maxCost = maxCost;
        this.currentCost = currentCost;
        this.paints = paints;
    }

    Optional<Solution> addPaint(int customer, Main.PaintType paintType) {

        if (sawCustomer(customer) || (currentCost == maxCost && paintType == Main.PaintType.MATTE)) {
            return Optional.empty();
        }

        //most inefficient part - in read-only paradigm we need to wrap previous values or copy whole array
        Main.PaintType[] paintTypes = new Main.PaintType[paints.length + 1];
        paintTypes[paints.length] = paintType;
        System.arraycopy(paints, 0, paintTypes, 0, paints.length);
        return Optional.of(new Solution(seenCustomers, customer, maxCost, currentCost + paintType.ordinal(), paintTypes));
    }

    boolean sawCustomer(int customer) {
        return seenCustomers.get(customer);
    }

    int customersSatisfied() {
        return seenCustomers.cardinality();
    }

    @Override
    public String toString() {
        //todo : fix to string to use stream / iterator. need join operation with space.
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < paints.length; i++) {
            b.append(paints[i].letter);
            if (i < paints.length - 1) {
                b.append(' ');
            }
        }

        return b.toString();
    }

}
