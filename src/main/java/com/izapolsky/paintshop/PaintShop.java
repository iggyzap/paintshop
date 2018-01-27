package com.izapolsky.paintshop;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A paint shop class that know how to configure itself from requirements and then can be used to generate solutions
 * to paint problem
 */
public class PaintShop implements Consumer<TaskPreference>, Iterable<Solution> {
    static class BucketRequirement {
        final Map<Integer, PaintType> requirements = new HashMap<>();
    }

    private int totalCustomers;
    private BucketRequirement[] bucketRequirements;

    @Override
    public Iterator<Solution> iterator() {
        return Collections.emptyIterator();
    }

    /**
     * Paint shop knows how many customers it have to satisfy
     * @return
     */
    public int customersToSatisfy() {
        return totalCustomers;
    }

    @Override
    public void accept(TaskPreference taskPreference) {
        if (taskPreference instanceof TaskPreference.PaintShopPreference) {
            bucketRequirements = new BucketRequirement[((TaskPreference.PaintShopPreference) taskPreference).paintBuckets];
        }
        if (taskPreference instanceof TaskPreference.UserPreference) {
            handleUserRequirementPref((TaskPreference.UserPreference) taskPreference);
        }
    }

    private void handleUserRequirementPref(TaskPreference.UserPreference pref) {
        totalCustomers++;
        pref.paints.forEach(p -> addRequirement(p, totalCustomers));
    }

    private void addRequirement(Paint p, int customer) {
        if (bucketRequirements[p.number] == null) {
            bucketRequirements[p.number] = new BucketRequirement();
        }

        bucketRequirements[p.number].requirements.put(customer, p.type);
    }

}
