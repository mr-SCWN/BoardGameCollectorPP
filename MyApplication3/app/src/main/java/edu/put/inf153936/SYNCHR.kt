package edu.put.inf153936


import android.content.Context
import android.widget.ProgressBar
import java.io.*
import java.lang.Thread.sleep
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*


class SYNCHR {
    private var query = ""
    private var fileName = ""
    private var filesDir = ""
    private lateinit var progress: ProgressBar
    private val file = "collection"
    fun start(f: String, c: Context, p: ProgressBar): String{
        progress = p
        filesDir = f
        var link = "https://www.boardgamegeek.com/xmlapi2/collection?username=" + ReadFile.USER_NAME
        var r = downloadFile(link)
        while (r == "wait"){
            sleep(5000)
            r = downloadFile(link)
        }
        if(r != "Success")
            return "Synchronization error: $r"
        val xml = XML()
        val idList: ArrayList<String> = xml.checkCollection(FileInputStream("$filesDir/XML/$file.xml"))
        p.max  = idList.count()

        link = "https://boardgamegeek.com/xmlapi2/collection?username=${ReadFile.USER_NAME}&stats=1&excludesubtype=boardgameexpansion"
        r = downloadFile(link)
        while (r == "wait"){
            sleep(5000)
            r = downloadFile(link)
        }
        if(r != "Success")
            return "Synchronization error: $r"
        xml.addCollection(FileInputStream("$filesDir/XML/$file.xml"), c, p, true)
        link = "https://boardgamegeek.com/xmlapi2/collection?username=${ReadFile.USER_NAME}&stats=1&subtype=boardgameexpansion"
        r = downloadFile(link)
        while (r == "wait"){
            sleep(5000)
            r = downloadFile(link)
        }
        if(r != "Success")
            return "Synchronization error: $r"
        xml.addCollection(FileInputStream("$filesDir/XML/$file.xml"), c, p, false)

        ReadFile.SYNCHRONIZATION_DATE = currentDate(true)
        ReadFile.SYNCHRONIZATION_TIME = Calendar.getInstance().timeInMillis.toString()
        saveToFile()
        return "Synchronizacja zakończona"
    }

    fun currentDate(hour:Boolean): String {
        val calendar = Calendar.getInstance()
        val intDay = calendar.get(Calendar.DATE)
        val day = if (intDay < 10)
            "0$intDay"
        else
            intDay.toString()
        val intMonth = (calendar.get(Calendar.MONTH) + 1)
        val month = if (intMonth < 10)
            "0$intMonth"
        else
            intMonth.toString()
        val year = calendar.get(Calendar.YEAR).toString()
        var result = "$year-$month-$day"
        if(hour)
        {
            result += " " + currentHour()
        }
        return result
    }

    fun currentHour(): String{
        val calendar = Calendar.getInstance()
        val intHour = calendar.get(Calendar.HOUR_OF_DAY)
        val h = if (intHour < 10)
            "0$intHour"
        else
            intHour.toString()

        val intMin = calendar.get((Calendar.MINUTE))
        val min = if (intMin <10)
            "0$intMin"
        else
            intMin.toString()

        return "$h:$min"
    }

    private fun saveToFile(){
        val file = File("$filesDir/user.txt")
        val text = ReadFile.USER_NAME + "\n" + ReadFile.GAMES_COUNT.toString() + "\n" +
                ReadFile.EXTENSIONS_COUNT.toString() + "\n" + ReadFile.SYNCHRONIZATION_DATE + "\n" + ReadFile.SYNCHRONIZATION_TIME
        file.writeText(text)
    }


    private fun downloadFile(q:String): String{
        query = q
        fileName = file
        try {
            val url = URL(query)
            val connection = url.openConnection() as HttpURLConnection
            connection.connect()
            if (connection.responseCode == 202){
                return "wait"
            }
            val isStream: InputStream = url.openStream()
            val directory = File("$filesDir/XML")
            if (!directory.exists()) directory.mkdir()
            val fos = FileOutputStream("$directory/$fileName.xml")
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
            return "Success"
        } catch (e: MalformedURLException){
            return "Malformed URL"
        }
        catch (e: IOException){
            return "IO Exception"
        }
        catch (e: FileNotFoundException){
            return "File Not Found"
        }
    }
}