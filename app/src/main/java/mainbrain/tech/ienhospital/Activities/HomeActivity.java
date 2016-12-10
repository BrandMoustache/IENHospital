package mainbrain.tech.ienhospital.Activities;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mainbrain.tech.ienhospital.Adapters.BottomRecycleAdaptar;
import mainbrain.tech.ienhospital.Application.App;
import mainbrain.tech.ienhospital.Helper.CheckConnection;
import mainbrain.tech.ienhospital.Helper.CustomProgressDialog;
import mainbrain.tech.ienhospital.Model.PatientDetails;
import mainbrain.tech.ienhospital.R;
import mainbrain.tech.ienhospital.Helper.Sansation;
import mainbrain.tech.ienhospital.Adapters.TopRecycleAdaptar;
import mainbrain.tech.ienhospital.Services.MySmackService;
import mainbrain.tech.ienhospital.Utilities.Cons;

/**
 * Created by MikeJaison on 12/2/16.
 */

public class HomeActivity extends AppCompatActivity implements View.OnClickListener,ServiceConnection
{
    RecyclerView topRecyclerView;
    RecyclerView bottomRecyclerView;
    Button btn_requestambulance;
    private LinearLayout ll_search;
    private RelativeLayout rl_search;
    private EditText edttxt_search;
    private ImageView imgv_serach,imgv_clear,imgv_close;
    BottomRecycleAdaptar bottomRecycleAdaptar;
    ArrayList<String> filteredList;
    ArrayList<PatientDetails> al_patientdetails;
    private CustomProgressDialog progressDialog;
    ArrayList<String> onGoingItems = new ArrayList<>(Arrays.asList("Cheesy...", "Crispy... ", "Fizzy...", "Cool...", "Softy...", "Fruity..."));
    ArrayList<String> oldItems = new ArrayList<>(Arrays.asList("Cheesy...", "Crispy... ", "Fizzy...", "Cool...", "Softy...", "Fruity...",
            "Cheesy...", "Crispy... ", "Fizzy...", "Cool...", "Softy...", "Fruity..."));
    private final Messenger mMessenger = new Messenger(new IncomingMessageHandler());
    private ServiceConnection mConnection = this;
    private Messenger mServiceMessenger = null;
    private  boolean mIsBound;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorStatus));
        }

        topRecyclerView = RecyclerView.class.cast(findViewById(R.id.topRecycle));
        bottomRecyclerView = RecyclerView.class.cast(findViewById(R.id.bottomRecycle));
        imgv_serach=(ImageView)findViewById(R.id.imgv_search);
        ll_search=(LinearLayout)findViewById(R.id.ll_patientsearch);
        rl_search=(RelativeLayout)findViewById(R.id.rl_searchLayout);
        edttxt_search=(EditText)findViewById(R.id.searchEdittext);
        imgv_clear=(ImageView)findViewById(R.id.imageViewcrossIcon);
        btn_requestambulance=(Button)findViewById(R.id.btn_requestambulance);
        al_patientdetails=new ArrayList<>();

        new Sansation().overrideFonts(getApplicationContext() , findViewById(R.id.homeLayout));

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        topRecyclerView.setLayoutManager(horizontalLayoutManager);

        TopRecycleAdaptar topRecycleAdaptar = new TopRecycleAdaptar(this, onGoingItems);
        topRecyclerView.setAdapter(topRecycleAdaptar);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(HomeActivity.this, 4);
        bottomRecyclerView.setLayoutManager(gridLayoutManager);
        btn_requestambulance.setOnClickListener(this);
        bottomRecycleAdaptar = new BottomRecycleAdaptar(this , onGoingItems);
        bottomRecyclerView.setAdapter(bottomRecycleAdaptar);
        if (!MySmackService.isRunning()) {
            startService(new Intent(HomeActivity.this, MySmackService.class));
        }
        automaticBind();
        imgv_serach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_search.setVisibility(View.GONE);
                ll_search.setVisibility(View.VISIBLE);
                edttxt_search.requestFocus();
            }
        });
        imgv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_search.setVisibility(View.VISIBLE);
                ll_search.setVisibility(View.GONE);
                edttxt_search.setText("");
            }

        });
        edttxt_search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                query = query.toString().toLowerCase();

                filteredList = new ArrayList<>();

                for (int i = 0; i < onGoingItems.size(); i++) {

                    final String text = onGoingItems.get(i).toLowerCase();
                    if (text.contains(query)) {

                        filteredList.add(onGoingItems.get(i));
                    }
                }
                bottomRecyclerView.setLayoutManager(new GridLayoutManager(HomeActivity.this,4));
                //mRecyclerView.setLayoutManager(mLayoutManager);
                bottomRecycleAdaptar = new BottomRecycleAdaptar(HomeActivity.this, filteredList);
                bottomRecyclerView.setAdapter(bottomRecycleAdaptar);
                bottomRecycleAdaptar.notifyDataSetChanged();  // data set changed
            }
        });
        if(CheckConnection.isOnline(HomeActivity.this))
        {
            //new getPatientlist().execute("Hospitalid");
        }else
        {
            Toast.makeText(this,"There is no internet connection",Toast.LENGTH_SHORT).show();
        }

    }
