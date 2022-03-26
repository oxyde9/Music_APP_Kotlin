package com.ald47.project.musicapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import com.ald47.project.musicapp.R
import com.ald47.project.musicapp.databinding.FragmentRechercherBinding
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.navigation.fragment.findNavController
import com.ald47.project.musicapp.RetrofitInterface
import com.ald47.project.musicapp.helpers.ApiHelper
import com.ald47.project.musicapp.recycler.AlbumSearchAdapter
import com.ald47.project.musicapp.recycler.ArtistsSearchAdapter
import com.ald47.project.musicapp.response_models.SearchAlbumResponse
import com.ald47.project.musicapp.response_models.SearchArtistsResponse
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_rechercher.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RechercherFragment : Fragment(R.layout.fragment_rechercher) {

    private lateinit var binding : FragmentRechercherBinding
    private lateinit var retrofit: Retrofit

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRechercherBinding.bind(view)

        retrofit = Retrofit.Builder()
            .baseUrl(ApiHelper.apiBaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        binding.searchET.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchET.text.trim().toString().lowercase()
                if(query.length>2){
                    binding.searchResultViews.visibility = View.VISIBLE
                    searchArtist(query)
                    searchAlbums(query)
                }
                else showSnack("La recherche doit contenir au moins 3 caractères.")

                return@OnEditorActionListener true
            }
            false
        })

        binding.clearTextIV.setOnClickListener {
            binding.searchET.text.clear()
        }
    }

    private fun searchArtist(query : String) {
        val artistsCall = retrofit.create(RetrofitInterface::class.java)
            .searchArtist(query)
        artistsCall.enqueue(object : Callback<SearchArtistsResponse>{
            override fun onResponse(
                call: Call<SearchArtistsResponse>,
                response: Response<SearchArtistsResponse>
            ) {
                binding.artistsPrg.visibility = View.GONE
                if(response.code()==200){
                    if(response.body()!=null){
                        val artists = response.body()!!.artists
                        if(!artists.isNullOrEmpty()){
                            val adapter = ArtistsSearchAdapter(requireContext(),artists,findNavController())
                            binding.artistsRV.adapter = adapter
                        }
                        else{
                            showSnack("Aucun artiste trouvé.")
                        }
                    }
                }
                else{
                    Log.e("apiFailures","Échec de la recherche d'artistes. ResponseCode : ${response.code()}")
                }
            }

            override fun onFailure(call: Call<SearchArtistsResponse>, t: Throwable) {
                binding.artistsPrg.visibility = View.GONE
                Log.e("apiFailures","Impossible de récupérer l'artiste. Raison : : ${t.message}")
            }
        })
    }

    private fun searchAlbums(query: String) {
        val albumsCall = retrofit.create(RetrofitInterface::class.java)
            .searchAlbum(query)
        albumsCall.enqueue(object : Callback<SearchAlbumResponse>{
            override fun onResponse(
                call: Call<SearchAlbumResponse>,
                response: Response<SearchAlbumResponse>
            ) {
                binding.albumsPrg.visibility = View.GONE
                if(response.code()==200){
                    if(response.body()!=null){
                        val albums = response.body()!!.album
                        if(!albums.isNullOrEmpty()){
                            val adapter = AlbumSearchAdapter(requireContext(),albums,findNavController())
                            binding.albumsRV.adapter = adapter
                        }
                        else{
                            showSnack("Aucun album trouvé.")
                        }
                    }
                }
                else{
                    Log.e("apiFailures","Échec de la récupération des albums. ResponseCode : ${response.code()}")
                }
            }

            override fun onFailure(call: Call<SearchAlbumResponse>, t: Throwable) {
                binding.albumsPrg.visibility = View.GONE
                Log.e("apiFailures","Échec de la récupération des albums d'artistes. Raison : : ${t.message}")
            }
        })
    }

    private fun showSnack(msg : String){
        Snackbar.make(binding.root,msg,Snackbar.LENGTH_SHORT).show()
    }

}