package com.example.tinkoff.presentation.recyclerFeatures.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.tinkoff.R
import com.example.tinkoff.databinding.PeopleRecyclerItemBinding
import com.example.tinkoff.model.states.UserStatus
import com.example.tinkoff.presentation.classes.User
import com.example.tinkoff.presentation.recyclerFeatures.diffUtils.UsersDiffUtil

class PeopleRecyclerAdapter(
    private val userClickCallBack: (user: User) -> Unit,
) :
    RecyclerView.Adapter<PeopleRecyclerAdapter.PeopleViewHolder>() {

    class PeopleViewHolder(
        private val context: Context,
        private val userClickCallBack: (User) -> Unit,
        private val binding: PeopleRecyclerItemBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.root.setOnClickListener {
                userClickCallBack(user)
            }
            binding.nameTextView.text = user.name
            binding.emailTextView.text = user.email
            binding.onlineStatus.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    when (user.status) {
                        UserStatus.ACTIVE -> R.color.green_online_status_color
                        UserStatus.IDLE -> R.color.yellow_online_status_color
                        UserStatus.OFFLINE -> R.color.red_online_status_color
                    }
                )
            )
            Glide.with(context).load(user.avatarUrl).placeholder(R.drawable.progress_animation)
                .error(R.drawable.no_avatar)
                .centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.avatarIcon)
        }
    }

    private val differ = AsyncListDiffer(this, UsersDiffUtil())
    private var list: List<User>
        private set(value) {
            differ.submitList(value) { }
        }
        get() = differ.currentList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
        val binding =
            PeopleRecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PeopleViewHolder(parent.context, userClickCallBack, binding)
    }

    override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun updateList(otherList: List<User>) {
        list = otherList
    }
}
