package com.example.arduinocoms.ui

class ComVariable(
    val matchChar: Char,
    value: String = "0",
) {
    var fieldData: String = value

    fun format(): String {
        return "$matchChar$fieldData"
    }

    fun update(newVal: String) {
        fieldData = newVal
    }
}