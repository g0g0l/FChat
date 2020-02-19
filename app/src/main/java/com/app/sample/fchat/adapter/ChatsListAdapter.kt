package com.app.sample.fchat.adapter

import android.content.Context
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.app.sample.fchat.R
import com.app.sample.fchat.data.SettingsAPI
import com.app.sample.fchat.model.ChatMessage
import com.app.sample.fchat.widget.CircleTransform
import com.squareup.picasso.Picasso
import java.util.*

class ChatsListAdapter// Provide a suitable constructor (depends on the kind of dataset)
(private val mContext: Context, items: ArrayList<ChatMessage>) : RecyclerView.Adapter<ChatsListAdapter.ViewHolder>(), Filterable {

    private val selectedItems: SparseBooleanArray

    private var original_items = ArrayList<ChatMessage>()
    private var filtered_items: ArrayList<ChatMessage> = ArrayList()
    private val mFilter = ItemFilter()
    private lateinit var set: SettingsAPI

    // for item click listener
    private var mOnItemClickListener: OnItemClickListener? = null

    // for item long click listener
    private val mOnItemLongClickListener: OnItemLongClickListener? = null

    /**
     * Here is the key method to apply the animation
     */
    private var lastPosition = -1

    val selectedItemCount: Int
        get() = selectedItems.size()

    interface OnItemClickListener {
        fun onItemClick(view: View, obj: ChatMessage, position: Int)
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = mItemClickListener
    }

    interface OnItemLongClickListener {
        fun onItemClick(view: View, obj: ChatMessage, position: Int)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        // each data item is just a string in this case
        var title: TextView
        var content: TextView
        var image: ImageView
        var lyt_parent: LinearLayout
        var unreadDot: LinearLayout

        init {
            title = v.findViewById<View>(R.id.title) as TextView
            content = v.findViewById<View>(R.id.content) as TextView
            image = v.findViewById<View>(R.id.image) as ImageView
            lyt_parent = v.findViewById<View>(R.id.lyt_parent) as LinearLayout
            unreadDot = v.findViewById<View>(R.id.unread) as LinearLayout
        }

    }

    override fun getFilter(): Filter {
        return mFilter
    }

    init {
        original_items = items
        filtered_items = items
        selectedItems = SparseBooleanArray()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsListAdapter.ViewHolder {
        // create a new view
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_chats, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return ViewHolder(v)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        set = SettingsAPI(mContext)
        val c = filtered_items[position]
        if (filtered_items[position].receiver.id == set.readSetting("myid") && (filtered_items[position].isRead == false))
            holder.unreadDot.visibility = View.VISIBLE
        else
            holder.unreadDot.visibility = View.INVISIBLE
        holder.content.text = c.text
        if (c.sender.id == set.readSetting("myid")) {
            holder.title.text = c.receiver.name
            Picasso.with(mContext).load(c.receiver.photo).resize(100, 100).transform(CircleTransform()).into(holder.image)
        } else if (c.receiver.id == set.readSetting("myid")) {
            holder.title.text = c.sender.name
            Picasso.with(mContext).load(c.sender.photo).resize(100, 100).transform(CircleTransform()).into(holder.image)
        }

        // Here you apply the animation when the view is bound
        setAnimation(holder.itemView, position)
        holder.lyt_parent.setOnClickListener { view ->
            if (mOnItemClickListener != null) {
                mOnItemClickListener!!.onItemClick(view, c, position)
            }
        }

        holder.lyt_parent.setOnLongClickListener { view ->
            mOnItemLongClickListener?.onItemClick(view, c, position)
            false
        }

        holder.lyt_parent.isActivated = selectedItems.get(position, false)

    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_bottom)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    /**
     * For multiple selection
     */
    fun toggleSelection(pos: Int) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos)
        } else {
            selectedItems.put(pos, true)
        }
        notifyItemChanged(pos)
    }

    fun clearSelections() {
        selectedItems.clear()
        notifyDataSetChanged()
    }

    fun removeSelectedItem() {
        val items = getSelectedItems()
        filtered_items.removeAll(items)
    }

    fun getSelectedItems(): List<ChatMessage> {
        val items = ArrayList<ChatMessage>()
        for (i in 0 until selectedItems.size()) {
            items.add(filtered_items[selectedItems.keyAt(i)])
        }
        return items
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return filtered_items.size
    }

    fun remove(position: Int) {
        filtered_items.removeAt(position)
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    //Original list contains all the last messages from all chats
    //We need only those messages where this particular user is involved
    private inner class ItemFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): Filter.FilterResults {
            val query = constraint.toString().toLowerCase()

            val results = Filter.FilterResults()
            val list = original_items
            val result_list = ArrayList<ChatMessage>(list.size)

            for (i in list.indices) {
                val str_title = list[i].receiver.name
                if (str_title.toLowerCase().contains(query)) {
                    result_list.add(list[i])
                }
            }

            results.values = result_list
            results.count = result_list.size

            return results
        }

        override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
            filtered_items = results.values as ArrayList<ChatMessage>
            notifyDataSetChanged()
        }

    }
}