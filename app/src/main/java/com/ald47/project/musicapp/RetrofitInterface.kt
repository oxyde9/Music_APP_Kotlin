package com.ald47.project.musicapp

import com.ald47.project.musicapp.response_models.*
import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface {

    @GET("trending.php")
    fun getTrending(
        @Query("country") country: String,
        @Query("type") type: String,
        @Query("format") format: String,
    ): Call<TopSinglesResponse>

    /// Search API calls
    @GET("search.php")
    fun searchArtist(@Query("s") artistName: String): Call<SearchArtistsResponse>

    @GET("searchalbum.php")
    fun searchAlbum(@Query("s") artist: String) : Call<SearchAlbumResponse>
    /////////////////////////////

    ////////// Artist API Calls
    @GET("artist.php")
    fun getArtistDetails(@Query("i") artistId : String) : Call<ArtistDetailsResponse>

    @GET("album.php")
    fun getArtistAlbums(@Query("i") artistId : String) : Call<SearchAlbumResponse>

    @GET("track-top10-mb.php")
    fun getArtistMostLikedTracks(@Query("s") mbId : String) : Call<ArtistTopTracksResponse>
    //////////////////////////////////

    ///// Album API Calls
    @GET("album.php")
    fun getAlbumDetails(@Query("m") albumMbId : String) : Call<SearchAlbumResponse>

    @GET("track.php")
    fun getAlbumTracks(@Query("m") albumId : String) : Call<ArtistTopTracksResponse>
}