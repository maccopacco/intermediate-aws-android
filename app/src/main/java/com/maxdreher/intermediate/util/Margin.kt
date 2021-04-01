package com.maxdreher.intermediate.util

import android.content.Context
import com.maxdreher.Util
import com.maxdreher.intermediate.R

class Margin {
    companion object {
        fun get(context: Context?): Int {
            return Util.getDefaultMargin(context, R.dimen.default_margin)
        }
    }
}