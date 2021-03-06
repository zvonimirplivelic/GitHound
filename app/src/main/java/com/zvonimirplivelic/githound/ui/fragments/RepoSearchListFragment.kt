package com.zvonimirplivelic.githound.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zvonimirplivelic.githound.GitHoundViewModel
import com.zvonimirplivelic.githound.R
import com.zvonimirplivelic.githound.model.QueryFilterParameter
import com.zvonimirplivelic.githound.ui.RepoSearchListAdapter
import com.zvonimirplivelic.githound.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber


class RepoSearchListFragment : Fragment() {
    private val args by navArgs<RepoSearchListFragmentArgs>()

    private lateinit var viewModel: GitHoundViewModel
    private lateinit var repoListAdapter: RepoSearchListAdapter
    private lateinit var recyclerView: RecyclerView

    private lateinit var etSearchListQuery: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var ibFilterList: ImageButton
    private lateinit var tvEmptyList: TextView
    private lateinit var tvEmptySearchString: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_list, container, false)

        val queryParams: QueryFilterParameter? = args.queryFilterParameters
        Timber.d("SEARCH_ARGS: sort${queryParams?.sortBy} order${queryParams?.orderBy} rpp${queryParams?.resultsPerPage} pno${queryParams?.pageNumber}")

        viewModel = ViewModelProvider(this)[GitHoundViewModel::class.java]

        recyclerView = view.findViewById(R.id.rv_repository_list)
        etSearchListQuery = view.findViewById(R.id.et_search_list_query)
        ibFilterList = view.findViewById(R.id.ib_search_list_button)
        progressBar = view.findViewById(R.id.progress_bar)
        tvEmptyList = view.findViewById(R.id.tv_empty_list)
        tvEmptySearchString = view.findViewById(R.id.tv_empty_search_string)

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
                    if (queryString.toString().isNotEmpty()) {
                        viewModel.getRepositoryList(
                            queryString.toString(),
                            queryParams?.sortBy,
                            queryParams?.orderBy,
                            queryParams?.resultsPerPage,
                            queryParams?.pageNumber
                        )
                        tvEmptySearchString.visibility = View.GONE
                        tvEmptyList.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                    } else {
                        tvEmptySearchString.visibility = View.VISIBLE
                        tvEmptyList.visibility = View.GONE
                        recyclerView.visibility = View.GONE
                    }
                }
            }
        }

        ibFilterList.setOnClickListener {
            showBottomSheet()
        }

        viewModel.repositoryList.observe(viewLifecycleOwner) { response ->

            when (response) {
                is Resource.Success -> {
                    progressBar.isVisible = false
                    tvEmptySearchString.isVisible = false

                    response.data?.let { repoList ->
                        if (repoList.items.isEmpty()) {
                            tvEmptyList.visibility = View.VISIBLE
                        } else {
                            tvEmptyList.visibility = View.GONE
                        }
                        repoListAdapter.differ.submitList(repoList.items)
                    }
                }

                is Resource.Error -> {
                    progressBar.isVisible = false
                    tvEmptySearchString.isVisible = false

                    response.message?.let { message ->
                        Toast.makeText(activity, "An error occured: $message", Toast.LENGTH_LONG)
                            .show()
                    }
                }

                is Resource.Loading -> {
                    progressBar.isVisible = true
                    tvEmptySearchString.isVisible = false
                }
            }

        }
        return view
    }

    private fun showBottomSheet() {
        findNavController().navigate(R.id.action_searchListFragment_to_bottomSheetFilterFragment)
    }
}
