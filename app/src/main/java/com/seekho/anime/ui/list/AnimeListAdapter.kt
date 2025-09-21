package com.seekho.anime.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seekho.anime.databinding.ItemAnimeBinding
import com.seekho.anime.model.Anime

class AnimeListAdapter(private val onClick: (Anime) -> Unit)
    : ListAdapter<Anime, AnimeListAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemAnimeBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val b: ItemAnimeBinding): RecyclerView.ViewHolder(b.root) {
        fun bind(item: Anime) {
            b.title.text = item.title
            b.episodes.text = "Episodes: ${item.episodes ?: "?"}"
            b.score.text = "Score: ${item.score ?: "N/A"}"
            val url = item.imageUrl
            Glide.with(b.poster.context)
                .load(url)
                .centerCrop()
                .into(b.poster)

            b.root.setOnClickListener { onClick(item) }
        }
    }

    companion object {
        val DIFF = object: DiffUtil.ItemCallback<Anime>() {
            override fun areItemsTheSame(oldItem: Anime, newItem: Anime) = oldItem.malId == newItem.malId
            override fun areContentsTheSame(oldItem: Anime, newItem: Anime) = oldItem == newItem
        }
    }
}
