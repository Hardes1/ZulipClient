package com.example.tinkoff.ui.fragments.profile

import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.tinkoff.R
import com.example.tinkoff.data.states.UserStatus
import com.example.tinkoff.databinding.FragmentProfileBinding
import timber.log.Timber


class ProfileFragment : Fragment() {


    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding
        get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (arguments != null) {
            val args: ProfileFragmentArgs by navArgs()
            val user = args.user
            binding.nameTextview.text = user?.name
            binding.logoutButton.visibility = View.INVISIBLE
            when (user?.status) {
                UserStatus.ONLINE -> {
                    binding.onlineStatusTextview.apply {
                        text =
                            resources.getString(R.string.user_online_text)
                        isEnabled = true
                    }
                }
                UserStatus.OFFLINE -> {
                    binding.onlineStatusTextview.apply {
                        text =
                            resources.getString(R.string.user_offline_text)
                        isEnabled = false
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val searchItem = menu.findItem(R.id.action_search)
        Timber.d("search: called onCreateOptionsMenu")
        searchItem.isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            ProfileFragment()
    }
}