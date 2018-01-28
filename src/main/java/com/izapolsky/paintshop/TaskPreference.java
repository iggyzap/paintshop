package com.izapolsky.paintshop;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface and classes associated with interface to provide preferences for paint shop
 */
public interface TaskPreference {

    //I'd use sealed types here to limit of number of potential types
    class PaintShopPreference implements TaskPreference {
        public final int paintBuckets;

        public PaintShopPreference(int paintBuckets) {
            this.paintBuckets = paintBuckets;
        }

        @Override
        public String toString() {
            return "PaintShopPreference{" +
                    "paintBuckets=" + paintBuckets +
                    '}';
        }
    }

    class UserPreference implements TaskPreference {
        public List<Paint> paints = new ArrayList<>();

        public UserPreference() {
        }

        public UserPreference(List<Paint> paints) {
            this.paints = paints;
        }

        @Override
        public String toString() {
            return "UserPreference{" +
                    "paints=" + paints +
                    '}';
        }
    }
}
