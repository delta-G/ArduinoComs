package com.example.arduinocoms.ui

data class ComsUIState(
    val isConnected: String = "Not Connected",
    val inString: String = "",
    val outString: String = "output",
    val varMap: Map<String, String>
)
