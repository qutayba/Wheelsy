package site.qutayba.wheelsy.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


import br.com.ilhasoft.support.validation.Validator;
import site.qutayba.wheelsy.R;
import site.qutayba.wheelsy.databinding.ActivityLoginBinding;
import site.qutayba.wheelsy.models.Login;

public class LoginActivity extends AppCompatActivity implements Validator.ValidationListener {

    private Login model;
    private FirebaseAuth mAuth;
    private Validator validator;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        model = new Login();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setLogin(model);
        binding.setHandler(this);
        binding.setIsLoading(false);
        validator = new Validator(binding);
        validator.setValidationListener(this);

        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);

    }



    @Override
    protected void onResume() {
        super.onResume();
        handleAuthenticaton();
    }



    public void loginClick(View view) {
        binding.setIsLoading(true);
        validator.toValidate();
    }

    public void signUpClick(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    @Override
    public void onValidationSuccess() {
        mAuth.signInWithEmailAndPassword(model.getEmail(), model.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                            showMainActivity();
                        else
                            showInvalidCreds();
                    }
                });
    }

    @Override
    public void onValidationError() {
        binding.setIsLoading(false);
    }

    private void handleAuthenticaton() {
        if(mAuth.getCurrentUser() != null)
            showMainActivity();
    }

    private void showMainActivity() {
        binding.setIsLoading(false);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showInvalidCreds() {
        new MaterialAlertDialogBuilder(this, R.style.AppTheme_Alert_Danger)
                .setTitle(getString(R.string.invalid_creds_title))
                .setMessage(getString(R.string.ìnvalid_creds_text))
                .setNegativeButton(getString(R.string.close), null)
                .show();
        binding.setIsLoading(false);
    }
}
