package com.zvonimirplivelic.githound.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zvonimirplivelic.githound.GitHoundViewModel
import com.zvonimirplivelic.githound.R
import com.zvonimirplivelic.githound.model.GitRepoListResponse
import com.zvonimirplivelic.githound.ui.RepoSearchListAdapter

class RepoSearchListFragment : Fragment() {
    private lateinit var viewModel: GitHoundViewModel
    private lateinit var repoListAdapter: RepoSearchListAdapter
    private lateinit var recyclerView: RecyclerView

    private lateinit var etSearchListQuery: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_list, container, false)

        viewModel = ViewModelProvider(this)[GitHoundViewModel::class.java]

        recyclerView = view.findViewById(R.id.rv_repository_list)
        etSearchListQuery = view.findViewById(R.id.et_search_list_query)

        repoListAdapter = RepoSearchListAdapter()
        recyclerView.apply {
            adapter = repoListAdapter
            layoutManager = LinearLayoutManager(activity)
        }

        viewModel.getRepositoryList()

        viewModel.repositoryList.observe(viewLifecycleOwner) { repositoryList ->

//            val repoSearch: MutableList<GitRepoListResponse.GitRepoResponseItem> = mutableListOf()
//            val queryString = etSearchListQuery.text.toString()
//
//            repoSearch.forEach { repoItem ->
//                val repositoryName = repoItem.name
//                if(repositoryName.contains(queryString)) {
//                    repoSearch.add(repoItem)
//                } else {
//                    repoListAdapter.differ.submitList(repositoryList.data)
//                }
//            }

            repoListAdapter.differ.submitList(repositoryList.data)
        }
        return view
    }
}