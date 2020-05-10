package com.example.findmygolda.alerts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.findmygolda.MainActivity
import com.example.findmygolda.R
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
        val alertViewModel =
            ViewModelProviders.of(
                this, viewModelFactory).get(AlertsViewModel::class.java)

        binding.lifecycleOwner = this
        binding.alertsViewModel = alertViewModel
        val adapter = AlertAdapter(AlertAdapter.ShareClickListener{ alert ->
            val shareIntent =  ShareIntent().getShareIntent(alert.description, alert.title)
            startActivity(shareIntent)
            },
            AlertAdapter.ReadClickListener{alert ->
                alertViewModel.updateAlert(alert)
            },
            AlertAdapter.DeleteAlertClickListener{alert ->
                alertViewModel.deleteAlert(alert)
            }
        )
        binding.alertsList.adapter = adapter
        alertViewModel.alerts.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.isThereNotifications = it.isNotEmpty()
                adapter.data = it
            }
        })

        return binding.root
    }
}
