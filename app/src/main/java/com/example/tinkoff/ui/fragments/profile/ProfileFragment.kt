package com.example.tinkoff.ui.fragments.profile


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
                else -> {
                    throw NotImplementedError()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
