package com.example.user.mycouponcodes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by User on 1/23/2017.
 */

public class UserProfilePage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.profileToolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Set Your Profile");
    }
}
