package de.trbnb.kotlindaggerdatabindingtemplate.base.mvvm

import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import de.trbnb.kotlindaggerdatabindingtemplate.app.app.App
import de.trbnb.kotlindaggerdatabindingtemplate.app.app.AppComponent
import javax.inject.Inject
import javax.inject.Provider

private const val LOADER_ID = 0

abstract class MvvmActivity<VM : ViewModel<V>, V : MvvmView> : AppCompatActivity(), MvvmView, LoaderManager.LoaderCallbacks<VM> {

    protected lateinit var viewModel: VM

    @Inject
    lateinit var viewModelLoaderProvider: Provider<ViewModelLoader<VM>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        injectDependencies(App.appComponent)
        initLoader()
    }

    protected abstract val view: V

    protected abstract fun injectDependencies(graph: AppComponent)

    protected open fun onViewModelLoaded(viewModel: VM){
        this.viewModel = viewModel
        viewModel.view = view
    }

    private fun initLoader(){
        supportLoaderManager.initLoader(LOADER_ID, null, this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<VM> {
        return viewModelLoaderProvider.get()
    }

    override fun onLoadFinished(loader: Loader<VM>, data: VM) {
        onViewModelLoaded(data)
    }

    override fun onLoaderReset(loader: Loader<VM>?) {

    }
}