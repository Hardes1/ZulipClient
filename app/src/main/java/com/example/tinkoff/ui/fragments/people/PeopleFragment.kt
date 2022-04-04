package com.example.tinkoff.ui.fragments.people

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.MenuInflater

import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tinkoff.R
import com.example.tinkoff.data.classes.User
import com.example.tinkoff.databinding.FragmentPeopleBinding
import com.example.tinkoff.network.Repository
import com.example.tinkoff.recyclerFeatures.adapters.PeopleRecyclerAdapter
import com.example.tinkoff.recyclerFeatures.decorations.UserItemDecoration
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber


class PeopleFragment : Fragment() {


    private var _binding: FragmentPeopleBinding? = null
    private val binding: FragmentPeopleBinding
        get() = _binding!!
    private val adapter: PeopleRecyclerAdapter by lazy {
        PeopleRecyclerAdapter(userClickCallBack)
    }

    private var dataList: List<User> = listOf()
    private val userClickCallBack: (Int) -> Unit = { index ->
        val user = dataList.find { it.id == index }
        val action = PeopleFragmentDirections.actionNavigationPeopleToNavigationOtherProfile(user)
        Timber.d("onPreNavigation called")
        findNavController().navigate(
            action
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("Fragment recreated.")
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
        Timber.d("View recreated.")
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
        initializeDataList()
    }

    private fun initializeDataList() {
        Single.create<List<User>> { emitter ->
            emitter.onSuccess(Repository.generateUsersData())
        }.subscribeOn(Schedulers.computation()).observeOn(mainThread())
            .subscribe(object : SingleObserver<List<User>> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onSuccess(list: List<User>) {
                    dataList = list
                    adapter.updateList(dataList)
                }

                override fun onError(e: Throwable) {
                }
            })
    }


    private fun filterListByString(filter: String) {
        val filteredList: List<User> = if (filter.isNotEmpty()) {
            dataList.filter { it.name.contains(filter, ignoreCase = true) }
        } else {
            dataList.map { it.copy() }
        }
        adapter.updateList(filteredList)
    }


    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

}
