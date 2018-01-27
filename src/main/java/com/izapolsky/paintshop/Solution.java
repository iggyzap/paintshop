package com.izapolsky.paintshop;

import java.util.BitSet;
import java.util.Optional;

/**
 * A solution is an object that provides logic to record currently selected paints with current prices.
 * Also adds helper methods to add new paint only if we have not exceeded price for current round
 */
public class Solution {
    private final BitSet seenCustomers = new BitSet();
    private final int maxCost;
    private final int currentCost;
    private final PaintType[] paints;

    public Solution(BitSet toClone, int extraCustomer, int maxCost, int currentCost, PaintType[] paints) {
        this.seenCustomers.or(toClone);
        this.seenCustomers.set(extraCustomer);
        this.maxCost = maxCost;
        this.currentCost = currentCost;
        this.paints = paints;
    }

    /**
     * This method generates new optional solution by adding next customer with given paint type.
     * @param customer customer number, will be used to restrict solution generation if given customer already present
     * @param paintType gloss or matte, will be used to restrict solution generation if already at max price
     * @return optional solution containing given customer's paint type
     */
    public Optional<Solution> addPaint(int customer, PaintType paintType) {

        if (sawCustomer(customer) || (currentCost == maxCost && paintType == PaintType.MATTE)) {
            return Optional.empty();
        }

        //most inefficient part - in read-only paradigm we need to wrap previous values or copy whole array
        PaintType[] paintTypes = new PaintType[paints.length + 1];
        paintTypes[paints.length] = paintType;
        System.arraycopy(paints, 0, paintTypes, 0, paints.length);
        return Optional.of(new Solution(seenCustomers, customer, maxCost, currentCost + paintType.ordinal(), paintTypes));
    }

    public boolean sawCustomer(int customer) {
        return seenCustomers.get(customer);
    }

    public int customersSatisfied() {
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
