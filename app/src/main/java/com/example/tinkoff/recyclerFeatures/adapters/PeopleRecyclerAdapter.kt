package com.example.tinkoff.recyclerFeatures.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.example.tinkoff.data.classes.User
import com.example.tinkoff.data.states.UserStatus
import com.example.tinkoff.databinding.PeopleRecyclerItemBinding
import com.example.tinkoff.recyclerFeatures.diffUtils.UserDiffUtil

class PeopleRecyclerAdapter(private val userClickCallBack: (Int) -> Unit) :
    RecyclerView.Adapter<PeopleRecyclerAdapter.PeopleViewHolder>() {

    private val differ = AsyncListDiffer(this, UserDiffUtil())
    private var list: List<User>
        set(value) {
            differ.submitList(value)
        }
        get() = differ.currentList

    class PeopleViewHolder(
        private val userClickCallBack: (Int) -> Unit,
        private val binding: PeopleRecyclerItemBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.root.setOnClickListener {
                userClickCallBack(user.id)
            }
            binding.nameTextView.text = user.name
            binding.emailTextView.text = user.email
            binding.onlineStatus.isEnabled = when (user.status) {
                UserStatus.ONLINE -> true
                UserStatus.OFFLINE -> false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
        val binding =
            PeopleRecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PeopleViewHolder(userClickCallBack, binding)
    }

    override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size


    fun updateList(otherList: List<User>) {
        list = otherList
    }


}