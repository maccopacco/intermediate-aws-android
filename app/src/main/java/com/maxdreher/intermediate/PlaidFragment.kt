package com.maxdreher.intermediate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class PlaidFragment : Fragment(R.layout.fragment_plaid) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            val result = findViewById<TextView>(R.id.plaid_result)
            findViewById<Button>(R.id.back).setOnClickListener {
                findNavController().navigate(R.id.action_plaidFragment_to_homeFragment)
            }
            findViewById<Button>(R.id.trigger_plaid).setOnClickListener {
                result.text = "Kinda random: ${"%3.2f".format(Math.random())}"
                Toast.makeText(activity, "Toast!", Toast.LENGTH_LONG).show()
            }
        }
    }
}