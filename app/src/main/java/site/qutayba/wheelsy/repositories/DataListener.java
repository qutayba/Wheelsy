package site.qutayba.wheelsy.repositories;

import android.util.Log;

import com.google.firebase.FirebaseError;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

import site.qutayba.wheelsy.models.IModel;

public interface DataListener<T extends IModel> {

    void onDataChange(ArrayList<T> items);
    void onCancelled(DatabaseError error);
}