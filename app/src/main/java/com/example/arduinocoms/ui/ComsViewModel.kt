package com.example.arduinocoms.ui

import androidx.lifecycle.ViewModel
import com.example.arduinocoms.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

const val address = "192.168.4.34"
const val port = 2080

class ComsViewModel : ViewModel() {
    val viewModelJob = SupervisorJob()
    val clientScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    val client = Client(address, port)

    private val comVars = mapOf(
        "Alpha" to ComVariable('A'),
        "Beta" to ComVariable('B'),
    )

    private val _uiState = MutableStateFlow(ComsUIState(varMap = comVars.map { (k, v) ->
        k to v.fieldData
    }.toMap()))

    val uiState: StateFlow<ComsUIState> = _uiState.asStateFlow()
    override fun onCleared() {
        super.onCleared()
        runBlocking {
            clientScope.launch {
                client.close()
            }
        }
        viewModelJob.cancel()
    }

    fun startClient() {
        clientScope.launch {
            client.connect()
            if (client.isConnected()) {
                _uiState.update { currentState ->
                    currentState.copy(isConnected = "Connected")
                }
            }
            while (client.isConnected()) {
                val message = client.ReadClient()
                onInput(message)
            }
            _uiState.update { currentState ->
                currentState.copy(isConnected = "Terminated")
            }
        }
    }

    fun onConnectButtonClick() {
        if(!client.isConnected()) {
            startClient()
        }
    }

    fun getLabels(): Set<String> {
        return comVars.keys
    }

    fun updateUIMap() {
        // update the ui state with the new ComVariable values.
        _uiState.update { currentState ->
            currentState.copy(varMap = comVars.map { (k, v) ->
                k to v.fieldData
            }.toMap())
        }
    }

    fun send(com: ComVariable) {
        // for right now just put in outString in UI state
        val outString = "<${com.format()}>"
        _uiState.update { currentState ->
            currentState.copy(outString = outString)
        }
        clientScope.launch {
            client.send(outString)
        }
    }

    fun onValueChange(label: String, newVal: String) {
        // parse the value in the appropriate handler
        comVars[label]?.update(newVal)
        // update the ui state with the new values.
        updateUIMap()
    }

    fun onClick(label: String) {
        // Find the matching CommVariable
        val cv: ComVariable? = comVars[label]
        // if there's a match, then send the command
        cv?.let {send(it)}
    }

    fun onInput(inString: String) {
        if (inString[0] == '<' && inString[inString.lastIndex] == '>'){
            // trim off the start and end markers and pass through the ComVariables
            val pattern = "<+([^<]+?)>".toRegex()
            val matches = pattern.findAll(inString)
            matches.forEach {
                comVars.forEach { (_, comvar) ->
                    comvar.parse(it.groups[1]?.value ?: "")
                }
            }
            // update the ui state
            updateUIMap()
        }
    }

    fun onInputChange(inString: String) {
        _uiState.update { currentState ->
            currentState.copy(inString = inString)
        }
    }

    fun onInputClick() {
        onInput(_uiState.value.inString)
    }
}