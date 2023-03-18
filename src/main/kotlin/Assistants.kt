package cn.mpsmc

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable


@Serializable
data class RequestObject(
    val model: String,
    val messages: List<Message>
)

@Serializable
data class ReceivedObject(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val usage: Usage,
    val choices: List<Choice>,
)

@Serializable
data class Usage(
    val prompt_tokens: Long,
    val completion_tokens: Long,
    val total_tokens: Long,
)

@Serializable
data class Choice(
    val message: Message,
    val finish_reason: String,
    val index: Long,
)

@Serializable
data class Message(
    val role: String,
    val content: String,
)


class UserDataHandler(private val source: String) {
    init {
        if (Data.lastChatTime[source] != null)
            if (Config.expirationTime != -1 && ((System.currentTimeMillis() / 1000)) - Data.lastChatTime[source]!! >= Config.expirationTime)
                Data.historicalMessages.remove(source)
    }

    val cooldown: Long
        get() = if (Data.lastChatTime[source] != null && Config.cooldown != -1) (Config.cooldown - ((System.currentTimeMillis() / 1000) - Data.lastChatTime[source]!!)) else -1

    fun updateLastChatTime() {
        Data.lastChatTime[source] = System.currentTimeMillis() / 1000
    }

    fun removeFromHistoricalMessages(): Boolean = Data.historicalMessages.remove(source) != null
    fun getHistoricalMessages() = Data.historicalMessages[source] ?: mutableListOf()
    fun addElementToHistoricalMessages(msg: Message) {
        if (Data.historicalMessages[source] != null)
            Data.historicalMessages[source]?.add(msg)
        else
            Data.historicalMessages[source] = mutableListOf(msg)
        val msgs = Data.historicalMessages[source]!!
        var contentBytes = 0
        msgs.forEach {
            contentBytes += it.content.length
        }
        while (contentBytes > Config.maxCharacters) {
            contentBytes -= msgs.removeAt(0).content.length
        }
        Data.historicalMessages[source] = msgs
    }
}


class Client {
    private var client: HttpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json()
        }
        install(HttpTimeout) {
            requestTimeoutMillis = Config.client.requestTimeout
            connectTimeoutMillis = Config.client.connectTimeout
            socketTimeoutMillis = Config.client.requestTimeout + Config.client.connectTimeout
        }
        engine {
            if (Config.client.httpProxyUrl != "")
                proxy = ProxyBuilder.http(Config.client.httpProxyUrl)
            if (Config.client.socksProxyUrl != "")
                proxy = ProxyBuilder.socks(Config.client.socksProxyUrl, Config.client.socksProxyPort)
        }
    }

    fun sendRequest(requestObject: RequestObject): ReceivedObject {
        val rec = runBlocking {
            client.post(Config.openAI.apiUrl) {
                headers {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer ${Config.openAI.token}")
                    }
                }
                contentType(ContentType.Application.Json)
                setBody(requestObject)
            }.body() as ReceivedObject
        }
        client.close()
        return rec
    }
}



