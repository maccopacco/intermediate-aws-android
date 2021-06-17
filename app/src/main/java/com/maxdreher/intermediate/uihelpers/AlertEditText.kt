package com.maxdreher.intermediate.uihelpers

import android.view.Gravity
import android.view.inputmethod.EditorInfo
import com.maxdreher.Util.setMargin
import com.maxdreher.extensions.IContextBase
import com.maxdreher.intermediate.ExtensionFunctions.defaultMargin

class AlertEditText(
    cb: IContextBase, hint: String? = null,
    singleLine: Boolean = true
) :
    androidx.appcompat.widget.AppCompatEditText(cb.getContext()!!) {
    init {
        setMargin(false, false, cb.defaultMargin())
        gravity = Gravity.CENTER
        hint?.let {
            this.hint = it
        }
        if (singleLine) {
            maxLines = 1
            minLines = 1
            isSingleLine = true
        }
    }

    /**
     * See [EditorInfo.IME_ACTION_DONE]
     */
    fun onAction(id: Int, onEvent: OnEditorActionListener) {
        imeOptions = id
        setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener if (actionId == id)
                onEvent.onEditorAction(v, actionId, event)
            else false
        }
    }

    fun get(): String {
        return text.toString()
    }
}