package tk.rngm33.noticeboard.adaptor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import tk.rngm33.noticeboard.R;
import tk.rngm33.noticeboard.model.Profile;

/**
 * Created by hp on 4/4/2018.
 */

public class DetailsAdaptor extends RecyclerView.Adapter <DetailsAdaptor.AdminViewHolder> {
    private Context mcontext;

    private ArrayList<Profile> mUploads = new ArrayList<>();

    public DetailsAdaptor(Context context, ArrayList<Profile> uploads) {
        mcontext = context;
        mUploads = uploads;

    }

    @Override
    public DetailsAdaptor.AdminViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mcontext).inflate(R.layout.viewuserprofilebyadmin, parent, false);
        return new AdminViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DetailsAdaptor.AdminViewHolder holder, int position) {
        Profile profile = mUploads.get(position);
        holder.textname.setText(profile.getName());
        holder.textaddress.setText(profile.getAdd());
        holder.textphone.setText(profile.getPhon());
        holder.textemail.setText(profile.getEmail());
        Picasso.with(mcontext).load(profile.getImage()).into(holder.img);

    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class AdminViewHolder extends RecyclerView.ViewHolder {
        public TextView textname, textaddress, textphone, textemail;
        ImageView img;

        public AdminViewHolder(final View itemView) {
            super(itemView);
            textname = itemView.findViewById(R.id.pname);
            textaddress = itemView.findViewById(R.id.padd);
            textphone = itemView.findViewById(R.id.pphone);
            textemail = itemView.findViewById(R.id.pemail);
            img= itemView.findViewById(R.id.pimage);

        }
    }
}
