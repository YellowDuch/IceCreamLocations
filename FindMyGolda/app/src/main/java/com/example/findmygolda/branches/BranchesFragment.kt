package com.example.findmygolda.branches

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.findmygolda.Constants.Companion.TOP_OF_RECYCLEVIEW
import com.example.findmygolda.R
import com.example.findmygolda.databinding.FragmentBranchesBinding

class BranchesFragment : Fragment() {
    private lateinit var branchViewModel : BranchesViewModel
    private  lateinit var adapter: BranchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentBranchesBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_branches, container, false)
        val viewModelFactory = BranchViewModelFactorty(requireNotNull(this.activity).application)
        branchViewModel =
            ViewModelProviders.of(
                this, viewModelFactory).get(BranchesViewModel::class.java)
        binding.lifecycleOwner = this
        adapter =BranchAdapter(BranchClickListener { branchPhoneNumber ->
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$branchPhoneNumber")
            startActivity(intent)
        })
        binding.branchesList.adapter = adapter
        binding.viewModel = branchViewModel
        branchViewModel.filteredBranches.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
                binding.branchesList.smoothScrollToPosition(TOP_OF_RECYCLEVIEW)
            }
        })

        return binding.root
    }

}
