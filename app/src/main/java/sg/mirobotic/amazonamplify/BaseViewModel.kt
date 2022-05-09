package sg.mirobotic.amazonamplify

import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sg.mirobotic.amazonamplify.data.remote.ApiRepository
import sg.mirobotic.amazonamplify.data.remote.OnResponseAnswerListener
import sg.mirobotic.amazonamplify.data.remote.OnResponseMessageListener
import java.util.*

class BaseViewModel : ViewModel() {

    private lateinit var textToSpeechEngine: TextToSpeech
    private lateinit var startForResult: ActivityResultLauncher<Intent>

    private val apiRepository = ApiRepository.getInstance()

    var intentName = ""
    var dialogType = ""

    fun initial(
        engine: TextToSpeech, launcher: ActivityResultLauncher<Intent>
    ) = viewModelScope.launch {
        textToSpeechEngine = engine
        startForResult = launcher
    }

    fun displaySpeechRecognizer() {
        startForResult.launch(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
        })
    }

    fun speak(text: String?) = viewModelScope.launch{
        textToSpeechEngine.speak(text, TextToSpeech.QUEUE_ADD, null, "")
    }

    fun sendTextMessage(text: String, onResponseMessageListener: OnResponseMessageListener) {
        Log.e(TAG,"sendTextMessage >> $text")
        viewModelScope.launch {
            apiRepository.askQuestion(text, intentName, dialogType, onResponseMessageListener)
        }
    }


    fun askQuestion(text: String, onResponseMessageListener: OnResponseAnswerListener) {
        Log.e(TAG,"askQuestion >> $text")
        viewModelScope.launch {
            apiRepository.askQuestion(text, onResponseMessageListener)
        }
    }

    companion object {

        private const val TAG = "BaseVM"

    }

}