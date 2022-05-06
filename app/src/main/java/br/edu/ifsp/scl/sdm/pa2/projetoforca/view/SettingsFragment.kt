package br.edu.ifsp.scl.sdm.pa2.projetoforca.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import br.edu.ifsp.scl.sdm.pa2.projetoforca.databinding.FragmentSettingsBinding


class SettingsFragment: Fragment() {

    private lateinit var fragmentSettingsBinding: FragmentSettingsBinding

    companion object {
        const val PREFS_NAME = "MyPreferences"
        const val KEY_RODADAS = "RODADAS"
        const val KEY_NIVEL = "NIVEL"
    }

    var rodadas : Int = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentSettingsBinding = FragmentSettingsBinding.inflate(inflater, container, false)

        val sharedPreferences = context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        rodadas = sharedPreferences?.getInt(KEY_RODADAS, 1) ?: 1

        //Recuperar os dados (width, etc.) do SeekBar apenas quando ele
        //já estiver desenhado/criado na Activity
        fragmentSettingsBinding.nivelSb.post(Runnable {
            configurarTextoSeekBar(rodadas)
        });

        val nivel: Int = sharedPreferences?.getInt(KEY_NIVEL, 1) ?: 1

        if (nivel == 1)
            fragmentSettingsBinding.opcaoFacil.isChecked = true
        if (nivel == 2)
            fragmentSettingsBinding.opcaoMedio.isChecked = true
        if (nivel == 3)
            fragmentSettingsBinding.opcaoDificil.isChecked = true


        fragmentSettingsBinding.nivelSb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                rodadas = progress
                configurarTextoSeekBar(progress)
            }


            override fun onStartTrackingTouch(p0: SeekBar?) {
              //Not Apply
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                //Not Apply
            }
        });

        fragmentSettingsBinding.cancelarBt.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()  //volta pro anterior
        }

        fragmentSettingsBinding.salvarBt.setOnClickListener {

        var nivel: Int = 1
            if (fragmentSettingsBinding.opcaoMedio.isChecked())
                nivel = 2
            if (fragmentSettingsBinding.opcaoDificil.isChecked())
                nivel = 3

            val sharedPref = context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            with (sharedPref!!.edit()) {
                putInt(KEY_NIVEL, nivel)
                putInt(KEY_RODADAS, rodadas)
                apply()
            }

            activity?.supportFragmentManager?.popBackStack()  //volta pro anterior
        }

        return fragmentSettingsBinding.root
    }

    fun configurarTextoSeekBar (progress: Int) {

        var largura = fragmentSettingsBinding.nivelSb?.width!!

        if (largura > 0) {
            val thumbOff = fragmentSettingsBinding.nivelSb?.thumbOffset!!
            val max = fragmentSettingsBinding.nivelSb?.max!!
            val deslocamento = progress * (largura - (2 * thumbOff)) / max;
            val position = fragmentSettingsBinding.nivelSb.x + deslocamento + thumbOff / 2
            fragmentSettingsBinding.tvSeekBar.setX(position);
        }
        fragmentSettingsBinding.nivelSb.progress = progress
        fragmentSettingsBinding.tvSeekBar.setText("" + progress);
    }

}