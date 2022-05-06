package br.edu.ifsp.scl.sdm.pa2.projetoforca.view

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import br.edu.ifsp.scl.sdm.pa2.projetoforca.R
import br.edu.ifsp.scl.sdm.pa2.projetoforca.databinding.FragmentMainBinding
import br.edu.ifsp.scl.sdm.pa2.projetoforca.viewmodel.ForcaWsViewModel
import java.util.*
import kotlin.random.Random
import kotlin.text.StringBuilder

class MainFragment : Fragment() {

    private lateinit var fragmentMainBinding: FragmentMainBinding

    private lateinit var forcaWsViewModel: ForcaWsViewModel

    //Lista de identificados de palavras
    private val listaIdentificadoresPalavras = mutableListOf<Int>()
    private val listaIdentificadoresPalavrasJaEscolhidas = mutableListOf<Int>()

    private lateinit var word: CharArray
    private lateinit var wordAtual: CharArray
    private var rodadas : Int = 1
    private var rodadaAtual: Int = 0
    private var nivel: Int = 1
    private var tentativas: Int = 0
    private var vitorias: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMainBinding = FragmentMainBinding.inflate(inflater, container, false)

        val sharedPreferences = context?.getSharedPreferences(SettingsFragment.PREFS_NAME, Context.MODE_PRIVATE)
        rodadas = sharedPreferences?.getInt(SettingsFragment.KEY_RODADAS, 1) ?: 1
        nivel = sharedPreferences?.getInt(SettingsFragment.KEY_NIVEL, 1) ?: 1
        rodadaAtual = 0

        fragmentMainBinding.tentativaBt.setOnClickListener {

            checkUserLetter()
            fragmentMainBinding.edtLetraTentativa.text.clear()
        }

        fragmentMainBinding.reiniciarBt.setOnClickListener {
            restartGame()
        }

        fragmentMainBinding.edtLetraTentativa.filters = fragmentMainBinding.edtLetraTentativa.filters + InputFilter.AllCaps()

        //Instanciando viewmodel
        forcaWsViewModel = ViewModelProvider
            .AndroidViewModelFactory(requireActivity().application)
            .create(ForcaWsViewModel::class.java)

        fragmentMainBinding.tentativaBt.setBackgroundColor(Color.GRAY)
        fragmentMainBinding.tentativaBt.isEnabled = false
        forcaWsViewModel.palavraMld.observe(requireActivity()) { palavraApi ->
            word = palavraApi.palavra.uppercase(Locale.getDefault()).toCharArray()
            wordAtual = CharArray(word.size)
            fragmentMainBinding.tentativaBt.setBackgroundColor(Color.BLUE)
            fragmentMainBinding.tentativaBt.isEnabled = true
        }

        forcaWsViewModel.niveisMld.observe(requireActivity()) { niveis ->
            listaIdentificadoresPalavrasJaEscolhidas.clear()
            listaIdentificadoresPalavras.clear()
            listaIdentificadoresPalavras.addAll(niveis)
            restartGame()
        }
        forcaWsViewModel.getNivel(nivel)

