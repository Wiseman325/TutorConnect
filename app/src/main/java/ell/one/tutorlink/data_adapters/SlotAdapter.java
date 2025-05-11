package ell.one.tutorlink.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ell.one.tutorlink.R;

public class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.SlotViewHolder> {

    private final List<String> slots;
    private final OnSlotClickListener listener;

    // Constructor
    public SlotAdapter(List<String> slots, OnSlotClickListener listener) {
        this.slots = slots;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.slot_item_layout, parent, false);
        return new SlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlotViewHolder holder, int position) {
        String slot = slots.get(position);
        holder.slotText.setText(slot);
        holder.itemView.setOnClickListener(v -> listener.onSlotClick(position));
    }

    @Override
    public int getItemCount() {
        return slots.size();
    }

    // ViewHolder Class
    static class SlotViewHolder extends RecyclerView.ViewHolder {
        TextView slotText;

        public SlotViewHolder(@NonNull View itemView) {
            super(itemView);
            slotText = itemView.findViewById(R.id.slotText);
        }
    }

    // Click Listener Interface
    public interface OnSlotClickListener {
        void onSlotClick(int position);
    }
}
