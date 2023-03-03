package cn.mpsmc

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

@Serializable
data class Messages(
    val role: String,
    val content: String
)

@Serializable
data class RequestObject(
    val model: String,
    val messages: List<Messages>
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

fun checkCoolDown(id: String): Long =
    if (Data.lastChatTime[id] != null && Config.cooldown != -1) (System.currentTimeMillis() / 1000) - Data.lastChatTime[id]!! - Config.cooldown else 1

object Client {
    private lateinit var client: HttpClient
    fun load() {
        client = HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json()
            }
            engine {
                if (Config.httpProxyUrl != "")
                    proxy = ProxyBuilder.http(Config.httpProxyUrl)
                if (Config.socksProxyUrl != "")
                    proxy = ProxyBuilder.socks(Config.socksProxyUrl, Config.socksProxyPort)
            }
        }
    }

    fun sendRequest(requestObject: RequestObject): ReceivedObject {
        return runBlocking {
            return@runBlocking client.post(Config.apiUrl) {
                headers {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer ${Config.token}")
                    }
                }
                contentType(ContentType.Application.Json)
                setBody(requestObject)
            }.body() as ReceivedObject
        }
    }

    fun close() = client.close()
}



