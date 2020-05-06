package com.example.findmygolda.alerts

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.findmygolda.MainActivity
import com.example.findmygolda.R
import com.example.findmygolda.database.AlertDatabase
import com.example.findmygolda.databinding.FragmentAlertsBinding

class AlertsFragment : Fragment() {
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentAlertsBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_alerts, container, false)
        mainActivity = activity as MainActivity
        val viewModelFactory = AlertViewModelFactory(mainActivity.alerManager)
        val alertsTrackerViewModel =
            ViewModelProviders.of(
                this, viewModelFactory).get(AlertsViewModel::class.java)

        binding.setLifecycleOwner(this)
        binding.alertsViewModel = alertsTrackerViewModel
        val adapter = AlertAdapter(AlertAdapter.AlertClickListener{alert ->
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT,  alert.description + " at " + alert.title)
                putExtra(Intent.EXTRA_TITLE, alert.title)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        })

        binding.alertsList.adapter = adapter

        alertsTrackerViewModel.alerts.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.isThereNotifications = it.isNotEmpty()
                adapter.data = it
            }
        })

        return binding.root

    }

}
