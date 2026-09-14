# better_player_android

Android implementation of the [better_player](https://pub.dev/packages/better_player) plugin.

## Usage

This package should not be used directly by app developers. Instead, they should use the main `better_player` package.

## Native Media3 sources

Apps can register custom URI schemes that produce a Media3 `MediaSource`
directly on Android:

```kotlin
pl.hasoft.better_player.BetterPlayerMediaSources.register("native-hls") { context, uri ->
    MyOfflineMediaSources.create(context, uri)
}
```

The provider is called for each playback request and must return a fresh
`MediaSource`. Better Player passes the application context to the provider, so
the provider must not retain an activity. Standard schemes such as `http`,
`https`, `file`, `content`, and `rtsp` cannot be overridden.

This hook is intended for sources backed by app-owned native infrastructure,
such as a Media3 download cache. Cache ownership stays with the registering
app; Better Player's `CacheConfiguration` still refers only to Better Player's
ordinary network playback cache.
