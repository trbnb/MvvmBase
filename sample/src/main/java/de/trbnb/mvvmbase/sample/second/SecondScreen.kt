package de.trbnb.mvvmbase.sample.second

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import de.trbnb.mvvmbase.utils.observeAsMutableState
import de.trbnb.mvvmbase.utils.observeAsState

@Composable
fun SecondScreen() {
    val viewModel = hiltNavGraphViewModel<SecondViewModel>()
    val text by viewModel::text.observeAsState()

    SecondScreenTemplate(
        text = text,
        progress = viewModel::progress.observeAsMutableState()
    )
}

@Preview(showSystemUi = true)
@Composable
internal fun SecondScreenTemplate(
    text: String = "Foo bar",
    progress: MutableState<Int> = remember { mutableStateOf(50) }
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = text, Modifier.padding(4.dp))
        Text(text = progress.value.toString(), Modifier.padding(4.dp))
        Slider(
            value = progress.value.toFloat(),
            valueRange = 0f..100f,
            onValueChange = { progress.value = it.toInt() }
        )
    }
}
