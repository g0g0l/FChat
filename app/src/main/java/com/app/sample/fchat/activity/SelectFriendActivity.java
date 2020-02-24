package com.app.sample.fchat.activity;

import android.os.Build;
import android.os.Bundle;

import com.app.sample.fchat.R;
import com.app.sample.fchat.adapter.FriendsListAdapter;
import com.app.sample.fchat.data.ParseFirebaseData;
import com.app.sample.fchat.model.Friend;
import com.app.sample.fchat.ui.CustomToast;
import com.app.sample.fchat.ui.ViewHelper;
import com.app.sample.fchat.util.Tools;
import com.app.sample.fchat.widget.DividerItemDecoration;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SelectFriendActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private RecyclerView recyclerView;
    private FriendsListAdapter mAdapter;
    List<Friend> friendList;

    public static final String USERS_CHILD = "users";
    ParseFirebaseData pfbd;
    ViewHelper viewHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);
        initToolbar();
        initComponent();
        friendList = new ArrayList<>();
        pfbd = new ParseFirebaseData(this);
        viewHelper=new ViewHelper(this);

        viewHelper.showProgressDialog();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(USERS_CHILD);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                // TODO: 25-05-2017 if number of items is 0 then show something else
                mAdapter = new FriendsListAdapter(SelectFriendActivity.this,
                    pfbd.getAllUser(dataSnapshot));
                recyclerView.setAdapter(mAdapter);

                mAdapter.setOnItemClickListener((view, obj, position) -> ChatActivity
                    .navigate((SelectFriendActivity) SelectFriendActivity.this,
                        findViewById(R.id.lyt_parent), obj));

                bindView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                new CustomToast(SelectFriendActivity.this)
                    .showError(getString(R.string.error_could_not_connect));
            }
        });

        // for system bar in lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Tools.systemBarLolipop(this);
        }
    }

    private void initComponent() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(
            new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }

    public void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
//        actionBar.setSubtitle(Constant.getFriendsData(this).size()+" friends");
    }

    public void bindView() {
        try {
            mAdapter.notifyDataSetChanged();
            viewHelper.dismissProgressDialog();
        } catch (Exception e) {
        }
    }
}
