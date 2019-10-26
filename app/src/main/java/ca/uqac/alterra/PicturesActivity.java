package ca.uqac.alterra;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;


public class PicturesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    RecyclerView mRecyclerView;

    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private FirebaseFirestore mFirestore;
    private FirebaseUser currentUser;

    private NavigationView mNavigationView;
    private DrawerLayout mNavDrawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures);

        setNavigationViewListener();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();

        mRecyclerView = findViewById(R.id.recyclerview);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        PicturesAdapter myAdapter = new PicturesAdapter(getApplicationContext());
        mRecyclerView.setAdapter(myAdapter);

        mFirestore.collection("photos")
                .whereEqualTo("owner", currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String link = (String) document.getData().get("link");
                                mStorage.getReference().child(link).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        System.out.println(uri.toString());
                                        myAdapter.addPicture(uri.toString());
                                        myAdapter.notifyItemInserted(myAdapter.getItemCount());

                                    }
                                });

                            }


                        }
                    }
                });




    }

    @Override
    public void onStart(){
        super.onStart();
        mNavDrawer = findViewById(R.id.navDrawerPictures);
        Toolbar toolbar = findViewById(R.id.toolbarPictures);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,mNavDrawer,toolbar,R.string.app_name,R.string.app_name);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorPrimaryDark));
        mNavDrawer.addDrawerListener(toggle);
        toggle.syncState();

        if (currentUser != null) {
            View headerView = mNavigationView.getHeaderView(0);
            TextView navUsername = (TextView) headerView.findViewById(R.id.navUsername);
            navUsername.setText(currentUser.getEmail()); //TODO : change by the login (need to be availible in register)
        } else {
            //TODO : how to handle this kind of error ?!
        }
    }


    private void setNavigationViewListener() {
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view_pictures);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_item_profile :
                Toast toastProfile = Toast.makeText(getApplicationContext(), "Profile", Toast.LENGTH_LONG);
                toastProfile.show();
                return true;
            case R.id.nav_item_pictures : //We are already on the pictures activity, nothing else must be do
                mNavDrawer.closeDrawers();
                return true;
            case R.id.nav_item_places :
                Toast toastPlaces = Toast.makeText(getApplicationContext(), "Places", Toast.LENGTH_LONG);
                toastPlaces.show();
                return true;
            case R.id.nav_item_settings :
                Toast toastSettings = Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_LONG);
                toastSettings.show();
                return true;
            case R.id.nav_item_about :
                Toast toastAbout = Toast.makeText(getApplicationContext(), "About", Toast.LENGTH_LONG);
                toastAbout.show();
                return true;
            case R.id.nav_item_logout :
                mAuth.signOut();
                startActivity(new Intent(this, AuthActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);


        }
    }



}
