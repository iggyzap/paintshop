package com.izapolsky.paintshop;

import com.google.common.collect.Iterables;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Consumer;

/**
 * A paint shop class that know how to configure itself from requirements and then can be used to generate solutions
 * to paint problem
 */
public class PaintShop extends Spliterators.AbstractSpliterator<Solution> implements Consumer<TaskPreference> {

    public PaintShop() {
        super(Long.MAX_VALUE, Spliterator.SORTED);
    }




    static class Context {
        int currentPrice;
        int nonEmptyIterators = 0;

        final Iterator[] iterators;

        public void setIterator(int position, Iterator it) {
            if (it.hasNext()) {
                if (iterators[position] != null && iterators[position].hasNext()) {
                    throw new IllegalArgumentException(String.format("Tried to replace iterator at position %1$s too early!", position));
                }
                iterators[position] = it;
                nonEmptyIterators++;
            }
        }

        public void clearIterator(int position) {
            if (iterators[position] != null && !iterators[position].hasNext()) {
                iterators[position] = null;
                nonEmptyIterators--;
            }
        }

        Context(int paints) {
            this.iterators = new Iterator[paints];
        }
    }

    private Context ctx;

    @Override
    public boolean tryAdvance(Consumer<? super Solution> action) {
        Solution sol = new Solution(ctx.currentPrice, ctx.currentPrice, new PaintType[0]);
        for (int cursor = 0; cursor < bucketRequirements.length; cursor++) {
            BucketRequirement currentReq = bucketRequirements[cursor];
            //iterable of current req should start with gloss

            Iterator<Pair<PaintType, OptionalInt>> iterator = currentReq.iterator();
            Iterator<Pair<PaintType, OptionalInt>> fromContext = ctx.iterators[cursor];

            //restore iteration
            if (fromContext != null && fromContext.hasNext()) {
                iterator = fromContext;
            } else {
                ctx.setIterator(cursor, iterator);
            }
            while (iterator.hasNext()) {
                Pair<PaintType, OptionalInt> p = iterator.next();
                Optional<Solution> os = sol.addPaint(p.getRight(), p.getLeft());
                if (!os.isPresent()) {
                    //for current iteration no solution exists
                    continue;
                }
                sol = os.get();
                //step deeper
                // need to add trail only if requirement is not empty
                break;
            }
            ctx.clearIterator(cursor);

        }

        action.accept(sol);

        return ctx.nonEmptyIterators > 0;
    }

    @Override
    public Comparator<? super Solution> getComparator() {
        return Comparator.comparingInt(Solution::getCost);
    }

    static class BucketRequirement implements Iterable<Pair<PaintType, OptionalInt>> {
        final SortedSet<Pair<PaintType, Integer>> requirements = new TreeSet<>();

        @Override
        public Iterator<Pair<PaintType, OptionalInt>> iterator() {
            if (requirements.isEmpty()) {
                return Collections.singleton(Pair.of(PaintType.GLOSS, OptionalInt.empty())).iterator();
            }
            //this order guarantees preference of glosses !
            return Iterables.transform(requirements,
                    p -> Pair.of(p.getLeft(), p.getRight() == null ? OptionalInt.empty() : OptionalInt.of(p.getRight()))).
                    iterator();
        }
    }

    private int totalCustomers;
    private BucketRequirement[] bucketRequirements;

    /**
     * Paint shop knows how many customers it have to satisfy
     *
     * @return
     */
    public int customersToSatisfy() {
        return totalCustomers;
    }

    @Override
    public void accept(TaskPreference taskPreference) {
        if (taskPreference instanceof TaskPreference.PaintShopPreference) {
            reInitialisePaintShop((TaskPreference.PaintShopPreference) taskPreference);
        }
        if (taskPreference instanceof TaskPreference.UserPreference) {
            handleUserRequirementPref((TaskPreference.UserPreference) taskPreference);
        }
    }

    private void reInitialisePaintShop(TaskPreference.PaintShopPreference taskPreference) {
        bucketRequirements = new BucketRequirement[taskPreference.paintBuckets];
        //initialise context
        ctx = new Context(taskPreference.paintBuckets);
        for (int i = 0; i < bucketRequirements.length; i++) {
            bucketRequirements[i] = new BucketRequirement();
        }
    }

    private void handleUserRequirementPref(TaskPreference.UserPreference pref) {
        int currentUser = totalCustomers++;
        pref.paints.forEach(p -> addRequirement(p, currentUser));
    }

    private void addRequirement(Paint p, int customer) {
        bucketRequirements[p.number].requirements.add(Pair.of(p.type, customer));
    }

}
