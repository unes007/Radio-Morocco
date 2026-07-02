package com.example.radiomaroc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.radiomaroc.databinding.ItemStationBinding

class StationAdapter(
    private val stations: List<Station>,
    private val onClick: (Station) -> Unit
) : RecyclerView.Adapter<StationAdapter.StationViewHolder>() {

    private var currentlyPlayingId: Int? = null

    inner class StationViewHolder(val binding: ItemStationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        val binding = ItemStationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return StationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
        val station = stations[position]
        holder.binding.textStationName.text = station.name
        holder.binding.textStationCategory.text = station.category

        val isPlaying = station.id == currentlyPlayingId
        holder.binding.iconPlayState.setImageResource(
            if (isPlaying) android.R.drawable.ic_media_pause
            else android.R.drawable.ic_media_play
        )

        holder.binding.root.setOnClickListener {
            onClick(station)
        }
    }

    override fun getItemCount(): Int = stations.size

    fun setPlayingStation(stationId: Int?) {
        currentlyPlayingId = stationId
        notifyDataSetChanged()
    }
}
