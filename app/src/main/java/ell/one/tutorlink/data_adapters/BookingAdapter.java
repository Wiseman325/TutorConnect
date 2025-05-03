package ell.one.tutorlink.data_adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ell.one.tutorlink.R;
import ell.one.tutorlink.models.BookingModel;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private final List<BookingModel> bookings;
    private final OnCancelClickListener cancelListener;

    public interface OnCancelClickListener {
        void onCancelClick(BookingModel booking);
    }

    public BookingAdapter(List<BookingModel> bookings, OnCancelClickListener listener) {
        this.bookings = bookings;
        this.cancelListener = listener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        BookingModel booking = bookings.get(position);

        holder.tutorName.setText("Tutor: " + booking.getTutorName());
        holder.slot.setText("Date: " + booking.getDate() + "\nTime: " + booking.getStartTime() + " - " + booking.getEndTime());
        holder.status.setText("Status: " + booking.getStatus());

        holder.cancelButton.setOnClickListener(v -> {
            if (cancelListener != null) {
                cancelListener.onCancelClick(booking);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookings != null ? bookings.size() : 0;
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tutorName, slot, status;
        Button cancelButton;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tutorName = itemView.findViewById(R.id.bookingTutorName);
            slot = itemView.findViewById(R.id.bookingSlot);
            status = itemView.findViewById(R.id.bookingStatus);
            cancelButton = itemView.findViewById(R.id.cancelBookingBtn);
        }
    }
}
