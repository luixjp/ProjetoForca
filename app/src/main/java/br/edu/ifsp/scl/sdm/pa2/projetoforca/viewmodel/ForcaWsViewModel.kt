package br.edu.ifsp.scl.sdm.pa2.projetoforca.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.edu.ifsp.scl.sdm.pa2.projetoforca.model.ForcaWsApi
import br.edu.ifsp.scl.sdm.pa2.projetoforca.model.Nivel
import br.edu.ifsp.scl.sdm.pa2.projetoforca.model.Palavra
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ForcaWsViewModel (application: Application): AndroidViewModel(application) {

    companion object {
        val URL_BASE = "https://nobile.pro.br/forcaws/" //Ou usar HTTPS ou colocar android:usesCleartextTraffic="true" na tag application do AndroidManifest
    }
    val niveisMld: MutableLiveData<Nivel> = MutableLiveData()
    val palavraMld: MutableLiveData<Palavra> = MutableLiveData()

    private val escopoCorrotinas = CoroutineScope(Dispatchers.IO + Job())

    private val gson: Gson = Gson()

    //Retrofit
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("${URL_BASE}/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val forcaWsApi: ForcaWsApi = retrofit.create(ForcaWsApi::class.java)

    fun getPalavra(pid: Int) {

        escopoCorrotinas.launch {
            forcaWsApi.retrievePalavra(pid).enqueue(object: Callback<List<Palavra>> {
                override fun onResponse(
                    call: Call<List<Palavra>>,
                    response: Response<List<Palavra>>
                ) {
                    val palavra = response.body()
                    palavraMld.postValue(palavra?.get(0)) //Assumindo que há sempre apenas uma palavra
                }

                override fun onFailure(call: Call<List<Palavra>>, t: Throwable) {
                    Log.e("$URL_BASE", t.message.toString())
                }
            })
        }
    }

    fun getNivel(nivel: Int) {
        escopoCorrotinas.launch {

            forcaWsApi.retrieveNivel(nivel).enqueue(object : Callback<Nivel> {
                override fun onResponse(call: Call<Nivel>, response: retrofit2.Response<Nivel>) {
                    val nivel = response.body()
                    niveisMld.postValue(nivel)
                }

                override fun onFailure(call: Call<Nivel>, t: Throwable) {
                    Log.e("$URL_BASE", t.message.toString())
                }
            })
        }
    }
}