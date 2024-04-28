package com.example.arduinocoms

import java.io.OutputStream
import java.net.Socket
import java.util.Scanner

class Client(
    val address: String,
    val port: Int
) {

    var connection: Socket? = null
    var connected: Boolean = true

    var reader: Scanner? = null
    var writer: OutputStream? = null


    suspend fun connect(): Unit {
        /* TODO: catch exceptions for no network and no host  */
        connection = Socket(address, port)
        if (connection?.isConnected() == true){
            reader = Scanner(connection?.getInputStream())
            writer= connection?.getOutputStream()
        }
    }

    fun isConnected(): Boolean {
        return (connection?.isConnected() == true)
    }

    suspend fun ReadClient(): String {
        var rv:String = ""
        rv = try {
            reader?.nextLine() ?: "ERROR Null Reader"
        } catch (e: NoSuchElementException) {
            "Exception"
        }
        return rv
    }

    fun send(message: String) {
        val req: String = "<$message>"
        writer?.write(req.toByteArray(charset = Charsets.UTF_8))
    }

    fun close() {
        connection?.close()
        connection = null
        writer = null
        reader = null
    }
}