package de.trbnb.base.mvvm

import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import de.trbnb.apptemplate.BR
import javax.inject.Provider

private const val LOADER_ID = 0

abstract class MvvmActivity<VM : ViewModel> : AppCompatActivity(), LoaderManager.LoaderCallbacks<VM> {

    protected lateinit var binding: ViewDataBinding
    protected var viewModel: VM? = null
        set(value) {
            if(field === value) return

            field = value
            val bindingWasSuccessful = binding.setVariable(viewModelBindingId, value)

            if(!bindingWasSuccessful){
                throw RuntimeException("Unable to set the ViewModel for the variable $viewModelBindingId.")
            }
        }

    private val viewModelBindingId: Int
        get() = BR.vm

    abstract val viewModelProvider: Provider<VM>

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = initBinding()

        initLoader()
    }

    protected abstract fun initBinding(): ViewDataBinding

    private fun initLoader(){
        supportLoaderManager.initLoader(LOADER_ID, null, this)
    }

    override final fun onCreateLoader(id: Int, args: Bundle?): Loader<VM> {
        return ViewModelLoader(this, viewModelProvider)
    }

    override final fun onLoadFinished(loader: Loader<VM>, data: VM) {
        this.viewModel = data
    }

    override final fun onLoaderReset(loader: Loader<VM>?) {
        // nothing to do here
    }

    override fun onDestroy() {
        super.onDestroy()

        if(isFinishing){
            viewModel?.onViewFinishing()
        }
    }
}