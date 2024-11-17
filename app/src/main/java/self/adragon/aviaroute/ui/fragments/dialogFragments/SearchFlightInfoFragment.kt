package self.adragon.aviaroute.ui.fragments.dialogFragments

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import self.adragon.aviaroute.R
import self.adragon.aviaroute.ui.viewmodels.SearchResulViewModel

class SearchFlightInfoFragment(private val onCustomDismiss: () -> Unit) :
    DialogFragment(R.layout.search_result_flight_info_item) {

    private val searchResultViewModel: SearchResulViewModel by activityViewModels()

    override fun onStart() {
        super.onStart()

        val mp = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.setLayout(mp, mp)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchResultViewModel.clickedItem.observe(viewLifecycleOwner) {
            Log.d("mytag", "clicked - $it")
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        onCustomDismiss()
    }
}