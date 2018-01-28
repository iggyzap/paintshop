package com.izapolsky.paintshop;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Iterator;
import java.util.Set;

/**
 * This set multiplication generator is required since we need to cover all possible unique permutations of specific
 * paint bucket satisfying given customer.
 */
class SetMultiplier implements Iterable<Pair<PaintType, Set<Integer>>> {
    private final Set<Integer> glosses;
    private final PaintType type;
    private final int startPosition;

    SetMultiplier(Set<Integer> glosses, PaintType type, int startPosition) {
        this.glosses = glosses;
        this.type = type;
        this.startPosition = startPosition;
    }

    @Override
    public Iterator<Pair<PaintType, Set<Integer>>> iterator() {
        return new AbstractIterator<Pair<PaintType, Set<Integer>>>() {
            Iterator<Set<Integer>> currentIterator;
            int currentCardinality = startPosition;

            @Override
            protected Pair<PaintType, Set<Integer>> computeNext() {
                do {
                    if (currentIterator == null && currentCardinality <= glosses.size()) {
                        Set<Set<Integer>> set = Sets.combinations(glosses, currentCardinality++);
                        currentIterator = set.iterator();
                    }
                    if ((currentIterator == null || !currentIterator.hasNext()) && currentCardinality > glosses.size()) {
                        return endOfData();
                    }
                    if (!currentIterator.hasNext()) {
                        currentIterator = null;
                    }
                } while (currentIterator == null);

                return Pair.of(type, currentIterator.next());
            }
        };
    }
}
