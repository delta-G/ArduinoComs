package com.example.arduinocoms.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


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
        if (cv != null) {
            _uiState.update { currentState ->
                currentState.copy(outString = "<${cv.format()}>")
            }
        }
    }

    fun onInputChange(inString: String) {
        _uiState.update { currentState ->
            currentState.copy(inString = inString)
        }
    }

    fun onInputClick() {
        val inString = _uiState.value.inString
        if (inString[0] == '<' && inString[inString.lastIndex] == '>'){
            // trim off the start and end markers and pass through the ComVariables
            val inside = inString.substring(1, inString.lastIndex)
            comVars.forEach { (_, comvar) ->
                comvar.parse(inside)
            }
            // update the ui state
            updateUIMap()
        }
    }
}