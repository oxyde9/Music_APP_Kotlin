package com.ald47.project.musicapp.recycler

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.ald47.project.musicapp.R
import com.ald47.project.musicapp.databinding.ItemTitresRankingBinding
import com.ald47.project.musicapp.response_models.Artist
import com.bumptech.glide.Glide

class LikedArtistsAdapter(
    private val context: Context,
    private val artists: List<Artist>,
    private val navController: NavController
) : RecyclerView.Adapter<LikedArtistsAdapter.LikedArtistsViewHolder>() {

    inner class LikedArtistsViewHolder(val binding: ItemTitresRankingBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikedArtistsViewHolder {
        val binding =
            ItemTitresRankingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LikedArtistsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LikedArtistsViewHolder, pos: Int) {
        val artist = artists[pos]
        with(holder) {
            binding.apply {
                rankTV.text = (pos + 1).toString()
                titleTV.text = artist.strArtist
                if (!artist.strArtistThumb.isNullOrEmpty()) {
                    Glide.with(context)
                        .load(artist.strArtistThumb)
                        .placeholder(R.drawable.place_holder_colored)
                        .into(itemCoverIV)
                }
                listItem.setOnClickListener {
                    val extras = Bundle()
                    extras.putString("artistId",artist.idArtist)
                    navController.navigate(R.id.action_favorisFragment_to_artistDetailsFragment,extras)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return artists.size // renvoie la taille de la liste
    }

}