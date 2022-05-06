package br.edu.ifsp.scl.sdm.pa2.projetoforca.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import br.edu.ifsp.scl.sdm.pa2.projetoforca.R
import br.edu.ifsp.scl.sdm.pa2.projetoforca.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val activityMainActivity: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainActivity.root)

        with(supportFragmentManager.beginTransaction()) {
            setReorderingAllowed(true)
            addToBackStack("principal")
            add(R.id.principalFcv, MainFragment(), "MainFragment")
            commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        if(item.itemId == R.id.settingsFragmentNi) {
            //supportFragmentManager?.beginTransaction().replace(R.id.principalFcv, SettingsFragment(), "SettingsFragment").commit()
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                addToBackStack("configurações")
                replace(R.id.principalFcv, SettingsFragment(), "SettingsFragment")
            }
            true
        }
        else
            false

    override fun onBackPressed() {
        Toast.makeText(this, "Você não pode voltar nas jogadas!", Toast.LENGTH_SHORT).show()
    }
}