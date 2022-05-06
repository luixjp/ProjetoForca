package br.edu.ifsp.scl.sdm.pa2.projetoforca.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ForcaWsApi {

    @GET("palavra/{pid}")
    fun retrievePalavra(@Path("pid") pid: Int) : Call<List<Palavra>>

    @GET("identificadores/{nivel}")
    fun retrieveNivel(@Path("nivel") nivel: Int) : Call<Nivel>

}