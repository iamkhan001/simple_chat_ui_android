package sg.mirobotic.amazonamplify.data.local

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray

object DataConverter {

    fun toAnswers(obj: JSONArray): ArrayList<Answer> {
        val typeToken = object : TypeToken<ArrayList<Answer>>() {}.type
        return Gson().fromJson(obj.toString(), typeToken)
    }


}