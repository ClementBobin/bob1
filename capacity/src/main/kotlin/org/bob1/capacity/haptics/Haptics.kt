package org.bob1.capacity.haptics

import android.view.View
import dev.kindling.android.natif.HapticHelper

class Haptics {
    private val helper = HapticHelper()
    fun click(view: View) = helper.click(view)
    fun confirm(view: View) = helper.confirm(view)
    fun reject(view: View) = helper.reject(view)
}
