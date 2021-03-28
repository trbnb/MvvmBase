package de.trbnb.apptemplate.main

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import de.trbnb.apptemplate.list.ListActivity
import de.trbnb.apptemplate.second.SecondActivity
import de.trbnb.mvvmbase.MvvmActivity
import de.trbnb.mvvmbase.events.Event
import de.trbnb.mvvmbase.viewmodel.viewModelProviderFactory

class MainActivity : MvvmActivity<MainViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel::textInput.observe { textInput ->
            supportActionBar?.subtitle = textInput
        }
    }

    /**
     * create a new Dialog and show it
     */
    private fun showDialog() {
        AlertDialog.Builder(this)
            .setTitle("Dialog title")
            .setMessage("This is a sample dialog to show how to create a dialog via binding.")
            .setPositiveButton(android.R.string.ok) { d, _ -> d.cancel() }
            .show()
    }

    /**
     * create a new Snackbar and show it
     */
    private fun showSnackbar(text: String) {
        Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG).apply {
            setAction("Hide") { dismiss() }
        }.show()
    }

    override fun onEvent(event: Event) {
        super.onEvent(event)

        when (event) {
            MainEvent.ShowToast -> Toast.makeText(this, "Toast message!", Toast.LENGTH_SHORT).show()
            MainEvent.ShowMainActivityAgainEvent -> startActivity(Intent(this, MainActivity::class.java))
            MainEvent.ShowSecondActivityEvent -> startActivity(Intent(this, SecondActivity::class.java))
            MainEvent.ShowConductorEvent -> {}
            MainEvent.ShowListEvent -> startActivity(Intent(this, ListActivity::class.java))
            MainEvent.ShowDialog -> showDialog()
            is MainEvent.ShowSnackbar -> showSnackbar(event.text)
        }
    }

    override fun getDefaultViewModelProviderFactory() = viewModelProviderFactory(::MainViewModel)
}
