package sg.mirobotic.amazonamplify.data.remote

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @POST("posb/speak/")
    fun conversation(@Body body: HashMap<String, String>): Call<ResponseBody>

}