package sg.mirobotic.amazonamplify.data.local

import com.google.gson.annotations.SerializedName

data class Answer(
    @SerializedName("questions") val questions: ArrayList<String>,
    @SerializedName("answer") val answer: String,
    @SerializedName("source") val source: String,
    @SerializedName("score") val score: Double,
) {
    fun getReadableText(): String = if(answer.length > 300) {
        answer.substring(0, 300)+"..."
    }else {
        answer
    }
}
