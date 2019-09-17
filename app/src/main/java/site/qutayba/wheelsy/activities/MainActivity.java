package site.qutayba.wheelsy.activities;

        import androidx.appcompat.app.AppCompatActivity;

        import android.content.Intent;
        import android.os.Bundle;

        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;

        import site.qutayba.wheelsy.R;
        import site.qutayba.wheelsy.activities.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Getting auth. instance
        mAuth = FirebaseAuth.getInstance();

        // Checking if the current user is authenticated
        if(!this.isAuthorized())
            return;
    }

    private boolean isAuthorized() {
        currentUser = mAuth.getCurrentUser();

        if(currentUser == null)
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return false;
        }

        return true;
    }
}
