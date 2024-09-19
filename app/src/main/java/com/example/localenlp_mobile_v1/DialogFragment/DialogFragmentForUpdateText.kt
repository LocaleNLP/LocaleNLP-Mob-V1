package com.example.localenlp_mobile_v1.DialogFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.localenlp_mobile_v1.R
import com.google.android.material.textfield.TextInputLayout

class DialogFragmentForUpdateText(private val initialText: String) : DialogFragment() {
    private lateinit var dialogListener: DialogListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.shape_of_update_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textInputLayout = view.findViewById<TextInputLayout>(R.id.textInputLayout)
        val updateButton = view.findViewById<Button>(R.id.updateText)
        val cancelButton = view.findViewById<Button>(R.id.cancelText)

        textInputLayout.editText?.setText(initialText)  // Set the initial text

        updateButton.setOnClickListener {
            val updatedText = textInputLayout.editText?.text.toString()
            dialogListener.onTextUpdated(updatedText)
            dismiss()
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    fun setDialogListener(listener: DialogListener) {
        this.dialogListener = listener
    }

    interface DialogListener {
        fun onTextUpdated(newText: String)
    }
}
