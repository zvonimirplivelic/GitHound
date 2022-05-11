package com.zvonimirplivelic.githound.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.squareup.picasso.Picasso
import com.zvonimirplivelic.githound.GitHoundViewModel
import com.zvonimirplivelic.githound.R
import com.zvonimirplivelic.githound.model.GitRepoDetailResponse
import com.zvonimirplivelic.githound.model.GitRepoListResponse
import com.zvonimirplivelic.githound.util.Resource

class RepositoryDetailsFragment : Fragment() {

    private val args by navArgs<RepositoryDetailsFragmentArgs>()
    private lateinit var viewModel: GitHoundViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_repository_details, container, false)

        val selectedRepository: GitRepoListResponse.GitRepoResponseItem = args.currentRepository
        var repositoryDetailsData: GitRepoDetailResponse?

        val tvRepoName: TextView = view.findViewById(R.id.tv_repo_name_repo_details)
        val ivAuthorAvatar: ImageView = view.findViewById(R.id.iv_author_avatar_repo_details)
        val tvAuthorName: TextView = view.findViewById(R.id.tv_author_name_repo_details)
        val tvRepoDescription: TextView = view.findViewById(R.id.tv_short_description_repo_details)
        val tvCreatedAt: TextView = view.findViewById(R.id.tv_created_at_repo_details)
        val tvUpdatedAt: TextView = view.findViewById(R.id.tv_updated_at_repo_details)
        val tvNumberOfForks: TextView = view.findViewById(R.id.tv_number_of_forks_repo_details)
        val tvNumberOfWatchers: TextView = view.findViewById(R.id.tv_number_of_watchers_repo_details)
        val tvNumberOfOpenIssues: TextView = view.findViewById(R.id.tv_number_of_open_issues_repo_details)
        val tvLanguage: TextView = view.findViewById(R.id.tv_language_repo_details)

        viewModel = ViewModelProvider(this)[GitHoundViewModel::class.java]
        viewModel.getRepositoryDetailsResponse(
            selectedRepository.owner.login,
            selectedRepository.name
        )

        viewModel.repositoryDetails.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
//                    progressBar.isVisible = false
                    response.data?.let { detailResponse ->
                        repositoryDetailsData = detailResponse

                        tvRepoName.text = repositoryDetailsData!!.name
                        tvAuthorName.text = repositoryDetailsData!!.owner.login

                        Picasso.get()
                            .load(repositoryDetailsData!!.owner.avatarUrl)
                            .resize(380, 380)
                            .into(ivAuthorAvatar)

                        tvRepoDescription.text = repositoryDetailsData!!.description
                        tvCreatedAt.text = repositoryDetailsData!!.createdAt
                        tvUpdatedAt.text = repositoryDetailsData!!.updatedAt
                        tvNumberOfForks.text = repositoryDetailsData!!.forks.toString()
                        tvNumberOfWatchers.text = repositoryDetailsData!!.watchersCount.toString()
                        tvNumberOfOpenIssues.text = repositoryDetailsData!!.openIssuesCount.toString()
                        tvLanguage.text = repositoryDetailsData!!.language
                    }
                }

                is Resource.Error -> {
//                    progressBar.isVisible = false
                    response.message?.let { message ->
                        Toast.makeText(activity, "An error occured: $message", Toast.LENGTH_LONG)
                            .show()
                    }
                }

                is Resource.Loading -> {
//                    progressBar.isVisible = true
                }
            }
        }
        return view
    }
}