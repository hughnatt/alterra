package ca.uqac.alterra.home;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ca.uqac.alterra.R;

public class AlterraDetailsActivity extends AppCompatActivity {

    private String mAlterraPoint;
    private TextView mTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterra_details);

        mAlterraPoint = this.getIntent().getStringExtra("AlterraPointTitle");

        mTitle = findViewById(R.id.alterraTitle);

        mTitle.setText(mAlterraPoint);

    }
}
