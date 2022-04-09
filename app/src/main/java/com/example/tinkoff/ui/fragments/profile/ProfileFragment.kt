package com.example.tinkoff.ui.fragments.profile


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d(getString(R.string.debug_fragment_recreated))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d(getString(R.string.debug_view_recreated))
        if (arguments != null) {
            val args: ProfileFragmentArgs by navArgs()
            viewModel.ownUser.value = args.user
            viewModel.state.value = LoadingData.FINISHED
        } else {
            viewModel.refreshProfile(requireContext())
        }
        viewModel.state.observe(viewLifecycleOwner) {
            if (it != LoadingData.NONE && it.ordinal != binding.root.displayedChild)
                binding.root.displayedChild = it.ordinal
        }

        viewModel.ownUser.observe(viewLifecycleOwner) {
            initializeUser(
                it,
                when (it.id) {
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {

        const val DELAY_TIME: Long = 1000
    }

}
