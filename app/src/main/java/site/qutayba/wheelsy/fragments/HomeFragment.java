package site.qutayba.wheelsy.fragments;


import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.joaquimley.faboptions.FabOptionsButtonContainer;

import site.qutayba.wheelsy.R;
import site.qutayba.wheelsy.databinding.FragmentHomeBinding;



public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        initFabOptions();
        return binding.getRoot();
    }

    private void initFabOptions() {
        initFabOptionsColors();

        binding.fabOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Clicked " + v.getId(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initFabOptionsColors() {

        // Setting the main button color
        ViewGroup btnGroup = (ViewGroup)binding.fabOptions.getChildAt(0);
        FloatingActionButton btn = btnGroup.findViewById(R.id.faboptions_fab);
        btn.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorOnPrimary)));

        // Setting the color of the option buttons
        binding.fabOptions.setButtonColor(R.id.faboptions_favorite, R.color.colorOnPrimary);
        binding.fabOptions.setButtonColor(R.id.faboptions_textsms, R.color.colorOnPrimary);
        binding.fabOptions.setButtonColor(R.id.faboptions_download, R.color.colorOnPrimary);
        binding.fabOptions.setButtonColor(R.id.faboptions_share, R.color.colorOnPrimary);
    }


}
