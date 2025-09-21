package com.seekho.anime.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.seekho.anime.databinding.ActivityDetailBinding
import com.seekho.anime.network.NetworkResult
import com.seekho.anime.model.Anime
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DetailActivity: AppCompatActivity() {
    companion object {
        const val EXTRA_ID = "anime_id"
    }

    private lateinit var binding: ActivityDetailBinding
    private val vm: DetailViewModel by viewModels()
    private var exoPlayer: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the toolbar with the back button
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val id = intent.getIntExtra(EXTRA_ID, -1)
        if (id == -1) {
            finish()
            return
        }
        vm.loadDetail(id)

        lifecycleScope.launch {
            vm.state.collectLatest { res ->
                when (res) {
                    is NetworkResult.Loading -> {
                        binding.progress.visibility = View.VISIBLE
                    }
                    is NetworkResult.Error -> {
                        binding.progress.visibility = View.GONE
                        binding.errorText.visibility = View.VISIBLE
                        binding.errorText.text = res.message
                    }
                    is NetworkResult.Success -> {
                        binding.progress.visibility = View.GONE
                        binding.errorText.visibility = View.GONE
                        display(res.data)
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun display(anime: Anime) {
        binding.title.text = anime.title
        binding.synopsis.text = anime.synopsis ?: "No synopsis available"
        binding.genres.text = if (anime.genres.isEmpty()) "N/A" else anime.genres.joinToString(", ")
        binding.cast.text = if (anime.cast.isEmpty()) "N/A" else anime.cast.joinToString(", ")
        binding.episodes.text = "Episodes: ${anime.episodes ?: "?"}"
        binding.score.text = "Score: ${anime.score ?: "N/A"}"

        val videoUrl = anime.trailerUrl
        if (!videoUrl.isNullOrBlank()) {
            if (videoUrl.contains("youtube.com") || videoUrl.contains("youtu.be")) {
                // If it's a YouTube URL, use WebView
                binding.webview.visibility = View.VISIBLE
                binding.poster.visibility = View.GONE

                // Make sure playerView is hidden
                binding.playerView.visibility = View.GONE

                binding.webview.settings.javaScriptEnabled = true
                binding.webview.webViewClient = WebViewClient()
                val id = videoUrl.substringAfter("v=", "").substringBefore("&")
                    .takeIf { it.isNotBlank() } ?: videoUrl.substringAfterLast("/")
                val embed = "<html><body style='margin:0;padding:0;'><iframe width='100%' height='100%' src='https://www.youtube.com/embed/$id?autoplay=0' frameborder='0' allowfullscreen></iframe></body></html>"
                binding.webview.loadData(embed, "text/html", "utf-8")
            } else {
                // For other video formats, use ExoPlayer
                binding.webview.visibility = View.GONE
                binding.poster.visibility = View.GONE
                initializePlayer(videoUrl)
            }
        } else {
            // Showing poster image if no video URL is available
            binding.webview.visibility = View.GONE
            binding.playerView.visibility = View.GONE
            binding.poster.visibility = View.VISIBLE
            Glide.with(this).load(anime.imageUrl).centerCrop().into(binding.poster)
        }
    }

    private fun initializePlayer(videoUrl: String) {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(this).build()
            binding.playerView.player = exoPlayer
            binding.playerView.visibility = View.VISIBLE
            binding.poster.visibility = View.GONE
        }
        val mediaItem = MediaItem.fromUri(videoUrl)
        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()
        exoPlayer?.play()
    }

    private fun releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer?.release()
            exoPlayer = null
        }
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }
}
