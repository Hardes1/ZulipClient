package com.example.tinkoff.presentation.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.tinkoff.R
import com.example.tinkoff.databinding.FragmentProfileBinding
import com.example.tinkoff.model.states.LoadingData
import com.example.tinkoff.model.states.UserStatus
import com.example.tinkoff.presentation.classes.User
import com.example.tinkoff.presentation.fragments.profile.elm.UserEffect
import com.example.tinkoff.presentation.fragments.profile.elm.UserEvent
import com.example.tinkoff.presentation.fragments.profile.elm.UserState
import com.example.tinkoff.presentation.fragments.profile.elm.UserStoreFactory
import timber.log.Timber
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store

class ProfileFragment : ElmFragment<UserEvent, UserEffect, UserState>() {

    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding
        get() = _binding!!
    private var refreshItem: MenuItem? = null
    private var refreshItemVisibility: Boolean = false

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
    }

    private fun initializeUser(user: User?) {
        if (user == null)
            return
        binding.nameTextview.text = user.name
        downloadImageByUrl(user.avatarUrl)
        when (user.status) {
            UserStatus.ACTIVE -> {
                binding.onlineStatusTextview.apply {
                    text =
                        resources.getString(R.string.user_online_text)
                    setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.green_online_status_color
                        )
                    )
                }
            }
            UserStatus.OFFLINE -> {
                binding.onlineStatusTextview.apply {
                    text =
                        resources.getString(R.string.user_offline_text)
                    setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red_online_status_color
                        )
                    )
                }
            }
            UserStatus.IDLE -> {
                binding.onlineStatusTextview.apply {
                    text = resources.getString(R.string.user_idle_text)
                    setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.yellow_online_status_color
                        )
                    )
                }
            }
        }
    }

    private fun downloadImageByUrl(url: String?) {
        Glide.with(requireContext()).load(url).placeholder(R.drawable.progress_animation)
            .error(R.drawable.no_avatar)
            .centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.avatarImageview)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        storeHolder
        refreshItem = menu.findItem(R.id.action_refresh)
        refreshItem?.setOnMenuItemClickListener {
            store.accept(UserEvent.UI.InitUser(null))
            true
        }
        refreshItem?.isVisible = refreshItemVisibility
        super.onCreateOptionsMenu(menu, inflater)
    }

    override val initEvent: UserEvent
        get() {
            return UserEvent.UI.InitUser(arguments?.getParcelable(USER_KEY))
        }

    override fun createStore(): Store<UserEvent, UserEffect, UserState> =
        UserStoreFactory().provide()

    override fun render(state: UserState) {
        initializeUser(state.user)
        when (state.status) {
            LoadingData.LOADING -> {
                binding.root.displayedChild = SHOW_SHIMMER
            }
            LoadingData.ERROR, LoadingData.FINISHED -> {
                binding.root.displayedChild = HIDE_SHIMMER
            }
            else -> {
                throw NotImplementedError()
            }
        }
        refreshItem?.isVisible = state.isRefreshVisible
        refreshItemVisibility = state.isRefreshVisible
    }

    override fun handleEffect(effect: UserEffect) {
        when (effect) {
            is UserEffect.UserLoadError -> {
                Timber.e(effect.error, "error while loading user profile")
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_profile_loading),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val SHOW_SHIMMER = 0
        private const val HIDE_SHIMMER = 1
        const val USER_KEY = "USER"
    }
}
