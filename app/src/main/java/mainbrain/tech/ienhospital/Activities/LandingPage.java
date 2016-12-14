package mainbrain.tech.ienhospital.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import mainbrain.tech.ienhospital.R;


/**
 * Created by iammike on 08/09/16.
 */

public class LandingPage extends AppCompatActivity
{
    SharedPreferences preferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landingpage);
        preferences=getSharedPreferences("ien", Context.MODE_PRIVATE);


        //return to Login.class page if the user not logged in
       // Log.e("landingpage","loggedin====="+App.shared.getBoolean("loggedin" , false));

        //Log.e("landingpage","first====="+first);
       // if(!App.shared.getBoolean("loggedin" , false))
        if(!preferences.getBoolean("loggedin" , false))
        {
            startActivity(new Intent(LandingPage.this , Login.class));
            //startActivity(new Intent(LandingPage.this , MainActivity.class));
            finish();
            return;
        }

        else
        {
            startActivity(new Intent(LandingPage.this , HomeActivity.class));
            finish();
        }
    }
}
