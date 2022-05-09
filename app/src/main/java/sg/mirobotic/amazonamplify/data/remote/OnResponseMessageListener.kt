package sg.mirobotic.amazonamplify.data.remote

import sg.mirobotic.amazonamplify.data.local.Answer
import sg.mirobotic.amazonamplify.data.local.Conversation

interface OnResponseMessageListener {

    fun onMessage(conversation: Conversation)

    fun onError(msg: String)

}

interface OnResponseAnswerListener {

    fun onMessage(results: ArrayList<Answer>)

    fun onError(msg: String)

}