        return fragmentMainBinding.root
    }

    private fun checkIfWordIsCompleted(sb: java.lang.StringBuilder) {
        if(String(word) == String(wordAtual)) {
            sb.append("PARABÉNS! VOCÊ ACERTOU A PALAVRA! (" + String(word) + ")\n")
            vitorias++
            fragmentMainBinding.tentativaBt.isEnabled = false
            fragmentMainBinding.tentativaBt.setBackgroundColor(Color.GRAY)
            fragmentMainBinding.reiniciarBt.isEnabled = true

            verificaGameOver(sb)
        }
        else {
            if(tentativas == 6) {
                sb.append("QUE PENA! VOCÊ ERROU A PALAVRA! (" + String(word) + ")\n")
                fragmentMainBinding.tentativaBt.isEnabled = false
                fragmentMainBinding.tentativaBt.setBackgroundColor(Color.GRAY)
                fragmentMainBinding.reiniciarBt.isEnabled = true

                verificaGameOver(sb)
            }
        }
    }

    private fun verificaGameOver(sb: StringBuilder) {
        if(rodadaAtual == rodadas) {
            showAlertMessage("FIM DAS RODADAS!\n VOCÊ ACERTOU ( $vitorias de $rodadas ) palavras!\n")
            rodadaAtual = 0
            vitorias = 0
            restartGame()
        }
    }

    private fun restartGame () {
        tentativas = 0
        fragmentMainBinding.tvPalavra.text = ""
        fragmentMainBinding.tvLetrasTentativas.text = ""
        fragmentMainBinding.edtLetraTentativa.setText("")

        var nivelStr = "Fácil"
        if(nivel == 2) {
            nivelStr = "Médio"
        }
        if(nivel == 3) {
            nivelStr = "Difícil"
        }
        fragmentMainBinding.tvNivel.text = nivelStr

        fragmentMainBinding.tvQtdRodadas.text = rodadas.toString()
        fragmentMainBinding.tentativaBt.isEnabled = true
        fragmentMainBinding.reiniciarBt.isEnabled = false
        changeHangManPicture()
        startGame()
    }

    private fun startGame () {
        getWordFromApi()
        rodadaAtual++
        fragmentMainBinding.tvRodadaAtual.text = rodadaAtual.toString()
    }

    private fun getWordFromApi() {
        var palavraSorteada = 0
        palavraSorteada = sorteiaPalavra()
        forcaWsViewModel.getPalavra(palavraSorteada)

    }

    private fun sorteiaPalavra(): Int {
        var identificador = Random.nextInt(0, 300)

        if(listaIdentificadoresPalavras.contains(identificador) &&
            !listaIdentificadoresPalavrasJaEscolhidas.contains(identificador)) {
                listaIdentificadoresPalavrasJaEscolhidas.add(identificador)
                return identificador
        }
        else {
            return sorteiaPalavra()
        }
    }

    private fun checkUserLetter() {
        if(fragmentMainBinding.edtLetraTentativa.text.isNotEmpty()) {
            val letter: Char = fragmentMainBinding.edtLetraTentativa.text.elementAt(0)
            val sb = StringBuilder()
            findLetterinWord(letter, sb)
            checkIfWordIsCompleted(sb)
            showAlertMessage(sb.toString())
        }
        else
        {
            showAlertMessage("Digite uma letra!\n");
        }
    }

    private fun findLetterinWord(letter: Char, sb: StringBuilder) {

        if (word.contains(letter))
        {
           for (i in 0..word.size-1) {
               if(word[i].equals(letter)){
                   setLetterInPosition(letter, i);
                   sb.append("Você acertou a letra: $letter , na posição: $i.\n")
               }
           }
        }
        else {
            setLetterInTries(letter)
            sb.append("Na palavra não há a letra: $letter\n")
        }
    }

    private fun setLetterInTries(char: Char) {

        var texto = fragmentMainBinding.tvLetrasTentativas.text.toString()
        if(texto.isEmpty()) {
            texto = char.toString()
        }
        else {
            texto += ", $char"
        }

        tentativas++
        changeHangManPicture()

        fragmentMainBinding.tvLetrasTentativas.setText(texto)
    }

    private fun setLetterInPosition(char: Char, pos: Int) {
        wordAtual.set(pos,char);

        var texto: String = ""
        var loop: Int = wordAtual.size-1

        for (i in 0..loop) {
            if(i != loop) {
                texto += wordAtual[i] + " - "
            }
            else {
                texto += wordAtual[i].toString()
            }
        }

        fragmentMainBinding.tvPalavra.setText(texto)
    }

    private fun changeHangManPicture () {

        when(tentativas) {
            1 -> fragmentMainBinding.ivForca.setImageResource(R.mipmap.ic_hang1)
            2 -> fragmentMainBinding.ivForca.setImageResource(R.mipmap.ic_hang2)
            3 -> fragmentMainBinding.ivForca.setImageResource(R.mipmap.ic_hang3)
            4 -> fragmentMainBinding.ivForca.setImageResource(R.mipmap.ic_hang4)
            5 -> fragmentMainBinding.ivForca.setImageResource(R.mipmap.ic_hang5)
            6 -> fragmentMainBinding.ivForca.setImageResource(R.mipmap.ic_hang6)
            else -> fragmentMainBinding.ivForca.setImageResource(R.mipmap.ic_hang0)
        }

    }

    private fun showAlertMessage(msg: String) {

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Aviso!")
        builder.setMessage(msg)
        builder.setPositiveButton("OK"){_,_ ->

        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }



}