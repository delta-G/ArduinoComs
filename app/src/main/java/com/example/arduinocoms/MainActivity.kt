package com.example.arduinocoms

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arduinocoms.ui.theme.ArduinoComsTheme
import com.example.arduinocoms.ui.ComsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArduinoComsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ComsApp()
                }
            }
        }
    }
}

@Composable
fun ComsApp(
    vModel: ComsViewModel = viewModel(),
    modifier: Modifier = Modifier,
) {
    val uiState by vModel.uiState.collectAsState()
    val labelList = vModel.getLabels()

    Column() {
        Text(
            text = "Comms Test",
            style = MaterialTheme.typography.headlineMedium
        )
        Button(
            onClick = {
                vModel.onConnectButtonClick()
            }
        ) {
            Text(uiState.isConnected)
        }
        EntryWidget(
            value = uiState.inString,
            label = "Input",
            onValueChange = {vModel.onInputChange(it)},
            onClick = { vModel.onInputClick() },
            keyboardOptions = KeyboardOptions.Default
        )
        labelList.forEach { label ->
            EntryWidget(
                value = uiState.varMap[label] ?: "N/A",
                label = label,
                onValueChange = { vModel.onValueChange(label, it) },
                onClick = { vModel.onClick(label) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
            )
        }
        Text(uiState.outString)
    }
}

@Composable
fun EntryWidget(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    onClick: () -> Unit,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier = Modifier
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = value,
            label = { Text(label) },
            singleLine = true,
            keyboardOptions = keyboardOptions,
            onValueChange = {
                onValueChange(it)
            },
            modifier = modifier.padding(10.dp)
        )
        Button(
            onClick = {
                onClick()
            }
        ) {
            Text("Send")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ArduinoComsTheme {
        ComsApp()
    }
}