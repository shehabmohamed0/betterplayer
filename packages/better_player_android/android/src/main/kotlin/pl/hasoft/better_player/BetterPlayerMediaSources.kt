package pl.hasoft.better_player

import android.content.Context
import android.net.Uri
import androidx.annotation.Keep
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.source.MediaSource
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

@Keep
@OptIn(UnstableApi::class)
object BetterPlayerMediaSources {
    private val standardSchemes = setOf(
        "http",
        "https",
        "file",
        "asset",
        "content",
        "rtsp",
    )
    private val providers = ConcurrentHashMap<String, (Context, Uri) -> MediaSource>()

    @JvmStatic
    fun register(scheme: String, provider: (Context, Uri) -> MediaSource) {
        val normalizedScheme = normalizeScheme(scheme)
        require(normalizedScheme !in standardSchemes) {
            "Cannot override standard media scheme: $normalizedScheme"
        }
        providers[normalizedScheme] = provider
    }

    @JvmStatic
    fun unregister(scheme: String) {
        providers.remove(normalizeScheme(scheme))
    }

    fun resolve(context: Context, uri: Uri): MediaSource? {
        val scheme = uri.scheme?.lowercase(Locale.US) ?: return null
        return providers[scheme]?.invoke(context.applicationContext, uri)
    }

    private fun normalizeScheme(scheme: String): String {
        val normalizedScheme = scheme.trim().lowercase(Locale.US)
        require(normalizedScheme.isNotEmpty()) {
            "Media source scheme must not be blank"
        }
        require(!normalizedScheme.contains("://")) {
            "Register only the URI scheme name, not a full URI"
        }
        return normalizedScheme
    }
}
