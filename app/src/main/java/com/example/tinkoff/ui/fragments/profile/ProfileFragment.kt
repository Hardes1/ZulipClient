package com.example.tinkoff.ui.fragments.profile

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.tinkoff.R
import com.example.tinkoff.data.states.UserStatus
import com.example.tinkoff.databinding.FragmentProfileBinding


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

        @JvmStatic
        fun newInstance() =
            ProfileFragment()
    }
}