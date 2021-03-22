package com.maxdreher.intermediate

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.maxdreher.Util

class SQLFragment : Fragment(R.layout.fragment_sql) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Util.buttonToAction(view, mapOf(R.id.sql_back to R.id.action_SQLFragment_to_homeFragment))
    }
}