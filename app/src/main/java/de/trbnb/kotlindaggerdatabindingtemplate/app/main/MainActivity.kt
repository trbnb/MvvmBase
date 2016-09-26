package de.trbnb.kotlindaggerdatabindingtemplate.app.main

import android.databinding.DataBindingUtil
import android.os.Bundle
import de.trbnb.kotlindaggerdatabindingtemplate.R
import de.trbnb.kotlindaggerdatabindingtemplate.app.app.AppComponent
import de.trbnb.kotlindaggerdatabindingtemplate.base.mvvm.MvvmActivity
import de.trbnb.kotlindaggerdatabindingtemplate.databinding.ActivityMainBinding

class MainActivity : MvvmActivity<MainViewModel, MainActivity>() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override val view: MainActivity
        get() = this

    override fun onViewModelLoaded(viewModel: MainViewModel) {
        super.onViewModelLoaded(viewModel)
        binding.vm = viewModel
    }

    override fun injectDependencies(graph: AppComponent) {
        graph.plus(MainModule()).inject(this)
    }
}
