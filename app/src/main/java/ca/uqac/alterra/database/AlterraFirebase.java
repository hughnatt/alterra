package ca.uqac.alterra.database;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.value.GeoPointValue;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.uqac.alterra.home.AlterraPoint;

public class AlterraFirebase implements AlterraDatabase {

    FirebaseFirestore mFirestore;
    FirebaseStorage mStorage;

    private static String COLLECTION_PATH_LOCATIONS = "locations";
    private static String COLLECTION_PATH_USERS = "users";

    public AlterraFirebase(){
        mFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void getAllAlterraLocations(@Nullable OnGetLocationsSuccessListener onGetLocationsSuccessListener) {
        mFirestore.collection(COLLECTION_PATH_LOCATIONS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (onGetLocationsSuccessListener == null) return;
                        ArrayList<AlterraPoint> alterraPoints = new ArrayList<AlterraPoint>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //Skip the default document
                            if (document.getId() == "_default") continue;

                            //Get and extract the document values
                            Map<String,Object> documentData = document.getData();
                            try {
                                GeoPoint coordinates = (GeoPoint) documentData.get("coordinates");
                                Map titles = (Map) documentData.get("name");
                                Map descriptions = (Map) documentData.get("description");
                                String title = (String) titles.get("default");
                                String description = (String) descriptions.get("default");
                                alterraPoints.add(new AlterraPoint(coordinates.getLatitude(), coordinates.getLongitude(), title, description));
                            } catch (NullPointerException ex){
                                System.out.println("Invalid Alterra location was skipped : [ID]=" + document.getId());
                            }
                        }
                        onGetLocationsSuccessListener.onSuccess(alterraPoints);
                    } else {
                       System.out.println("Error getting documents: " + task.getException());
                    }
                });
    }

    @Override
    public void registerAlterraUser(String userID, String userEmail) {
        //Add user document in database
        HashMap<String, Object> data = new HashMap<>();
        data.put("displayName", userEmail);
        mFirestore.collection(COLLECTION_PATH_USERS).document(userID).set(data);
    }
}
