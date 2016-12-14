package mainbrain.tech.ienhospital.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import mainbrain.tech.ienhospital.Application.App;
import mainbrain.tech.ienhospital.Interfaces.ItemClickListener;
import mainbrain.tech.ienhospital.R;
import mainbrain.tech.ienhospital.Helper.Sansation;

/**
 * Created by anupamchugh on 10/12/15.
 */

public class LeftRecycleAdaptar extends RecyclerView.Adapter<LeftRecycleAdaptar.ViewHolder> {

    ArrayList<String> alName;
    Context context;

    public LeftRecycleAdaptar(Context context, ArrayList<String> alName)
    {
        super();
        this.context = context;
        this.alName = alName;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.grid_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.tvSpecies.setText(alName.get(i));
        new Sansation().overrideFonts(context , viewHolder.ll_layout);

        viewHolder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (isLongClick) {
                    Toast.makeText(context, "#" + position + " - " + alName.get(position) + " (Long click)", Toast.LENGTH_SHORT).show();
                    //context.startActivity(new Intent(context, PatientDetailsActivity.class));
                } else {
                     Toast.makeText(context, "#" + position + " - " + alName.get(position), Toast.LENGTH_SHORT).show();
//                    Intent patientdetail=new Intent(context,PatientDetailsActivity.class);
//                    context.startActivities(new Intent[]{patientdetail});


                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return alName.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public CircleImageView imgThumbnail;
        public TextView tvSpecies;
        private ItemClickListener clickListener;
        private LinearLayout ll_layout;

        public ViewHolder(View itemView) {
            super(itemView);
            imgThumbnail = (CircleImageView) itemView.findViewById(R.id.img_thumbnail);
            tvSpecies = (TextView) itemView.findViewById(R.id.tv_species);
            ll_layout=(LinearLayout)itemView.findViewById(R.id.layout);

            ll_layout.getLayoutParams().height = App.swidth/4;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            setClickListener(clickListener);
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

