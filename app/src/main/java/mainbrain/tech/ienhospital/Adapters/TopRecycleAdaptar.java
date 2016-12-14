package mainbrain.tech.ienhospital.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import mainbrain.tech.ienhospital.Activities.PatientDetailsActivity;
import mainbrain.tech.ienhospital.Helper.Sansation;
import mainbrain.tech.ienhospital.Interfaces.ItemClickListener;
import mainbrain.tech.ienhospital.R;

/**
 * Created by anupamchugh on 10/12/15.
 */

public class TopRecycleAdaptar extends RecyclerView.Adapter<TopRecycleAdaptar.ViewHolder> {

    ArrayList<String> alName;
    Context context;

    public TopRecycleAdaptar(Context context, ArrayList<String> alName) {
        super();
        this.context = context;
        this.alName = alName;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.top_recycel_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v , context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.tvSpecies.setText(alName.get(i));
        viewHolder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (isLongClick) {
                    Toast.makeText(context, "#" + position + " - " + alName.get(position) + " (Long click)", Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, PatientDetailsActivity.class));
                } else {
                    // Toast.makeText(context, "#" + position + " - " + alName.get(position), Toast.LENGTH_SHORT).show();
                    Intent patientdetail=new Intent(context,PatientDetailsActivity.class);
                    context.startActivities(new Intent[]{patientdetail});


                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return alName.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public CircleImageView circleImageView;
        public TextView tvSpecies;
        private ItemClickListener clickListener;
        private LinearLayout ll_layout;

        public ViewHolder(View itemView , Context context) {
            super(itemView);
            circleImageView = (CircleImageView) itemView.findViewById(R.id.img_thumbnail);
            tvSpecies = (TextView) itemView.findViewById(R.id.tv_species);
            ll_layout=(LinearLayout)itemView.findViewById(R.id.layout);

            new Sansation().overrideFonts(context , ll_layout);

            ll_layout.getLayoutParams().width = 600;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            circleImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.circle));
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onClick(view, getPosition(), true);
            return true;
        }
    }

}

