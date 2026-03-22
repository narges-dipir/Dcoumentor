package app.narges.documentor.data.articles.fake

import app.narges.documentor.data.articles.model.CreateArticleRequest
import app.narges.documentor.data.articles.model.UpdateArticleRequest
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer

internal class FakeArticleApiInterceptor(
    private val backend: FakeArticleBackend,
    private val gson: Gson,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val pathSegments = request.url.pathSegments

        return try {
            when {
                request.method == "GET" && pathSegments == listOf("articles") -> {
                    val cursor = request.url.queryParameter("cursor")
                    val limit = request.url.queryParameter("limit")?.toIntOrNull() ?: 20
                    val response = backend.getArticles(cursor = cursor, limit = limit)
                    jsonResponse(request, 200, gson.toJson(response))
                }

                request.method == "GET" && pathSegments.size == 2 && pathSegments[0] == "articles" -> {
                    val articleNumber = pathSegments[1].toIntOrNull()
                        ?: return jsonResponse(request, 400, errorJson("invalid article number"))
                    val response = backend.getArticle(articleNumber)
                    jsonResponse(request, 200, gson.toJson(response))
                }

                request.method == "POST" && pathSegments == listOf("articles") -> {
                    val body = request.body?.toUtf8String().orEmpty()
                    val parsed = gson.fromJson(body, CreateArticleRequest::class.java)
                        ?: return jsonResponse(request, 400, errorJson("invalid request body"))
                    val response = backend.createArticle(parsed)
                    jsonResponse(request, 201, gson.toJson(response))
                }

                request.method == "PUT" && pathSegments.size == 2 && pathSegments[0] == "articles" -> {
                    val articleNumber = pathSegments[1].toIntOrNull()
                        ?: return jsonResponse(request, 400, errorJson("invalid article number"))
                    val body = request.body?.toUtf8String().orEmpty()
                    val parsed = gson.fromJson(body, UpdateArticleRequest::class.java)
                        ?: return jsonResponse(request, 400, errorJson("invalid request body"))
                    val response = backend.updateArticle(articleNumber, parsed)
                    jsonResponse(request, 200, gson.toJson(response))
                }

                else -> jsonResponse(request, 404, errorJson("route not found"))
            }
        } catch (e: IllegalArgumentException) {
            jsonResponse(request, 400, errorJson(e.message ?: "bad request"))
        } catch (e: NoSuchElementException) {
            jsonResponse(request, 404, errorJson(e.message ?: "not found"))
        }
    }

    private fun errorJson(message: String): String = gson.toJson(mapOf("message" to message))

    private fun jsonResponse(request: Request, code: Int, body: String): Response {
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message("Fake API")
            .body(body.toResponseBody("application/json".toMediaType()))
            .build()
    }
}

private fun RequestBody.toUtf8String(): String {
    val buffer = Buffer()
    writeTo(buffer)
    return buffer.readUtf8()
}
