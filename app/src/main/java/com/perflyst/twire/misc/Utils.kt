package com.perflyst.twire.misc

import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.format.DateUtils
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import java.io.UnsupportedEncodingException
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.NumberFormat
import java.util.Locale

object Utils {

    /**
     * Calculates the contrast ratio between two colors using the WCAG formula.
     * A ratio of 3.0 or higher is considered readable for large text.
     * A ratio of 4.5 or higher is recommended for normal text.
     *
     * @param color1 First color
     * @param color2 Second color
     * @return Contrast ratio (1.0 to 21.0)
     */
    fun calculateContrast(@ColorInt color1: Int, @ColorInt color2: Int): Double {
        val lum1 = Color.luminance(color1).toDouble()
        val lum2 = Color.luminance(color2).toDouble()
        val lighter = maxOf(lum1, lum2)
        val darker = minOf(lum1, lum2)
        return (lighter + 0.05) / (darker + 0.05)
    }

    /**
     * Returns a color that has sufficient contrast against the given background.
     * If the foreground color already has enough contrast, it is returned unchanged.
     * Otherwise, black or white is returned depending on the background luminance.
     *
     * @param foreground The desired foreground color
     * @param background The background color to check against
     * @param minContrast Minimum contrast ratio required (default 3.0 for large text)
     * @return A color with sufficient contrast
     */
    fun ensureContrast(
        @ColorInt foreground: Int,
        @ColorInt background: Int,
        minContrast: Double = 3.0
    ): Int {
        val contrast = calculateContrast(foreground, background)
        if (contrast >= minContrast) {
            return foreground
        }
        // Use black or white depending on background luminance
        val backgroundLuminance = Color.luminance(background)
        return if (backgroundLuminance > 0.5f) Color.BLACK else Color.WHITE
    }
    val systemLanguage: String
        get() = Locale.getDefault().language

    fun appendSpan(
        builder: SpannableStringBuilder,
        charSequence: CharSequence?,
        vararg whats: Any?
    ): SpannableStringBuilder {
        val preLength = builder.length
        builder.append(charSequence)

        for (what in whats) {
            builder.setSpan(what, preLength, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return builder
    }

    /**
     * Sets the text of a [TextView] to a locale aware number.
     *
     * @param textView The [TextView] to set.
     * @param number   The number to set.
     */
    fun setNumber(textView: TextView, number: Long) {
        textView.text = NumberFormat.getIntegerInstance().format(number)
    }

    /**
     * Sets the text of a [TextView] to a locale aware percent.
     *
     * @param textView The [TextView] to set.
     * @param percent  The percent to set.
     */
    fun setPercent(textView: TextView, @FloatRange(from = 0.0, to = 1.0) percent: Double) {
        textView.text = NumberFormat.getPercentInstance().format(percent)
    }

    @JvmStatic
    fun safeEncode(s: String?): String? {
        return try {
            URLEncoder.encode(s, StandardCharsets.UTF_8.toString())
        } catch (_: UnsupportedEncodingException) {
            s
        }
    }

    fun safeUrl(url: String?): URL? {
        return try {
            URL(url)
        } catch (_: MalformedURLException) {
            null
        }
    }

    fun getPreviewUrl(url: String?, width: String, height: String): String? {
        if (url == null) return null
        return url.replace("%?\\{width\\}".toRegex(), width)
            .replace("%?\\{height\\}".toRegex(), height)
    }

    fun getPreviewUrl(url: String?): String? {
        return getPreviewUrl(url, "320", "180")
    }

    fun getOnlineSince(startedAt: Long): String {
        return DateUtils.formatElapsedTime((System.currentTimeMillis() - startedAt) / 1000)
    }
}
