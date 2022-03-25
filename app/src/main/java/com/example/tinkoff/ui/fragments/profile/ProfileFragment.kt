package com.example.tinkoff.ui.fragments.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tinkoff.R
import com.example.tinkoff.data.classes.User
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
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("${arguments?.getParcelable<User>(KEY)}")
        if (arguments != null) {
            val user = arguments?.getParcelable<User>(KEY)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        const val KEY = "user"

        @JvmStatic
        fun newInstance() =
            ProfileFragment()
    }
}