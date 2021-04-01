package com.maxdreher.intermediate.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.maxdreher.Util
import com.maxdreher.intermediate.R

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Util.buttonToAction(
            view,
            mapOf(
                R.id.go_to_plaid to R.id.action_homeFragment_to_plaidFragment,
                R.id.go_to_sql to R.id.action_homeFragment_to_SQLFragment
            )
        )
    }
}
