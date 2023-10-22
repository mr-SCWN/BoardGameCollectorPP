package edu.put.inf153936

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class Synchronization: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            builder.setMessage("Czy chcesz rozpocząć synchronizację? \n " +
                    "(Historia rankingu jest aktualizowana raz dziennie)")
                .setPositiveButton("Tak"
                ) { _, _ ->
                    listener.PositiveClick(this)
                }
                .setNegativeButton("Nie"
                ) { _, _ ->
                    listener.NegativeClick(this)
                }

            builder.create()
        } ?: throw IllegalStateException("Wartość aktywności nie może być pusta")
    }

    private lateinit var listener: NoticeDialogListener

    interface NoticeDialogListener {
        fun PositiveClick(dialog: DialogFragment)
        fun NegativeClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as NoticeDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() +
                    " Należy zaimplementować NoticeDialogListener"))
        }
    }
}