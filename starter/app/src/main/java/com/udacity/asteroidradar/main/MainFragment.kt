package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        setHasOptionsMenu(true)

        val adapter = AsteroidListAdapter(AsteroidClickListener { asteroidId ->
            viewModel.onAsteroidClicked(asteroidId)
        })

        binding.asteroidRecycler.adapter = adapter

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.asteroidsDataFlow.collect {
                    adapter.submitList(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.navigateToAsteroidDetailEventFlow.collect {
                val action = MainFragmentDirections.actionShowDetail(it)
                findNavController().navigate(action)
            }
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_all_menu -> {
                viewModel.getAsteroids(AsteroidFilter.ALL)
            }
            R.id.show_week_menu -> {
                viewModel.getAsteroids(AsteroidFilter.WEEK)
            }
            R.id.show_today_menu -> {
                viewModel.getAsteroids(AsteroidFilter.TODAY)
            }
        }
        return true
    }
}
