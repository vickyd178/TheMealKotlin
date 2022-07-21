package com.doops.themealkotlin.other

import android.animation.LayoutTransition
import android.app.Activity
import android.text.Selection
import android.text.Spannable
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


fun <T> Fragment.collectLatestLifeCycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest(collect)
        }
    }

}

fun Fragment.showToast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this.requireContext(), message, length).show()
}

fun Activity.showToast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}


fun <T> AppCompatActivity.collectLatestLifeCycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest(collect)
        }
    }

}

fun AppCompatActivity.collectLifeCycleFlow(collect: suspend () -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            collect.invoke()
        }
    }
}


private fun View.hide() {
    visibility = View.GONE
}

private fun View.show() {
    visibility = View.VISIBLE
}


fun TextView.setExpandableText(value: String, length: Int = 150) {
    var firstHalf = ""
    var secondHalf = ""
    var hiddenText = true

    if (value.length > length) {
        firstHalf = value.substring(0, length - 1)
        secondHalf = value.substring(length, value.length)
        //Enable animation on text change
        val layoutTransition = LayoutTransition()
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        (this.parent as ViewGroup).layoutTransition = layoutTransition;
        //END

        setOnClickListener {
            hiddenText = !hiddenText
            updateText(this, firstHalf, secondHalf, hiddenText)
        }
    } else {
        firstHalf = value
    }

    updateText(this, firstHalf, secondHalf, hiddenText)

}

private fun updateText(
    textView: TextView,
    firstHalf: String,
    secondHalf: String,
    hiddenText: Boolean,
) {
    val clickable = object : ClickableSpan() {
        override fun updateDrawState(textPaint: TextPaint) {
            // use this to change the link color
            textPaint.color = ContextCompat.getColor(
                textView.context!!, android.R.color.holo_blue_dark
            )
            // toggle below value to enable/disable
            // the underline shown below the clickable text
            textPaint.isUnderlineText = false
        }

        override fun onClick(view: View) {
            Selection.setSelection((view as TextView).text as Spannable, 0)
            view.invalidate()
            Log.e("", "onClick: $firstHalf")
            updateText(textView, firstHalf, secondHalf, hiddenText)
        }
    }

    textView.text = buildSpannedString {
        if (secondHalf.isBlank()) {
            append(firstHalf)
        } else {
            if (hiddenText) append("$firstHalf...") else append("$firstHalf$secondHalf")
        }
        inSpans(
            RelativeSizeSpan(1.05f),
            clickable,
        ) {
            if (secondHalf.isNotBlank()) {
                val textToAppend = if (hiddenText) "\nShow more ▼" else "\nShow less ▲"
                append(textToAppend)
            }
        }
    }
}