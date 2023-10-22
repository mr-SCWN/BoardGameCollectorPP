package edu.put.inf153936


import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import edu.put.inf153936.databinding.ActivityExtensionListBinding
import java.net.URL

class ExtensionsList : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityExtensionListBinding
    private var spinner: Spinner? = null
    private var sortList = arrayOf (ReadFile.COLUMN_TITLE, ReadFile.COLUMN_RELEASED)
    private var order = ReadFile.COLUMN_TITLE
    private lateinit var tableExtensions: TableLayout
    private var extensions: MutableList<Extensions> = mutableListOf()
    private var name = "List of extensions"
    private val extensionsOnPage = 50
    private var pageCount: Int = ReadFile.EXTENSIONS_COUNT/extensionsOnPage
    private val onLastPage = ReadFile.EXTENSIONS_COUNT%extensionsOnPage
    private var pageNumber: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExtensionListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = title

        spinner = findViewById(R.id.spinner2)
        spinner!!.onItemSelectedListener = this

        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortList)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner!!.adapter = arrayAdapter

        tableExtensions = findViewById(R.id.tableGames)

        findViewById<Button>(R.id.buttonSort2).setOnClickListener {
            pageNumber = 0
            tableExtensions.removeAllViews()
            val FILE = ReadFile(this, null, null, 1)
            extensions = mutableListOf()
            val n: Int = if (pageNumber == pageCount)
                onLastPage
            else
                extensionsOnPage
            for (i in 0..n){
                FILE.findExtension(i + (pageNumber*extensionsOnPage), order)?.let { extensions.add(it) }
            }
            FILE.close()
            showExtensions()
        }

        tableExtensions.removeAllViews()
        val FILE = ReadFile(this, null, null, 1)
        extensions = mutableListOf()
        val n: Int = if (pageNumber == pageCount)
            onLastPage
        else
            extensionsOnPage
        for (i in 0..n){
            FILE.findExtension(i + (pageNumber*extensionsOnPage), order)?.let { extensions.add(it) }
        }
        FILE.close()
        showExtensions()
    }

    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        if(arg0.id == R.id.spinner2) {
            order = sortList[position]
        }
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {
        order = ReadFile.COLUMN_TITLE
    }

    private fun showExtensions() {
        val leftRowMargin = 0
        val topRowMargin = 0
        val rightRowMargin = 0
        val bottomRowMargin = 0

        val textSize: Int = resources.getDimension(R.dimen.font_size_verysmall).toInt()
        val smallTextSize: Int = resources.getDimension(R.dimen.font_size_small).toInt()
        val mediumTextSize: Int = resources.getDimension(R.dimen.font_size_medium).toInt()
        val rows: Int = if (pageNumber != pageCount)
            extensionsOnPage
        else onLastPage
        supportActionBar!!.title = "Extensions"
        var textSpacer: TextView?


        for (i in -1 until rows) {
            var row: Extensions? = null

            if (i < 0) {
                textSpacer = TextView(this)
                textSpacer.text = name
            } else {
                row = extensions[i]
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
            } else run{
                numberInList.setBackgroundColor(Color.parseColor("#ffffff"))
                numberInList.text = row?.i.toString()
                numberInList.setTextSize(TypedValue.COMPLEX_UNIT_PX, mediumTextSize.toFloat())
            }

            val imageOfExtension = ImageButton(this)
            if (i == -1) {
                imageOfExtension.layoutParams = TableRow.LayoutParams()
            } else {
                imageOfExtension.layoutParams = TableRow.LayoutParams(200,200)
            }

            imageOfExtension.setPadding(20, 15, 20, 15)
            if (i == -1) {
                imageOfExtension.setBackgroundColor(Color.parseColor("#ffffff"))
            } else {
                imageOfExtension.setBackgroundColor(Color.parseColor("#ffffff"))
                if (row?.Img != "") {
                    Thread {
                        try {
                            val url = URL(row?.Img)
                            val bmp =
                                BitmapFactory.decodeStream(url.openConnection().getInputStream())
                            runOnUiThread {
                                imageOfExtension.setImageBitmap(bmp)
                            }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }.start()
                }
            }

            val nameOfExtension = LinearLayout(this)
            nameOfExtension.orientation = LinearLayout.VERTICAL
            nameOfExtension.setPadding(20, 10, 20, 10)
            nameOfExtension.setBackgroundColor(Color.parseColor("#f8f8f8"))

            val tv3 = TextView(this)
            if (i == -1) {
                tv3.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT)
                tv3.setPadding(5, 5, 0, 5)
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize.toFloat())
            } else {
                tv3.layoutParams = TableRow.LayoutParams(600,
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
            nameOfExtension.addView(tv3)


            if (i > -1) {
                val yearOfExtension = TextView(this)
                yearOfExtension.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT)

                yearOfExtension.gravity = Gravity.END
                yearOfExtension.setTextSize(TypedValue.COMPLEX_UNIT_PX, mediumTextSize.toFloat())
                yearOfExtension.setPadding(5, 1, 0, 5)
                yearOfExtension.setTextColor(Color.parseColor("#aaaaaa"))
                yearOfExtension.setBackgroundColor(Color.parseColor("#f8f8f8"))
                yearOfExtension.text = "(" + row?.Year + ")"
                nameOfExtension.addView(yearOfExtension)
            }

            val tr = TableRow(this)
            tr.id = i + 1
            val trParams = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT)
            trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin)
            tr.setPadding(10, 0, 10, 0)
            tr.layoutParams = trParams

            tr.addView(numberInList)
            tr.addView(nameOfExtension)
            tr.addView(imageOfExtension)


            tableExtensions.addView(tr, trParams)

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
                tableExtensions.addView(trSep, trParamsSep)
            }


        }

        if (pageNumber != pageCount) {
            val nextButton = Button(this)
            nextButton.text = getString(R.string.next_page)
            nextButton.setBackgroundColor(Color.GREEN)
            nextButton.setOnClickListener {
                pageNumber += 1
                findViewById<NestedScrollView>(R.id.scrollView).fullScroll(View.FOCUS_UP)
                tableExtensions.removeAllViews()
                val FILE = ReadFile(this, null, null, 1)
                extensions = mutableListOf()
                val n: Int = if (pageNumber == pageCount)
                    onLastPage
                else
                    extensionsOnPage
                for (i in 0..n){
                    FILE.findExtension(i + (pageNumber*extensionsOnPage), order)?.let { extensions.add(it) }
                }
                FILE.close()
                showExtensions()
            }
            tableExtensions.addView(nextButton)
        }

        if (pageNumber != 0) {
            val prevButton = Button(this)
            prevButton.text = getString(R.string.prev_page)
            prevButton.setBackgroundColor(Color.CYAN)
            prevButton.setOnClickListener {
                pageNumber -= 1
                findViewById<NestedScrollView>(R.id.scrollView).fullScroll(View.FOCUS_UP)
                tableExtensions.removeAllViews()
                val FILE = ReadFile(this, null, null, 1)
                extensions = mutableListOf()
                val n: Int = if (pageNumber == pageCount)
                    onLastPage
                else
                    extensionsOnPage
                for (i in 0..n){
                    FILE.findExtension(i + (pageNumber*extensionsOnPage), order)?.let { extensions.add(it) }
                }
                FILE.close()
                showExtensions()
            }
            tableExtensions.addView(prevButton)
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
        tableExtensions.addView(trDate, trParamsSep)
    }
}