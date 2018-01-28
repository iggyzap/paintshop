package com.izapolsky.paintshop;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.util.*;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class MainTest {

    private PaintShop shop = new PaintShop();

    private PrintStream originalOut = System.out;

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
        if (originalOut != System.out) {
            System.setOut(originalOut);
        }
    }

    @Test
    public void testMain() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);
        Main.main(getClass().getResource("/sample_1.txt").getFile());
        ps.flush();
        LineNumberReader sr = new LineNumberReader(new InputStreamReader(new ByteArrayInputStream(baos.toByteArray())));

        assertEquals("Solution from out", "G G M G G", sr.readLine());
    }

    @Test
    public void testTaskParser() {
        List<TaskPreference> prefs = new ArrayList<>();
        Main.paintshopParser(() -> new StringReader("5\n 1 G\n2 G\n3 M\n4 G\n5 G")).forEach(prefs::add);

        assertEquals("Size", 6, prefs.size());

        assertEquals("Buckets", 5, ((TaskPreference.PaintShopPreference)prefs.get(0)).paintBuckets);
        assertEquals("Paint user 1", Collections.singletonList(new Paint(0, PaintType.GLOSS)), ((TaskPreference.UserPreference)prefs.get(1)).paints);
        assertEquals("Paint user 2", Collections.singletonList(new Paint(1, PaintType.GLOSS)), ((TaskPreference.UserPreference)prefs.get(2)).paints);
        assertEquals("Paint user 3", Collections.singletonList(new Paint(2, PaintType.MATTE)), ((TaskPreference.UserPreference)prefs.get(3)).paints);
        assertEquals("Paint user 4", Collections.singletonList(new Paint(3, PaintType.GLOSS)), ((TaskPreference.UserPreference)prefs.get(4)).paints);
        assertEquals("Paint user 5", Collections.singletonList(new Paint(4, PaintType.GLOSS)), ((TaskPreference.UserPreference)prefs.get(5)).paints);
    }

    @Test
    public void findSolution1Paint1UserGloss() {
        shop.accept(new TaskPreference.PaintShopPreference(1));
        shop.accept(new TaskPreference.UserPreference(Arrays.asList(new Paint(0, PaintType.GLOSS))));
        Optional<Solution> s = Main.findSolution(shop);

        assertTrue("Should have solution for 1 paint and 1 user", s.isPresent());
        assertEquals("1 gloss", "G", s.get().toString());
    }

    @Test
    public void findSolution1PaintNoUsersGloss() {
        shop.accept(new TaskPreference.PaintShopPreference(1));
        Optional<Solution> s = Main.findSolution(shop);

        assertTrue("Should have solution for 1 paint and 1 user", s.isPresent());
        assertEquals("1 gloss", "G", s.get().toString());
    }


    @Test
    public void test2PaintMatteGloss() {
        shop.accept(new TaskPreference.PaintShopPreference(2));
        shop.accept(new TaskPreference.UserPreference(Arrays.asList(new Paint(0, PaintType.MATTE))));
        shop.accept(new TaskPreference.UserPreference(Arrays.asList(new Paint(1, PaintType.GLOSS), new Paint(0, PaintType.GLOSS))));
        Optional<Solution> s = Main.findSolution(shop);

        assertTrue("Should have solution for 2 paints and 2 users", s.isPresent());
        assertEquals("1 matte", "M G", s.get().toString());

    }

    @Test
    public void test5PaintGlossGlossMatteMatte() {
        shop.accept(new TaskPreference.PaintShopPreference(5));
        shop.accept(new TaskPreference.UserPreference(Arrays.asList(new Paint(1, PaintType.GLOSS), new Paint(2, PaintType.GLOSS))));
        shop.accept(new TaskPreference.UserPreference(Arrays.asList(new Paint(1, PaintType.MATTE), new Paint(3, PaintType.MATTE))));
        Optional<Solution> s = Main.findSolution(shop);

        assertTrue("Should have solution for 2 paints and 2 users", s.isPresent());
        assertEquals("1 matte", "G G G M G", s.get().toString());

    }

    protected TaskPreference.UserPreference userPref(Paint... paints) {
        return new TaskPreference.UserPreference(Arrays.asList(paints));
    }

    @Test
    public void test5PaintUsers() {
        shop.accept(new TaskPreference.PaintShopPreference(5));
        shop.accept(userPref(new Paint(0, PaintType.MATTE), new Paint(2, PaintType.GLOSS), new Paint(4, PaintType.MATTE)));
        shop.accept(userPref(new Paint(1, PaintType.GLOSS), new Paint(2, PaintType.MATTE), new Paint(3, PaintType.GLOSS)));
        shop.accept(userPref(new Paint(4, PaintType.MATTE)));
        Optional<Solution> s = Main.findSolution(shop);

        assertTrue("Should have solution", s.isPresent());
        assertEquals("1 matte", "G G G G M", s.get().toString());

    }

    @Test
    public void test5PaintUsersRich() {
        shop.accept(new TaskPreference.PaintShopPreference(5));
        shop.accept(userPref(new Paint(1, PaintType.MATTE)));
        shop.accept(userPref(new Paint(4, PaintType.GLOSS)));
        shop.accept(userPref(new Paint(0, PaintType.GLOSS)));
        shop.accept(userPref(new Paint(4, PaintType.GLOSS), new Paint(0, PaintType.GLOSS), new Paint(3, PaintType.MATTE)));
        shop.accept(userPref(new Paint(2, PaintType.GLOSS)));
        shop.accept(userPref(new Paint(4, PaintType.GLOSS)));
        shop.accept(userPref(new Paint(2, PaintType.GLOSS), new Paint(4, PaintType.GLOSS), new Paint(0, PaintType.GLOSS)));
        shop.accept(userPref(new Paint(2, PaintType.GLOSS)));
        shop.accept(userPref(new Paint(1, PaintType.MATTE)));
        shop.accept(userPref(new Paint(4, PaintType.GLOSS), new Paint(0, PaintType.GLOSS)));
        shop.accept(userPref(new Paint(1, PaintType.MATTE)));
        shop.accept(userPref(new Paint(4, PaintType.GLOSS)));
        shop.accept(userPref(new Paint(3, PaintType.MATTE)));
        shop.accept(userPref(new Paint(4, PaintType.GLOSS), new Paint(3, PaintType.MATTE)));
        Optional<Solution> s = Main.findSolution(shop);

        assertTrue("Should have solution", s.isPresent());
        assertEquals("1 matte", "G M G M G", s.get().toString());

    }


    @Test
    public void findSolution1Paint2UsersNoSolution() {
        shop.accept(new TaskPreference.PaintShopPreference(1));
        shop.accept(new TaskPreference.UserPreference(Arrays.asList(new Paint(0, PaintType.GLOSS))));
        shop.accept(new TaskPreference.UserPreference(Arrays.asList(new Paint(0, PaintType.MATTE))));

        assertTrue("Should return no solution", !Main.findSolution(shop).isPresent());
    }

    @Test
    public void findSolution1Paint1UserMatte() {
        shop.accept(new TaskPreference.PaintShopPreference(1));
        shop.accept(new TaskPreference.UserPreference(Arrays.asList(new Paint(0, PaintType.MATTE))));
        Optional<Solution> s = Main.findSolution(shop);

        assertTrue("Should have solution for 1 paint and 1 user", s.isPresent());
        assertEquals("1 matte", "M", s.get().toString());
    }
}