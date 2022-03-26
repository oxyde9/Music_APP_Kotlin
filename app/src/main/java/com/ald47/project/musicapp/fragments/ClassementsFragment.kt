package com.ald47.project.musicapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.ald47.project.musicapp.R
import com.ald47.project.musicapp.RetrofitInterface
import com.ald47.project.musicapp.TitresAlbumsListAdapter
import com.ald47.project.musicapp.databinding.FragmentClassementsBinding
import com.ald47.project.musicapp.helpers.ApiHelper
import com.ald47.project.musicapp.response_models.TopSinglesResponse
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ClassementsFragment : Fragment(R.layout.fragment_classements) {

    private lateinit var binding : FragmentClassementsBinding
    private lateinit var retrofit: Retrofit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        if(this::binding.isInitialized){ // cela doit être fait pour que la sélection de l'onglet Albums/Titres reste intacte lors du changement de Fragments
            return binding.root
        }

        binding = DataBindingUtil.inflate(layoutInflater,R.layout.fragment_classements,container,false)
        setBtnBehaviour()

        // Créer un objet de Retrofit à utiliser dans ce fragment
        retrofit = Retrofit.Builder()
            .baseUrl(ApiHelper.apiBaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        getTitres()
        return binding.root
    }

    private fun getTitres(){
        val titresCall = retrofit.create(RetrofitInterface::class.java)
            .getTrending(
                format = "singles",
                country = "us",
                type = "itunes"
            )
        Log.d("apiRequests->","Request : ${titresCall.request().url()}")
        binding.prgBar.visibility = View.VISIBLE
        titresCall.enqueue(object : Callback<TopSinglesResponse>{
            override fun onResponse(
                call: Call<TopSinglesResponse>,
                response: Response<TopSinglesResponse>
            ) {
                binding.prgBar.visibility = View.GONE
                if(response.code() == 200){
                    if(response.body()!=null){
                        val titres = response.body()!!.trending
                        if(titres.isNullOrEmpty()){
                            showSnack("Api n'a rien retourné.")
                        }
                        else{
                            val adapter = TitresAlbumsListAdapter(requireContext(),"titres",titres,findNavController())
                            binding.resultsRV.adapter = adapter
                        }
                    }
                }
                else{
                    showSnack("Failed to fetch titres. ResponseCode: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<TopSinglesResponse>, t: Throwable) {
                binding.prgBar.visibility = View.GONE
                showSnack("Échec de la récupération des titres. Raison : ${t.message}")
            }
        })
    }

    private fun getAlbums(){
        val albumsCall = retrofit.create(RetrofitInterface::class.java)
            .getTrending(
                format = "albums",
                country = "us",
                type = "itunes"
            )
        binding.prgBar.visibility = View.VISIBLE
        albumsCall.enqueue(object : Callback<TopSinglesResponse>{
            override fun onResponse(
                call: Call<TopSinglesResponse>,
                response: Response<TopSinglesResponse>
            ) {
                binding.prgBar.visibility = View.GONE
                if(response.code() == 200){
                    if(response.body()!=null){
                        val titres = response.body()!!.trending
                        if(titres.isNullOrEmpty()){
                            showSnack("Api n'a rien retourné.")
                        }
                        else{
                            val adapter = TitresAlbumsListAdapter(requireContext(),"albums",titres,findNavController())
                            binding.resultsRV.adapter = adapter
                        }
                    }
                }
                else{
                    showSnack("Échec de la récupération des albums. ResponseCode : ${response.code()}")
                }
            }

            override fun onFailure(call: Call<TopSinglesResponse>, t: Throwable) {
                binding.prgBar.visibility = View.GONE
                showSnack("Échec de la récupération des titres. Raison : ${t.message}")
            }
        })
    }

    private fun setBtnBehaviour(){
        binding.apply {

            titresTag.setOnClickListener {
                line1.visibility = View.VISIBLE
                line2.visibility = View.INVISIBLE
                getTitres()
            }
            albumsTag.setOnClickListener {
                line1.visibility = View.INVISIBLE
                line2.visibility = View.VISIBLE
                getAlbums()
            }
        }
    }

    private fun showSnack(msg : String){
        Snackbar.make(binding.root,msg,Snackbar.LENGTH_SHORT).show()
    }
}