package ca.uqac.alterra.details;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ca.uqac.alterra.R;
import ca.uqac.alterra.home.AlterraPoint;

public class DetailsActivity extends AppCompatActivity {

    private TextView mTitle;
    private AlterraPoint mAlterraPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mTitle = findViewById(R.id.alterraTitle);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mAlterraPoint = (AlterraPoint) extras.getSerializable("AlterraPoint");
            assert(mAlterraPoint!=null);
            mTitle.setText(mAlterraPoint.getTitle());
        }
    }
}
