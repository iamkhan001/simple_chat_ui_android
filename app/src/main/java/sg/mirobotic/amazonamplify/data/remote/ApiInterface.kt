package sg.mirobotic.amazonamplify.data.remote

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import sg.mirobotic.amazonamplify.data.local.Content

interface ApiInterface {

    @POST("posb/speak/")
    fun conversation(@Body body: HashMap<String, String>): Call<ResponseBody>


    @POST("posb/ask/")
    fun askQuestion(@Body body: HashMap<String, String>): Call<ResponseBody>

}