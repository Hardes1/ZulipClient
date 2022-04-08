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
import com.example.tinkoff.data.states.UserStatus
import com.example.tinkoff.databinding.FragmentProfileBinding
import com.example.tinkoff.network.Repository
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit


class ProfileFragment : Fragment() {


    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding
        get() = _binding!!
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
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
            initializeUser(args.user, View.INVISIBLE)
            binding.root.showNext()
        } else {
            if (viewModel.ownUser == null)
                getOwnUserFromWeb()
            else {
                initializeUser(viewModel.ownUser, View.VISIBLE)
                binding.root.showNext()
            }
        }
    }

    private fun getOwnUserFromWeb() {
        Single.create<User> { emitter ->
            emitter.onSuccess(Repository.generatePersonalUserData(requireContext()))
        }.delay(DELAY_TIME, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<User> {

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                    binding.shimmerLayout.startShimmer()
                }

                override fun onSuccess(value: User) {
                    viewModel.ownUser = value
                    initializeUser(viewModel.ownUser, View.VISIBLE)
                    binding.shimmerLayout.stopShimmer()
                    binding.root.showNext()
                }


                override fun onError(e: Throwable) {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.error_profile_loading),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun initializeUser(user: User?, exitButtonVisibility: Int) {
        binding.nameTextview.text = user?.name
        binding.logoutButton.visibility = exitButtonVisibility
        binding.avatarImageview.setImageResource(user?.drawableId ?: R.drawable.ic_send)
        binding.stateTextview.apply {
            visibility = View.VISIBLE
        }
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        compositeDisposable.dispose()
    }

    companion object {
        const val DELAY_TIME: Long = 1000
    }

}
