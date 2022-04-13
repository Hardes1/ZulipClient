package com.example.tinkoff.ui.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.tinkoff.R
import com.example.tinkoff.data.classes.User
import com.example.tinkoff.data.states.LoadingData
import com.example.tinkoff.data.states.UserStatus
import com.example.tinkoff.databinding.FragmentProfileBinding
import com.example.tinkoff.ui.fragments.messages.MessageFragment
import timber.log.Timber

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding
        get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private var refreshItem: MenuItem? = null

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
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d(getString(R.string.debug_view_recreated))
        if (arguments != null) {
            viewModel.ownUser.value = arguments?.getParcelable(USER_KEY)
            viewModel.state.value = LoadingData.FINISHED
        }
        initializeLiveDataObservers()
    }

    private fun initializeLiveDataObservers() {
        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                LoadingData.LOADING, LoadingData.FINISHED -> {
                    refreshItem?.isVisible = false
                    if (binding.root.displayedChild != it.ordinal)
                        binding.root.displayedChild = it.ordinal
                }
                LoadingData.ERROR -> {
                    refreshItem?.isVisible = true
                    if (binding.root.displayedChild != LoadingData.FINISHED.ordinal)
                        binding.root.displayedChild = LoadingData.FINISHED.ordinal
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error_profile_loading),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> throw NotImplementedError()
            }
        }

        viewModel.ownUser.observe(viewLifecycleOwner) { user ->
            initializeUser(
                user,
                when (user.id) {
                    MessageFragment.MY_ID -> View.VISIBLE
                    else -> View.INVISIBLE
                }
            )
        }
    }

    private fun initializeUser(user: User, exitButtonVisibility: Int) {
        binding.nameTextview.text = user.name
        binding.logoutButton.visibility = exitButtonVisibility
        binding.avatarImageview.setImageResource(user.drawableId)
        binding.stateTextview.apply {
            visibility = View.VISIBLE
        }
        when (user.status) {
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        refreshItem = menu.findItem(R.id.action_refresh)
        refreshItem?.setOnMenuItemClickListener {
            viewModel.state.value = LoadingData.LOADING
            viewModel.refreshProfile(requireContext())
            true
        }
        if (arguments == null)
            viewModel.refreshProfile(requireContext())
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val USER_KEY = "USER"
        const val DELAY_TIME: Long = 1000
    }
}
