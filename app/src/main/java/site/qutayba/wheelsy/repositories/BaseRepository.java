package site.qutayba.wheelsy.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import site.qutayba.wheelsy.models.IModel;
import site.qutayba.wheelsy.models.Trip;

public abstract class BaseRepository<T extends IModel> {

    private DatabaseReference databaseReference;

    public BaseRepository(String reference) {
        this.databaseReference = FirebaseDatabase.getInstance().getReference(reference);
    }

    public void get(final Class<T> tClass, @Nullable Integer skip, @Nullable Integer take, @Nullable String sort, @Nullable String search, final DataListener<T> listener) {

        Query query = databaseReference;

        if (skip != null && search == null)
            query = query.endAt(skip);

        if (take != null)
            query = query.limitToFirst(take);

        if(sort != null)
            query = query.orderByChild(sort);

        if(search != null)
            query = query.startAt(search).endAt(search + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<T> list = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    T object = snapshot.getValue(tClass);
                    list.add(object);
                }

                listener.onDataChange(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onCancelled(databaseError);
            }
        });
    }

    public void get(final Class<T> tClass, final DataListener<T> listener) {
        get(tClass, null, null, null, null, listener);
    }

    public Task<Void> save(T model) {
        return databaseReference
                .child(model.getId())
                .setValue(model);
    }

}
