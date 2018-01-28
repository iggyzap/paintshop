package com.izapolsky.paintshop;

import com.google.common.collect.Iterables;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Class that defines requirements for specific bucket. It also covers logic of multiple clients being satisfied by paint in
 * given bucket
 */
public class BucketRequirement implements Iterable<Pair<PaintType, Set<Integer>>> {

    private Set<Integer> glosses = new HashSet<>();
    private Set<Integer> mattes = new HashSet<>();

    /**
     * Adds a paint requirement for specific user to this bucket
     * @param t type of paint
     * @param customer customer number
     */
    public void add(PaintType t, int customer) {
        switch (t) {
            case GLOSS:
                glosses.add(customer);
                break;
            case MATTE:
                mattes.add(customer);
                break;
            default:
                throw new IllegalArgumentException("Not supported type " + t);
        }
    }

    @Override
    public Iterator<Pair<PaintType, Set<Integer>>> iterator() {
        //that thing has to make cartesian product of all possible combinations of one paint
        // we pay special attention to have glosses at first since the are cheaper
        return Iterables.concat(
                new SetMultiplier(glosses, PaintType.GLOSS, 0),
                //it does not make sense to get sets with empty mattes since they are not cheap
                new SetMultiplier(mattes, PaintType.MATTE, 1)).iterator();
    }
}
