package mainbrain.tech.ienhospital.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;


import mainbrain.tech.ienhospital.Helper.CheckConnection;
import mainbrain.tech.ienhospital.Helper.Sansation;
import mainbrain.tech.ienhospital.Helper.UsPhoneNumberFormatter;
import mainbrain.tech.ienhospital.R;

/**
 * Created by Brand Moustache on 11/27/2016.
 */

public class RequestAmbulanceActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,View.OnClickListener {
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private GoogleMap googleMap;
    ImageView imgv_thumbnail;
    EditText edttxt_phonenumber,edttxt_name,edttxt_address,edttxt_landmark;
    TextView txtv_time;
    Button btn_requstambulance;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_ambulance);

        imgv_thumbnail=(ImageView)findViewById(R.id.img_thumbnail);
        edttxt_phonenumber=(EditText)findViewById(R.id.edtxt_phonenumber);
        edttxt_name=(EditText)findViewById(R.id.edtxt_name);
        edttxt_address=(EditText)findViewById(R.id.edtxt_Address);
        edttxt_landmark=(EditText)findViewById(R.id.edtxt_nearestlandmark);
        txtv_time=(TextView)findViewById(R.id.txtv_time);
        btn_requstambulance=(Button)findViewById(R.id.btn_reqambulance);
        btn_requstambulance.setOnClickListener(this);
        preferences=getSharedPreferences("ienhospital", Context.MODE_PRIVATE);
         new Sansation().overrideFonts(getApplicationContext() , findViewById(R.id.layout));
        UsPhoneNumberFormatter addLineNumberFormatter = new UsPhoneNumberFormatter(
                new WeakReference<EditText>(edttxt_phonenumber));
        edttxt_phonenumber.addTextChangedListener(addLineNumberFormatter);
        Date dNow = new Date( );
        SimpleDateFormat ft = new SimpleDateFormat("E hh:mm a");
        Log.e("Requestambulance","dayandtime==="+ft.format(dNow));
        txtv_time.setText(ft.format(dNow).toString());
        //check runtime permissions for google map..........
        if (ContextCompat.checkSelfPermission(RequestAmbulanceActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fetchLocationData();
        } else {
            ActivityCompat.requestPermissions(RequestAmbulanceActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 11);
        }
        buildGoogleApiClient();
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000); // Update location every second

    }
    /*This method is used to fetch the current location and set the marker on google map */
    private void fetchLocationData()
    {
        /* Here Initialization the google map*/
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.location_map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 11:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocationData();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access location data.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (CheckConnection.isOnline(this)) {
            if (mLastLocation != null) {
                getLocation();

            } else {
                Toast.makeText(this, "Couldn't get the location. Make sure location is enabled on the device", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, " There Is No InternetConnection", Toast.LENGTH_SHORT).show();
        }

    }
    private void getLocation() {
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng sydney = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(sydney).title("").draggable(true));
       // double latitude = Double.parseDouble(str_latitude);
       // double longtude = Double.parseDouble(str_longitude);
       // latlan = new LatLng(latitude, longtude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14.0f));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);

    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
     //   mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

    }

    @Override
    public void onMapReady(GoogleMap googlemap) {
        //Here to set the marker and move to camera depend up on lat,lang values on google maps.............
        googleMap = googlemap;

    }
    @Override
    public void onClick(View v)
    {
        if(v.equals(btn_requstambulance))
        {
            Intent reqambulance=new Intent(RequestAmbulanceActivity.this,RequestAmbulanceDetailActivity.class);
            startActivity(reqambulance);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("name",edttxt_name.getText().toString().trim());
            editor.putString("address",edttxt_address.getText().toString().trim());
            editor.putString("phone",edttxt_phonenumber.getText().toString().trim());
            editor.putString("time",txtv_time.getText().toString().trim());
            editor.commit();
        }

    }
}
