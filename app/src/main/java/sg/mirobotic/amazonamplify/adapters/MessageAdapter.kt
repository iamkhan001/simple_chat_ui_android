package sg.mirobotic.amazonamplify.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import sg.mirobotic.amazonamplify.data.local.ItemClickListener
import sg.mirobotic.amazonamplify.data.local.Message
import sg.mirobotic.amazonamplify.databinding.ItemMessageBinding

class MessageAdapter(private val itemClickListener: ItemClickListener<String>) : RecyclerView.Adapter<MessageAdapter.MyViewHolder>() {

    private var list = ArrayList<Message>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: ArrayList<Message>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun addMessage(message: Message) {
        list.add(message)
        notifyItemInserted(list.size-1)
    }

    class MyViewHolder(view: ItemMessageBinding) : RecyclerView.ViewHolder(view.root) {
        val binding: ItemMessageBinding = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]

        val binding = holder.binding
        if (item.type == Message.Type.RECEIVED) {
            binding.viewReceived.visibility = View.VISIBLE
            binding.viewSent.visibility = View.GONE
            binding.confirmButtons.visibility = View.GONE

            binding.textReceived.text = item.text

            if (!binding.itemReceived.isVisible) {
                binding.itemReceived.visibility = View.VISIBLE
            }

            if (item.cards.isEmpty()) {
                binding.rvOptions.visibility = View.GONE

                if (item.showConfirmButtons) {
                    binding.confirmButtons.visibility = View.VISIBLE

                    binding.btnNo.setOnClickListener {
                        itemClickListener.onClick("no")
                    }

                    binding.btnYes.setOnClickListener {
                        itemClickListener.onClick("yes")
                    }
                }

            }else {
                if (item.text.isEmpty()) {
                    binding.itemReceived.visibility = View.GONE
                }
                binding.rvOptions.visibility = View.VISIBLE
                val cardsAdapter = CardsAdapter(item.cards, itemClickListener)
                binding.rvOptions.adapter = cardsAdapter
            }
        }else {
            binding.viewReceived.visibility = View.GONE
            binding.viewSent.visibility = View.VISIBLE

            binding.textSent.text = item.text
        }

    }

    override fun getItemCount(): Int = list.size


}