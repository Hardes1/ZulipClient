package com.example.tinkoff.ui.fragments.people

import android.os.Bundle
import android.view.*

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
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.rxkotlin.subscribeBy
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
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val publishSubject: PublishSubject<String> = PublishSubject.create()
    private lateinit var searchItem: MenuItem
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
        searchItem = menu.findItem(R.id.action_search)
        searchItem.isVisible = true
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                publishSubject.onNext(newText)
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
            initializePublishSubject()
            binding.shimmerLayout.stopShimmer()
            binding.root.showNext()
        }
    }

    private fun initializePublishSubject() {
        publishSubject.observeOn(Schedulers.computation()).map { it.trim() }
            .distinctUntilChanged().debounce(DEBOUNCE_TIME, TimeUnit.MILLISECONDS)
            .switchMapSingle { queryString ->
                if (queryString.isEmpty() || queryString.isBlank())
                    Single.just<List<User>>(viewModel.list)
                else
                    Single.just<List<User>>(viewModel.list?.filter {
                        it.name.contains(
                            queryString,
                            ignoreCase = true
                        )
                    })
            }.observeOn(mainThread()).subscribeBy(
                onNext = { adapter.updateList(it) },
                onError = {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.error_filtering_data),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            ).addTo(compositeDisposable)
    }

    private fun initializeDataList() {
        Single.create<List<User>> { emitter ->
            emitter.onSuccess(Repository.generateUsersData())
        }.delay(DELAY_TIME, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.computation()).observeOn(mainThread())
            .subscribe(object : SingleObserver<List<User>> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                    binding.shimmerLayout.startShimmer()
                }

                override fun onSuccess(list: List<User>) {
                    viewModel.list = list
                    val searchView = searchItem.actionView
                    require(searchView is SearchView)
                    adapter.updateList(viewModel.list ?: emptyList())
                    initializePublishSubject()
                    publishSubject.onNext(searchView.query.toString())
                    binding.shimmerLayout.stopShimmer()
                    binding.root.showNext()
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
        compositeDisposable.dispose()
    }


    companion object {
        private const val DELAY_TIME: Long = 2500
        private const val DEBOUNCE_TIME: Long = 300
    }


}
