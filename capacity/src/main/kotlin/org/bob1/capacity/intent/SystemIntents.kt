package org.bob1.capacity.intent

import android.content.Context
import android.content.Intent
import android.net.Uri

/** "Itinéraire": hands an address to whatever maps app the user has. */
object Directions {
    fun open(context: Context, destination: String): Boolean {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${Uri.encode(destination)}"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return runCatching { context.startActivity(intent) }.isSuccess
    }
}

/** Android share sheet for plain text (family invitation code). */
object ShareText {
    fun open(context: Context, text: String, chooserTitle: String = "Partager"): Boolean {
        val send = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, text)
        val chooser = Intent.createChooser(send, chooserTitle).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return runCatching { context.startActivity(chooser) }.isSuccess
    }
}
