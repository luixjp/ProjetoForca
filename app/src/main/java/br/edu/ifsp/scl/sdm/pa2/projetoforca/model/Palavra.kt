package br.edu.ifsp.scl.sdm.pa2.projetoforca.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Palavra (
    @Expose
    @SerializedName("Id")
    val id: Int,

    @Expose
    @SerializedName("Palavra")
    val palavra: String,

    @Expose
    @SerializedName("Letras")
    val letras: Int,

    @Expose
    @SerializedName("Nivel")
    val nivel: Int
)