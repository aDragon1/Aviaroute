package self.adragon.aviaroute.utils

import android.app.SearchManager
import android.database.Cursor
import android.database.MatrixCursor
import android.provider.BaseColumns
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener

class SearchViewHelper(val airportNames: List<Pair<String, Int>>) {

    fun setSearchViewListener(searchView: SearchView, onSubmit: (Int) -> Unit) {
        val autocompleteId = androidx.appcompat.R.id.search_src_text
        val searchAutoCompleteTextView =
            searchView.findViewById<AutoCompleteTextView>(autocompleteId)
        searchAutoCompleteTextView.threshold = 1

        searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(query: String?): Boolean {
                val q = query?.trim() ?: ""
                val names = if (q.isEmpty()) emptyList() else airportNames

                val cursor = createSuggestionsCursor(q, names)
                searchView.suggestionsAdapter.changeCursor(cursor)

                return true
            }
        })

        searchView.setOnQueryTextFocusChangeListener { v, hasFocus ->
            changeSearchViewCursorVisibility(v as SearchView, hasFocus)
        }

        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return true
            }

            override fun onSuggestionClick(position: Int): Boolean {
                val clickedRow = searchView.suggestionsAdapter.getItem(position) as Cursor

                val nameID = SearchManager.SUGGEST_COLUMN_TEXT_1
                val indexID = SearchManager.SUGGEST_COLUMN_TEXT_2

                val clickedItemName = getItem<String>(clickedRow, nameID) ?: return false
                val clickedItemIndex = getItem<Int>(clickedRow, indexID) ?: return false

                searchView.setQuery(clickedItemName, false)
                searchView.clearFocus()

                onSubmit(clickedItemIndex)
                return true
            }
        })
    }

    inline fun <reified T> getItem(row: Cursor, id: String): T? {
        val colIndex = row.getColumnIndex(id)

        if (colIndex == 0) return null
        return when (T::class) {
            Int::class -> row.getInt(colIndex) as T
            String::class -> row.getString(colIndex) as T
            else -> null
        }
    }

    private fun createSuggestionsCursor(
        query: String?,
        airportNames: List<Pair<String, Int>>
    ): Cursor {
        val id1 = BaseColumns._ID
        val id2 = SearchManager.SUGGEST_COLUMN_TEXT_1
        val id3 = SearchManager.SUGGEST_COLUMN_TEXT_2

        val arr = arrayOf(id1, id2, id3)
        val matrixCursor = MatrixCursor(arr)

        var i = 0
        airportNames.filter { it.first.contains(query ?: "", ignoreCase = true) }
            .forEach { el ->
                val name = el.first
                val index = el.second
                matrixCursor.addRow(arrayOf(i++, name, index))
            }
        return matrixCursor
    }

    private fun changeSearchViewCursorVisibility(searchView: SearchView, isCursorVisible: Boolean) {
        val searchTextView =
            searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchTextView.isCursorVisible = isCursorVisible
    }
}