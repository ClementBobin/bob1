package com.mirage.bob1.ui.core

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity

/** The hosting [FragmentActivity] (MainActivity), which the system biometric prompt needs. */
@Composable
fun rememberFragmentActivity(): FragmentActivity? {
    val context = LocalContext.current
    return remember(context) { context.findFragmentActivity() }
}

private tailrec fun Context.findFragmentActivity(): FragmentActivity? = when (this) {
    is FragmentActivity -> this
    is ContextWrapper -> baseContext.findFragmentActivity()
    else -> null
}
