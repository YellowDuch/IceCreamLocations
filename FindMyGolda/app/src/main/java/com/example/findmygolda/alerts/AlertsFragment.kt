package com.example.findmygolda.alerts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.findmygolda.R
import com.example.findmygolda.databinding.FragmentAlertsBinding

class AlertsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentAlertsBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_alerts, container, false)
        val alertViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireNotNull(this.activity).application).create(AlertsViewModel::class.java)
        binding.lifecycleOwner = this
        binding.alertsViewModel = alertViewModel

        val adapter = AlertAdapter(ShareClickListener{ alert ->
            val shareIntent =  ShareIntent.getShareIntent(alert.description, alert.title)
            startActivity(shareIntent)
            },
            ReadClickListener{alert ->
                alertViewModel.changeIsReadStatus(alert)
            },
            DeleteAlertClickListener{alert ->
                alertViewModel.deleteAlert(alert)
            }
        )
        binding.alertsList.adapter = adapter
        alertViewModel.alerts.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.isContentEmpty = it.isEmpty()
                adapter.submitList(it)
            }
        })

        return binding.root
    }
}
