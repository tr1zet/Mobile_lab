package com.example.topicsgrid;

import static org.junit.Assert.assertEquals;

import java.text.NumberFormat;
import org.junit.Test;

public class TipCalculatorTests {
    @Test
    public void calculateTip_20PercentNoRoundUp_returnsTwo() {
        String expected = NumberFormat.getCurrencyInstance().format(2);
        String actual = MainActivityKt.calculateTip(10.0, 20.0, false);

        assertEquals(expected, actual);
    }
}
