package repository

import android.util.Log
import network.GeminiRequest
import network.Content
import network.Part
import network.RetrofitClient
import retrofit2.HttpException

class GeminiRepository(private val apiKey: String) {
    
    suspend fun generateContent(prompt: String): String? {
        if (apiKey.isBlank() || apiKey == "unused") {
            Log.e("GeminiRepository", "ERRO: Chave de API não encontrada no BuildConfig!")
            return "Erro: Chave de API não configurada no arquivo .env"
        }

        val request = GeminiRequest(
            contents = listOf(
                Content(role = "user", parts = listOf(Part(text = prompt)))
            )
        )
        
        return try {
            val response = RetrofitClient.geminiService.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "A IA não retornou um roteiro. Tente mudar os interesses."
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("GeminiRepository", "Erro HTTP ${e.code()}: $errorBody")
            
            when (e.code()) {
                403 -> "Erro 403: Chave de API inválida ou sem permissão. Verifique se a chave no .env começa com 'AIza'."
                404 -> "Erro 404: Modelo não encontrado. Verifique o endpoint no GeminiService."
                429 -> "Erro 429: Limite de uso da IA excedido. Aguarde um momento."
                else -> "Erro na IA (${e.code()}): Verifique o Logcat para detalhes."
            }
        } catch (e: Exception) {
            Log.e("GeminiRepository", "Falha de conexão", e)
            "Falha na rede: Verifique sua conexão com a internet."
        }
    }

    suspend fun generateItinerary(destino: String, periodo: String, interesses: String): String? {
        val prompt = "Gere um roteiro detalhado para $destino por $periodo focado em $interesses."
        return generateContent(prompt)
    }
}
