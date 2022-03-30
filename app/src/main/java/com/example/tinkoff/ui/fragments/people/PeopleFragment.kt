package com.example.tinkoff.ui.fragments.people

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tinkoff.R
import com.example.tinkoff.data.classes.User
import com.example.tinkoff.data.states.UserStatus
import com.example.tinkoff.databinding.FragmentPeopleBinding
import com.example.tinkoff.recyclerFeatures.adapters.PeopleRecyclerAdapter
import com.example.tinkoff.recyclerFeatures.decorations.UserItemDecoration
import timber.log.Timber


class PeopleFragment : Fragment() {


    private var _binding: FragmentPeopleBinding? = null
    private val binding: FragmentPeopleBinding
        get() = _binding!!
    private val adapter: PeopleRecyclerAdapter by lazy {
        PeopleRecyclerAdapter(userClickCallBack)
    }

    private var dataList: List<User> = generateData()
    private val userClickCallBack: (Int) -> Unit = { index ->
        val user = dataList.find { it.id == index }
        val action = PeopleFragmentDirections.actionNavigationPeopleToNavigationOtherProfile(user)
        Timber.d("onPreNavigation called")
        findNavController().navigate(
            action
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPeopleBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        binding.peopleRecyclerView.addItemDecoration(
            UserItemDecoration(
                resources.getDimensionPixelSize(R.dimen.people_small_spacing_recycler_view),
                resources.getDimensionPixelSize(R.dimen.people_big_spacing_recycler_view)
            )
        )
        binding.peopleRecyclerView.adapter = adapter
        adapter.updateList(dataList)
    }

    private fun generateData(): List<User> {
        var counter = 0
        return listOf(
            User(counter++, "Устинов Георгий", "abobaMail@mail.ru", UserStatus.ONLINE),
            User(counter++, "Устинова Алёна", "abobaMail@mail.ru", UserStatus.OFFLINE),
            User(counter++, "Привет, как дела", "abobaMail@mail.ru", UserStatus.OFFLINE),
            User(counter++, "Проверяю текст", "abobaMail@mail.ru", UserStatus.ONLINE),
            User(counter++, "Мельников Игорь", "abobaMail@mail.ru", UserStatus.OFFLINE),
            User(counter++, "Как же хочется прыгать", "abobaMail@mail.ru", UserStatus.OFFLINE),
            User(counter++, "Откуда я знаю", "abobaMail@mail.ru", UserStatus.OFFLINE),
            User(counter++, "ABOBA spirs", "abobaMail@mail.ru", UserStatus.ONLINE),
            User(counter++, "ABOBA pirs", "abobaMail@mail.ru", UserStatus.OFFLINE),
            User(counter++, "ABOBA poso", "abobaMail@mail.ru", UserStatus.OFFLINE)
        )
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


    companion object {

        @JvmStatic
        fun newInstance() =
            PeopleFragment()
    }
}