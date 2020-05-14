package com.example.findmygolda.branches

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.findmygolda.Constants.Companion.FIRST_CHIP_INDEX
import com.example.findmygolda.MainActivity
import com.example.findmygolda.R
import com.example.findmygolda.databinding.FragmentBranchesBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class BranchesFragment : Fragment() {
    private lateinit var branchViewModel : BranchesViewModel
    private  lateinit var adapter: BranchAdapter
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentBranchesBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_branches, container, false)
        mainActivity = activity as MainActivity
        val viewModelFactory = BranchViewModelFactorty(mainActivity.branchManager, mainActivity.locationAdapter)
        branchViewModel =
            ViewModelProviders.of(
                this, viewModelFactory).get(BranchesViewModel::class.java)
        binding.lifecycleOwner = this
        adapter =BranchAdapter(BranchAdapter.BranchClickListener { branchId ->
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:<$branchId>")
            startActivity(intent)
        })
        binding.branchesList.adapter = adapter
        val chipGroup = binding.chipGroup
        setListenerOnChips(chipGroup)
        branchViewModel.filteredBranches.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.data = it
            }
        })

        return binding.root
    }

    private fun setListenerOnChips(chipGroup: ChipGroup) {
        for (index in FIRST_CHIP_INDEX until chipGroup.childCount) {
            val chip: Chip = chipGroup.getChildAt(index) as Chip
            chip.setOnCheckedChangeListener { view, isChecked ->
                if (isChecked) {
                    branchViewModel.chipPicked(view.text.toString())
                }
            }
        }
    }

}
