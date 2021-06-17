package de.lmu.nfcrevision

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.*
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

/* Activate the intent filters for this activity in the manifest to write to new NFC tags */
class TagWriterActivity: AppCompatActivity() {

    private lateinit var adapter: NfcAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_tag_writer)

        val nfcManager = getSystemService(Context.NFC_SERVICE) as NfcManager
        adapter = nfcManager.defaultAdapter
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
        writeMessage(ndef)
    }

    private fun writeMessage(ndef: Ndef) {

        val message = NdefMessage(
            arrayOf(
                NdefRecord.createMime("text/plain", "Location1".toByteArray()), // adjust the location you want by replacing "Location1"
                NdefRecord.createApplicationRecord("de.lmu.nfcrevision")
            )
        )

        ndef.connect()
        ndef.writeNdefMessage(message)

    }

}