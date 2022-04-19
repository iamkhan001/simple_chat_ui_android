package sg.mirobotic.amazonamplify.data.local

class Message(val type: Type, val text: String) {

    var cards = ArrayList<Content.Card>()
    var showConfirmButtons = false

    constructor(text: String, showConfirmButtons: Boolean, cards: ArrayList<Content.Card>): this(Type.RECEIVED, text) {
        this.cards = cards
        this.showConfirmButtons = showConfirmButtons
    }

    companion object {

        fun sendMessage(message: String): Message {
            return Message(Type.SENT, message)
        }

        fun receiveMessage(message: String, showConfirmButtons: Boolean = false, cards: ArrayList<Content.Card> = ArrayList()): Message {
            return Message(message, showConfirmButtons, cards)
        }

    }

    enum class Type {
        SENT, RECEIVED
    }

}