package site.qutayba.wheelsy.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

import site.qutayba.wheelsy.R;
import site.qutayba.wheelsy.adapters.TripsDataAdapter;
import site.qutayba.wheelsy.databinding.FragmentTripsBinding;
import site.qutayba.wheelsy.models.Trip;
import site.qutayba.wheelsy.repositories.DataListener;
import site.qutayba.wheelsy.repositories.TripRepository;
import site.qutayba.wheelsy.utilities.EndlessScrollListener;
import site.qutayba.wheelsy.utilities.OnItemClickListener;

import static androidx.navigation.Navigation.findNavController;


public class TripsFragment extends Fragment implements OnItemClickListener<Trip> {

    private final int MAX_ITEMS_PER_PAGE = 15;

    private FragmentTripsBinding binding;
    private TripRepository repository;
    private TripsDataAdapter adapter;
    private int skip = 0;
    private int take = MAX_ITEMS_PER_PAGE;
    private String search = null;


    public TripsFragment() {
        repository = new TripRepository();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_trips, container, false);
        binding.setHandler(this);
        binding.setIsLoading(true);
        binding.setIsScrollLoading(false);
        binding.setIsEmpty(false);
        setHasOptionsMenu(true);

        // Initialize recycler view
        binding.tripsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.tripsRecycler.setHasFixedSize(true);
        initRecyclerScrollListener();

        // Initialize adapter
        adapter = new TripsDataAdapter();
        adapter.setOnItemClickListener(this);
        binding.tripsRecycler.setAdapter(adapter);

        // Initialize data
        getTrips();

        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        initSearchView(menu);
    }

    @Override
    public void onItemClick(Trip item) {
        TripsFragmentDirections.ActionTripsDetails action = TripsFragmentDirections.actionTripsDetails(item);
        Navigation
                .findNavController(getView())
                .navigate(action);
    }

    public boolean shouldShowNoItemsView() {
        return adapter.getItemCount() == 0 && !binding.getIsLoading() && !binding.getIsScrollLoading();
    }

    private void initRecyclerScrollListener() {
        binding.tripsRecycler.addOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore() {
                binding.setIsScrollLoading(true);
                getTrips();
                return false;
            }
        });
    }

    private void initSearchView(@NonNull Menu menu) {
        // Getting the search item from the action-bar
        MenuItem searchMenu = menu.findItem(R.id.app_bar_search);

        // Set it visible for trips fragment
        searchMenu.setVisible(true);
        if(searchMenu == null)
            return;

        // Getting the search-view and set listener
        SearchView searchView = (SearchView) searchMenu.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                configureSearch(query.length() > 0 ? query : null);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // We will do nothing on live text changes
                if(newText.length() == 0)
                    configureSearch(null);
                return true;
            }
        });
    }

    private void getTrips() {
        String sort = search != null && search.length() > 0? "name" : "date";
        repository.get(Trip.class, skip, take, sort, search, new DataListener<Trip>() {
            @Override
            public void onDataChange(ArrayList<Trip> items) {
                adapter.setTrips(items);
                configurePagination();
                binding.setIsLoading(false);
                binding.setIsScrollLoading(false);
                binding.setIsEmpty(shouldShowNoItemsView());
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void configurePagination() {
        take = take + MAX_ITEMS_PER_PAGE;
    }

    private void configureSearch(@Nullable  String keyword) {
        this.search = keyword;
        binding.setIsScrollLoading(true);
        this.getTrips();
    }
}
