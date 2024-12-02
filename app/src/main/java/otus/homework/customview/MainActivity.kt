package otus.homework.customview

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import java.util.Date

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data = collectData(this)
        val pieChartView = findViewById<PieChartView>(R.id.pie_chart_view)
        val barGraphView = findViewById<BarGraphView>(R.id.bar_graph_view)
        pieChartView?.draw(data) { barGraphView.draw(data, pieChartView.getCategory()) }
        barGraphView.draw(data, pieChartView.getCategory())
    }


    private fun collectData(context: Context) : List<Model> {
        val json: String = context.resources.openRawResource(R.raw.payload).bufferedReader().readText()
        return Gson().fromJson(json, Array<Model>::class.java).toList();
    }
}