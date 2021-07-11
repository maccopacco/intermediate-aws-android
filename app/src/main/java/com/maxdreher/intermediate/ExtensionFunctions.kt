package com.maxdreher.intermediate

import com.maxdreher.Util
import com.maxdreher.extensions.IContextBase
import java.text.SimpleDateFormat

/**
 * A file to store all extension functions
 */

object ExtensionFunctions {

    fun IContextBase.defaultMargin(): Int {
        return Util.getDefaultMargin(getContext(), R.dimen.default_margin)
    }

    object Date {
        private val timeFormat = SimpleDateFormat("h:mm a")
        private val viewFormat = SimpleDateFormat("MMMM d")

        fun java.util.Date.toView(): String {
            return viewFormat.format(this)
        }

        fun java.util.Date.toTime(): String {
            return timeFormat.format(this)
        }
    }
}