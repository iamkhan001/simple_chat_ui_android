package sg.mirobotic.amazonamplify.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.mirobotic.amazonamplify.data.local.Content
import sg.mirobotic.amazonamplify.data.local.ItemClickListener
import sg.mirobotic.amazonamplify.databinding.ItemCardOptionBinding

class CardOptionsAdapter(private val options: ArrayList<Content.Button>, private val itemClickListener: ItemClickListener<String>) : RecyclerView.Adapter<CardOptionsAdapter.MyViewHolder>() {

    class MyViewHolder(view: ItemCardOptionBinding) : RecyclerView.ViewHolder(view.root) {
        val binding: ItemCardOptionBinding = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemCardOptionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = options[position]

        val binding = holder.binding

        binding.title.text = item.text

        binding.root.setOnClickListener {
            itemClickListener.onClick(item.value)
        }

    }

    override fun getItemCount(): Int = options.size
}