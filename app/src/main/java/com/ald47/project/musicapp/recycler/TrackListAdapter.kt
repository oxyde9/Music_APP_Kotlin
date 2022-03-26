package com.ald47.project.musicapp.recycler

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ald47.project.musicapp.databinding.ItemTopTracksBinding
import com.ald47.project.musicapp.response_models.Track

class TrackListAdapter(
    private val tracksList: List<Track>
) : RecyclerView.Adapter<TrackListAdapter.ArtistsPostsViewHolder>() {

    inner class ArtistsPostsViewHolder(val binding: ItemTopTracksBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistsPostsViewHolder {
        val binding = ItemTopTracksBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArtistsPostsViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ArtistsPostsViewHolder, pos: Int) {
        val track = tracksList[pos]
        with(holder) {
            binding.apply {
                itemNumberTV.text = "${pos+1}"
               trackNameTV.text = track.strTrack
            }
        }
    }

    override fun getItemCount(): Int {
        return tracksList.size // renvoie la taille de la liste
    }

}