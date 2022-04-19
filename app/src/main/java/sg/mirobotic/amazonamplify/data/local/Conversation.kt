package sg.mirobotic.amazonamplify.data.local

import com.google.gson.annotations.SerializedName

class Conversation {

    @SerializedName("data")
    val messages: ArrayList<Content>? = null

    @SerializedName("state")
    val state: State? = null

}

class Content {

    @SerializedName("contentType")
    val contentType: String = ""

    @SerializedName("content")
    val content: String? = null

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
    }

    class DialogAction {
        @SerializedName("type")
        val type: String = ""
    }
}