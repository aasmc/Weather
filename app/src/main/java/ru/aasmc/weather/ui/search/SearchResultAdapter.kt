package ru.aasmc.weather.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.aasmc.weather.data.model.SearchResult
import ru.aasmc.weather.data.preferences.WeatherPreferences
import ru.aasmc.weather.databinding.ItemSearchResultBinding

class SearchResultAdapter(
    private val delegate: OnItemClickListener,
) : PagedListAdapter<SearchResult, SearchResultAdapter.ViewHolder>(
    SearchResultDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchResult = getItem(position)
        searchResult?.let { result ->
            holder.itemView.setOnClickListener {
                delegate.onSearchResultClicked(result)
            }
            holder.bind(result)
        }
    }

    class ViewHolder(private val binding: ItemSearchResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(searchResult: SearchResult) {
            binding.searchResult = searchResult
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding =
                    ItemSearchResultBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class SearchResultDiffCallback : DiffUtil.ItemCallback<SearchResult>() {
        override fun areItemsTheSame(
            oldItem: SearchResult,
            newItem: SearchResult
        ): Boolean = oldItem.name == newItem.name

        override fun areContentsTheSame(
            oldItem: SearchResult,
            newItem: SearchResult
        ): Boolean = oldItem == newItem

    }

    interface OnItemClickListener {
        fun onSearchResultClicked(searchResult: SearchResult)
    }
}