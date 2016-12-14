package mainbrain.tech.ienhospital.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import java.util.ArrayList;
import java.util.Arrays;

import mainbrain.tech.ienhospital.Adapters.BottomRecycleAdaptar;
import mainbrain.tech.ienhospital.Adapters.LeftRecycleAdaptar;
import mainbrain.tech.ienhospital.Helper.CheckConnection;
import mainbrain.tech.ienhospital.Model.PatientDetails;
import mainbrain.tech.ienhospital.R;
import mainbrain.tech.ienhospital.Helper.Sansation;

/**
 * Created by MikeJaison on 12/2/16.
 */

public class PatientDetailsActivity extends AppCompatActivity implements View.OnClickListener,OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener
{
    RecyclerView leftRecyclerView;
    private LinearLayout ll_search;
    private RelativeLayout rl_search;
    private EditText edttxt_search;
    private ImageView imgv_serach,imgv_clear,imgv_close;
    ArrayList<String> filteredList;
    private ProgressDialog progressDialog;
    LeftRecycleAdaptar leftRecycleAdaptar;
    ArrayList<String> oldItems = new ArrayList<>(Arrays.asList("Cheesy...", "Crispy... ", "Fizzy...", "Cool...", "Softy...", "Fruity...",
            "Cheesy...", "Crispy... ", "Fizzy...", "Cool...", "Softy...", "Fruity..."));
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_details);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorStatus));
        }
        ll_search=(LinearLayout)findViewById(R.id.ll_patientsearch);
        rl_search=(RelativeLayout)findViewById(R.id.rl_search);
        leftRecyclerView = RecyclerView.class.cast(findViewById(R.id.leftRecycle));
        imgv_serach=(ImageView)findViewById(R.id.imgv_search);
        imgv_clear=(ImageView)findViewById(R.id.imageViewcrossIcon);
        imgv_close=(ImageView)findViewById(R.id.imgv_close);
        edttxt_search=(EditText)findViewById(R.id.searchEdittext);
        imgv_close.setOnClickListener(this);

        new Sansation().overrideFonts(getApplicationContext() , findViewById(R.id.detailsLayout));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(PatientDetailsActivity.this, 2);
        leftRecyclerView.setLayoutManager(gridLayoutManager);

        leftRecycleAdaptar = new LeftRecycleAdaptar(getApplicationContext() , oldItems);
        leftRecyclerView.setAdapter(leftRecycleAdaptar);
        if (ContextCompat.checkSelfPermission(PatientDetailsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fetchLocationData();
        } else {
            ActivityCompat.requestPermissions(PatientDetailsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 11);
        }
        buildGoogleApiClient();
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000); // Update location every second
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

                for (int i = 0; i < oldItems.size(); i++) {

                    final String text = oldItems.get(i).toLowerCase();
                    if (text.contains(query)) {

                        filteredList.add(oldItems.get(i));
                    }
                }
                leftRecyclerView.setLayoutManager(new GridLayoutManager(PatientDetailsActivity.this,2));
                //mRecyclerView.setLayoutManager(mLayoutManager);
                leftRecycleAdaptar= new LeftRecycleAdaptar(PatientDetailsActivity.this, filteredList);
                leftRecyclerView.setAdapter(leftRecycleAdaptar);
                leftRecycleAdaptar.notifyDataSetChanged();  // data set changed
            }
        });

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
        if(view.equals(imgv_close))
        {
            finish();
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
