package sg.mirobotic.amazonamplify.data.remote

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sg.mirobotic.amazonamplify.data.local.*
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

    private fun renewSession() {
            sessionId = UUID.randomUUID()
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
                        Log.e(TAG,"Slots > = ${conversation.state?.intent?.slots}")

                        val intent = conversation.state?.intent?.name ?: ""
                        val slots = conversation.state?.intent?.slots ?: JsonObject()

                        try {
                            when(intent) {

                                INTENT_ACCOUNT -> {

                                    if (slots.has(SLOT_EXISTING_ACCOUNT)) {
                                        val value = slots.getAsJsonObject(SLOT_EXISTING_ACCOUNT).getAsJsonObject("value").getAsJsonArray("resolvedValues").get(0).asString.lowercase()

                                        Log.e(TAG,"$INTENT_ACCOUNT Value > $value")

                                        if (value == "yes") {
                                            renewSession()
                                            askQuestion("I have account with DBS", intentName, dialogType, onResponseMessageListener)
                                            return
                                        }else if (value == "no") {
                                            renewSession()
                                            askQuestion("open new account", intentName, dialogType, onResponseMessageListener)
                                            return
                                        }

                                    }
                                }

                                INTENT_ACCOUNT_NEW -> {
                                    if (slots.has(SLOT_CITIZEN)) {
                                        val value = slots.getAsJsonObject(SLOT_CITIZEN).getAsJsonObject("value").getAsJsonArray("resolvedValues").get(0).asString

                                        Log.e(TAG,"$SLOT_CITIZEN Value > $value")
                                        when(value.lowercase()) {
                                            "ep" -> {
                                                renewSession()
                                                askQuestion("Foreigner working in singapore", intentName, dialogType, onResponseMessageListener)
                                                return
                                            }
                                        }

                                    }
                                }
                                INTENT_ACCOUNT_OLD -> {
                                    if (slots.has(SLOT_INTERNET_BANKING)) {
                                        val value = slots.getAsJsonObject(SLOT_INTERNET_BANKING).getAsJsonObject("value").getAsJsonArray("resolvedValues").get(0).asString

                                        Log.e(TAG,"$SLOT_INTERNET_BANKING Value > $value")

                                        if (value.lowercase() == "no") {
                                            onResponseMessageListener.onMessage(Conversation.getConversation("Please reach out to our executive, he will assist you to open without internet banking account."))
                                            return
                                        }
                                    }
                                }

                            }
                        }catch (e: Exception) {
                            e.printStackTrace()
                        }

                        onResponseMessageListener.onMessage(conversation)
                        return
                    }catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return
                }
                var error = "Sorry! I cannot understand your request.\nPlease try again."
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