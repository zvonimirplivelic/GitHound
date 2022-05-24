package com.zvonimirplivelic.githound.ui.fragments

import android.os.Build.VERSION_CODES.S
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zvonimirplivelic.githound.R
import com.zvonimirplivelic.githound.model.QueryFilterParameter
import com.zvonimirplivelic.githound.util.Constants.SORT_FORKS
import com.zvonimirplivelic.githound.util.Constants.SORT_NONE
import com.zvonimirplivelic.githound.util.Constants.SORT_OPEN_ISSUES
import com.zvonimirplivelic.githound.util.Constants.SORT_WATCHERS

class BottomSheetFilterFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_filter_dialog, container, false)
        
        val rgSortBy: RadioGroup = view.findViewById(R.id.rg_sort_by)
        var sortBy: String = SORT_NONE

        val rgOrderBy: RadioGroup = view.findViewById(R.id.rg_order_by)
        var orderBy: String = SORT_NONE

        val etResultsPerPage: EditText = view.findViewById(R.id.et_results_per_page)
        val etPageNumber: EditText = view.findViewById(R.id.et_page_number)

        val btnCancel: Button = view.findViewById(R.id.btn_cancel)
        val btnApply: Button = view.findViewById(R.id.btn_apply_filters)

        rgSortBy.setOnCheckedChangeListener { _, checkedId ->
            sortBy = when(checkedId) {
                R.id.rb_forks -> SORT_FORKS
                R.id.rb_watchers -> SORT_WATCHERS
                R.id.rb_open_issues -> SORT_OPEN_ISSUES
                else -> SORT_NONE
            }
        }

        rgOrderBy.setOnCheckedChangeListener { _, checkedId ->
            orderBy = when(checkedId) {
                R.id.rb_ascending -> SORT_FORKS
                R.id.rb_descending -> SORT_WATCHERS
                else -> SORT_NONE
            }
        }


        btnCancel.setOnClickListener {
            this.dismiss()
        }

        btnApply.setOnClickListener {
            val action =
                BottomSheetFilterFragmentDirections.actionBottomSheetFilterFragmentToSearchListFragment(
                    QueryFilterParameter(
                        sortBy,
                        orderBy,
                        etResultsPerPage.text.toString().toInt(),
                        etPageNumber.text.toString().toInt()
                    )
                )
            findNavController().navigate(action)
        }

        return view
    }
}