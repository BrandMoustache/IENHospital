package mainbrain.tech.ienhospital.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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

import mainbrain.tech.ienhospital.Application.App;
import mainbrain.tech.ienhospital.Helper.CheckConnection;

import mainbrain.tech.ienhospital.Helper.Sansation;
import mainbrain.tech.ienhospital.BroadcastReceivers.SmsReceiver;
import mainbrain.tech.ienhospital.R;
import mainbrain.tech.ienhospital.Utilities.Cons;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;


/**
 * Created by iammike on 16/07/16.
 */
public class Verification extends AppCompatActivity
{
    private MaterialProgressBar progress;
    private TextView time;
    private TextView changeNumber;
    private TextView otpNumber;
    private SmsReceiver smsReceiver;
    private CountDownTimer cdt;
    private int total;
    private ProgressDialog progressDialog;
    private boolean callHandler = true;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verification);

        smsReceiver = new SmsReceiver();


        progress = (MaterialProgressBar) findViewById(R.id.horizontal_progress_library);
        time = TextView.class.cast(findViewById(R.id.textView13));
        changeNumber = TextView.class.cast(findViewById(R.id.textView6));
        otpNumber = TextView.class.cast(findViewById(R.id.textView5));

        otpNumber.setText("Waiting to automatically detect an \nSMS sent to " + getIntent().getStringExtra("number"));
        changeNumber.setText("Not " + getIntent().getStringExtra("number") + "?");

        startTimer();

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeNumber();
            }
        });

        new Sansation().overrideFonts(getApplicationContext(), findViewById(R.id.layout));

    }

    private void changeNumber()
    {
        callHandler = false;
        Intent intent = new Intent(Verification.this , Login.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    private void startTimer()
    {
        total = 0;

        final Handler handler = new Handler();
        final Runnable r = new Runnable()
        {
            public void run()
            {
                total++;
                time.setText((60-total)+"s");
                progress.setProgress(total);

                if(total==60)
                {
                    Intent intent = new Intent(Verification.this , CheckOTP.class);
                    intent.putExtra("number", getIntent().getStringExtra("number"));
                    intent.putExtra("code", getIntent().getStringExtra("code"));
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                }
                else
                {
                    try
                    {
                        String message = smsReceiver.getMessage();

                        if(message.contains(getIntent().getStringExtra("code")) && callHandler)
                        {
                            if(CheckConnection.isOnline(getApplicationContext())) {
                                new checkAmbulance().execute(getIntent().getStringExtra("number"));
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                                handler.postDelayed(this, 1000);
                            }
                        }
                        else
                        {
                            handler.postDelayed(this, 1000);
                        }
                    }
                    catch(Exception e)
                    {
                        if(callHandler)
                            handler.postDelayed(this, 1000);
                    }
                }
            }
        };

        handler.postDelayed(r, 1000);
    }

    public class checkAmbulance extends AsyncTask<String, Void, String> {
        private String results;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(Verification.this, AlertDialog.THEME_HOLO_LIGHT);
            progressDialog.setMessage("processing");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                HttpClient http_client = new DefaultHttpClient();
                HttpPost http_post = new HttpPost(Cons.port+"services/checkHospital");
//                HttpPost http_post = new HttpPost("http://139.59.24.15/ien/checkAmbulance.php");
                List<NameValuePair> nameVP = new ArrayList<NameValuePair>(2);
                nameVP.add(new BasicNameValuePair("number", params[0]));
                http_post.setEntity(new UrlEncodedFormEntity(nameVP));
                HttpEntity entity = http_client.execute(http_post).getEntity();
                if (entity != null) {
                    String response = EntityUtils.toString(entity);
                    Log.e("test", response + "michael");
                    entity.consumeContent();
                    http_client.getConnectionManager().shutdown();

                    results = response;
                } else {
                    results = "Failure";
                }
            } catch (Exception e) {
                results = e.toString();
                Log.e("server", results);
            }
            return results;
        }

        @Override
        protected void onPostExecute(final String result)
        {
            progressDialog.cancel();
            if (result.equals("0"))
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Verification.this);
                alertDialogBuilder.setTitle("Not Registered");
                alertDialogBuilder
                        .setMessage("Not registerd as ambulance. Please contact us on 9443153157 for registration.")
                        .setCancelable(false)
                        .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                finish();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
            else if(result.contains("amb_id_"))
            {
                App.editor.putBoolean("loggedin", true).commit();
                Intent intent = new Intent(Verification.this, HomeActivity.class);
                App.editor.putString("number", getIntent().getStringExtra("number")).commit();
                App.editor.putString("id", result).commit();

                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                }
            else
            {

            }
        }
    }
}
