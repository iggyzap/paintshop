package com.izapolsky.paintshop;

import com.google.common.collect.AbstractIterator;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Consumer;

/**
 * A paint shop class that know how to configure itself from requirements and then can be used to generate solutions
 * to paint problem
 */
public class PaintShop implements Consumer<TaskPreference>, Iterable<Solution> {
    static class BucketRequirement {
        final Map<Integer, PaintType> requirements = new HashMap<>();

        public Pair<OptionalInt, PaintType> getCustomerRequirement(int customer) {
            PaintType type = requirements.get(customer);
            return Pair.of(type == null ? OptionalInt.empty() : OptionalInt.of(customer), type);
        }
    }

    private int totalCustomers;
    private BucketRequirement[] bucketRequirements;

    @Override
    public Iterator<Solution> iterator() {
        List<Solution> s = new ArrayList<>();
        int currentPrice = 0;
        int[] trail = new int[bucketRequirements.length];
        int position = 0;
        Solution sol = new Solution(currentPrice, currentPrice, new PaintType[0]);
        for (int cursor = 0; cursor < trail.length; cursor++) {
            BucketRequirement currentReq = bucketRequirements[cursor];
            Pair<OptionalInt, PaintType> req = currentReq.getCustomerRequirement(trail[cursor]);
            Optional<Solution> os = sol.addPaint(req.getLeft(), req.getRight());
            if (!os.isPresent()) {
                //for current iteration no solution exists
                break;
            }
            sol = os.get();
        }

        return Collections.singletonList(sol).iterator();
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

    private void reInitialisePaintShop(TaskPreference.PaintShopPreference taskPreference) {
        bucketRequirements = new BucketRequirement[taskPreference.paintBuckets];
        for (int i =0; i< bucketRequirements.length; i++) {
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
