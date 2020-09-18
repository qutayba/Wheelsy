package site.qutayba.wheelsy.repositories;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import site.qutayba.wheelsy.models.Trip;

public class TripRepository extends BaseRepository<Trip> {

    public TripRepository() {
        super("trips");
    }

    public Task<Void> uploadImages(Trip model, Bitmap[] images) {

        StorageReference tripImagesRef = storageReference.child(model.getId());
        List<UploadTask> uploadTasks = new ArrayList<>();

        for (int i = 0; i < images.length; i++) {
            if (images[i] == null)
                continue;

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            images[i].compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] imageData = stream.toByteArray();

            UploadTask imageUploadTask = tripImagesRef.child(String.format("image_%s", i)).putBytes(imageData);
            uploadTasks.add(imageUploadTask);
        }

        return Tasks.whenAll(uploadTasks);
    }

    public Task<ListResult> getImagesList(Trip model) {
        StorageReference tripImagesRef = storageReference.child(model.getId());
        return tripImagesRef.listAll();
    }

    public Task<byte[]> getImageData(StorageReference ref) {
        final long MAX_IMG_SIZE = 1024 * 1024 * 3;
        return ref.getBytes(MAX_IMG_SIZE);
    }
}
