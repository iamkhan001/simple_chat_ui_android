package sg.mirobotic.amazonamplify.data.local

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

class Conversation {

    @SerializedName("data")
    var messages: ArrayList<Content>? = null

    @SerializedName("state")
    var state: State? = null

    companion object {
        fun getConversation(msg: String, state: State? = null): Conversation {
            val conversation = Conversation()
            conversation.state = state

            val content = Content()
            content.contentType = "PlainText"
            content.content = msg
            conversation.messages = arrayListOf(content)
            return conversation
        }
    }

}

class Content {

    @SerializedName("contentType")
    var contentType: String = ""

    @SerializedName("content")
    var content: String? = null

    @SerializedName("imageResponseCard")
    val card: Card? = null

    class Card() {

        @SerializedName("title")
        val title: String = ""

        @SerializedName("buttons")
        var options: ArrayList<Button>? = null

        constructor(title: String, options: ArrayList<Button>): this() {
            this.options = options
        }

    }

    class Button {

        @SerializedName("text")
        var text: String = ""

        @SerializedName("value")
        var value: String = ""

        companion object {
            fun getButton(text: String): Button {
                val button = Button()
                button.text = text
                button.value = text
                return button
            }
        }
    }

}

class State {

    @SerializedName("name")
    val originatingRequestId: String = ""

    @SerializedName("intent")
    val intent: Intent? = null

    @SerializedName("dialogAction")
    val dialogAction: DialogAction? = null

    class Intent {
        @SerializedName("name")
        val name: String = ""
        @SerializedName("state")
        val state: String = ""

        @SerializedName("slots")
        val slots: JsonObject = JsonObject()
    }

    class DialogAction {
        @SerializedName("type")
        val type: String = ""
    }
}