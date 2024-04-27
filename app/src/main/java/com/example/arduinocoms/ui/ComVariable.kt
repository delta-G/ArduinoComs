package com.example.arduinocoms.ui

class ComVariable(
    val matchChar: Char,
    value: String = "0",
) {
    var fieldData: String = value

    fun format(): String {
        return "$matchChar$fieldData"
    }

    fun parse(str: String) {
        if (str[0] == matchChar) {
            if(str.length > 1) {
                update(str.substring(1))
            }
        }
    }

    fun update(newVal: String) {
        fieldData = newVal
    }
}