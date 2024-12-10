package self.adragon.aviaroute.ui.fragments.buyTicketBranch.searchResult

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.slider.RangeSlider
import self.adragon.aviaroute.R
import self.adragon.aviaroute.data.model.typeConverters.LocalDateConverter
import self.adragon.aviaroute.ui.viewmodels.SearchResulViewModel
import kotlin.math.ceil
import kotlin.math.floor

class SearchResultParamsRangeSlider(
    private val title: String,
    private val minMaxValue: Pair<Long, Long>,
    private val curMinMaxValue: Pair<Long, Long>,
    private val isPrice: Boolean
) :
    Fragment(R.layout.search_result_params_range_slider) {

    private lateinit var titleTextView: TextView
    private lateinit var rangeSlider: RangeSlider
    private lateinit var minValueTextView: TextView
    private lateinit var maxValueTextView: TextView

    private val localDateConverter = LocalDateConverter()

    private val searchResultViewModel: SearchResulViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)

        val (minValue, maxValue) = if (isPrice)
            floor(minMaxValue.first.toFloat()) to ceil(minMaxValue.second.toFloat())
        else minMaxValue.first.toFloat() to minMaxValue.second.toFloat()

        val (curMinValue, curMaxValue) = if (isPrice)
            floor(curMinMaxValue.first.toFloat()) to ceil(curMinMaxValue.second.toFloat())
        else curMinMaxValue.first.toFloat() to curMinMaxValue.second.toFloat()

        val stepSize = if (isPrice) 0.5f else 1f

        titleTextView.text = title
        rangeSlider.values = listOf(curMinValue, curMaxValue)
        rangeSlider.valueFrom = minValue
        rangeSlider.valueTo = maxValue
        rangeSlider.stepSize = stepSize


        rangeSlider.addOnChangeListener { slider, _, _ ->
            updateTextView(slider.values[0], slider.values[1])
        }
        rangeSlider.setLabelFormatter { value -> sliderLabelFormatter(value) }

        rangeSlider.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: RangeSlider) {}

            override fun onStopTrackingTouch(slider: RangeSlider) {
                val minSelectedValue = slider.values[0]
                val maxSelectedValue = slider.values[1]

                val minMax = minSelectedValue.toLong() to maxSelectedValue.toLong()
                searchResultViewModel.setPriceFlightTimeRange(minMax, isPrice)
            }
        })

        updateTextView(curMinValue, curMaxValue)
    }

    private fun initViews(view: View) {
        titleTextView = view.findViewById(R.id.titleTextView)
        rangeSlider = view.findViewById(R.id.rangeSlider)
        minValueTextView = view.findViewById(R.id.minValueTextView)
        maxValueTextView = view.findViewById(R.id.maxValueTextView)

        rangeSlider.isTickVisible = false
    }

    private fun sliderLabelFormatter(value: Float) =
        if (isPrice) "$value" else localDateConverter.fromEpochSecondToTimeString(value.toLong())


    private fun updateTextView(minValue: Float, maxValue: Float) {
        if (isPrice) updatePriceTextView(minValue, maxValue)
        else updateDateTextView(minValue, maxValue)
    }

    private fun updatePriceTextView(minValue: Float, maxValue: Float) {
        minValueTextView.text = "$minValue"
        maxValueTextView.text = "$maxValue"
    }

    private fun updateDateTextView(minValue: Float, maxValue: Float) {
        val minDateStr = localDateConverter.fromEpochSecondToTimeString(minValue.toLong())
        val maxDateStr = localDateConverter.fromEpochSecondToTimeString(maxValue.toLong())

        minValueTextView.text = minDateStr
        maxValueTextView.text = maxDateStr
    }
}