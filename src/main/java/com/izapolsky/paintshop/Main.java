package com.izapolsky.paintshop;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public class Main {


    public static void main(String... args) throws IOException {

        // this can be transformed into stream of requirements that will be transformed into one possible solution

        paintshopParser(() -> {
            try {
                return "-stdin".equals(args[0]) ? new InputStreamReader(System.in) :  new FileReader(args[0]);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }).forEach(pref -> {
            System.out.println("Parsed line: ");
            System.out.println(pref);
        });

    }

    /**
     * This method provides a lazy stream of TaskPreferences coming from given input. At the moment it constructs preferences
     * eagerly, which limits how many preferences can be processed simultaneously. This parser does not have any validation for
     * incoming data.
     * @param inputSupplier supplier that can provide a reader of task data
     * @return
     */
    static Iterable<? extends TaskPreference> paintshopParser(Supplier<Reader> inputSupplier) {

        //first implementation is eager. I will be able to add laziness at later stage
        return (Iterable<TaskPreference>) () -> {
            List<TaskPreference> list = new ArrayList<>();
            try (LineNumberReader lnr = new LineNumberReader(inputSupplier.get())) {
                int paintsNumber = Integer.parseInt(lnr.readLine());
                list.add(new TaskPreference.PaintShopPreference(paintsNumber));

                for (String paintsLine = lnr.readLine(); paintsLine != null; paintsLine = lnr.readLine()) {
                    String[] colors = paintsLine.split(" ");
                    TaskPreference.UserPreference pref = new TaskPreference.UserPreference();
                    list.add(pref);
                    for (int i = 0; i < colors.length; i += 2) {
                        int colorNumber = Integer.parseInt(colors[i]);
                        boolean isMatte = "M".equals(colors[i + 1]);
                        pref.paints.add(new Paint(colorNumber, isMatte));
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return list.iterator();
        };
    }

    private static interface TaskPreference {

        //I'd use sealed interface here to limit of number of potential types
        class PaintShopPreference implements TaskPreference {
            final int paintBuckets;

            private PaintShopPreference(int paintBuckets) {
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
            List<Paint> paints = new ArrayList<Paint>();

            @Override
            public String toString() {
                return "UserPreference{" +
                        "paints=" + paints +
                        '}';
            }
        }
    }


    private static class Paint {
        final int number;
        final boolean matte;

        private Paint(int number, boolean matte) {
            this.number = number;
            this.matte = matte;
        }

        @Override
        public String toString() {
            return "Paint{" +
                    "number=" + number +
                    ", matte=" + matte +
                    '}';
        }
    }

    private static class PaintPref {
        int[] glosses;
        int[] mattes;
    }

    /**
     * Problem definition:
     * You   want   to   mix   the   colors,   so   that:
     *   There   is   just   one   batch   for   each   color,   and   it's   either   gloss   or   matte.
     *   For   each   customer,   there   is   at   least   one   color   they   like.
     *   You   make   as   few   mattes   as   possible   (because   they   are   more   expensive).

     */
}
