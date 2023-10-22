package edu.put.inf153936


import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import java.util.*

class SynchronizationScreen : AppCompatActivity(), Synchronization.NoticeDialogListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_synchronization_screen)

        val p: ProgressBar = findViewById(R.id.progressBar2)
        p.progress = 0

        findViewById<TextView>(R.id.dateSync).text = ReadFile.SYNCHRONIZATION_DATE

        findViewById<Button>(R.id.synchronization_Button2).setOnClickListener{

            if(!compareDate()) {
                showNoticeDialog()
            }
            else{
                val FILE = ReadFile(applicationContext, null, null, 1)
                FILE.deleteDB("$filesDir")
                FILE.create()
                val sync = SYNCHR()
                val result = sync.start("$filesDir", applicationContext, findViewById(R.id.progressBar2))
                runOnUiThread {
                    Toast.makeText(applicationContext, result, Toast.LENGTH_SHORT).show()
                }
                if (result == "Synchronizacja zakończona") {
                    FILE.close()
                    finish()
                }
                else
                    FILE.deleteDB("$filesDir")
            }

        }

        findViewById<Button>(R.id.back_Button).setOnClickListener {
            finish()
        }
    }

    override fun finish() {
        setResult(Activity.RESULT_OK)
        super.finish()
    }


    private fun compareDate(): Boolean{
        val d1 = Calendar.getInstance().timeInMillis
        val d2 = ReadFile.SYNCHRONIZATION_TIME.toLong()
        return (d1-24*60*60*1000) > d2
    }

    private fun showNoticeDialog() {
        val dialog = Synchronization()
        dialog.show(supportFragmentManager, "NoticeDialogFragment")
    }

    override fun PositiveClick(dialog: DialogFragment) {
        val p: ProgressBar = findViewById(R.id.progressBar2)
        p.visibility = android.view.View.VISIBLE
        Thread {
            try {
                val FILE = ReadFile(applicationContext, null, null, 1)
                FILE.deleteDB("$filesDir")
                FILE.create()
                val sync = SYNCHR()
                val result = sync.start("$filesDir", applicationContext, findViewById(R.id.progressBar2))
                runOnUiThread {
                    Toast.makeText(applicationContext, result, Toast.LENGTH_SHORT).show()
                }
                if (result == "Synchronizacja zakończona") {
                    FILE.close()
                    finish()
                }
                else
                    FILE.deleteDB("$filesDir")
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }.start()
    }

    override fun NegativeClick(dialog: DialogFragment) {
        val p: ProgressBar = findViewById(R.id.progressBar2)
        p.visibility = android.view.View.GONE
    }
}