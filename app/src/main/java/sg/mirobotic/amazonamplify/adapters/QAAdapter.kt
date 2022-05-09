package sg.mirobotic.amazonamplify.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import sg.mirobotic.amazonamplify.data.local.ItemClickListener
import sg.mirobotic.amazonamplify.data.local.Message
import sg.mirobotic.amazonamplify.data.local.QAMessage
import sg.mirobotic.amazonamplify.databinding.ItemMessageBinding
import sg.mirobotic.amazonamplify.databinding.ItemQaMessageBinding

class QAAdapter(private val itemClickListener: ItemClickListener<String>) : RecyclerView.Adapter<QAAdapter.MyViewHolder>() {

    private var list = ArrayList<QAMessage>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: ArrayList<QAMessage>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun addMessage(message: QAMessage) {
        list.add(message)
        notifyItemInserted(list.size-1)
    }

    class MyViewHolder(view: ItemQaMessageBinding) : RecyclerView.ViewHolder(view.root) {
        val binding: ItemQaMessageBinding = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemQaMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]

        val binding = holder.binding
        if (item.type == QAMessage.Type.RECEIVED) {
            binding.viewReceived.visibility = View.VISIBLE
            binding.viewSent.visibility = View.GONE

            if (item.answer == null) {
                binding.textReceived.text = item.text
                binding.info.text = ""
            }else {
                val info = "source: ${item.answer?.source}\nscore: ${item.answer?.score}"
                binding.info.text = info
                binding.textReceived.text = item.answer?.getReadableText()
            }

        }else {
            binding.viewReceived.visibility = View.GONE
            binding.viewSent.visibility = View.VISIBLE

            binding.textSent.text = item.text
        }

    }

    override fun getItemCount(): Int = list.size


}