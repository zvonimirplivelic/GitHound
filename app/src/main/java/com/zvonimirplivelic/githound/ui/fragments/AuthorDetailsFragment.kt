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
import androidx.navigation.fragment.navArgs
import com.squareup.picasso.Picasso
import com.zvonimirplivelic.githound.GitHoundViewModel
import com.zvonimirplivelic.githound.R
import com.zvonimirplivelic.githound.model.GitAuthorResponse
import com.zvonimirplivelic.githound.model.GitRepoListResponse
import com.zvonimirplivelic.githound.util.Constants.FRAGMENT_IMAGE_DIMENSION
import com.zvonimirplivelic.githound.util.Resource

class AuthorDetailsFragment : Fragment() {

    private val args by navArgs<RepositoryDetailsFragmentArgs>()
    private lateinit var viewModel: GitHoundViewModel

    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_author_details, container, false)
        val selectedRepository: GitRepoListResponse.GitRepoResponseItem = args.currentRepository

        val authorDataLayout: ConstraintLayout = view.findViewById(R.id.author_data_layout)
        val ivAuthorAvatar: ImageView = view.findViewById(R.id.iv_avatar_author_details)
        val tvAuthorName: TextView = view.findViewById(R.id.tv_name_author_details)
        val btnOpenAuthorDetails: Button = view.findViewById(R.id.btn_open_author_profile)

        progressBar = view.findViewById(R.id.progress_bar)

        viewModel = ViewModelProvider(this)[GitHoundViewModel::class.java]
        viewModel.getAuthorDetailsResponse(selectedRepository.owner.login)

        btnOpenAuthorDetails.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW)
            browserIntent.data = Uri.parse(selectedRepository.owner.htmlUrl)
            startActivity(browserIntent)
        }

        viewModel.authorDetails.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    progressBar.isVisible = false
                    authorDataLayout.isVisible = true

                    response.data?.let { detailResponse ->
                        tvAuthorName.text =
                            resources.getString(R.string.author_name, detailResponse.login)

                        Picasso.get()
                            .load(detailResponse.avatarUrl)
                            .noFade()
                            .resize(FRAGMENT_IMAGE_DIMENSION, FRAGMENT_IMAGE_DIMENSION)
                            .into(ivAuthorAvatar)
                    }
                }

                is Resource.Error -> {
                    progressBar.isVisible = false
                    authorDataLayout.isVisible = true

                    response.message?.let { message ->
                        Toast.makeText(activity, "An error occured: $message", Toast.LENGTH_LONG)
                            .show()
                    }
                }

                is Resource.Loading -> {
                    progressBar.isVisible = true
                    authorDataLayout.isVisible = false
                }
            }
        }
        return view
    }
}