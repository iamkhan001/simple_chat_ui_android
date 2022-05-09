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
import sg.mirobotic.amazonamplify.adapters.QAAdapter
import sg.mirobotic.amazonamplify.data.local.*
import sg.mirobotic.amazonamplify.data.remote.OnResponseAnswerListener
import sg.mirobotic.amazonamplify.data.remote.OnResponseMessageListener
import sg.mirobotic.amazonamplify.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var messageAdapter: QAAdapter
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

        messageAdapter = QAAdapter(itemClickListener)
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



    }


    private val  onResponseMessageListener = object : OnResponseAnswerListener {

        override fun onMessage(results: ArrayList<Answer>) {
            binding.progressBar.visibility = View.INVISIBLE
            if (results.isNotEmpty()) {
                val answer = results[0]
                model.speak(answer.getReadableText())
                messageAdapter.addMessage(QAMessage.receiveMessage("", results))
            }else {
                val msg = "Sorry! I cannot understand your request.\nPlease try again."
                model.speak(msg)
                messageAdapter.addMessage(QAMessage.receiveMessage(msg, results))
            }
            goToEnd()
            enableViews(true)
        }

        override fun onError(msg: String) {
            binding.progressBar.visibility = View.INVISIBLE
            model.speak(msg)
            messageAdapter.addMessage(QAMessage.receiveMessage(msg, ArrayList()))
            goToEnd()
            enableViews(true)
        }

    }

    private fun updateSentMessage(text: String) {

        messageAdapter.addMessage(QAMessage.sendMessage(text))

        binding.progressBar.visibility = View.VISIBLE
        enableViews(false)
        model.askQuestion(text, onResponseMessageListener)

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