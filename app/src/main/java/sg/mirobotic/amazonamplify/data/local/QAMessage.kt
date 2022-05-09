package sg.mirobotic.amazonamplify.data.local

class QAMessage(val type: Type, val text: String) {

    var answer: Answer? = null
    var results: Int = 0

    constructor(text: String, answers: ArrayList<Answer>): this(Type.RECEIVED, text) {
        if (answers.isNotEmpty()) {
            this.answer = answers[0]
            this.results = answers.size
        }
    }

    companion object {

        fun sendMessage(message: String): QAMessage {
            return QAMessage(Type.SENT, message)
        }

        fun receiveMessage(message: String, answers: ArrayList<Answer>): QAMessage {
            return QAMessage(message, answers)
        }

    }

    enum class Type {
        SENT, RECEIVED
    }

}