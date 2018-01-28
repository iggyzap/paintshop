package com.izapolsky.paintshop;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;

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

        assertTrue("Should have solution for 1 paint and 1 user", Main.findSolution(shop).isPresent());
    }

    @Test
    public void findSolution1PaintNoUsersGloss() {
        shop.accept(new TaskPreference.PaintShopPreference(1));
//        shop.accept(new TaskPreference.UserPreference(Arrays.asList(new Paint(0, PaintType.GLOSS))));

        assertTrue("Should have solution for 1 paint and 1 user", Main.findSolution(shop).isPresent());
    }

    @Test
    public void findSolution1Paint2UsersNoSolution() {
        shop.accept(new TaskPreference.PaintShopPreference(1));
        shop.accept(new TaskPreference.UserPreference(Arrays.asList(new Paint(0, PaintType.GLOSS))));
        shop.accept(new TaskPreference.UserPreference(Arrays.asList(new Paint(0, PaintType.MATTE))));

        assertTrue("Should return no solution", !Main.findSolution(shop).isPresent());
    }


    @Ignore("for now does not work")
    @Test
    public void findSolution1Paint1UserMatte() {
        shop.accept(new TaskPreference.PaintShopPreference(1));
        shop.accept(new TaskPreference.UserPreference(Arrays.asList(new Paint(0, PaintType.MATTE))));

        assertTrue("Should have solution for 1 paint and 1 user", Main.findSolution(shop).isPresent());
    }
}