//Code to connect with smackservice

    private void automaticBind() {
        doBindService();
    }

    private void doBindService() {
        bindService(new Intent(this, MySmackService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        sendMessageToService(0);
    }

    private void doUnbindService() {
        if (mIsBound) {
            if (mServiceMessenger != null) {
                try {
                    android.os.Message msg = android.os.Message.obtain(null, MySmackService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mServiceMessenger.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service has crashed.
                }
            }
            unbindService(mConnection);
            mIsBound = false;
        }
    }
    @Override
    public void onClick(View view)
    {
        if(view.equals(btn_requestambulance))
        {
            startActivity(new Intent(HomeActivity.this,RequestAmbulanceActivity.class));
        }
    }

    private void sendMessageToService(int intvaluetosend)
    {
        if (mIsBound)
        {
            if (mServiceMessenger != null)
            {
                try
                {
                    android.os.Message msg = android.os.Message.obtain(null, App.HOME, intvaluetosend, 0);
                    msg.replyTo = mMessenger;
                    mServiceMessenger.send(msg);
                }
                catch (RemoteException e)
                {
                    Log.e("BIND", e.toString());
                }
            }
        }
    }
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service)
    {
        mServiceMessenger = new Messenger(service);
        try {
            android.os.Message msg = android.os.Message.obtain(null, MySmackService.MSG_REGISTER_CLIENT);
            msg.replyTo = mMessenger;
            mServiceMessenger.send(msg);
        } catch (RemoteException e) {
            // In this case the service has crashed before we could even do anything with it
        }
        sendMessageToService(0);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }
    private class IncomingMessageHandler extends Handler
    {
        @Override
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case MySmackService.MSG_SET_INT_VALUE:
                    break;

                case MySmackService.MSG_SET_STRING_VALUE:
                    break;

                case App.HOME_REQUEST:

                    Intent resultIntent = new Intent(HomeActivity.this, ServiceRequest.class);
                    resultIntent.putExtra("sosid" , msg.getData().getString("sosid"));
                    resultIntent.putExtra("userid" , msg.getData().getString("userid"));
                    resultIntent.putExtra("self" , msg.getData().getString("self"));
                    resultIntent.putExtra("latitude" , msg.getData().getDouble("latitude"));
                    resultIntent.putExtra("longitude" , msg.getData().getDouble("longitude"));

                    startActivity(resultIntent);

                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }
    @Override
    protected void onStop() {
        sendMessageToService(1);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        sendMessageToService(1);
        doUnbindService();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //getFragmentManager().popBackStack();
    }

    @Override
    protected void onResume() {
        sendMessageToService(0);
        super.onResume();

    }
    public class getPatientlist extends AsyncTask<String, Void, String>
    {
        private String results;

        @Override
        protected void onPreExecute()
        {
            progressDialog = new CustomProgressDialog(HomeActivity.this, AlertDialog.THEME_HOLO_LIGHT);
            progressDialog.setMessage("processing");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params)
        {

            try {
                HttpClient http_client = new DefaultHttpClient();
                HttpPost http_post = new HttpPost(Cons.port+"services/getPatientlist");
//                HttpPost http_post = new HttpPost("http://139.59.24.15/ien/getContactDetails.php");
                List<NameValuePair> nameVP = new ArrayList<NameValuePair>(1);
                nameVP.add(new BasicNameValuePair("hospitalid" , params[0]));
                //nameVP.add(new BasicNameValuePair("longitude" , params[2]));
                http_post.setEntity(new UrlEncodedFormEntity(nameVP));
                HttpEntity entity = http_client.execute(http_post).getEntity();
                if (entity != null) {
                    String response = EntityUtils.toString(entity);
                    Log.e("test" , response);
                    entity.consumeContent();
                    http_client.getConnectionManager().shutdown();
                    try
                    {
                        if(!response.equals("0"))
                        {
                            JSONArray jarray= new JSONArray(response);
                            if(jarray.length()>0)
                            {
                                for (int i = 0; i < jarray.length(); i++)
                                {
                                    final JSONObject data = jarray.getJSONObject(i);
                                    PatientDetails patientDetails=new PatientDetails();
                                    patientDetails.setStr_name(data.getString("name"));
                                    patientDetails.setStr_address(data.getString("address"));
                                    patientDetails.setInt_image(data.getInt("thumbnail"));
                                    patientDetails.setStr_dateofadmit(data.getString("dateofadmit"));
                                    patientDetails.setStr_emergencytype(data.getString("emergencytype"));
                                    patientDetails.setDouble_latitude(data.getDouble("latitude"));
                                    patientDetails.setDouble_longitude(data.getDouble("longtude"));
                                    al_patientdetails.add(patientDetails);

                                    }
                            }
                            results = params[0];
                        }
                        else
                        {
                            results = "Error";
                        }
                    }
                    catch(Exception e)
                    {
                        Log.e("test" , e.toString());
                        results = "NoEvents";
                    }

                }
                else
                {
                    results = "Failure";
                }
            }
            catch (Exception e)
            {
                results = e.toString();
                Log.e("server", results);
            }
            return results;
        }

        @Override
        protected void onPostExecute(final String result)
        {
            progressDialog.cancel();
            bottomRecycleAdaptar = new BottomRecycleAdaptar(HomeActivity.this , al_patientdetails);
            bottomRecyclerView.setAdapter(bottomRecycleAdaptar);
            bottomRecycleAdaptar.notifyDataSetChanged();
        }
    }
}
