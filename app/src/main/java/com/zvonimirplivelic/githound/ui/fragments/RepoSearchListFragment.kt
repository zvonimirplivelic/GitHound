package com.zvonimirplivelic.githound.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isEmpty
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zvonimirplivelic.githound.GitHoundViewModel
import com.zvonimirplivelic.githound.R
import com.zvonimirplivelic.githound.ui.RepoSearchListAdapter
import com.zvonimirplivelic.githound.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class RepoSearchListFragment : Fragment() {
    private lateinit var viewModel: GitHoundViewModel
    private lateinit var repoListAdapter: RepoSearchListAdapter
    private lateinit var recyclerView: RecyclerView

    private lateinit var etSearchListQuery: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var ibFilterList: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_list, container, false)

        viewModel = ViewModelProvider(this)[GitHoundViewModel::class.java]

        recyclerView = view.findViewById(R.id.rv_repository_list)
        etSearchListQuery = view.findViewById(R.id.et_search_list_query)
        ibFilterList = view.findViewById(R.id.ib_search_list_button)
        progressBar = view.findViewById(R.id.progress_bar)

        repoListAdapter = RepoSearchListAdapter()
        recyclerView.apply {
            adapter = repoListAdapter
            layoutManager = LinearLayoutManager(activity)
        }

        var job: Job? = null
        etSearchListQuery.addTextChangedListener { queryString ->
            job?.cancel()
            job = MainScope().launch {
                delay(1000L)
                queryString?.let {
                    if (queryString.toString().isNotEmpty() && recyclerView.isEmpty()) {

                        // empty recycler image

                        viewModel.getRepositoryList(queryString.toString(), "", "", 30, 1)
                    }
                }
            }
        }

        ibFilterList.setOnClickListener {

        }

        viewModel.repositoryList.observe(viewLifecycleOwner) { response ->

            when (response) {
                is Resource.Success -> {
                    progressBar.isVisible = false

                    response.data?.let { repoList ->
                        repoListAdapter.differ.submitList(repoList.items)
                    }
                }

                is Resource.Error -> {
                    progressBar.isVisible = false

                    response.message?.let { message ->
                        Toast.makeText(activity, "An error occured: $message", Toast.LENGTH_LONG)
                            .show()
                    }
                }

                is Resource.Loading -> {
                    progressBar.isVisible = true
                }
            }

        }
        return view
    }
}