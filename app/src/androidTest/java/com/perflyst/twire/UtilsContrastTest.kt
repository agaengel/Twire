package com.perflyst.twire

import android.graphics.Color
import com.perflyst.twire.misc.Utils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UtilsContrastTest {

    @Test
    fun calculateContrast_blackOnWhite_returnsMaxContrast() {
        val contrast = Utils.calculateContrast(Color.BLACK, Color.WHITE)
        // Black on white should have maximum contrast (21:1)
        assertTrue("Black on white should have high contrast", contrast > 20.0)
    }

    @Test
    fun calculateContrast_sameColor_returnsMinContrast() {
        val contrast = Utils.calculateContrast(Color.RED, Color.RED)
        // Same color should have contrast ratio of 1:1
        assertEquals(1.0, contrast, 0.01)
    }

    @Test
    fun calculateContrast_yellowOnYellow_returnsLowContrast() {
        val yellow1 = Color.YELLOW
        val yellow2 = Color.parseColor("#FFA500") // Orange/amber
        val contrast = Utils.calculateContrast(yellow1, yellow2)
        // Similar bright colors should have low contrast
        assertTrue("Similar colors should have low contrast", contrast < 3.0)
    }

    @Test
    fun ensureContrast_sufficientContrast_returnsSameColor() {
        val foreground = Color.BLACK
        val background = Color.WHITE
        val result = Utils.ensureContrast(foreground, background)
        assertEquals("Should return same color when contrast is sufficient", foreground, result)
    }

    @Test
    fun ensureContrast_insufficientContrast_onLightBackground_returnsBlack() {
        val foreground = Color.YELLOW
        val background = Color.parseColor("#FFB300") // Amber (light)
        val result = Utils.ensureContrast(foreground, background)
        assertEquals("Should return black on light background", Color.BLACK, result)
    }

    @Test
    fun ensureContrast_insufficientContrast_onDarkBackground_returnsWhite() {
        val foreground = Color.parseColor("#330000") // Very dark red
        val background = Color.parseColor("#880000") // Dark red
        val result = Utils.ensureContrast(foreground, background)
        assertEquals("Should return white on dark background", Color.WHITE, result)
    }

    @Test
    fun ensureContrast_customMinContrast_respected() {
        val foreground = Color.GRAY
        val background = Color.WHITE
        // Gray on white has ~4.5 contrast
        val resultWith3 = Utils.ensureContrast(foreground, background, 3.0)
        val resultWith7 = Utils.ensureContrast(foreground, background, 7.0)

        assertEquals("Should keep gray with minContrast 3.0", foreground, resultWith3)
        assertEquals("Should replace gray with minContrast 7.0", Color.BLACK, resultWith7)
    }
}