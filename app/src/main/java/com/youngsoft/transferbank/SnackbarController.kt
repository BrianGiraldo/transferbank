package com.youngsoft.transferbank

import android.view.View
import com.google.android.material.snackbar.Snackbar

class SnackbarController(view: View, message:String) {
    var contextView: View = view
    var make:Snackbar = Snackbar.make(contextView, message, Snackbar.LENGTH_LONG)
}