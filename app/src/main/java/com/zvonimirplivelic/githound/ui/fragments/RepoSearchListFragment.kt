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
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zvonimirplivelic.githound.GitHoundViewModel
import com.zvonimirplivelic.githound.R
import com.zvonimirplivelic.githound.model.GitRepoListResponse
import com.zvonimirplivelic.githound.ui.RepoSearchListAdapter
import com.zvonimirplivelic.githound.util.Resource
import timber.log.Timber
import java.util.*

class RepoSearchListFragment : Fragment() {
    private lateinit var viewModel: GitHoundViewModel
    private lateinit var repoListAdapter: RepoSearchListAdapter
    private lateinit var recyclerView: RecyclerView

    private lateinit var etSearchListQuery: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var ibSearchList: ImageButton

    private lateinit var displayedList: MutableList<GitRepoListResponse.GitRepoResponseItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_list, container, false)

        viewModel = ViewModelProvider(this)[GitHoundViewModel::class.java]

        recyclerView = view.findViewById(R.id.rv_repository_list)
        etSearchListQuery = view.findViewById(R.id.et_search_list_query)
        ibSearchList = view.findViewById(R.id.ib_search_list_button)
        progressBar = view.findViewById(R.id.progress_bar)

        repoListAdapter = RepoSearchListAdapter()
        recyclerView.apply {
            adapter = repoListAdapter
            layoutManager = LinearLayoutManager(activity)
        }

        viewModel.getRepositoryList()

        ibSearchList.setOnClickListener {
            filterList(etSearchListQuery.text.toString(), displayedList)
        }

        viewModel.repositoryList.observe(viewLifecycleOwner) { response ->

            when (response) {
                is Resource.Success -> {
                    progressBar.isVisible = false
                    etSearchListQuery.isVisible = true
                    ibSearchList.isVisible = true
                    recyclerView.isVisible = true

                    response.data?.let { repoList ->
                        displayedList = repoList
                        repoListAdapter.differ.submitList(repoList)
                    }
                }

                is Resource.Error -> {
                    progressBar.isVisible = false
                    etSearchListQuery.isVisible = true
                    ibSearchList.isVisible = true
                    recyclerView.isVisible = true

                    response.message?.let { message ->
                        Toast.makeText(activity, "An error occured: $message", Toast.LENGTH_LONG)
                            .show()
                    }
                }

                is Resource.Loading -> {
                    progressBar.isVisible = true
                    etSearchListQuery.isVisible = false
                    ibSearchList.isVisible = false
                    recyclerView.isVisible = false
                }
            }

        }
        return view
    }

    private fun filterList(
        queryString: String,
        repoList: MutableList<GitRepoListResponse.GitRepoResponseItem>
    ) {
        val listSearch: MutableList<GitRepoListResponse.GitRepoResponseItem> = mutableListOf()

        if (queryString.isBlank()) {
            repoListAdapter.differ.submitList(repoList)
        } else {
            repoList.forEach { repoItem ->

                val queryTerm = queryString.lowercase(Locale.ROOT)
                val repoName = repoItem.name.lowercase(Locale.ROOT)

                if (repoName.contains(queryTerm))
                    listSearch.add(repoItem)
            }
            if (listSearch.size == 0) {
                Toast.makeText(requireContext(), "No results found", Toast.LENGTH_SHORT).show()
            }
            repoListAdapter.differ.submitList(listSearch)
        }
    }
}