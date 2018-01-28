package com.izapolsky.paintshop;

import java.util.BitSet;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

/**
 * A solution is an object that provides logic to record currently selected paints with current prices.
 * Also adds helper methods to add new paint only if we have not exceeded price for current round
 */
public class Solution {
    private final BitSet seenCustomers;
    private final PaintType[] paints;

    public Solution(PaintType[] paints) {
        this.seenCustomers = new BitSet();
        this.paints = paints;
    }

    public Solution(BitSet passedIn, PaintType[] paints) {
        this.seenCustomers = passedIn;
        this.paints = paints;
    }

    /**
     * This method generates new solution by adding next customer with given paint type.
     *
     * @param customers a set of customers that satisfy given paint bucket. can be empty
     * @param paintType gloss or matte
     * @return resulting solution
     */
    public Solution addPaint(Set<Integer> customers, PaintType paintType) {
        //most inefficient part - in read-only paradigm we need to wrap previous values or copy whole array
        PaintType[] paintTypes = new PaintType[paints.length + 1];
        paintTypes[paints.length] = paintType;
        System.arraycopy(paints, 0, paintTypes, 0, paints.length);
        BitSet toPass = new BitSet();
        toPass.or(seenCustomers);
        //because OptionalInt does not implement Comparable!

        for (Integer customer : customers) {
            toPass.set(customer);
        }

        return new Solution(toPass, paintTypes);
    }

    /**
     * Returns how many customers this solution satisfy
     *
     * @return
     */
    public int customersSatisfied() {
        return seenCustomers.cardinality();
    }

    @Override
    public String toString() {
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
