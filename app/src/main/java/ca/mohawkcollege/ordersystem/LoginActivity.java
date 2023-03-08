package ca.mohawkcollege.ordersystem;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "===LoginActivity===";
    FirebaseAuth mAuth;
    TextInputEditText emailEditText;
    TextInputEditText passwordEditText;
    Button loginButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.email_input);
        passwordEditText = findViewById(R.id.pwd_input);
        loginButton = findViewById(R.id.login_btn);

        loginButton.setOnClickListener(this::login);
    }

    public void login(View view) {
        String email = Objects.requireNonNull(emailEditText.getText()).toString();
        String password = Objects.requireNonNull(passwordEditText.getText()).toString();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(this, user.getEmail() + " login successfully", Toast.LENGTH_SHORT).show();
                        Intent intent;
                        if (user.getEmail().equals("aaaa@qq.com")) {
                            intent = new Intent(this, MainActivity.class);
                        } else {
                            intent = new Intent(this, SecondaryActivity.class);
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            Log.d(TAG, "onStart: user is not signed in");
            return;
        }
        Log.d(TAG, "onStart: user is signed in");
        if (mAuth.getCurrentUser().getEmail().equals("bbbb@qq.com")) {
            Log.d(TAG, "onStart: user is BACK");
            startActivity(new Intent(this, SecondaryActivity.class));
            finish();
        } else {
            Log.d(TAG, "onStart: user is FRONT");
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}