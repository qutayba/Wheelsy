package site.qutayba.wheelsy.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.ilhasoft.support.validation.Validator;
import site.qutayba.wheelsy.R;
import site.qutayba.wheelsy.databinding.ActivitySignUpBinding;
import site.qutayba.wheelsy.models.SignUp;
import site.qutayba.wheelsy.models.User;

public class SignUpActivity extends AppCompatActivity implements Validator.ValidationListener {

    private SignUp model;
    private Validator validator;
    private ActivitySignUpBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        model = new SignUp();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        binding.setModel(model);
        binding.setHandler((this));
        binding.setIsLoading(false);

        validator = new Validator(binding);
        validator.setValidationListener(this);
    }

    public void signUpClick(View view) {
        binding.setIsLoading(true);
        validator.toValidate();
    }

    public void backClick(View view) {
        onBackPressed();
    }

    @Override
    public void onValidationSuccess() {
        mAuth.createUserWithEmailAndPassword(model.getEmail(), model.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                createUser(user);
                            }
                        }
                    }
                });
    }

    @Override
    public void onValidationError() {
        binding.setIsLoading(false);
    }

    private void createUser(FirebaseUser authUser) {
        User user = new User(authUser.getUid(), model.getFirstName(), model.getLastName());
        mDatabase.child(user.getId())
                .setValue(user)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            binding.setIsLoading(false);
                            finish();
                        }
                    }
                });
    }
}
