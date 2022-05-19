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
import com.zvonimirplivelic.githound.model.GitSearchListResponse
import com.zvonimirplivelic.githound.util.Constants.FRAGMENT_IMAGE_DIMENSION
import com.zvonimirplivelic.githound.util.Resource

class AuthorDetailsFragment : Fragment() {
    private val args by navArgs<AuthorDetailsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_author_details, container, false)

        val selectedAuthor = args.selectedOwner

        val ivAuthorAvatar: ImageView = view.findViewById(R.id.iv_avatar_author_details)
        val tvAuthorName: TextView = view.findViewById(R.id.tv_name_author_details)
        val btnOpenAuthorDetails: Button = view.findViewById(R.id.btn_open_author_profile)

        Picasso.get()
            .load(selectedAuthor!!.avatarUrl)
            .resize(
                FRAGMENT_IMAGE_DIMENSION,
                FRAGMENT_IMAGE_DIMENSION
            )
            .noFade()
            .into(ivAuthorAvatar)

        tvAuthorName.text = selectedAuthor.login

        btnOpenAuthorDetails.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW)
            browserIntent.data = Uri.parse(selectedAuthor.htmlUrl)
            startActivity(browserIntent)
        }


        return view
    }
}