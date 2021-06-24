package de.lmu.nfcrevision

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.*
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import de.lmu.nfcrevision.datahandling.Location
import de.lmu.nfcrevision.datahandling.LocationDao
import de.lmu.nfcrevision.datahandling.QuestionDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/* Activate the intent filters for this activity in the manifest to write to new NFC tags */
class TagWriterActivity: AppCompatActivity() {

    private lateinit var writeButton: View
    private lateinit var editLocation: EditText
    private lateinit var locationTitle: View
    private lateinit var initialInstructions: View

    private lateinit var adapter: NfcAdapter

    private lateinit var locationDao: LocationDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_tag_writer)

        writeButton = findViewById(R.id.add_location_button)
        editLocation = findViewById(R.id.enter_location)
        locationTitle = findViewById(R.id.location_title)
        initialInstructions = findViewById(R.id.instruction_text_location)

        val nfcManager = getSystemService(Context.NFC_SERVICE) as NfcManager
        adapter = nfcManager.defaultAdapter

        locationDao = QuestionDatabase.getDatabase(application).locationDao()
    }

    override fun onResume() {
        super.onResume()

        try {
            val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val nfcPendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
            adapter.enableForegroundDispatch(this, nfcPendingIntent, null, null)
        } catch (ex: IllegalStateException) {
            Log.e("Main", "Error enabling NFC foreground dispatch", ex)
        }
    }

    override fun onPause() {
        super.onPause()

        try {
            val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val nfcPendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
            adapter.enableForegroundDispatch(this, nfcPendingIntent, null, null)
        } catch (ex: IllegalStateException) {
            Log.e("Main", "Error enabling NFC foreground dispatch", ex)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        // write to the tag
        val tag = intent?.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        val ndef = Ndef.get(tag)

        writeButton.visibility = View.VISIBLE
        editLocation.visibility = View.VISIBLE
        locationTitle.visibility = View.VISIBLE
        initialInstructions.visibility = View.GONE

        editLocation.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                triggerWriting(ndef)
                true
            }
            else {
                false
            }
        }

        writeButton.setOnClickListener {
            triggerWriting(ndef)
        }
    }

    private fun triggerWriting(ndef: Ndef) {
        if (editLocation.text.toString().isBlank() ) {
            Snackbar.make(editLocation, getString(R.string.location_missing), Snackbar.LENGTH_SHORT).show()
        }
        else{
            try {
                writeMessage(ndef)

                val snackbar = Snackbar.make(writeButton, getString(R.string.location_added), Snackbar.LENGTH_SHORT)
                snackbar.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        onBackPressed()
                        super.onDismissed(transientBottomBar, event)
                    }
                })
                snackbar.show()
            }
            catch(exception: Exception) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(editLocation.windowToken, 0)
                Snackbar.make(writeButton, getString(R.string.connection_error), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun writeMessage(ndef: Ndef) {

        val message = NdefMessage(
            arrayOf(
                NdefRecord.createMime("text/plain", editLocation.text.toString().toByteArray()),
                NdefRecord.createApplicationRecord("de.lmu.nfcrevision")
            )
        )

        ndef.connect()
        ndef.writeNdefMessage(message)


        lifecycleScope.launch(Dispatchers.IO) {
            locationDao.insert(Location(editLocation.text.toString()))
        }

    }

}