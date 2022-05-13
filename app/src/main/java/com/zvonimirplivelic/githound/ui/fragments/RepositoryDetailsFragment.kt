package com.zvonimirplivelic.githound.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.squareup.picasso.Picasso
import com.zvonimirplivelic.githound.GitHoundViewModel
import com.zvonimirplivelic.githound.R
import com.zvonimirplivelic.githound.model.GitRepoListResponse
import com.zvonimirplivelic.githound.util.Constants.FRAGMENT_IMAGE_DIMENSION
import com.zvonimirplivelic.githound.util.Resource

class RepositoryDetailsFragment : Fragment() {

    private val args by navArgs<RepositoryDetailsFragmentArgs>()
    private lateinit var viewModel: GitHoundViewModel

    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_repository_details, container, false)

        val selectedRepository: GitRepoListResponse.GitRepoResponseItem = args.currentRepository

        val repositoryDataLayout: ConstraintLayout = view.findViewById(R.id.repository_data_layout)
        val tvRepoName: TextView = view.findViewById(R.id.tv_repo_name_repo_details)
        val ivAuthorAvatar: ImageView = view.findViewById(R.id.iv_author_avatar_repo_details)
        val tvAuthorName: TextView = view.findViewById(R.id.tv_author_name_repo_details)
        val tvRepoDescription: TextView = view.findViewById(R.id.tv_short_description_repo_details)
        val tvCreatedAt: TextView = view.findViewById(R.id.tv_created_at_repo_details)
        val tvUpdatedAt: TextView = view.findViewById(R.id.tv_updated_at_repo_details)
        val tvNumberOfForks: TextView = view.findViewById(R.id.tv_number_of_forks_repo_details)
        val tvNumberOfWatchers: TextView =
            view.findViewById(R.id.tv_number_of_watchers_repo_details)
        val tvNumberOfOpenIssues: TextView =
            view.findViewById(R.id.tv_number_of_open_issues_repo_details)
        val tvLanguage: TextView = view.findViewById(R.id.tv_language_repo_details)
        val btnOpenRepoDetails: Button = view.findViewById(R.id.btn_open_github_repository)

        progressBar = view.findViewById(R.id.progress_bar)

        viewModel = ViewModelProvider(this)[GitHoundViewModel::class.java]
        viewModel.getRepositoryDetailsResponse(
            selectedRepository.owner.login,
            selectedRepository.name
        )

        tvAuthorName.setOnClickListener {
            navigateToAuthorDetails(selectedRepository)
        }
        ivAuthorAvatar.setOnClickListener {
            navigateToAuthorDetails(selectedRepository)
        }

        btnOpenRepoDetails.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW)
            browserIntent.data = Uri.parse(selectedRepository.htmlUrl)
            startActivity(browserIntent)
        }

        viewModel.repositoryDetails.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    progressBar.isVisible = false
                    repositoryDataLayout.isVisible = true

                    response.data?.let { detailResponse ->

                        tvRepoName.text =
                            resources.getString(R.string.repository_name, detailResponse.name)
                        tvAuthorName.text =
                            resources.getString(R.string.author_name, detailResponse.owner.login)

                        Picasso.get()
                            .load(detailResponse.owner.avatarUrl)
                            .noFade()
                            .resize(FRAGMENT_IMAGE_DIMENSION, FRAGMENT_IMAGE_DIMENSION)
                            .into(ivAuthorAvatar)

                        tvRepoDescription.text = resources.getString(
                            R.string.repository_description,
                            detailResponse.description
                        )

                        tvCreatedAt.text =
                            resources.getString(R.string.created_at, detailResponse.createdAt)
                        tvUpdatedAt.text =
                            resources.getString(R.string.updated_at, detailResponse.updatedAt)
                        tvNumberOfForks.text =
                            resources.getString(R.string.number_of_forks, detailResponse.forks)
                        tvNumberOfWatchers.text = resources.getString(
                            R.string.number_of_watchers,
                            detailResponse.watchersCount
                        )
                        tvNumberOfOpenIssues.text = resources.getString(
                            R.string.number_of_open_issues,
                            detailResponse.openIssuesCount
                        )
                        tvLanguage.text =
                            resources.getString(R.string.language, detailResponse.language)
                    }
                }

                is Resource.Error -> {
                    progressBar.isVisible = false
                    repositoryDataLayout.isVisible = true
                    response.message?.let { message ->
                        Toast.makeText(activity, "An error occured: $message", Toast.LENGTH_LONG)
                            .show()
                    }
                }

                is Resource.Loading -> {
                    progressBar.isVisible = true
                    repositoryDataLayout.isVisible = false
                }
            }
        }
        return view
    }

    private fun navigateToAuthorDetails(selectedRepository: GitRepoListResponse.GitRepoResponseItem) {
        val action =
            RepositoryDetailsFragmentDirections.actionRepositoryDetailsFragmentToAuthorDetailsFragment(
                selectedRepository
            )
        findNavController().navigate(action)
    }
}