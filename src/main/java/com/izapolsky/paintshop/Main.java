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


        Optional<Solution> result = findSolution(shop);

        if (!result.isPresent()) {
            System.out.println("No Solution Exists");
        } else {
            System.out.println(result.get());
        }
    }

    protected static Optional<Solution> findSolution (PaintShop paintShop) {
        return StreamSupport.
                stream(paintShop.spliterator(), false).
                filter(s -> s.customersSatisfied() == paintShop.customersToSatisfy()).
                findFirst();
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

    /**
     * Problem definition:
     * You   want   to   mix   the   colors,   so   that:
     *   There   is   just   one   batch   for   each   color,   and   it's   either   gloss   or   matte.
     *   For   each   customer,   there   is   at   least   one   color   they   like.
     *   You   make   as   few   mattes   as   possible   (because   they   are   more   expensive).

     */
}
