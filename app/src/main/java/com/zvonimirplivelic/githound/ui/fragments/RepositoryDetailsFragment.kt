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
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.squareup.picasso.Picasso
import com.zvonimirplivelic.githound.GitHoundViewModel
import com.zvonimirplivelic.githound.R
import com.zvonimirplivelic.githound.model.GitSearchListResponse
import com.zvonimirplivelic.githound.util.Constants
import com.zvonimirplivelic.githound.util.Constants.FRAGMENT_IMAGE_DIMENSION
import com.zvonimirplivelic.githound.util.Resource

class RepositoryDetailsFragment : androidx.fragment.app.Fragment() {

    private val args by navArgs<RepositoryDetailsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_repository_details, container, false)

        val selectedRepository: GitSearchListResponse.Item? = args.selectedRepository

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

        tvRepoName.text =
            resources.getString(R.string.repository_name, selectedRepository!!.name)

        Picasso.get()
            .load(selectedRepository.owner.avatarUrl)
            .resize(Constants.ADAPTER_IMAGE_DIMENSION, Constants.ADAPTER_IMAGE_DIMENSION)
            .noFade()
            .into(ivAuthorAvatar)

        tvAuthorName.text =
            resources.getString(R.string.author_name, selectedRepository.owner.login)
        tvRepoDescription.text = resources.getString(R.string.repository_description, selectedRepository.description)
        tvCreatedAt.text = resources.getString(R.string.created_at, selectedRepository.createdAt)
        tvUpdatedAt.text = resources.getString(R.string.updated_at, selectedRepository.updatedAt)
        tvNumberOfForks.text = resources.getString(R.string.number_of_forks, selectedRepository.forksCount)
        tvNumberOfWatchers.text = resources.getString(R.string.number_of_watchers, selectedRepository.watchersCount)
        tvNumberOfOpenIssues.text = resources.getString(R.string.number_of_open_issues, selectedRepository.openIssuesCount)
        tvLanguage.text = resources.getString(R.string.language, selectedRepository.language)

        btnOpenRepoDetails.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW)
            browserIntent.data = Uri.parse(selectedRepository.htmlUrl)
            startActivity(browserIntent)
        }
        return view
    }
}