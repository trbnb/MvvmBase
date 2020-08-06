package de.trbnb.mvvmbase.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.trbnb.mvvmbase.ViewModel

/**
 * Basic [ListAdapter] implementation for ViewModel lists.
 *
 * Uses referential equality for [DiffUtil.ItemCallback.areContentsTheSame]
 * and [ViewModel.equals] for [DiffUtil.ItemCallback.areItemsTheSame].
 *
 * @param layoutId Layout resource ID of the item layout.
 */
open class BindingListAdapter<VM : ViewModel, B : ViewDataBinding>(val layoutId: Int) : ListAdapter<VM, BindingViewHolder<B>>(
    object : DiffUtil.ItemCallback<VM>() {
        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: VM, newItem: VM) = oldItem == newItem
        override fun areItemsTheSame(oldItem: VM, newItem: VM) = oldItem === newItem
    }
) {
    override fun onBindViewHolder(holder: BindingViewHolder<B>, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BindingViewHolder<B>(
        DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutId, parent, false)
    )
}

/**
 * Binding adapter function to make use of [BindingListAdapter].
 */
@BindingAdapter("items", "itemLayout")
fun RecyclerView.setItems(items: List<ViewModel>, itemLayout: Int) {
    @Suppress("UNCHECKED_CAST")
    (adapter as? BindingListAdapter<ViewModel, ViewDataBinding> ?: BindingListAdapter(itemLayout)).submitList(items)
}
