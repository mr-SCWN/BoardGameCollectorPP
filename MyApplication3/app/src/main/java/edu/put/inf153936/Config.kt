package edu.put.inf153936


import android.app.Activity
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.*
import java.lang.Exception
import java.net.MalformedURLException
import java.net.URL

class Config : AppCompatActivity() {

    private var foundUser = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration_screen)

        val FILE = ReadFile(this, null, null, 1)
        FILE.deleteDB("$filesDir")

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener { searchUser() }
    }

    override fun finish() {
        setResult(Activity.RESULT_OK)
        super.finish()
    }

    private fun searchUser() {
        val FILE = ReadFile(this, null, null, 1)
        FILE.create()
        FILE.close()
        val userName: EditText = findViewById(R.id.editTextUserName)
        ReadFile.USER_NAME = userName.text.toString()
        userName.setText("")
        val xml = XmlDownloader()
        xml.execute()
    }

    private fun checkResult(){
        if  (foundUser) {
            val sync = SYNCHR()
            val result = sync.start("$filesDir", applicationContext, findViewById(R.id.progressBar))
            runOnUiThread {
                Toast.makeText(applicationContext, result, Toast.LENGTH_SHORT).show()
            }
            if (result == "Synchronizacja zakończona")
                finish()
            else {
                val FILE = ReadFile(this, null, null, 1)
                FILE.deleteDB("$filesDir")
            }
        }
        else {
            ReadFile.USER_NAME = ""
            findViewById<ProgressBar>(R.id.progressBar).visibility = android.view.View.GONE
        }
    }

    @Suppress("DEPRECATION")
    private inner class XmlDownloader: AsyncTask<String, Int, String>() {

        @Deprecated("Deprecated in Java")
        override fun onPreExecute(){
            super.onPreExecute()
            val p =  findViewById<ProgressBar>(R.id.progressBar)
            p.visibility = android.view.View.VISIBLE
        }

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            Toast.makeText(applicationContext, result, Toast.LENGTH_LONG).show()
            foundUser = result == "Loggowanie"
            Thread {
                try {
                    checkResult()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }.start()
        }

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg p0: String?): String {
            try {
                val url = URL("https://boardgamegeek.com/xmlapi2/user?name=" + ReadFile.USER_NAME)
                val connection = url.openConnection()
                connection.connect()
                val isStream = url.openStream()
                val directory = File("$filesDir/XML")
                if (!directory.exists()) directory.mkdir()
                val fos = FileOutputStream("$directory/user.xml")
                val data = ByteArray(1024)
                var count: Int
                var total: Long = 0
                count = isStream.read(data)
                while (count != -1) {
                    total += count.toLong()
                    fos.write(data, 0, count)
                    count = isStream.read(data)
                }
                isStream.close()
                fos.close()

                val xml = XML()
                return if (xml.checkUser(FileInputStream("$directory/user.xml")))
                    "Loggowanie"
                else "Użytkownik nie został znaleziony"
            } catch (e: MalformedURLException){
                return "Nieprawidłowy format adresu URL"
            }
            catch (e: FileNotFoundException){
                return "Plik nie został znaleziony"
            }
            catch (e: IOException){
                return "Wystąpił wyjątek IO"
            }
        }
    }

}