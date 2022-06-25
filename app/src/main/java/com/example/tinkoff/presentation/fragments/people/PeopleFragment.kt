package com.example.tinkoff.presentation.fragments.people

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import com.example.tinkoff.R
import com.example.tinkoff.databinding.FragmentPeopleBinding
import com.example.tinkoff.model.states.LoadingData
import com.example.tinkoff.presentation.activities.MainActivity
import com.example.tinkoff.presentation.classes.User
import com.example.tinkoff.presentation.fragments.people.di.DaggerUsersComponent
import com.example.tinkoff.presentation.fragments.people.elm.UsersEffect
import com.example.tinkoff.presentation.fragments.people.elm.UsersEvent
import com.example.tinkoff.presentation.fragments.people.elm.UsersState
import com.example.tinkoff.presentation.fragments.people.elm.UsersStoreFactory
import com.example.tinkoff.presentation.fragments.profile.ProfileFragment
import com.example.tinkoff.presentation.recyclerFeatures.adapters.PeopleRecyclerAdapter
import com.example.tinkoff.presentation.recyclerFeatures.decorations.UserItemDecoration
import timber.log.Timber
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store
import javax.inject.Inject

class PeopleFragment : ElmFragment<UsersEvent, UsersEffect, UsersState>() {
    private var _binding: FragmentPeopleBinding? = null
    private val binding: FragmentPeopleBinding
        get() = _binding!!
    private val adapter: PeopleRecyclerAdapter by lazy {
        PeopleRecyclerAdapter(::userClickCallBack)
    }
    private var searchItem: MenuItem? = null
    private var state: UsersState = UsersState()
    private lateinit var peopleLoadingErrorToast: Toast

    private val itemDecoration: UserItemDecoration by lazy {
        UserItemDecoration(
            resources.getDimensionPixelSize(R.dimen.people_small_spacing_recycler_view),
            resources.getDimensionPixelSize(R.dimen.people_big_spacing_recycler_view)
        )
    }

    @Inject
    lateinit var factory: UsersStoreFactory

    private fun userClickCallBack(user: User) {
        val action =
            PeopleFragmentDirections.actionNavigationPeopleToNavigationOtherProfile().apply {
                arguments.putParcelable(
                    ProfileFragment.USER_KEY,
                    user
                )
            }
        findNavController().navigate(
            action
        )
    }

    override fun onAttach(context: Context) {
        DaggerUsersComponent.factory()
            .create(
                (requireActivity() as MainActivity)
                    .getMainActivityComponent()
            )
            .inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d(getString(R.string.debug_fragment_recreated))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentPeopleBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d(getString(R.string.debug_view_recreated))
        peopleLoadingErrorToast = Toast.makeText(
            requireContext(),
            getString(R.string.error_people_loading),
            Toast.LENGTH_SHORT
        )
        initializeRecyclerView()
    }

    override fun handleEffect(effect: UsersEffect) {
        when (effect) {
            is UsersEffect.UsersLoadError -> {
                peopleLoadingErrorToast.cancel()
                peopleLoadingErrorToast.show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        searchItem = menu.findItem(R.id.action_search)
        searchItem?.isVisible = true
        val searchView = searchItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                store.accept(UsersEvent.UI.FilterUsers(query))
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                store.accept(UsersEvent.UI.FilterUsers(newText))
                return false
            }
        })
        val refreshItem = menu.findItem(R.id.action_refresh)
        refreshItem?.isVisible = true
        refreshItem?.setOnMenuItemClickListener {
            store.accept(UsersEvent.UI.LoadUsers)
            true
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun initializeRecyclerView() {
        binding.peopleRecyclerView.addItemDecoration(
            itemDecoration
        )
        binding.peopleRecyclerView.adapter = adapter
    }

    override val initEvent: UsersEvent
        get() = UsersEvent.UI.InitUsers

    override fun createStore(): Store<UsersEvent, UsersEffect, UsersState> {
        return factory.provide()
    }

    override fun render(state: UsersState) {
        if (state.usersList != null)
            adapter.updateList(state.usersList)
        val status =
            when (state.status) {
                LoadingData.LOADING -> {
                    SHOW_SHIMMER
                }
                LoadingData.ERROR -> {
                    HIDE_SHIMMER
                }
                LoadingData.FINISHED -> {
                    HIDE_SHIMMER
                }
            }
        if (status != binding.root.displayedChild)
            binding.root.displayedChild = status
        if (state.needToSearch) {
            store.accept(UsersEvent.UI.FilterUsers(getSearchString()))
        }
        this.state = state
    }

    private fun getSearchString(): String {
        return (searchItem?.actionView as SearchView?)?.query?.toString() ?: ""
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val SHOW_SHIMMER = 0
        private const val HIDE_SHIMMER = 1
    }
}
