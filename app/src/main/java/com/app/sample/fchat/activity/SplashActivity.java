package com.app.sample.fchat.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.app.sample.fchat.R;
import com.app.sample.fchat.data.SettingsAPI;
import com.app.sample.fchat.ui.CustomToast;
import com.app.sample.fchat.ui.ViewHelper;
import com.app.sample.fchat.util.Constants;
import com.app.sample.fchat.util.Tools;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.app.sample.fchat.util.Constants.NODE_NAME;
import static com.app.sample.fchat.util.Constants.NODE_PHOTO;
import static com.app.sample.fchat.util.Constants.NODE_USER_ID;

public class SplashActivity extends AppCompatActivity
    implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 100;
    private SignInButton signInButton;
    private ProgressBar loginProgress;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    DatabaseReference ref;
    SettingsAPI set;

    CustomToast customToast;
    ViewHelper viewHelper;

    public static final String USERS_CHILD = "users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        bindLogo();

        customToast = new CustomToast(this);
        viewHelper = new ViewHelper(getApplicationContext());
        viewHelper.clearNotofication();

        // Assign fields
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        loginProgress = (ProgressBar) findViewById(R.id.login_progress);

        // Set click listeners
        signInButton.setOnClickListener(v -> signIn());

        GoogleSignInOptions gso =
            new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
            .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();
        set = new SettingsAPI(this);

        if (getIntent().getStringExtra("mode") != null) {
            if (getIntent().getStringExtra("mode").equals("logout")) {
                mGoogleApiClient.connect();
                mGoogleApiClient
                    .registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(
                            @Nullable
                                Bundle bundle) {
                            mFirebaseAuth.signOut();
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                            set.deleteAllSettings();
                        }

                        @Override
                        public void onConnectionSuspended(int i) {

                        }
                    });
            }
        }
        if (!mGoogleApiClient.isConnecting()) {
            if (!set.readSetting(Constants.PREF_MY_ID).equals("na")) {
                signInButton.setVisibility(View.GONE);
                final Handler handler = new Handler();
                handler.postDelayed(() -> {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }, 3000);
            }
        }
        // for system bar in lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Tools.systemBarLolipop(this);
        }
    }

    private void bindLogo() {
        // Start animating the image
        final ImageView splash = (ImageView) findViewById(R.id.splash);
        final AlphaAnimation animation1 = new AlphaAnimation(0.2f, 1.0f);
        animation1.setDuration(700);
        final AlphaAnimation animation2 = new AlphaAnimation(1.0f, 0.2f);
        animation2.setDuration(700);
        //animation1 AnimationListener
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                // start animation2 when animation1 ends (continue)
                splash.startAnimation(animation2);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationStart(Animation arg0) {
            }
        });

        //animation2 AnimationListener
        animation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                // start animation1 when animation2 ends (repeat)
                splash.startAnimation(animation1);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationStart(Animation arg0) {
            }
        });

        splash.startAnimation(animation1);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                signInButton.setVisibility(View.GONE);
                loginProgress.setVisibility(View.VISIBLE);
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                customToast.showError(getString(R.string.error_login_failed));
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            // If sign in fails, display a message to the user. If sign in succeeds
            // the auth state listener will be notified and logic to handle the
            // signed in user can be handled in the listener.
            if (!task.isSuccessful()) {
                customToast.showError(getString(R.string.error_authetication_failed));
            } else {
                ref = FirebaseDatabase.getInstance().getReference(USERS_CHILD);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(
                        @NotNull
                            DataSnapshot snapshot) {
                        final String usrNm = acct.getDisplayName();
                        final String usrId = acct.getId();
                        final String usrDp = acct.getPhotoUrl().toString();

                        set.addUpdateSettings(Constants.PREF_MY_ID, usrId);
                        set.addUpdateSettings(Constants.PREF_MY_NAME, usrNm);
                        set.addUpdateSettings(Constants.PREF_MY_DP, usrDp);

                        if (!snapshot.hasChild(usrId)) {
                            ref.child(usrId + "/" + NODE_NAME).setValue(usrNm);
                            ref.child(usrId + "/" + NODE_PHOTO).setValue(usrDp);
                            ref.child(usrId + "/" + NODE_USER_ID).setValue(usrId);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onConnectionFailed(
        @NonNull
            ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        customToast.showError("Google Play Services error.");
    }
}
