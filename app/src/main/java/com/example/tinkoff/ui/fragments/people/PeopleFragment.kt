package com.example.tinkoff.ui.fragments.people

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.tinkoff.R
import com.example.tinkoff.data.states.LoadingData
import com.example.tinkoff.databinding.FragmentPeopleBinding
import com.example.tinkoff.recyclerFeatures.adapters.PeopleRecyclerAdapter
import com.example.tinkoff.recyclerFeatures.decorations.UserItemDecoration
import timber.log.Timber


class PeopleFragment : Fragment() {


    private var _binding: FragmentPeopleBinding? = null
    private val binding: FragmentPeopleBinding
        get() = _binding!!
    private val adapter: PeopleRecyclerAdapter by lazy {
        PeopleRecyclerAdapter(userClickCallBack, shimmerCallBack)
    }
    private val viewModel: PeopleViewModel by viewModels()
    private var searchItem: MenuItem? = null
    private val userClickCallBack: (Int) -> Unit = { index ->
        val user = viewModel.displayedUsersList.value?.find { it.id == index }
        val action = PeopleFragmentDirections.actionNavigationPeopleToNavigationOtherProfile(user)
        findNavController().navigate(
            action
        )
    }
    private val shimmerCallBack = {
        if (viewModel.state.value != LoadingData.FINISHED)
            viewModel.state.value = LoadingData.FINISHED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d(getString(R.string.debug_fragment_recreated))
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentPeopleBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d(getString(R.string.debug_view_recreated))
        initializeRecyclerView()
        viewModel.displayedUsersList.observe(viewLifecycleOwner) {
            adapter.updateList(it)
        }
        initializeLiveDataObservers()
    }


    private fun initializeLiveDataObservers() {
        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                LoadingData.LOADING, LoadingData.FINISHED -> {
                    binding.root.displayedChild =
                        it.ordinal
                }
                LoadingData.ERROR -> {
                    binding.root.displayedChild = LoadingData.FINISHED.ordinal
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error_people_loading),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> throw NotImplementedError()
            }
        }
        viewModel.isDownloaded.observe(viewLifecycleOwner) { isDownloaded ->
            if (isDownloaded) {
                val query = (searchItem?.actionView as SearchView?)?.query?.toString() ?: ""
                viewModel.searchUsers(query)
            } else
                viewModel.refreshPeopleData(requireContext())
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        searchItem = menu.findItem(R.id.action_search)
        searchItem?.isVisible = true
        val searchView = searchItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.searchUsers(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.searchUsers(newText)
                return false
            }
        })
        val refreshItem = menu.findItem(R.id.action_refresh)
        refreshItem.isVisible = true
        refreshItem.setOnMenuItemClickListener {
            viewModel.isDownloaded.value = false
            true
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun initializeRecyclerView() {
        binding.peopleRecyclerView.addItemDecoration(
            UserItemDecoration(
                resources.getDimensionPixelSize(R.dimen.people_small_spacing_recycler_view),
                resources.getDimensionPixelSize(R.dimen.people_big_spacing_recycler_view)
            )
        )
        binding.peopleRecyclerView.adapter = adapter
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
