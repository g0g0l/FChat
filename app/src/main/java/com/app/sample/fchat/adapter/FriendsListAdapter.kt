package com.app.sample.fchat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.app.sample.fchat.R
import com.app.sample.fchat.model.Friend
import com.app.sample.fchat.widget.CircleTransform
import com.squareup.picasso.Picasso
import java.util.*

class FriendsListAdapter// Provide a suitable constructor (depends on the kind of dataset)
(private val mContext: Context, items: ArrayList<Friend>) : RecyclerView.Adapter<FriendsListAdapter.ViewHolder>(), Filterable {

    private var original_items = ArrayList<Friend>()
    private var filtered_items: List<Friend> = ArrayList()
    private val mFilter = ItemFilter()

    private var mOnItemClickListener: OnItemClickListener? = null

    /**
     * Here is the key method to apply the animation
     */
    private var lastPosition = -1

    interface OnItemClickListener {
        fun onItemClick(view: View, obj: Friend, position: Int)
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = mItemClickListener
    }

    init {
        original_items = items
        filtered_items = items
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        // each data item is just a string in this case
        var name: TextView
        var image: ImageView
        var lyt_parent: LinearLayout

        init {
            name = v.findViewById<View>(R.id.name) as TextView
            image = v.findViewById<View>(R.id.image) as ImageView
            lyt_parent = v.findViewById<View>(R.id.lyt_parent) as LinearLayout
        }
    }

    override fun getFilter(): Filter {
        return mFilter
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsListAdapter.ViewHolder {
        // create a new view
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_friends, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return ViewHolder(v)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val c = filtered_items[position]
        holder.name.text = c.name
        Picasso
                .with(mContext)
                .load(c.photo)
                .error(R.drawable.unknown_avatar)
                .placeholder(R.drawable.unknown_avatar)
                .resize(100, 100)
                .transform(CircleTransform())
                .into(holder.image)

        // Here you apply the animation when the view is bound
        setAnimation(holder.itemView, position)

        holder.lyt_parent.setOnClickListener { view ->
            if (mOnItemClickListener != null) {
                mOnItemClickListener!!.onItemClick(view, c, position)
            }
        }
    }

    fun getItem(position: Int): Friend {
        return filtered_items[position]
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_bottom)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return filtered_items.size
    }


    private inner class ItemFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): Filter.FilterResults {
            val query = constraint.toString().toLowerCase()

            val results = Filter.FilterResults()
            val list = original_items
            val result_list = ArrayList<Friend>(list.size)

            for (i in list.indices) {
                val str_title = list[i].name
                if (str_title.toLowerCase().contains(query)) {
                    result_list.add(list[i])
                }
            }

            results.values = result_list
            results.count = result_list.size

            return results
        }

        override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
            filtered_items = results.values as List<Friend>
            notifyDataSetChanged()
        }
    }

}