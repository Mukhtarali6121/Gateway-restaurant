package com.example.gatewayrestaurant.Utils

import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned


class LanguageInputFilter :
    InputFilter {
    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        var keepOriginal = true
        val sb = StringBuilder(end - start)
        for (i in start until end) {
            val c = source[i]
            if (c.toString().matches((Regex("[\\u0000-\\u007F]"))))
                sb.append(c) else keepOriginal = false
        }
        return if (keepOriginal) null else {
            if (source is Spanned) {
                val sp = SpannableString(sb)
                sp
            } else {
                sb
            }
        }
    }
}
