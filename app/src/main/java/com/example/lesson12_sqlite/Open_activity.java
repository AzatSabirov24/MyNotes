package com.example.lesson12_sqlite;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class Open_activity extends AppCompatActivity {

    private GoogleSignInClient googleSignInClient;
    private com.google.android.gms.common.SignInButton buttonSignIn;
    private Button continueBtn, signOutBtn;
    private TextView emailView;
    private final int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);
        initView();
        googleInitSign();
    }

    private void initView() {
        buttonSignIn = findViewById(R.id.sign_in_button);
        buttonSignIn.setOnClickListener(v -> signIn());
        emailView = findViewById(R.id.email);
        continueBtn = findViewById(R.id.continue_);
        continueBtn.setOnClickListener(v -> {
            Intent i = new Intent(Open_activity.this, MainActivity.class);
            startActivity(i);
        });
        signOutBtn = findViewById(R.id.sing_out_button);
        signOutBtn.setOnClickListener(v -> signOut());
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            updateUI(account.getEmail());
            disableSign();
        } else enableSign();
    }

    private void googleInitSign() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }


    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            disableSign();
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            disableSign();
            assert account != null;
            updateUI(account.getEmail());
        } catch (ApiException e) {
            Toast.makeText(this, R.string.error_sign_in, Toast.LENGTH_SHORT).show();
        }
    }

    private void enableSign() {
        buttonSignIn.setEnabled(true);
        continueBtn.setEnabled(false);
        signOutBtn.setEnabled(false);
    }

    private void disableSign() {
        buttonSignIn.setEnabled(false);
        continueBtn.setEnabled(true);
        signOutBtn.setEnabled(true);

    }

    private void updateUI(String email) {
        emailView.setText(email);
    }

    private void signOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener(task -> {
                    updateUI("");
                    enableSign();
                });
    }
}
