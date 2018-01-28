package com.izapolsky.paintshop;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

public class Main {


    public static void main(String... args) {

        PaintShop shop = new PaintShop();
        Consumer<TaskPreference> taskConsumer = ((Consumer<TaskPreference>) taskPreference -> {
            if (args.length > 1 && "-v".equals(args[1])) {
                System.out.println("Parsed line: ");
                System.out.println(taskPreference);
            }
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

    /**
     * This method find a first correct solution for given configured paint shop.
     *
     * @param paintShop
     * @return
     */
    protected static Optional<Solution> findSolution(PaintShop paintShop) {
        return StreamSupport.
                stream(paintShop, false).
                filter(s -> s.customersSatisfied() == paintShop.customersToSatisfy()).
                findFirst();
    }

    /**
     * This method provides an iterable of TaskPreferences coming from given input.
     * This parser does assume that incoming data correct.
     *
     * @param inputSupplier supplier that can provide a reader of task data
     * @return an iterable that can be consumed
     */
    protected static Iterable<? extends TaskPreference> paintshopParser(Supplier<Reader> inputSupplier) {
        //there is no point in implementing this as completely lazy solution since we still have to gather all
        // user's requirements to begin finding a solution
        return (Iterable<TaskPreference>) () -> {
            List<TaskPreference> list = new ArrayList<>();
            try (LineNumberReader lnr = new LineNumberReader(inputSupplier.get())) {
                int paintsNumber = Integer.parseInt(lnr.readLine());
                list.add(new TaskPreference.PaintShopPreference(paintsNumber));

                for (String paintsLine = lnr.readLine(); paintsLine != null; paintsLine = lnr.readLine()) {
                    String[] colors = StringUtils.trim(paintsLine).split(" ");
                    TaskPreference.UserPreference pref = new TaskPreference.UserPreference();
                    list.add(pref);
                    for (int i = 0; i < colors.length; i += 2) {
                        int colorNumber = Integer.parseInt(colors[i]) - 1;
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

}
