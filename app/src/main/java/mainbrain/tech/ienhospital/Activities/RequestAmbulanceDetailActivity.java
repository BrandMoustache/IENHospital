package mainbrain.tech.ienhospital.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import mainbrain.tech.ienhospital.Helper.CheckConnection;
import mainbrain.tech.ienhospital.Helper.Sansation;
import mainbrain.tech.ienhospital.R;

public class RequestAmbulanceDetailActivity extends AppCompatActivity implements View.OnClickListener,OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    TextView txtv_time,txtv_name,txtv_address,txtv_phone,txtv_reqambulanedetailtime;
    String str_name,str_address,str_phone,str_time;
    ImageView imgv_thumbnail;
    FloatingActionButton fab_close;
    Button btn_contactambulance;
    LinearLayout ll_main;
    SharedPreferences preferences;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private GoogleMap googleMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_ambulance_display);
        txtv_name=(TextView)findViewById(R.id.txtv_name);
        txtv_address=(TextView)findViewById(R.id.txtv_address);
        txtv_phone=(TextView)findViewById(R.id.txtv_phone);
        imgv_thumbnail=(ImageView)findViewById(R.id.img_thumbnail);
        txtv_reqambulanedetailtime=(TextView)findViewById(R.id.txtv_reqambulancetime);
        btn_contactambulance=(Button)findViewById(R.id.btn_contactambulance);
        ll_main=(LinearLayout)findViewById(R.id.ll_mainlayout);
        preferences=getSharedPreferences("ienhospital", Context.MODE_PRIVATE);
        fab_close=(FloatingActionButton)findViewById(R.id.fabclose);
        str_name=preferences.getString("name","");
        str_address=preferences.getString("address","");
        str_phone=preferences.getString("phone","");
        str_time=preferences.getString("time","");
        fab_close.setOnClickListener(this);
        btn_contactambulance.setOnClickListener(this);
        new Sansation().overrideFonts(getApplicationContext() , ll_main);
        txtv_name.setText(str_name);
        txtv_address.setText(str_address);
        txtv_phone.setText(str_phone);
        txtv_reqambulanedetailtime.setText(str_time);
        Glide.with(this)
                .load(R.drawable.cheese_1)
                .fitCenter()
                .into(imgv_thumbnail);
        if (ContextCompat.checkSelfPermission(RequestAmbulanceDetailActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fetchLocationData();
        } else {
            ActivityCompat.requestPermissions(RequestAmbulanceDetailActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 11);
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
    public void onClick(View view)
    {
        if(view.equals(fab_close))
        {
            finish();
        }else if(view.equals(btn_contactambulance))
        {
         startActivity(new Intent(RequestAmbulanceDetailActivity.this,ContactAmbulanceActivity.class));
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
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

    }

    @Override
    public void onMapReady(GoogleMap googlemap)
    {
        //Here to set the marker and move to camera depend up on lat,lang values on google maps.............
        googleMap = googlemap;
    }
}
