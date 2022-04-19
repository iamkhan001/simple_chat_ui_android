package sg.mirobotic.amazonamplify.data.remote

import android.util.Log
import com.google.gson.Gson
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sg.mirobotic.amazonamplify.data.local.Conversation
import sg.mirobotic.amazonamplify.data.local.DIALOG_CLOSE
import sg.mirobotic.amazonamplify.data.local.INTENT_WELCOME
import java.util.*
import kotlin.collections.HashMap

class ApiRepository() {

    companion object {

        private const val TAG = "WebRepository"

        private var webRepository: ApiRepository? = null

        fun getInstance(): ApiRepository {

            if (webRepository == null) {
                webRepository = ApiRepository()
            }

            return webRepository!!
        }

    }


    private var sessionId = UUID.randomUUID()

    private fun getCurrentSessionId(intentName: String, dialogType: String): String {

        if (intentName == INTENT_WELCOME || dialogType == DIALOG_CLOSE) {
            sessionId = UUID.randomUUID()
        }

        return sessionId.toString()
    }

    private val apiInterfaceSecure = ApiClientSecure().getRetrofitInstance().create(ApiInterface::class.java)

    fun askQuestion(text: String, intentName: String, dialogType: String, onResponseMessageListener: OnResponseMessageListener) {

        val map = HashMap<String, String>()
        map["sessionId"] = getCurrentSessionId(intentName, dialogType)
        map["text"] = text

        val call = apiInterfaceSecure.conversation(map)

        call.enqueue(object : Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(TAG, "Error: ${t.printStackTrace()}")
                onResponseMessageListener.onError("No internet access")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                val r = response.body()?.string()
                Log.e(TAG,"res: $r")

                if (response.code() == 200 && response.body() != null) {
                    try {
                        val obj = JSONObject(r!!)
                        val conversation: Conversation = Gson().fromJson(obj.toString(), Conversation::class.java)
                        onResponseMessageListener.onMessage(conversation)
                        return
                    }catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return
                }
                var error = "Invalid username or password"
                try {
                    if (response.errorBody() != null) {
                        val obj = JSONObject(response.errorBody()!!.string())
                        if (obj.has("msg")) {
                            error = obj.getString("msg")
                        }
                    }
                }catch (e: Exception) {
                    e.printStackTrace()
                }
                onResponseMessageListener.onError(error)
            }
        })

    }
}