package de.trbnb.mvvmbase

import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import javax.inject.Provider

/**
 * The ID that is used for the [ViewModelLoader].
 * Since the IDs for Loaders are Activity-specific there is no need to generate one dynamically.
 * A constant value is sufficient.
 */
private const val LOADER_ID = LoaderIdGenerator.ACTIVITY_ID

/**
 * Base class for Activities that serve as view within an MVVM structure.
 *
 * It automatically creates the binding and sets the view model as that bindings parameter.
 * Note that the parameter has to have to name "vm".
 *
 * The view model will be loaded by the [ViewModelLoader], thus making sure it survives the Activitys
 * lifecycle. If an Activity is created for the first time the Loader will instantiate the view model
 * via the [viewModelProvider]. This [Provider] can either be implemented manually or injected by
 * Dagger.
 *
 * @param[VM] The type of the specific [ViewModel] implementation for this Activity.
 * @param[B] The type of the specific [ViewDataBinding] implementation for this Activity.
 */
abstract class MvvmBindingActivity<VM : ViewModel, B : ViewDataBinding> : AppCompatActivity(), LoaderManager.LoaderCallbacks<VM> {

    /**
     * The [ViewDataBinding] implementation for a specific layout.
     * Will only be set in [onCreate].
     */
    protected lateinit var binding: B
        private set

    /**
     * The [ViewModel] that is used for data binding.
     */
    protected var viewModel: VM? = null
        set(value) {
            if(field === value) return

            field?.onUnbind()
            field?.removeOnPropertyChangedCallback(viewModelObserver)

            field = value

            if(value != null) {
                val bindingWasSuccessful = binding.setVariable(viewModelBindingId, value)

                if (!bindingWasSuccessful) {
                    throw RuntimeException("Unable to set the ViewModel for the variable $viewModelBindingId.")
                }

                onViewModelLoaded(value)
                value.addOnPropertyChangedCallback(viewModelObserver)
                value.onBind()
            }
        }

    /**
     * The [de.trbnb.mvvmbase.BR] value that is used as parameter for the view model in the binding.
     * Is always [de.trbnb.mvvmbase.BR.vm].
     */
    private val viewModelBindingId: Int
        get() = BR.vm

    /**
     * The layout resource ID for this Activity.
     * Is used in [onCreate] to create the [ViewDataBinding].
     */
    protected abstract val layoutId: Int

    /**
     * The [Provider] implementation that is used by the [ViewModelLoader] if a new view model
     * has to be instantiated. Can either be implemented manually or injected by Dagger.
     */
    abstract val viewModelProvider: Provider<VM>

    /**
     * Callback implementation that delegates the parametes to [onViewModelPropertyChanged].
     */
    private val viewModelObserver = object : Observable.OnPropertyChangedCallback(){
        @Suppress("UNCHECKED_CAST")
        override fun onPropertyChanged(sender: Observable, fieldId: Int) {
            onViewModelPropertyChanged(sender as VM, fieldId)
        }
    }

    /**
     * Called by the lifecycle.
     * Creates the [ViewDataBinding] and initializes the [ViewModelLoader].
     */
    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = initBinding()

        initLoader()
    }

    /**
     * Creates the [ViewDataBinding].
     *
     * @return The new [ViewDataBinding] instance that fits this Activity.
     */
    private fun initBinding(): B = DataBindingUtil.setContentView(this, layoutId)

    // region Loader
    /**
     * Initializes the Loader mechanism.
     */
    private fun initLoader(){
        supportLoaderManager.initLoader(LOADER_ID, null, this)
    }

    /**
     * Called by the Loader API when a new [ViewModelLoader] instance is needed.
     *
     * @param[id] The ID whose loader is to be created. Will be [LOADER_ID].
     * @param[args] Any arguments supplied by the caller.
     *
     * @return A new [ViewModelLoader] instance.
     */
    override final fun onCreateLoader(id: Int, args: Bundle?): Loader<VM> {
        return ViewModelLoader(this, viewModelProvider)
    }

    /**
     * Called by the Loader API when the [ViewModelLoader] delivers a view model via [Loader.deliverResult].
     * Sets the loaded [ViewModel] instance as [viewModel].
     *
     * @param[loader] The [ViewModelLoader] that delivered the view model.
     * @param[data] The delivered [ViewModel] instance.
     */
    override final fun onLoadFinished(loader: Loader<VM>, data: VM) {
        this.viewModel = data
    }

    /**
     * Called by the Loader API when the [ViewModelLoader] will be reset.
     * Nothing happens here because we handle this already in the ViewModelLoader implementation.
     *
     * @param[loader] The Loader that is reset.
     */
    override final fun onLoaderReset(loader: Loader<VM>?) { }
    //endregion

    /**
     * Called when the view model has been delivered by the [ViewModelLoader] and is set as [viewModel].
     *
     * @param[viewModel] The [ViewModel] instance that was loaded.
     */
    protected open fun onViewModelLoaded(viewModel: VM) { }

    /**
     * Called when the view model notifies listeners that a property has changed.
     *
     * @param[viewModel] The [ViewModel] instance whose property has changed.
     * @param[fieldId] The ID of the field in the BR file that indicates which property in the view model has changed.
     */
    protected open fun onViewModelPropertyChanged(viewModel: VM, fieldId: Int) { }

    /**
     * Called by the lifecycle.
     * Removes the view model callback.
     * If the Activity is finishing the view model is notified.
     */
    override fun onDestroy() {
        super.onDestroy()

        viewModel = null
    }
}