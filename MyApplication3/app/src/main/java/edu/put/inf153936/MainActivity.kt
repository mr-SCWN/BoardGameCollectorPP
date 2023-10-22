package edu.put.inf153936

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import java.io.File
import java.io.FileNotFoundException
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity(), Delete.NoticeDialogListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val FILE = ReadFile(this, null, null, 1)
        FILE.create()
        val configuration = !File("$filesDir/user.txt").exists()

        if (configuration) {
            val intent = Intent(this, Config::class.java)
            activityLauncher.launch(intent)
        }
        try {
            FILE.readInfo("$filesDir")
        }
        catch (e: FileNotFoundException){
            ReadFile.USER_NAME = ""
            ReadFile.GAMES_COUNT = 0
            ReadFile.EXTENSIONS_COUNT = 0
            ReadFile.SYNCHRONIZATION_DATE = ""
            ReadFile.SYNCHRONIZATION_TIME = ""

        }
        findViewById<TextView>(R.id.userName).text = ReadFile.USER_NAME
        findViewById<TextView>(R.id.gamesCount).text = ReadFile.GAMES_COUNT.toString()
        findViewById<TextView>(R.id.expansionsCount).text = ReadFile.EXTENSIONS_COUNT.toString()
        findViewById<TextView>(R.id.lastSync).text = ReadFile.SYNCHRONIZATION_DATE
        FILE.close()

        findViewById<Button>(R.id.gameList_Button).setOnClickListener {
            val intent = Intent(this, GamesList::class.java)
            activityLauncher.launch(intent)
        }

        findViewById<Button>(R.id.extensionList_Button).setOnClickListener {
            val intent = Intent(this, ExtensionsList::class.java)
            activityLauncher.launch(intent)
        }

        findViewById<Button>(R.id.synchronization_Button).setOnClickListener {
            val intent = Intent(this, SynchronizationScreen::class.java)
            activityLauncher.launch(intent)
        }

        findViewById<Button>(R.id.clear_Button).setOnClickListener {
            val dialog = Delete()
            dialog.show(supportFragmentManager, "NoticeDialogFragment")
        }
    }

    private var activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val FILE = ReadFile(this, null, null, 1)
            FILE.readInfo("$filesDir")
            FILE.close()
            findViewById<TextView>(R.id.userName).text = ReadFile.USER_NAME
            findViewById<TextView>(R.id.gamesCount).text = ReadFile.GAMES_COUNT.toString()
            findViewById<TextView>(R.id.expansionsCount).text = ReadFile.EXTENSIONS_COUNT.toString()
            findViewById<TextView>(R.id.lastSync).text = ReadFile.SYNCHRONIZATION_DATE
        }
    }


    override fun PositiveClick(dialog: DialogFragment) {
        val FILE = ReadFile(this, null, null, 1)
        FILE.deleteDB("$filesDir")
        finish()
        exitProcess(0)
    }

    override fun NegativeClick(dialog: DialogFragment) {

    }

}