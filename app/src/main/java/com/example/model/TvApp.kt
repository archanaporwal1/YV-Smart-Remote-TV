package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.BrandAppleTv
import com.example.ui.theme.BrandDisney
import com.example.ui.theme.BrandMax
import com.example.ui.theme.BrandNetflix
import com.example.ui.theme.BrandPrimeVideo
import com.example.ui.theme.BrandSpotify
import com.example.ui.theme.BrandTwitch
import com.example.ui.theme.BrandYouTube

data class TvApp(
  val id: String,
  val name: String,
  val packageName: String,
  val brandColor: Color,
  val shortLabel: String,
  val category: String = "Streaming"
)

object PredefinedTvApps {
  val defaultApps = listOf(
    TvApp("youtube", "YouTube", "com.google.android.youtube.tv", BrandYouTube, "YT"),
    TvApp("netflix", "Netflix", "com.netflix.ninja", BrandNetflix, "N"),
    TvApp("prime_video", "Prime Video", "com.amazon.amazonvideo.livingroom", BrandPrimeVideo, "Prime"),
    TvApp("disney", "Disney+", "com.disney.disneyplus", BrandDisney, "Disney+"),
    TvApp("spotify", "Spotify", "com.spotify.tv.android", BrandSpotify, "Spotify", "Music"),
    TvApp("twitch", "Twitch", "tv.twitch.android.app", BrandTwitch, "Twitch", "Live"),
    TvApp("max", "Max", "com.wbd.stream", BrandMax, "Max"),
    TvApp("apple_tv", "Apple TV", "com.apple.atve.androidtv.appletv", BrandAppleTv, "Apple TV"),
    TvApp("plex", "Plex", "com.plexapp.android", Color(0xFFE5A00D), "Plex", "Media"),
    TvApp("kodi", "Kodi", "org.xbmc.kodi", Color(0xFF13B5EA), "Kodi", "Media"),
    TvApp("play_store", "Google Play", "com.android.vending", Color(0xFF00C853), "Play Store", "System"),
    TvApp("settings", "TV Settings", "com.android.tv.settings", Color(0xFF607D8B), "Settings", "System")
  )
}
