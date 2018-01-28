package com.izapolsky.paintshop;

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
    private int totalCustomers;
    private BucketRequirement[] bucketRequirements;
    private Context ctx;


    /**
     * This class is required since we need to be able to recover execution context on next iteration.
     */
    static class Context {
        int nonEmptyIterators = 0;

        final Iterator[] iterators;
        final Solution[] solutionsRemembered;

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
            this.solutionsRemembered = new Solution[paints];
        }
    }

    /**
     * This method generates a singular solution for given paint problem using depth-first search.
     * It does not guarantee that solution will be satisfactory. It's extremely strange how
     * we adapt generator ( yield statement) and tail-recursion into normal cycle logic
     */
    @Override
    public boolean tryAdvance(Consumer<? super Solution> action) {
        Solution sol = new Solution(0, new PaintType[0]);
        int cursor = 0;
        if (ctx.nonEmptyIterators > 0) {
            //have to find last context & non-empty iterator. that will give start for cycle below
            for (cursor = bucketRequirements.length - 1; cursor >= 0; cursor--) {
                if (ctx.iterators[cursor] != null) {
                    sol = ctx.solutionsRemembered[cursor];
                    break;
                }
            }
        }
        for (; cursor < bucketRequirements.length; cursor++) {
            BucketRequirement currentReq = bucketRequirements[cursor];
            //iterable of current req should start with gloss

            Iterator<Pair<PaintType, Set<Integer>>> iterator = currentReq.iterator();
            Iterator<Pair<PaintType, Set<Integer>>> fromContext = ctx.iterators[cursor];

            //restore iteration
            if (fromContext != null && fromContext.hasNext()) {
                iterator = fromContext;
            } else {
                ctx.setIterator(cursor, iterator);
            }
            //take next available and descend
            while (iterator.hasNext()) {
                Pair<PaintType, Set<Integer>> p = iterator.next();
                ctx.solutionsRemembered[cursor] = sol;
                sol = sol.addPaint(p.getRight(), p.getLeft());
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

    /**
     * When we receive message about number of paint buckets this resets all bucket requirements for a batch
     * @param taskPreference
     */
    private void reInitialisePaintShop(TaskPreference.PaintShopPreference taskPreference) {
        bucketRequirements = new BucketRequirement[taskPreference.paintBuckets];
        //initialise context
        ctx = new Context(taskPreference.paintBuckets);
        for (int i = 0; i < bucketRequirements.length; i++) {
            bucketRequirements[i] = new BucketRequirement();
        }
    }

    /**
     * We record user's paint preferences here
     * @param pref
     */
    private void handleUserRequirementPref(TaskPreference.UserPreference pref) {
        int currentUser = totalCustomers++;
        pref.paints.forEach(p -> addRequirement(p, currentUser));
    }

    private void addRequirement(Paint p, int customer) {
        bucketRequirements[p.number].add(p.type, customer);
    }

}
