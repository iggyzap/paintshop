package com.izapolsky.paintshop;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

public class Main {


    public static void main(String... args) throws IOException {

        // this can be transformed into stream of requirements that will be transformed into one possible solution

        PaintShop shop = new PaintShop();
        Consumer<TaskPreference> taskConsumer = ((Consumer<TaskPreference>) taskPreference -> {
            System.out.println("Parsed line: ");
            System.out.println(taskPreference);

        }).andThen(shop);

        paintshopParser(() -> {
            try {
                return "-stdin".equals(args[0]) ? new InputStreamReader(System.in) : new FileReader(args[0]);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }).forEach(taskConsumer);


        Optional<Solution> result = StreamSupport.stream(shop.spliterator(), false).filter(s -> s.customersSatisfied() == shop.totalCustomers).findFirst();

        if (!result.isPresent()) {
            System.out.println("No Solution Exists");
        } else {
            System.out.println(result.get());
        }
    }

    enum PaintType {

        GLOSS("G"),
        MATTE("M");

        final String letter;

        PaintType(String letter) {
            this.letter = letter;
        }

        final static Map<String, PaintType> lookup;
        static {
            HashMap<String, PaintType> map = new HashMap<>();
            for (PaintType t: PaintType.values()) {
                map.put(t.letter, t);
            }
            lookup = Collections.unmodifiableMap(map);
        }

        public static Optional<PaintType> fromString(String c) {
            return lookup.get(c) == null ? Optional.empty() : Optional.of(lookup.get(c));
        }
    }

    static class PaintShop implements Consumer<TaskPreference>, Iterable<Solution> {
        int totalCustomers;
        BucketRequirement[] bucketRequirements;

        @Override
        public Iterator<Solution> iterator() {
            return Collections.emptyIterator();
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

    static class BucketRequirement {
        final Map<Integer, PaintType> requirements = new HashMap<>();
    }

    /**
     * This method provides a lazy stream of TaskPreferences coming from given input. At the moment it constructs preferences
     * eagerly, which limits how many preferences can be processed simultaneously. This parser does not have any validation for
     * incoming data.
     *
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
                        Optional<PaintType> p = PaintType.fromString(colors[i + 1]);

                        pref.paints.add(new Paint(colorNumber, p.orElseThrow(IllegalArgumentException::new)));
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
            List<Paint> paints = new ArrayList<>();

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
        PaintType type;

        private Paint(int number, PaintType type) {
            this.number = number;
            this.type = type;
        }

        @Override
        public String toString() {
            return "Paint{" +
                    "number=" + number +
                    ", type=" + type +
                    '}';
        }
    }

    /**
     * Problem definition:
     * You   want   to   mix   the   colors,   so   that:
     *   There   is   just   one   batch   for   each   color,   and   it's   either   gloss   or   matte.
     *   For   each   customer,   there   is   at   least   one   color   they   like.
     *   You   make   as   few   mattes   as   possible   (because   they   are   more   expensive).

     */
}
