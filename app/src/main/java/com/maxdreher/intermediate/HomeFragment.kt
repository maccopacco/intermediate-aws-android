package com.maxdreher.intermediate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import androidx.navigation.fragment.findNavController

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            findViewById<Button>(R.id.go_to_nav).setOnClickListener {
                findNavController().navigate(
                    R.id.action_homeFragment_to_plaidFragment
                )
            }
        }
    }
}