package com.sarthak.better_player_enhanced

import android.content.Context
import android.net.Uri
import androidx.media3.exoplayer.source.MediaSource
import java.util.concurrent.ConcurrentHashMap

/** Host-provided Media3 sources. Providers retain ownership of shared caches. */
object BetterPlayerMediaSources {
    private val providers = ConcurrentHashMap<String, (Context, Uri) -> MediaSource>()

    fun register(scheme: String, provider: (Context, Uri) -> MediaSource) {
        require(scheme !in setOf("http", "https", "file", "asset", "content", "rtsp"))
        providers[scheme] = provider
    }

    fun resolve(context: Context, uri: Uri): MediaSource {
        val provider = providers[uri.scheme]
            ?: throw IllegalArgumentException("No media source provider for ${uri.scheme}")
        return provider(context, uri)
    }
}
