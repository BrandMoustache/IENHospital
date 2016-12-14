package mainbrain.tech.ienhospital.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mainbrain.tech.ienhospital.Helper.Sansation;
import mainbrain.tech.ienhospital.R;


/**
 * Created by iammike on 16/07/16.
 */
public class Login extends AppCompatActivity
{
    private EditText number;
    private String code;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        number = EditText.class.cast(findViewById(R.id.editText));

        findViewById(R.id.toplayout).getLayoutParams().height = getResources().getDisplayMetrics().heightPixels/3;

        code = String.valueOf(new Random().nextInt(99999 - 11111) + 11111);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getPermission()) {
                    if (number.getText().length() != 10) {
                        Toast.makeText(getApplicationContext(), "Enter a valid number", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("testcode" , code);
                        new reqMessage().execute(number.getText().toString(), code);
                        Intent intent = new Intent(Login.this, Verification.class);
                        intent.putExtra("number", number.getText().toString());
                        intent.putExtra("code", code);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    }
                }
                else{}}
        });

        new Sansation().overrideFonts(getApplicationContext() , findViewById(R.id.layout));

        getPermission();
    }

    public boolean getPermission()
    {
        if (ContextCompat.checkSelfPermission(Login.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(Login.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(Login.this, new String[]{Manifest.permission.RECEIVE_SMS , Manifest.permission.READ_SMS}, 11);

           return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode) {
            case 11: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }
        }
    }

    public class reqMessage extends AsyncTask<String, Void, String>
    {
        private String results;
        private boolean res=true;

        @Override
        protected void onPreExecute()
        {

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                HttpClient http_client = new DefaultHttpClient();
                HttpPost http_post = new HttpPost("http://isunapps.com/sales/TaxiApp/Taxi_registration.php");
                List<NameValuePair> nameVP = new ArrayList<NameValuePair>(2);
                nameVP.add(new BasicNameValuePair("number", params[0]));
                nameVP.add(new BasicNameValuePair("code", params[1]));
                http_post.setEntity(new UrlEncodedFormEntity(nameVP));
                HttpEntity entity = http_client.execute(http_post).getEntity();
                if (entity != null) {
                    String response = EntityUtils.toString(entity);
                    entity.consumeContent();
                    http_client.getConnectionManager().shutdown();
                    results = "Success";
                }
                else
                {
                    results = "Failure";
                }
            }
            catch (Exception e)
            {
                results = e.toString();
            }
            return results;
        }

        @Override
        protected void onPostExecute(final String result)
        {

        }
    }
}
