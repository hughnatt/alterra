package ca.uqac.alterra.home;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import ca.uqac.alterra.R;

public class AlterraDetailsActivity extends AppCompatActivity {

    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterra_details);

        String jsonMyObject;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jsonMyObject = extras.getString("AlterraPoint");
            AlterraPoint myObject = new Gson().fromJson(jsonMyObject, AlterraPoint.class);
            mTitle = findViewById(R.id.alterraTitle);
            mTitle.setText(myObject.getTitle());
        }
    }
}
