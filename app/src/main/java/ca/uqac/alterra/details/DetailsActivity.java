package ca.uqac.alterra.details;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import ca.uqac.alterra.R;
import ca.uqac.alterra.home.AlterraPoint;

public class DetailsActivity extends AppCompatActivity {

    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

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
