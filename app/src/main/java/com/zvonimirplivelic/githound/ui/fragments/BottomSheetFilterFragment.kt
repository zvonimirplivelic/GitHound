package com.zvonimirplivelic.githound.ui.fragments

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

class BottomSheetFilterFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_filter_dialog, container, false)
        val rgSortBy: RadioGroup = view.findViewById(R.id.rg_sort_by)
        val rgOrderBy: RadioGroup = view.findViewById(R.id.rg_order_by)
        val rbSelectedSort: RadioButton =
            view.findViewById(rgSortBy.checkedRadioButtonId)
        val rbSelectedOrder: RadioButton =
            view.findViewById(rgOrderBy.checkedRadioButtonId)
        val etResultsPerPage: EditText = view.findViewById(R.id.et_results_per_page)
        val etPageNumber: EditText = view.findViewById(R.id.et_page_number)

        val btnCancel: Button = view.findViewById(R.id.btn_cancel)
        val btnApply: Button = view.findViewById(R.id.btn_apply_filters)



        btnCancel.setOnClickListener {
            this.dismiss()
        }

        btnApply.setOnClickListener {
            val action =
                BottomSheetFilterFragmentDirections.actionBottomSheetFilterFragmentToSearchListFragment(
                    QueryFilterParameter(
                        rbSelectedSort.text.toString(),
                        rbSelectedOrder.text.toString(),
                        etResultsPerPage.text.toString().toInt(),
                        etPageNumber.text.toString().toInt()
                    )
                )
            findNavController().navigate(action)
        }

        return view
    }
}