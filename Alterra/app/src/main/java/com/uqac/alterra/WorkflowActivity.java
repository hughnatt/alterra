package com.uqac.alterra;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class WorkflowActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workflow);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.faded_in,
                R.anim.faded_out);
        ft.replace(R.id.emptyContainer, new WorkflowFragment());
        ft.commit();
    }
}
