package com.dhbw.cicd.tictactoe

import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer
import javax.enterprise.context.ApplicationScoped
import javax.websocket.*
import javax.websocket.server.PathParam
import javax.websocket.server.ServerEndpoint

@kotlinx.serialization.Serializable
data class Message(val playerID: String)

@ServerEndpoint("/connect/{id}")
@ApplicationScoped
class Websocket {

    var sessions: MutableMap<String, Session> = ConcurrentHashMap<String, Session>()

    @OnOpen
    fun onOpen(session: Session, @PathParam("id") id: String) {
        if (sessions.containsKey(id)) {
            session.close(CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "user with this ID is already connected"))
            return
        }
        sessions[id] = session
        println("User $id connected")
    }

    @OnClose
    fun onClose(session: Session?, @PathParam("id") id: String) {
        sessions.remove(id)
        println("User $id no longer connected")
    }

    @OnError
    fun onError(session: Session?, @PathParam("id") id: String, throwable: Throwable) {
        sessions.remove(id)
        println("User $id encountered error: $throwable")
    }

    private val json = Json { ignoreUnknownKeys = true }

    @OnMessage
    fun onMessage(message: String, @PathParam("id") id: String) {
        val msg = json.decodeFromString(Message.serializer(), message)
        if (msg.playerID != "" && sessions.containsKey(msg.playerID)) {
            println("sending message from $id to ${msg.playerID}")
            sessions[msg.playerID]!!.asyncRemote.sendText(message)
        }
    }
}