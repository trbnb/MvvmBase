package de.trbnb.mvvmbase.list

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import de.trbnb.mvvmbase.BR
import de.trbnb.mvvmbase.ViewModel

/**
 * [RecyclerView.ViewHolder] implementation for item-ViewModels.
 * Setting the item for this ViewHolder should only happen via [bind] so calling of [ViewModel.onUnbind], [ViewModel.onBind]
 * and [ViewDataBinding.executePendingBindings] is ensured.
 */
class BindingViewHolder<B : ViewDataBinding>(
    val binding: B,
    private val viewModelFieldId: Int = BR.vm
) : RecyclerView.ViewHolder(binding.root) {
    private var viewModel: ViewModel? = null
        set(value) {
            field?.onUnbind()
            field = value
            binding.setVariable(viewModelFieldId, value)
            binding.executePendingBindings()
            value?.onBind()
        }

    fun bind(viewModel: ViewModel) {
        this.viewModel = viewModel
    }
}
