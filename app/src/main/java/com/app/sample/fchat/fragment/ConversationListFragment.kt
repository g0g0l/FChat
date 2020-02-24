package com.app.sample.fchat.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.sample.fchat.R
import com.app.sample.fchat.activity.ChatActivity
import com.app.sample.fchat.activity.MainActivity
import com.app.sample.fchat.adapter.ChatsListAdapter
import com.app.sample.fchat.data.ParseFirebaseData
import com.app.sample.fchat.data.SettingsAPI
import com.app.sample.fchat.model.ChatMessage
import com.app.sample.fchat.ui.ViewHelper
import com.app.sample.fchat.util.Constants
import com.app.sample.fchat.widget.DividerItemDecoration
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_chat.*

class ConversationListFragment : Fragment() {
    private var mLayoutManager: LinearLayoutManager? = null
    var mAdapter: ChatsListAdapter? = null
    var valueEventListener: ValueEventListener? = null
    var ref: DatabaseReference? = null
    var viewHelper: ViewHelper? = null
    var pfbd: ParseFirebaseData? = null
    var set: SettingsAPI? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        pfbd = ParseFirebaseData(context)
        set = SettingsAPI(context)
        viewHelper = ViewHelper(context!!)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // activate fragment menu
        setHasOptionsMenu(true)
        // use a linear layout manager
        mLayoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(
                DividerItemDecoration(activity, DividerItemDecoration.VERTICAL_LIST))
        viewHelper!!.showProgressDialog()
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(Constants.LOG_TAG, "Data changed from fragment")
                if (dataSnapshot.value != null) {
                    if (pfbd!!.getAllLastMessages(dataSnapshot).size <= 0) {
                        recyclerView.visibility = View.GONE
                        llChatNotFound.visibility = View.VISIBLE
                    }
                    mAdapter = ChatsListAdapter(context!!, pfbd!!.getAllLastMessages(dataSnapshot))
                }
                recyclerView.adapter = mAdapter
                mAdapter!!.setOnItemClickListener(object : ChatsListAdapter.OnItemClickListener {
                    override fun onItemClick(v: View, obj: ChatMessage, position: Int) {
                        if (obj.receiver.id == set!!.readSetting(Constants.PREF_MY_ID)) {
                            ChatActivity
                                    .navigate(activity as MainActivity?, v.findViewById(R.id.lyt_parent),
                                            obj.sender)
                        } else if (obj.sender.id
                                == set!!.readSetting(Constants.PREF_MY_ID)) {
                            ChatActivity
                                    .navigate(activity as MainActivity?, v.findViewById(R.id.lyt_parent),
                                            obj.receiver)
                        }
                    }
                })
                bindView()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        ref = FirebaseDatabase.getInstance().getReference(Constants.MESSAGE_CHILD)
        ref!!.addValueEventListener(valueEventListener!!)
    }

    fun bindView() {
        try {
            mAdapter!!.notifyDataSetChanged()
            viewHelper!!.dismissProgressDialog()
        } catch (e: Exception) {
        }
    }

    override fun onDestroy() {
        //Remove the listener, otherwise it will continue listening in the background
        //We have service to run in the background
        ref!!.removeEventListener(valueEventListener!!)
        super.onDestroy()
    }
}