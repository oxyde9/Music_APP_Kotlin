package com.ald47.project.musicapp.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ald47.project.musicapp.R
import com.ald47.project.musicapp.databinding.ItemTitresRankingBinding
import com.ald47.project.musicapp.response_models.Trending
import com.bumptech.glide.Glide

class TitresAlbumsListAdapter(
    private val context: Context,
    private val from: String,
    private val itemsList: List<Trending>
) : RecyclerView.Adapter<TitresAlbumsListAdapter.FollowingPostsViewHolder>() {

    inner class FollowingPostsViewHolder(val binding: ItemTitresRankingBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowingPostsViewHolder {
        val binding =
            ItemTitresRankingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FollowingPostsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FollowingPostsViewHolder, pos: Int) {
        val track = itemsList[pos]
        with(holder) {
            binding.apply {
                rankTV.text = (pos + 1).toString()
                artistNameTV.text = track.strArtist
                when (from) { // décider si les données concernent un artiste ou un album
                    "titres" -> {
                        titleTV.text = track.strTrack
                        if (!track.strTrackThumb.isNullOrEmpty()) {
                            Glide.with(context)
                                .load(track.strTrackThumb)
                                .placeholder(R.drawable.place_holder_colored)
                                .into(itemCoverIV)
                        }
                    }
                    "albums" -> {
                        titleTV.text = track.strAlbum
                        if (!track.strAlbumThumb.isNullOrEmpty()) {
                            Glide.with(context)
                                .load(track.strAlbumThumb)
                                .placeholder(R.drawable.place_holder_colored)
                                .into(itemCoverIV)
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return itemsList.size // renvoie la taille de la liste
    }

}