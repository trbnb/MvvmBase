package de.trbnb.mvvmbase.sample.list

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import de.trbnb.mvvmbase.sample.app.AppTheme
import de.trbnb.mvvmbase.utils.observeAsState

@Composable
fun ListScreen() {
    val viewModel = hiltViewModel<ListViewModel>()
    ListScreenTemplate(viewModel::items.observeAsState())
}

@Preview
@Composable
fun ListScreenTemplate(
    items: State<List<Item>> = mutableStateOf(listOf(
        Item(text = "One"),
        Item(text = "Two"),
        Item(text = "Three"),
        Item(text = "Four"),
        Item(text = "Five"),
    ))
) = AppTheme {
    LazyColumn {
        items(items.value, { it.id }) {
            Text(it.text)
        }
    }
}

