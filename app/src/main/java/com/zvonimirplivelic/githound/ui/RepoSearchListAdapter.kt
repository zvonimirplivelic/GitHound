package com.zvonimirplivelic.githound.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.zvonimirplivelic.githound.R
import com.zvonimirplivelic.githound.model.GitSearchListResponse
import com.zvonimirplivelic.githound.ui.fragments.RepoSearchListFragmentDirections
import com.zvonimirplivelic.githound.util.Constants.ADAPTER_IMAGE_DIMENSION

class RepoSearchListAdapter:
    RecyclerView.Adapter<RepoSearchListAdapter.RepoSearchItemViewHolder>() {

    inner class RepoSearchItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val diffCallback =
        object : DiffUtil.ItemCallback<GitSearchListResponse.Item>() {
            override fun areItemsTheSame(
                oldItem: GitSearchListResponse.Item,
                newItem: GitSearchListResponse.Item
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: GitSearchListResponse.Item,
                newItem: GitSearchListResponse.Item
            ): Boolean {
                return oldItem == newItem
            }
        }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoSearchItemViewHolder {
        return RepoSearchItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.search_list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RepoSearchItemViewHolder, position: Int) {
        val repositoryItem = differ.currentList[position]

        holder.itemView.apply {
            val cvRepoItem: CardView = findViewById(R.id.cv_repo_item)
            val cvAuthor: CardView = findViewById(R.id.cv_author)
            val ivAuthorAvatar: ImageView = findViewById(R.id.iv_author_avatar)
            val tvAuthorName: TextView = findViewById(R.id.tv_author_name)
            val tvRepositoryName: TextView = findViewById(R.id.tv_repository_name)

            Picasso.get()
                .load(repositoryItem.owner.avatarUrl)
                .resize(ADAPTER_IMAGE_DIMENSION, ADAPTER_IMAGE_DIMENSION)
                .noFade()
                .into(ivAuthorAvatar)

            tvAuthorName.text = repositoryItem.owner.login
            tvRepositoryName.text = repositoryItem.name

            cvAuthor.setOnClickListener {
                val action =
                    RepoSearchListFragmentDirections.actionSearchListFragmentToAuthorDetailsFragment(
                        repositoryItem.owner
                    )
                findNavController().navigate(action)
            }

            cvRepoItem.setOnClickListener {
                val action =
                    RepoSearchListFragmentDirections.actionSearchListFragmentToRepositoryDetailsFragment(
                        repositoryItem
                    )
                findNavController().navigate(action)
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size
}