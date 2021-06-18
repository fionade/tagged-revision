package de.lmu.nfcrevision

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import de.lmu.nfcrevision.datahandling.Question
import de.lmu.nfcrevision.datahandling.QuestionDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuestionEditorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setting up the layout
        setContentView(R.layout.activity_question_editor)

        val submitButton = findViewById<Button>(R.id.add_question_button)

        val questionText = findViewById<EditText>(R.id.enter_question)
        val answerText = findViewById<EditText>(R.id.enter_answer)

        val locationOptions = findViewById<RadioGroup>(R.id.location_options)

        val questionDao =  QuestionDatabase.getDatabase(application).questionDao()

        val currentActivity = this

        // retrieving all locations that already have questions
        // TODO: add new locations!
        lifecycleScope.launch(Dispatchers.IO) {
            val locations = questionDao.getAllLocations()
            runOnUiThread {
                for (location in locations) {
                    val option = RadioButton(currentActivity)
                    option.text = location
                    locationOptions.addView(option)
                }
            }
        }


        submitButton.setOnClickListener {
            if (questionText.text.toString().isBlank() || answerText.text.toString().isBlank()) {
                Snackbar.make(submitButton, getString(R.string.question_or_answer_missing), Snackbar.LENGTH_LONG).show()
            }
            else if (locationOptions.checkedRadioButtonId == -1) {
                Snackbar.make(submitButton, getString(R.string.no_location_selected), Snackbar.LENGTH_LONG).show()
            }
            else {

                val selectedRadioButton = findViewById<RadioButton>(locationOptions.checkedRadioButtonId)

                lifecycleScope.launch(Dispatchers.IO) {
                    questionDao.insert(Question(questionText.text.toString(), answerText.text.toString(), selectedRadioButton.text.toString()))
                }

                val snackbar = Snackbar.make(submitButton, getString(R.string.question_added), Snackbar.LENGTH_SHORT)
                snackbar.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        onBackPressed()
                        super.onDismissed(transientBottomBar, event)
                    }
                })
                snackbar.show()
            }
        }
    }
}