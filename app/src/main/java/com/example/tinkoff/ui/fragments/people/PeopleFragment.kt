package com.example.tinkoff.ui.fragments.people

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.MenuInflater

import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.tinkoff.R
import com.example.tinkoff.data.classes.User
import com.example.tinkoff.databinding.FragmentPeopleBinding
import com.example.tinkoff.network.Repository
import com.example.tinkoff.recyclerFeatures.adapters.PeopleRecyclerAdapter
import com.example.tinkoff.recyclerFeatures.decorations.UserItemDecoration
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit


class PeopleFragment : Fragment() {


    private var _binding: FragmentPeopleBinding? = null
    private val binding: FragmentPeopleBinding
        get() = _binding!!
    private val adapter: PeopleRecyclerAdapter by lazy {
        PeopleRecyclerAdapter(userClickCallBack)
    }
    private val viewModel: PeopleViewModel by viewModels()
    private val userClickCallBack: (Int) -> Unit = { index ->
        val user = viewModel.list?.find { it.id == index }
        val action = PeopleFragmentDirections.actionNavigationPeopleToNavigationOtherProfile(user)
        findNavController().navigate(
            action
        )
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
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val searchItem = menu.findItem(R.id.action_search)
        searchItem.isVisible = true
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterListByString(newText)
                return false
            }
        })
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
        if (viewModel.list == null) {
            initializeDataList()
        } else {
            adapter.updateList(viewModel.list ?: listOf())
            binding.root.showNext()
        }
    }

    private fun initializeDataList() {
        Single.create<List<User>> { emitter ->
            emitter.onSuccess(Repository.generateUsersData())
        }.delay(DELAY_TIME, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.computation()).observeOn(mainThread())
            .subscribe(object : SingleObserver<List<User>> {
                override fun onSubscribe(d: Disposable) {
                    binding.shimmerLayout.startShimmer()
                }

                override fun onSuccess(list: List<User>) {
                    viewModel.list = list
                    adapter.updateList(viewModel.list ?: emptyList())
                    if (_binding != null) {
                        binding.shimmerLayout.stopShimmer()
                        binding.root.showNext()
                    }
                }

                override fun onError(e: Throwable) {
                    Snackbar.make(
                        binding.peopleRecyclerView,
                        getString(R.string.error_people_loading),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            })
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    private fun filterListByString(filter: String) {
        val filteredList: List<User>? = if (filter.isNotEmpty()) {
            viewModel.list?.filter { it.name.contains(filter, ignoreCase = true) }
        } else {
            viewModel.list?.map { it.copy() }
        }
        adapter.updateList(filteredList ?: emptyList())
    }

    companion object {
        private const val DELAY_TIME: Long = 1000
    }


}
