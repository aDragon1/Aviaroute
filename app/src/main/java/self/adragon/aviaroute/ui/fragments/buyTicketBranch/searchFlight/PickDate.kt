package self.adragon.aviaroute.ui.fragments.buyTicketBranch.searchFlight

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import self.adragon.aviaroute.R
import java.time.ZoneId
import java.util.Calendar
import java.util.TimeZone

class PickDate(private val onDatePick: (Long) -> Unit) : Fragment(R.layout.date_picker) {

    private lateinit var datePickerRoot: LinearLayout
    private lateinit var dateTextView: TextView

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        datePickerRoot = view.findViewById(R.id.datePickerRoot)
        dateTextView = view.findViewById(R.id.dateTextView)

        datePickerRoot.setOnClickListener {
            val onDateSetListener =
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    val dayOfWeek = getDayOfWeekString(year, month, dayOfMonth)
                    val epochSeconds = getTimeInEpochSeconds(year, month, dayOfMonth)

                    dateTextView.text = "$dayOfWeek, $dayOfMonth ${getMonthString(month)} $year"
                    onDatePick(epochSeconds)
                }

            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(), onDateSetListener, year, month, day
            )

            datePickerDialog.show()
        }
    }

    private fun getDayOfWeekString(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return when (dayOfWeek) {
            Calendar.SUNDAY -> "Воскресенье"
            Calendar.MONDAY -> "Понедельник"
            Calendar.TUESDAY -> "Вторник"
            Calendar.WEDNESDAY -> "Среда"
            Calendar.THURSDAY -> "Четверг"
            Calendar.FRIDAY -> "Пятница"
            Calendar.SATURDAY -> "Суббота"
            else -> "Неизвестно"
        }
    }

    private fun getMonthString(month: Int) = when (month) {
        0 -> "янв."
        1 -> "фев."
        2 -> "мар."
        3 -> "апр."
        4 -> "май"
        5 -> "июн."
        6 -> "июл."
        7 -> "авг."
        8 -> "сен."
        9 -> "окт."
        10 -> "ноя."
        11 -> "дек."
        else -> ""
    }

    private fun getTimeInEpochSeconds(year: Int, month: Int, dayOfMonth: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)

        calendar.timeZone = TimeZone.getTimeZone(ZoneId.systemDefault())
        return calendar.timeInMillis / 1000
    }
}