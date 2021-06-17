/**
 *
 */

package de.lmu.nfcrevision

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import de.lmu.nfcrevision.datahandling.Question
import de.lmu.nfcrevision.datahandling.QuestionDao
import de.lmu.nfcrevision.datahandling.QuestionDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: NfcAdapter

    private lateinit var questionDao: QuestionDao

    private lateinit var questionText: TextView
    private lateinit var answerText: TextView
    private lateinit var showAnswerButton: Button
    private lateinit var nextButton: Button
    private lateinit var instructionText: View

    private lateinit var questionList: List<Question>
    private var questionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // set up the database connection
        questionDao =  QuestionDatabase.getDatabase(application).questionDao()


        // check if the activity was started because of an NDEF_DISCOVERED action
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            handleNDEFIntent(intent)
        }

        // set up the view
        questionText = findViewById(R.id.question_text)
        answerText = findViewById(R.id.answer_text)

        showAnswerButton = findViewById(R.id.show_answer_button)
        showAnswerButton.setOnClickListener {
            answerText.visibility = View.VISIBLE

            // update the time where this question was last seen
            questionList[questionIndex].lastSeen = System.currentTimeMillis()
            lifecycleScope.launch(Dispatchers.IO) {
                questionDao.update(questionList[questionIndex])
            }

            showAnswerButton.visibility = View.GONE
            nextButton.visibility = View.VISIBLE
        }

        nextButton = findViewById(R.id.next_button)
        nextButton.setOnClickListener {

            // proceed to the next question and
            // start from the beginning if the last question was reached
            questionIndex = (questionIndex + 1) % questionList.size
            setNewQuestion()

        }

        instructionText = findViewById(R.id.instruction_text)

        /* uncomment this line to add sample data to the database */
//        populateDatabaseWithSampleData()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        // set up the database connection
        questionDao =  QuestionDatabase.getDatabase(application).questionDao()

        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent?.action) {
            handleNDEFIntent(intent)
        }
    }

    /**
     * Reads messages on a detected NFC tag
     * If a valid location is found, questions for this location are retrieved and shown
     */
    private fun handleNDEFIntent(intent: Intent) {
        intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)?.also { rawMessages ->
            val messages: List<NdefMessage> = rawMessages.map { it as NdefMessage }
            // Process the messages array.
            for (message in messages) {
                for (record in message.records) {
                    val payload = String(record.payload)
                    Log.d("Main", payload)
                    // TODO: adjust to more generic locations, e.g. via mimeType
                    if (payload.startsWith("Loc")) {
                        getQuestionsForLocation(payload)
                    }
                }
            }
        }
    }

    /**
     * Retrieves questions for the specified location from the database
     * And sets the question in the UI
     */
    private fun getQuestionsForLocation(location: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            questionList = questionDao.getQuestions(location)
            if (questionList.isNotEmpty()) {
                runOnUiThread {
                    setNewQuestion()
                }
            }
        }
    }

    /**
     * Sets the next question in the UI and resets the buttons
     */
    private fun setNewQuestion() {
        questionText.text = questionList[questionIndex].question
        answerText.text = questionList[questionIndex].answer
        answerText.visibility = View.GONE
        showAnswerButton.visibility = View.VISIBLE
        nextButton.visibility = View.GONE

        instructionText.visibility = View.GONE
    }

    /**
     * Populates the database with a few sample questions
     * at the locations "Location1" and "Location2"
     * Run this once by uncommenting the call of this method in onCreate when you first start the app
     * Retrieving the questions requires NFC tags that were set up as done in TagWriterActivity
     */
    private fun populateDatabaseWithSampleData() {

        lifecycleScope.launch(Dispatchers.IO) {
            questionDao.insert(arrayListOf(
                Question("What is the statement for printing the line \"Hello World\" in Java code?", "System.out.println(\"Hello World\");", "Location1"),
                Question("What is the result of the expression \"true | false\" in Java?", "true", "Location1"),
                Question("Which of these are primitive data types in Java: byte, int, String, int[], Byte?", "byte and int", "Location1"),
                Question("What does the term \"affordance\" refer to in interaction design?", "Norman: \"The term affordance refers to the perceived and actual properties of the thing, primarily those fundamental properties that determine just how the thing could possibly be used\"", "Location2"),
                Question("What does Murphy's Law state and what does this imply for interaction design?", "Whatever can go wrong, will go wrong – Anticipate what can happen and prepare for (human) error", "Location2"),
                Question("In an experiment where the effect of coffee consumption on productivity is measured, what are the independent and dependent variable?", "Independent variable: coffee consumption (e.g., cups per day)\nDependent variable: productivity (e.g., number of features implemented in a program)", "Location2")
            ))
        }
    }

}