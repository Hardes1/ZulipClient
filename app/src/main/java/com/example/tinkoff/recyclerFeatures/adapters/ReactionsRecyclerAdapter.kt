package com.example.tinkoff.recyclerFeatures.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tinkoff.databinding.ReactionItemBinding

class ReactionsRecyclerAdapter(
    private val onPositionChanged : (Int) -> Unit,
    private val listOfReactions: List<String>
) :
    RecyclerView.Adapter<ReactionsRecyclerAdapter.ReactionViewHolder>() {

    class ReactionViewHolder(
        private val onPositionChanged: (Int) -> Unit,
        private val binding: ReactionItemBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.reactionTextview.setOnClickListener { onPositionChanged(adapterPosition) }
            binding.reactionTextview.text = item
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReactionViewHolder {
        return ReactionViewHolder(
            onPositionChanged,
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
