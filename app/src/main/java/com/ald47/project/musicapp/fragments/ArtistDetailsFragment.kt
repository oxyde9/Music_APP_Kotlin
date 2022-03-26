package com.ald47.project.musicapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.ald47.project.musicapp.R
import com.ald47.project.musicapp.RetrofitInterface
import com.ald47.project.musicapp.databinding.FragmentArtistDetailsBinding
import com.ald47.project.musicapp.helpers.ApiHelper
import com.ald47.project.musicapp.recycler.ArtistAlbumsAdapter
import com.ald47.project.musicapp.recycler.TrackListAdapter
import com.ald47.project.musicapp.response_models.*
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ArtistDetailsFragment : Fragment(R.layout.fragment_artist_details) {

    private var artistId: String? = null
    private lateinit var binding : FragmentArtistDetailsBinding
    private lateinit var retrofit: Retrofit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            artistId = it.getString("artistId")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArtistDetailsBinding.bind(view)
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        if(artistId.isNullOrEmpty()){
            Toast.makeText(requireContext(),"Échec de l'analyse de l'identifiant de l'artiste.",Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
        else{
            // Artist Id is now safe to use
            // Create objet of Retrofit to use in this fragment
            retrofit = Retrofit.Builder()
                .baseUrl(ApiHelper.apiBaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            fetchArtistDetails(artistId!!)
            getArtistAlbums(artistId!!)
        }

    }

    private fun fetchArtistDetails(artistID : String) {
        val artistDetailsCall =  retrofit.create(RetrofitInterface::class.java)
            .getArtistDetails(artistID)
        artistDetailsCall.enqueue(object : Callback<ArtistDetailsResponse>{
            override fun onResponse(
                call: Call<ArtistDetailsResponse>,
                response: Response<ArtistDetailsResponse>
            ) {
                binding.detailsPrg.visibility = View.GONE
                if(response.code() == 200){
                    if(response.body()!=null){
                        if(! response.body()!!.artists.isNullOrEmpty()){
                            val artistInfo = response.body()!!.artists[0]
                            binding.apply {
                                artistNameTV.text = artistInfo.strArtist
                                locationTV.text = artistInfo.strCountry
                                artistGenreTV.text = artistInfo.strGenre
                                artistBioTV.text = artistInfo.strBiographyEN
                                if(!artistInfo.strArtistWideThumb.isNullOrEmpty()){
                                    Glide.with(requireActivity())
                                        .load(artistInfo.strArtistWideThumb)
                                        .into(artistCoverIV)
                                }
                                if(!artistInfo.strMusicBrainzID.isNullOrEmpty()){
                                    getArtistTracks(artistInfo.strMusicBrainzID)
                                }

                            }
                        }
                    }
                    else{
                        showSnack("Réponse API retournée Null.")
                    }
                }
                else{
                    showSnack("Échec de la récupération des détails de l'artiste.\nRéponse Code : ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ArtistDetailsResponse>, t: Throwable) {
                binding.detailsPrg.visibility = View.GONE
                showSnack("Échec de la récupération des détails de l'artiste.\nRaison: ${t.message}")
            }

        })
    }

    private fun getArtistAlbums(artistID : String){
        val albumsCall = retrofit.create(RetrofitInterface::class.java)
            .getArtistAlbums(artistID)
        albumsCall.enqueue(object : Callback<SearchAlbumResponse>{
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<SearchAlbumResponse>,
                response: Response<SearchAlbumResponse>
            ) {
                if(response.code() == 200){
                    if(response.body()!=null){
                        if(! response.body()!!.album.isNullOrEmpty()){
                            val albums = response.body()!!.album
                            binding.albumsCountTV.text = "(${albums.size})"
                            val adapter = ArtistAlbumsAdapter(requireContext(),albums,findNavController())
                            binding.artistAlbumsRV.adapter = adapter
                        }
                    }
                    else showSnack("Échec de la récupération des albums d'artistes. Résultat API vide.")
                }
                else showSnack("Échec de la récupération des albums d'artistes. Code : ${response.code()}")

            }
            override fun onFailure(call: Call<SearchAlbumResponse>, t: Throwable) {
                showSnack("Échec de la récupération des albums d'artistes. Raison : : ${t.message}")
            }
        })
    }

    private fun getArtistTracks(mbID : String){
        val tracksCall = retrofit.create(RetrofitInterface::class.java)
            .getArtistMostLikedTracks(mbID)
        tracksCall.enqueue(object : Callback<ArtistTopTracksResponse>{
            override fun onResponse(
                call: Call<ArtistTopTracksResponse>,
                response: Response<ArtistTopTracksResponse>
            ) {
                if(response.code() == 200 && response.body()!=null){
                    val tracks = response.body()!!.track
                    if(!tracks.isNullOrEmpty()){
                        binding.artistTracksRV.adapter = TrackListAdapter(tracks)
                    }
                }
                else{
                    showSnack("Échec de la récupération des pistes d'artistes. Code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ArtistTopTracksResponse>, t: Throwable) {
                showSnack("Échec de la récupération des meilleures pistes de l'artiste. Raison : ${t.message}")
            }

        })
    }

    private fun showSnack(msg : String){
        Snackbar.make(binding.root,msg,Snackbar.LENGTH_SHORT).show()
    }

}