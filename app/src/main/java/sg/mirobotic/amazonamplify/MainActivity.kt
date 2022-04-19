package sg.mirobotic.amazonamplify

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import sg.mirobotic.amazonamplify.adapters.MessageAdapter
import sg.mirobotic.amazonamplify.data.local.*
import sg.mirobotic.amazonamplify.data.remote.OnResponseMessageListener
import sg.mirobotic.amazonamplify.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var messageAdapter: MessageAdapter
    private val model: BaseViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val spokenText: String? =
                result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    .let { text -> text?.get(0) }
            spokenText?.let {
                if (it.isEmpty()) {
                    return@let
                }
                updateSentMessage(it)
            }
        }
    }

    private val textToSpeechEngine: TextToSpeech by lazy {
        TextToSpeech(this) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        model.initial(textToSpeechEngine, startForResult)

        val itemClickListener = object : ItemClickListener<String> {
            override fun onClick(item: String) {
                updateSentMessage(item)
            }
        }

        messageAdapter = MessageAdapter(itemClickListener)
        binding.list.adapter = messageAdapter

        binding.fabSend.setOnClickListener {
            val text = binding.message.text.toString().trim()

            if (text.isEmpty()) {
                Toast.makeText(this, "Please enter message", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.message.setText("")
            updateSentMessage(text)
        }

        binding.fabMic.setOnClickListener {
            model.displaySpeechRecognizer()
        }


        val message1 = Message.receiveMessage("IMPORTANT NOTICE: To support safe distancing, please visit branches only when necessary. Learn More.\n" +
                "\n" +
                "Check out our digital services below.\n" +
                "\n" +
                "What can I help you with?")
        messageAdapter.addMessage(message1)

        val cards = ArrayList<Content.Card>()
        cards.add(
            Content.Card(
                "Transaction",
                arrayListOf(
                    Content.Button.getButton("Cash Withdraw"),
                    Content.Button.getButton("Cash Deposit"),
                    Content.Button.getButton("Funds Transfer")
                )
            )
        )
        cards.add(
            Content.Card(
                "Account",
                arrayListOf(
                    Content.Button.getButton("Open account"),
                    Content.Button.getButton("Statements"),
                    Content.Button.getButton("Balance Inquiry")
                )
            )
        )
        cards.add(
            Content.Card(
                "Loan",
                arrayListOf(
                    Content.Button.getButton("Home Loan"),
                    Content.Button.getButton("Personal Loan"),
                    Content.Button.getButton("Car Loan")
                )
            )
        )
        messageAdapter.addMessage(Message.receiveMessage("", false, cards))

    }

    private val  onResponseMessageListener = object : OnResponseMessageListener {

        override fun onMessage(conversation: Conversation) {
            binding.progressBar.visibility = View.INVISIBLE
            model.intentName = conversation.state?.intent?.name ?: ""

            val showYesNo = conversation.state?.dialogAction?.type == DIALOG_CONFIRM && model.intentName != INTENT_WELCOME

            conversation.messages?.forEach {
                if (it.contentType == "PlainText") {
                    model.speak(it.content)
                    messageAdapter.addMessage(Message.receiveMessage(it.content ?: "", showYesNo))
                }else if (it.contentType == "ImageResponseCard") {
                    it.card?.let { card ->
                        model.speak(card.title)
                        messageAdapter.addMessage(Message.receiveMessage(card.title, showYesNo, arrayListOf(card)))
                    }
                }
            }
            Log.e(TAG,"Intent > ${model.intentName}")
            goToEnd()
            enableViews(true)
        }

        override fun onError(msg: String) {
            binding.progressBar.visibility = View.INVISIBLE
            messageAdapter.addMessage(Message.receiveMessage(msg))
            goToEnd()
            enableViews(true)
        }

    }

    private fun updateSentMessage(text: String) {

        messageAdapter.addMessage(Message.sendMessage(text))

        binding.progressBar.visibility = View.VISIBLE
        enableViews(false)
        model.sendTextMessage(text, onResponseMessageListener)

        goToEnd()
    }

    private fun enableViews(enable: Boolean) {
        binding.fabSend.isEnabled = enable
        binding.fabMic.isEnabled = enable
        binding.message.isEnabled = enable
    }

    private fun goToEnd() {
        runOnUiThread {
            if (binding.list.isComputingLayout.not()) {
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    binding.list.post {
                        binding.list.postDelayed({
                            binding.scrollView.smoothScrollTo(
                                0,
                                binding.scrollView.getChildAt(0).height
                            )
                        }, 300)
                    }
                } else {
                    binding.list.postDelayed({
                        binding.scrollView.smoothScrollTo(
                            0,
                            binding.scrollView.getChildAt(0).height
                        )
                    }, 300)
                }
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }

}