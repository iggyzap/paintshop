package com.izapolsky.paintshop;

import java.util.Iterator;

/**
 * This class is required since we need to be able to recover execution context on next iteration.
 */
public class GenerationContext {
    private int nonEmptyIterators = 0;

    final Iterator[] iterators;
    final Solution[] solutionsRemembered;

    /**
     * Initialised given generation context with number of paint buckets available to shop
     * @param paints
     */
    public GenerationContext(int paints) {
        this.iterators = new Iterator[paints];
        this.solutionsRemembered = new Solution[paints];
    }

    /**
     * Changes iterator on given position to specified iterator, only if it's not empty.
     * @param position
     * @param it
     */
    public void setIterator(int position, Iterator it) {
        if (it.hasNext()) {
            //sanity check
            if (iterators[position] != null && iterators[position].hasNext()) {
                throw new IllegalArgumentException(String.format("Tried to replace iterator at position %1$s too early!", position));
            }
            iterators[position] = it;
            nonEmptyIterators = getNonEmptyIterators() + 1;
        }
    }

    /**
     * Removes given iterator from position only when such iterator was exhausted
     * @param position
     */
    public void clearIterator(int position) {
        if (iterators[position] != null && !iterators[position].hasNext()) {
            iterators[position] = null;
            nonEmptyIterators = getNonEmptyIterators() - 1;
        }
    }

    public int getNonEmptyIterators() {
        return nonEmptyIterators;
    }
}
