package de.trbnb.mvvmbase.databinding.recyclerview

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import de.trbnb.mvvmbase.databinding.BR
import de.trbnb.mvvmbase.databinding.ViewModel

/**
 * [RecyclerView.ViewHolder] implementation for item-ViewModels.
 * Setting the item for this ViewHolder should only happen via [bind] so calling of [ViewModel.onUnbind], [ViewModel.onBind]
 * and [ViewDataBinding.executePendingBindings] is ensured.
 *
 * @param binding Binding containing the view associated with this ViewHolder.
 */
open class BindingViewHolder<out B : ViewDataBinding>(
    val binding: B,
    private val viewModelFieldId: Int = BR.vm
) : RecyclerView.ViewHolder(binding.root) {
    /**
     * Gets the current ViewModel associated with the [binding].
     */
    var viewModel: ViewModel? = null
        private set(value) {
            field?.onUnbind()
            field = value
            binding.setVariable(viewModelFieldId, value)
            binding.executePendingBindings()
            value?.onBind()
        }

    /**
     * Sets the ViewModel as [binding] variable.
     */
    fun bind(viewModel: ViewModel) {
        this.viewModel = viewModel
        onBound(viewModel)
    }

    /**
     * Called when [viewModel] was bound to [binding].
     * The ViewModel with specific type can be accessed via [binding].
     */
    open fun onBound(viewModel: ViewModel) {}
}
