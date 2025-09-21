package com.seekho.anime

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.SearchView
import com.seekho.anime.databinding.ActivityMainBinding
import com.seekho.anime.ui.detail.DetailActivity
import com.seekho.anime.ui.list.AnimeListAdapter
import com.seekho.anime.ui.list.AnimeListViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val vm: AnimeListViewModel by viewModels()
    private lateinit var adapter: AnimeListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the toolbar with the search view
        setSupportActionBar(binding.toolbar)

        // Setup RecyclerView
        adapter = AnimeListAdapter { anime ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_ID, anime.malId)
            startActivity(intent)
        }
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = adapter

        // Pull-to-refresh
        binding.swipeRefresh.setOnRefreshListener {
            vm.refresh()
        }

        // Set up the SearchView listener
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // This is called when the user submits the search query
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    // Call the ViewModel to perform the search
                    // You will need to implement this 'search' function in your ViewModel
                    // to handle the API call for the search query.
                    vm.search(query)
                }
                // Return true to indicate the query has been handled
                return true
            }

            // This is called when the query text is changed by the user
            override fun onQueryTextChange(newText: String?): Boolean {
                // You can add real-time search logic here if desired
                // For example, if the query is empty, show the original list
                if (newText.isNullOrEmpty()) {
                    vm.refresh()
                }
                return true
            }
        })

        // Collect state from the ViewModel
        lifecycleScope.launch {
            vm.uiState.collectLatest { state ->
                when (state) {
                    is AnimeListViewModel.UiState.Loading -> {
                        binding.progress.visibility = View.VISIBLE
                        binding.errorText.visibility = View.GONE
                        binding.swipeRefresh.isRefreshing = false
                    }
                    is AnimeListViewModel.UiState.Success -> {
                        binding.progress.visibility = View.GONE
                        adapter.submitList(state.data)
                        binding.swipeRefresh.isRefreshing = false
                        binding.errorText.visibility =
                            if (state.data.isEmpty()) View.VISIBLE else View.GONE
                        binding.errorText.text =
                            if (state.data.isEmpty()) "No cached data." else ""
                    }
                    is AnimeListViewModel.UiState.Error -> {
                        binding.progress.visibility = View.GONE
                        binding.errorText.visibility = View.VISIBLE
                        binding.errorText.text = state.message
                        binding.swipeRefresh.isRefreshing = false
                    }
                }
            }
        }
    }
}