package com.izapolsky.paintshop;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterables;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

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
        Queue<Integer> trail = new LinkedList<>();

    }

    private Context ctx;


    @Override
    public boolean tryAdvance(Consumer<? super Solution> action) {
        Solution sol = new Solution(ctx.currentPrice, ctx.currentPrice, new PaintType[0]);
        boolean hasMore = false;
        for (int cursor = 0; cursor < bucketRequirements.length; cursor++) {
            BucketRequirement currentReq = bucketRequirements[cursor];
            //iterable of current req should start with gloss
            for (Pair<OptionalInt, PaintType> p : currentReq) {
                Optional<Solution> os = sol.addPaint(p.getLeft(), p.getRight());
                if (!os.isPresent()) {
                    //for current iteration no solution exists
                    continue;
                }
                sol = os.get();
                //step deeper
                break;
            }

        }

        action.accept(sol);

        return hasMore;
    }

    @Override
    public Comparator<? super Solution> getComparator() {
        return Comparator.comparingInt(Solution::getCost);
    }

    static class BucketRequirement implements Iterable<Pair<OptionalInt, PaintType>>{
        final Map<Integer, PaintType> requirements = new HashMap<>();

        public Pair<OptionalInt, PaintType> getCustomerRequirement(int customer) {
            PaintType type = requirements.get(customer);
            return Pair.of(type == null ? OptionalInt.empty() : OptionalInt.of(customer), type);
        }

        @Override
        public Iterator<Pair<OptionalInt, PaintType>> iterator() {
            if (requirements.isEmpty()) {
                return Collections.singleton(Pair.of(OptionalInt.empty(), PaintType.GLOSS)).iterator();
            }
            return Iterables.transform(requirements.entrySet(), item -> Pair.of(OptionalInt.of(item.getKey()), item.getValue())).iterator();
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
        ctx = new Context();
        for (int i = 0; i < bucketRequirements.length; i++) {
            bucketRequirements[i] = new BucketRequirement();
        }
    }

    private void handleUserRequirementPref(TaskPreference.UserPreference pref) {
        pref.paints.forEach(p -> addRequirement(p, totalCustomers++));
    }

    private void addRequirement(Paint p, int customer) {
        bucketRequirements[p.number].requirements.put(customer, p.type);
    }

}
