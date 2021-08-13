package de.trbnb.mvvmbase.sample.main

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import de.trbnb.mvvmbase.commands.Command
import de.trbnb.mvvmbase.commands.invoke
import de.trbnb.mvvmbase.commands.simpleCommand
import de.trbnb.mvvmbase.compose.observeAsMutableState
import de.trbnb.mvvmbase.compose.observeAsState
import de.trbnb.mvvmbase.compose.setter
import de.trbnb.mvvmbase.events.Event
import de.trbnb.mvvmbase.events.OnEvent
import de.trbnb.mvvmbase.sample.app.AppTheme
import kotlinx.coroutines.launch

@Preview
@Composable
fun MainScreenTemplate(
    input: MutableState<String> = remember { mutableStateOf("Foo") },
    showToastCommand: Command<Unit, Unit> = simpleCommand { },
    navigateToSecondScreen: () -> Unit = {},
    navigateToListScreen: () -> Unit = {}
) = AppTheme {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar {
                Column(Modifier.padding(8.dp, 0.dp)) {
                    Text(
                        text = "Example",
                        style = MaterialTheme.typography.h6
                    )
                    Text(
                        text = input.value,
                        style = MaterialTheme.typography.subtitle1
                    )
                }
            }
        },
        content = {
            Column(
                Modifier
                    .fillMaxHeight()
                    .padding(it),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
            ) {
                TextField(
                    value = input.value,
                    onValueChange = input.setter,
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                Button(enabled = showToastCommand.isEnabled, onClick = showToastCommand::invoke) { Text(text = "Show Toast") }
                Button(onClick = {
                    coroutineScope.launch {
                        scaffoldState.snackbarHostState.showSnackbar("Snackbar example!")
                    }
                }) { Text(text = "Show Snackbar") }
                Button(onClick = navigateToSecondScreen) { Text(text = "To second screen") }
                Button(navigateToListScreen) { Text("Show list screen") }
            }
        }
    )
}

@Composable
fun OnEvent(event: Event?) {
    when (event) {
        is MainEvent.ShowToast -> Toast.makeText(LocalContext.current, event.text, Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun MainScreen(navController: NavController) {
    val viewModel = viewModel<MainViewModel>()
    val inputState = viewModel::textInput.observeAsMutableState()
    viewModel.showToastCommand::isEnabled.observeAsState()
    MainScreenTemplate(
        inputState,
        showToastCommand = viewModel.showToastCommand,
        navigateToSecondScreen = { navController.navigate("second") },
        navigateToListScreen = { navController.navigate("list") }
    )
    viewModel.OnEvent { OnEvent(it) }
}
