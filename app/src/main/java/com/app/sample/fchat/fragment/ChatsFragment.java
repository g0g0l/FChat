package com.app.sample.fchat.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.app.sample.fchat.R;
import com.app.sample.fchat.activity.ChatDetailsActivity;
import com.app.sample.fchat.activity.MainActivity;
import com.app.sample.fchat.adapter.ChatsListAdapter;
import com.app.sample.fchat.data.ParseFirebaseData;
import com.app.sample.fchat.data.SettingsAPI;
import com.app.sample.fchat.model.ChatMessage;
import com.app.sample.fchat.util.Constants;
import com.app.sample.fchat.widget.DividerItemDecoration;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatsFragment extends Fragment {

    public RecyclerView recyclerView;

    private LinearLayoutManager mLayoutManager;
    public ChatsListAdapter mAdapter;
    private ProgressBar progressBar;

    ValueEventListener valueEventListener;
    DatabaseReference ref;

    View view;

    ParseFirebaseData pfbd;
    SettingsAPI set;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_chat, container, false);
        pfbd = new ParseFirebaseData(getContext());
        set = new SettingsAPI(getContext());

        // activate fragment menu
        setHasOptionsMenu(true);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));


        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(Constants.LOG_TAG, "Data changed from fragment");
                if (dataSnapshot.getValue() != null)
                    // TODO: 25-05-2017 if number of items is 0 then show something else
                    mAdapter = new ChatsListAdapter(getContext(), pfbd.getAllLastMessages(dataSnapshot));
                recyclerView.setAdapter(mAdapter);

                mAdapter.setOnItemClickListener(new ChatsListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, ChatMessage obj, int position) {
                        if (obj.getReceiver().getId().equals(set.readSetting(Constants.PREF_MY_ID)))
                            ChatDetailsActivity.navigate((MainActivity) getActivity(), v.findViewById(R.id.lyt_parent), obj.getSender());
                        else if (obj.getSender().getId().equals(set.readSetting(Constants.PREF_MY_ID)))
                            ChatDetailsActivity.navigate((MainActivity) getActivity(), v.findViewById(R.id.lyt_parent), obj.getReceiver());
                    }
                });

                bindView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        ref = FirebaseDatabase.getInstance().getReference(Constants.MESSAGE_CHILD);
        ref.addValueEventListener(valueEventListener);

        return view;
    }

    public void bindView() {
        try {
            mAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        } catch (Exception e) {
        }

    }

    @Override
    public void onDestroy() {
        //Remove the listener, otherwise it will continue listening in the background
        //We have service to run in the background
        ref.removeEventListener(valueEventListener);
        super.onDestroy();
    }
}
