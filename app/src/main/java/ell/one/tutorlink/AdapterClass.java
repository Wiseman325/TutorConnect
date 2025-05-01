package ell.one.tutorlink;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterClass extends RecyclerView.Adapter<ViewHolder_> {
    private final Context context;
    private final List<AppointmentHelperClass> dataList;

    public AdapterClass(Context context, List<AppointmentHelperClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    public ViewHolder_ onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder_(view);
    }

    public void onBindViewHolder(@NonNull ViewHolder_ holder, int position) {
        holder.recService.setText(dataList.get(position).getService());
        holder.recReason.setText(dataList.get(position).getVisit_reason());

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("reason", dataList.get(holder.getAdapterPosition()).getVisit_reason());
                intent.putExtra("service", dataList.get(holder.getAdapterPosition()).getService());
                intent.putExtra("Key",dataList.get(holder.getAdapterPosition()).getKey());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

class ViewHolder_ extends RecyclerView.ViewHolder{
    TextView recService, recReason;
    CardView recCard;

    public ViewHolder_(@NonNull View itemView) {
        super(itemView);

        recCard = itemView.findViewById(R.id.recCard);
        recReason = itemView.findViewById(R.id.recReas);
        recService = itemView.findViewById(R.id.recService);
    }
}