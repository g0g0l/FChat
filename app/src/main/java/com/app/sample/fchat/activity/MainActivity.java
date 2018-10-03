package com.app.sample.fchat.activity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.app.sample.fchat.R;
import com.app.sample.fchat.data.Tools;
import com.app.sample.fchat.fragment.ChatsFragment;
import com.app.sample.fchat.service.NotificationService;
import com.app.sample.fchat.util.CustomToast;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    public FloatingActionButton fab;
    JobScheduler mJobScheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.add);

        prepareActionBar(toolbar);
        initComponent();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, SelectFriendActivity.class);
                startActivity(i);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // for system bar in lollipop
            Tools.systemBarLolipop(this);
            //Create the scheduler
            mJobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
            JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(getPackageName(), NotificationService.class.getName()));
            builder.setPeriodic(900000);
            //If it needs to continue even after boot, persisted needs to be true
            //builder.setPersisted(true);
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            mJobScheduler.schedule(builder.build());
        }
    }

    private void initComponent() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ChatsFragment ctf = new ChatsFragment();
        //icf.setRetainInstance(true);
        fragmentTransaction.add(R.id.main_container, ctf, "Chat History");
        fragmentTransaction.commit();

    }

    private void prepareActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_logout: {
                Intent logoutIntent = new Intent(this, SplashActivity.class);
                logoutIntent.putExtra("mode", "logout");
                startActivity(logoutIntent);
                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private long exitTime = 0;

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            new CustomToast(this).showInfo(getString(R.string.press_again_exit_app));
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        doExitApp();
    }
}
