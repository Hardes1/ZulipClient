package com.example.tinkoff.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.tinkoff.databinding.ReactionItemBinding

class ReactionsRecyclerAdapter(
    private val positionLiveData: MutableLiveData<Int>,
    private val listOfReactions: List<String>
) :
    RecyclerView.Adapter<ReactionsRecyclerAdapter.ReactionViewHolder>() {

    class ReactionViewHolder(
        private val positionLiveData: MutableLiveData<Int>,
        private val binding: ReactionItemBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.reactionTextview.setOnClickListener { positionLiveData.value = adapterPosition }
            binding.reactionTextview.text = item
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReactionViewHolder {
        return ReactionViewHolder(
            positionLiveData,
            ReactionItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ReactionViewHolder, position: Int) {
        holder.bind(listOfReactions[position])
    }

    override fun getItemCount(): Int {
        return listOfReactions.size
    }
}
