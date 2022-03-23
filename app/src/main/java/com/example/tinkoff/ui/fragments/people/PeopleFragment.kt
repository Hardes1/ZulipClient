package com.example.tinkoff.ui.fragments.people

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.tinkoff.R
import com.example.tinkoff.data.classes.User
import com.example.tinkoff.data.states.UserStatus
import com.example.tinkoff.databinding.FragmentPeopleBinding
import com.example.tinkoff.recyclerFeatures.adapters.PeopleRecyclerAdapter
import com.example.tinkoff.recyclerFeatures.decorations.UserItemDecoration


class PeopleFragment : Fragment() {


    private var _binding: FragmentPeopleBinding? = null
    private val binding: FragmentPeopleBinding
        get() = _binding!!
    private val adapter = PeopleRecyclerAdapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPeopleBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity).supportActionBar?.show()
        binding.peopleRecyclerView.addItemDecoration(
            UserItemDecoration(
                resources.getDimensionPixelSize(R.dimen.people_small_spacing_recycler_view),
                resources.getDimensionPixelSize(R.dimen.people_big_spacing_recycler_view)
            )
        )
        binding.peopleRecyclerView.adapter = adapter
        adapter.updateList(generateData())
    }

    fun generateData(): List<User> {
        var counter = 0
        return listOf(
            User(counter++, "ABOBA", "abobaMail@mail.ru", UserStatus.OFFLINE),
            User(counter++, "ABOBA", "abobaMail@mail.ru", UserStatus.OFFLINE),
            User(counter++, "ABOBAfds", "abobaMail@mail.ru", UserStatus.OFFLINE),
            User(counter++, "ABOBAa", "abobaMail@mail.ru", UserStatus.ONLINE),
            User(counter++, "ABOBAfds", "abobaMail@mail.ru", UserStatus.OFFLINE),
            User(counter++, "ABOBA", "abobaMail@mail.ru", UserStatus.OFFLINE),
            User(counter++, "ABOBAfds", "abobaMail@mail.ru", UserStatus.OFFLINE),
            User(counter++, "ABOBA spirs", "abobaMail@mail.ru", UserStatus.ONLINE),
            User(counter++, "ABOBA pirs", "abobaMail@mail.ru", UserStatus.OFFLINE),
            User(counter++, "ABOBA poso", "abobaMail@mail.ru", UserStatus.OFFLINE)
        )
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