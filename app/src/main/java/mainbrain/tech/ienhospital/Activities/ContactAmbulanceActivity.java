package mainbrain.tech.ienhospital.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import mainbrain.tech.ienhospital.Helper.Sansation;
import mainbrain.tech.ienhospital.R;

public class ContactAmbulanceActivity extends AppCompatActivity implements View.OnClickListener {
    TextView txtv_time,txtv_name,txtv_address,txtv_phone,txtv_reqambulanedetailtime;
    String str_name,str_address,str_phone,str_time;
    ImageView imgv_thumbnail;
    FloatingActionButton fab_close;
    Button btn_contactambulance;
    LinearLayout ll_main;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_ambulance);

        txtv_name=(TextView)findViewById(R.id.txtv_name);
        txtv_address=(TextView)findViewById(R.id.txtv_address);
        txtv_phone=(TextView)findViewById(R.id.txtv_phone);
        imgv_thumbnail=(ImageView)findViewById(R.id.img_thumbnail);
        txtv_reqambulanedetailtime=(TextView)findViewById(R.id.txtv_reqambulancetime);
        btn_contactambulance=(Button)findViewById(R.id.btn_contactambulance);
        ll_main=(LinearLayout)findViewById(R.id.ll_mainlayout);
        preferences=getSharedPreferences("ienhospital", Context.MODE_PRIVATE);
        str_name=preferences.getString("name","");
        str_address=preferences.getString("address","");
        str_phone=preferences.getString("phone","");
        str_time=preferences.getString("time","");

        fab_close=(FloatingActionButton)findViewById(R.id.fabclose);
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


    }

    @Override
    public void onClick(View view)
    {
        if(view.equals(fab_close))
        {
            finish();
        }else if(view.equals(btn_contactambulance))
        {

        }
    }
}
