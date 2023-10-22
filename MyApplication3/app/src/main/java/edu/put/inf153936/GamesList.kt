package edu.put.inf153936

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import edu.put.inf153936.databinding.ActivityGameListBinding
import java.net.URL


class GamesList : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityGameListBinding
    private var spinner: Spinner? = null
    private var sortList = arrayOf (ReadFile.COLUMN_TITLE, ReadFile.COLUMN_RELEASED, ReadFile.COLUMN_RANK)
    private var order = ReadFile.COLUMN_TITLE
    private lateinit var tableGames: TableLayout
    private var games: MutableList<Games> = mutableListOf()
    private var name = "List of games"
    private val gamesOnPage = 50
    private var pageCount: Int = ReadFile.GAMES_COUNT/gamesOnPage
    private val onLastPage = ReadFile.GAMES_COUNT%gamesOnPage
    private var pageNumber: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = title

        spinner = findViewById(R.id.spinner1)
        spinner!!.onItemSelectedListener = this

        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortList)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner!!.adapter = arrayAdapter

        tableGames = findViewById(R.id.tableGames)

        findViewById<Button>(R.id.buttonSort1).setOnClickListener {
            pageNumber = 0
            tableGames.removeAllViews()
            val FILE = ReadFile(this, null, null, 1)
            games = mutableListOf()
            val n: Int = if (pageNumber == pageCount)
                onLastPage
            else
                gamesOnPage
            for (i in 0..n){
                FILE.findGame(i + (pageNumber*gamesOnPage), order)?.let { games.add(it) }
            }
            FILE.close()
            showGames()
        }

        tableGames.removeAllViews()
        val FILE = ReadFile(this, null, null, 1)
        games = mutableListOf()
        val n: Int = if (pageNumber == pageCount)
            onLastPage
        else
            gamesOnPage
        for (i in 0..n){
            FILE.findGame(i + (pageNumber*gamesOnPage), order)?.let { games.add(it) }
        }
        FILE.close()
        showGames()
    }

    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long)
    {
        if(arg0.id == R.id.spinner1) {
            order = sortList[position]
        }
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {
        order = ReadFile.COLUMN_TITLE
    }

    private fun showGames(){

        val leftRowMargin = 0
        val topRowMargin = 0
        val rightRowMargin = 0
        val bottomRowMargin = 0

        val textSize: Int = resources.getDimension(R.dimen.font_size_verysmall).toInt()
        val smallTextSize: Int = resources.getDimension(R.dimen.font_size_small).toInt()
        val mediumTextSize: Int = resources.getDimension(R.dimen.font_size_medium).toInt()
        val rows: Int = if (pageNumber != pageCount)
            gamesOnPage
        else onLastPage
        supportActionBar!!.title = "Games"
        var textSpacer: TextView?


        for (i in -1 until rows) {
            var row: Games? = null

            if (i < 0) {
                textSpacer = TextView(this)
                textSpacer.text = name
            } else {
                row = games[i]
            }


            val numberInList = TextView(this)
            numberInList.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT)
            numberInList.gravity = Gravity.START
            numberInList.setPadding(20, 15, 20, 15)

            if (i == -1) run {
                numberInList.text = " " //name
                numberInList.setBackgroundColor(Color.parseColor("#ffffff"))
                numberInList.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize.toFloat())
            } else run {
                numberInList.setBackgroundColor(Color.parseColor("#ffffff"))
                numberInList.text = row?.i.toString()
                numberInList.setTextSize(TypedValue.COMPLEX_UNIT_PX, mediumTextSize.toFloat())
            }

            val imageOfGame = ImageButton(this)
            if (i == -1) {
                imageOfGame.layoutParams = TableRow.LayoutParams()
            } else {
                imageOfGame.layoutParams = TableRow.LayoutParams(200,200)
            }


            imageOfGame.setPadding(20, 15, 20, 15)
            if (i == -1) {
                imageOfGame.setBackgroundColor(Color.parseColor("#ffffff"))
            } else {
                imageOfGame.setBackgroundColor(Color.parseColor("#ffffff"))
                if (row?.Img != "") {
                    Thread {
                        try {
                            val url = URL(row?.Img)
                            val bmp =
                                BitmapFactory.decodeStream(url.openConnection().getInputStream())
                            runOnUiThread {
                                imageOfGame.setImageBitmap(bmp)
                            }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }.start()
                }
            }

            val nameOfGame = LinearLayout(this)
            nameOfGame.orientation = LinearLayout.VERTICAL
            nameOfGame.setPadding(20, 10, 20, 10)
            nameOfGame.setBackgroundColor(Color.parseColor("#f8f8f8"))

            val tv3 = TextView(this)
            if (i == -1) {
                tv3.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT)
                tv3.setPadding(5, 5, 0, 5)
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize.toFloat())
            } else {
                tv3.layoutParams = TableRow.LayoutParams(450,
                    TableRow.LayoutParams.MATCH_PARENT)
                tv3.setPadding(5, 0, 0, 5)
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
            }

            tv3.gravity = Gravity.TOP

            if (i == -1) {
                tv3.text = ReadFile.COLUMN_TITLE
                tv3.setBackgroundColor(Color.parseColor("#f0f0f0"))
            } else {
                tv3.setBackgroundColor(Color.parseColor("#f8f8f8"))
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, mediumTextSize.toFloat())
                tv3.text = row?.Title
            }
            nameOfGame.addView(tv3)


            if (i > -1) {
                val yearOfGame = TextView(this)
                yearOfGame.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT)

                yearOfGame.gravity = Gravity.END
                yearOfGame.setTextSize(TypedValue.COMPLEX_UNIT_PX, mediumTextSize.toFloat())
                yearOfGame.setPadding(5, 1, 0, 5)
                yearOfGame.setTextColor(Color.parseColor("#aaaaaa"))
                yearOfGame.setBackgroundColor(Color.parseColor("#f8f8f8"))
                yearOfGame.text = "(" + row?.Year + ")"
                nameOfGame.addView(yearOfGame)
            }

            val numberInRanking  = TextView(this)
            numberInRanking.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT)
            numberInRanking.gravity = Gravity.START
            numberInRanking.setPadding(20, 15, 20, 15)

            if (i == -1) run {
                numberInRanking.text = ReadFile.COLUMN_RANK
                numberInRanking.setBackgroundColor(Color.parseColor("#f0f0f0"))
                numberInRanking.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize.toFloat())
                numberInRanking.setPadding(5, 5, 0,5)
            } else run {
                numberInRanking.setBackgroundColor(Color.parseColor("#ffffff"))
                numberInRanking.text = row?.Rank.toString()
                numberInRanking.setTextSize(TypedValue.COMPLEX_UNIT_PX, mediumTextSize.toFloat())
            }

            val tr = TableRow(this)
            tr.id = i + 1
            if(i > -1) {
                tr.setOnClickListener {
                    if (row != null) {
                        val intent = Intent(this, HistoryScreen::class.java)
                        intent.putExtra("name", row.Title)
                        startActivity(intent)
                    }
                }
            }

            val trParams = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT)
            trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin)
            tr.setPadding(10, 0, 10, 0)
            tr.layoutParams = trParams

            tr.addView(numberInList)
            tr.addView(nameOfGame)
            tr.addView(imageOfGame)
            tr.addView(numberInRanking)

            tableGames.addView(tr, trParams)

            if (i > -1) {

                val trSep = TableRow(this)
                val trParamsSep = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT)
                trParamsSep.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin)

                trSep.layoutParams = trParamsSep
                val tvSep = TextView(this)
                val tvSepLay = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT)
                tvSepLay.span = 4
                tvSep.layoutParams = tvSepLay
                tvSep.setBackgroundColor(Color.parseColor("#d9d9d9"))
                tvSep.height = 1

                trSep.addView(tvSep)
                tableGames.addView(trSep, trParamsSep)
            }

        }

        if (pageNumber != pageCount) {
            val nextButton = Button(this)
            nextButton.text = getString(R.string.next_page)
            nextButton.setBackgroundColor(Color.GREEN)
            nextButton.setOnClickListener {
                pageNumber += 1
                findViewById<NestedScrollView>(R.id.scrollView).fullScroll(View.FOCUS_UP)
                tableGames.removeAllViews()
                val FILE = ReadFile(this, null, null, 1)
                games = mutableListOf()
                val n: Int = if (pageNumber == pageCount)
                    onLastPage
                else
                    gamesOnPage
                for (i in 0..n){
                    FILE.findGame(i + (pageNumber*gamesOnPage), order)?.let { games.add(it) }
                }
                FILE.close()
                showGames()
            }
            tableGames.addView(nextButton)
        }

        if (pageNumber != 0) {
            val prevButton = Button(this)
            prevButton.text = getString(R.string.prev_page)
            prevButton.setBackgroundColor(Color.CYAN)
            prevButton.setOnClickListener {
                pageNumber -= 1
                findViewById<NestedScrollView>(R.id.scrollView).fullScroll(View.FOCUS_UP)
                tableGames.removeAllViews()
                val FILE = ReadFile(this, null, null, 1)
                games = mutableListOf()
                val n: Int = if (pageNumber == pageCount)
                    onLastPage
                else
                    gamesOnPage
                for (i in 0..n){
                    FILE.findGame(i + (pageNumber*gamesOnPage), order)?.let { games.add(it) }
                }
                FILE.close()
                showGames()
            }
            tableGames.addView(prevButton)
        }

        val trDate = TableRow(this)
        val trParamsSep = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
            TableLayout.LayoutParams.WRAP_CONTENT)
        trParamsSep.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin)

        trDate.layoutParams = trParamsSep
        val tvSep = TextView(this)
        val tvSepLay = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.MATCH_PARENT)
        tvSepLay.span = 4
        tvSep.layoutParams = tvSepLay
        tvSep.setBackgroundColor(Color.parseColor("#d9d9d9"))

        tvSep.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize.toFloat())

        trDate.addView(tvSep)
        tableGames.addView(trDate, trParamsSep)

    }
}