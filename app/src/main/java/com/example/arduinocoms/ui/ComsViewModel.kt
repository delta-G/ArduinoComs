package com.example.arduinocoms.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class ComsViewModel : ViewModel() {
    private val comVars = mapOf(
        "Alpha" to ComVariable('A'),
        "Beta" to ComVariable('B'),
    )

    private val _uiState = MutableStateFlow(ComsUIState(varMap = comVars.map { (k, v) ->
        k to v.fieldData
    }.toMap()))

    val uiState: StateFlow<ComsUIState> = _uiState.asStateFlow()

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
        _uiState.update { currentState ->
            currentState.copy(outString = "<${com.format()}>")
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