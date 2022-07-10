package com.doops.themealkotlin.other

import android.content.Context


fun Float.toPixels(context: Context): Float {
    return this * context.resources.displayMetrics.density
}
