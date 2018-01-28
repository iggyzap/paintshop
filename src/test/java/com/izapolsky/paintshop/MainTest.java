package com.izapolsky.paintshop;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;

public class MainTest {

    private PaintShop shop = new PaintShop();

    @Before
    public void setUp() {

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
//        shop.accept(new TaskPreference.UserPreference(Arrays.asList(new Paint(0, PaintType.GLOSS))));
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

    protected TaskPreference.UserPreference userPref (Paint ... paints) {
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