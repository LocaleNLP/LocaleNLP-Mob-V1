package com.example.localenlp_mobile_v1.DialogFragment

import TextDB
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.localenlp_mobile_v1.R
import com.google.android.material.textfield.TextInputLayout
import android.widget.Button

class DialogFragmentForText : DialogFragment() {
    interface DialogListener {
        fun onTextInput(text: String)
        fun onCancel()
    }

    private var listener: DialogListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_for_add_text, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textInputLayout = view.findViewById<TextInputLayout>(R.id.textInputLayout)
        val addButton = view.findViewById<Button>(R.id.addText)
        val cancelButton = view.findViewById<Button>(R.id.cancelText)

        addButton.setOnClickListener {
            val text = textInputLayout.editText?.text.toString()
            listener?.onTextInput(text)
            dismiss()
        }

        cancelButton.setOnClickListener {
            listener?.onCancel()
            dismiss()
        }
    }
    fun setDialogListener(listener: DialogListener) {
        this.listener = listener
    }
}
