package site.qutayba.wheelsy.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Dictionary;
import java.util.Hashtable;

import site.qutayba.wheelsy.R;
import site.qutayba.wheelsy.activities.LoginActivity;
import site.qutayba.wheelsy.fragments.HomeFragment;
import site.qutayba.wheelsy.fragments.TripsFragment;

public class MainActivity extends AppCompatActivity  {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private BottomNavigationView bottomNavigationView;
    private Dictionary<Integer, Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Getting auth. instance
        mAuth = FirebaseAuth.getInstance();

        // Checking if the current user is authenticated
        if (!this.handleAuthentication())
            return;

        initFragments();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        initActionBar(menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void initFragments() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.home_nav, R.id.trips_nav, R.id.profile_nav).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.nav_host_fragment, fragment)
                .commit();
    }

    private boolean handleAuthentication() {
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return false;
        }

        return true;
    }

    private void initActionBar(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        menu.findItem(R.id.app_bar_search).setVisible(false);
    }

    private void logout() {
        mAuth.signOut();
        handleAuthentication();
    }


}
