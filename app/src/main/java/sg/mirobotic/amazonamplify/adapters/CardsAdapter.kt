package sg.mirobotic.amazonamplify.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.mirobotic.amazonamplify.data.local.Content
import sg.mirobotic.amazonamplify.data.local.ItemClickListener
import sg.mirobotic.amazonamplify.databinding.ItemCardOptionBinding

class CardsAdapter(private val cards: ArrayList<Content.Card>, private val itemClickListener: ItemClickListener<String>) : RecyclerView.Adapter<CardsAdapter.MyViewHolder>() {

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
        val item = cards[position]

        val binding = holder.binding
        binding.title.text = item.title

        val optionsAdapter = CardOptionsAdapter(item.options ?: ArrayList(), itemClickListener)
        binding.rvButtons.adapter = optionsAdapter
    }

    override fun getItemCount(): Int = cards.size
}