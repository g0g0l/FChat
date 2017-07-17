package com.app.sample.fchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.sample.fchat.data.SettingsAPI;
import com.app.sample.fchat.model.ChatMessage;
import com.app.sample.fchat.R;
import com.app.sample.fchat.widget.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChatsListAdapter extends RecyclerView.Adapter<ChatsListAdapter.ViewHolder> implements Filterable {

    private SparseBooleanArray selectedItems;

    private List<ChatMessage> original_items = new ArrayList<>();
    private List<ChatMessage> filtered_items = new ArrayList<>();
    private ItemFilter mFilter = new ItemFilter();

    private Context mContext;
    SettingsAPI set;

    // for item click listener
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, ChatMessage obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // for item long click listener
    private OnItemLongClickListener mOnItemLongClickListener;

    public interface OnItemLongClickListener {
        void onItemClick(View view, ChatMessage obj, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView content;
        public ImageView image;
        public LinearLayout lyt_parent;

        public ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            content = (TextView) v.findViewById(R.id.content);
            image = (ImageView) v.findViewById(R.id.image);
            lyt_parent = (LinearLayout) v.findViewById(R.id.lyt_parent);
        }

    }

    public Filter getFilter() {
        return mFilter;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ChatsListAdapter(Context mContext, List<ChatMessage> items) {
        this.mContext = mContext;
        original_items = items;
        filtered_items = items;
        selectedItems = new SparseBooleanArray();
    }

    @Override
    public ChatsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chats, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        set=new SettingsAPI(mContext);
        final ChatMessage c = filtered_items.get(position);
        holder.content.setText(c.getText());
        if (c.getSender().getId().equals(set.readSetting("myid"))) {
            holder.title.setText(c.getReceiver().getName());
            Picasso.with(mContext).load(c.getReceiver().getPhoto()).resize(100, 100).transform(new CircleTransform()).into(holder.image);
        }
        else if(c.getReceiver().getId().equals(set.readSetting("myid")))
        {
            holder.title.setText(c.getSender().getName());
            Picasso.with(mContext).load(c.getSender().getPhoto()).resize(100, 100).transform(new CircleTransform()).into(holder.image);
        }

        // Here you apply the animation when the view is bound
        setAnimation(holder.itemView, position);
        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, c, position);
                }
            }
        });

        holder.lyt_parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mOnItemLongClickListener != null) {
                    mOnItemLongClickListener.onItemClick(view, c, position);
                }
                return false;
            }
        });

        holder.lyt_parent.setActivated(selectedItems.get(position, false));

    }

    /**
     * Here is the key method to apply the animation
     */
    private int lastPosition = -1;
    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    /**
     * For multiple selection
     */
    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        } else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public void removeSelectedItem(){
        List<ChatMessage> items = getSelectedItems();
        filtered_items.removeAll(items);
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<ChatMessage> getSelectedItems() {
        List<ChatMessage> items = new ArrayList<>();
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(filtered_items.get(selectedItems.keyAt(i)));
        }
        return items;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return filtered_items.size();
    }

    public void remove(int position) {
        filtered_items.remove(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String query = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();
            final List<ChatMessage> list = original_items;
            final List<ChatMessage> result_list = new ArrayList<>(list.size());

            for (int i = 0; i < list.size(); i++) {
                String str_title = list.get(i).getReceiver().getName();
                if (str_title.toLowerCase().contains(query)) {
                    result_list.add(list.get(i));
                }
            }

            results.values = result_list;
            results.count = result_list.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filtered_items = (List<ChatMessage>) results.values;
            notifyDataSetChanged();
        }

    }
}