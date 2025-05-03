package ell.one.tutorlink.data_adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ell.one.tutorlink.R;
import ell.one.tutorlink.models.BookingModel;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private final List<BookingModel> bookings;
    private final boolean isTutor;
    private final OnCancelClickListener cancelListener;
    private final OnStatusChangeListener statusChangeListener;

    public interface OnCancelClickListener {
        void onCancelClick(BookingModel booking);
    }

    public interface OnStatusChangeListener {
        void onStatusChange(BookingModel booking, String newStatus);
    }

    public BookingAdapter(List<BookingModel> bookings, boolean isTutor,
                          OnCancelClickListener cancelListener,
                          OnStatusChangeListener statusChangeListener) {
        this.bookings = bookings;
        this.isTutor = isTutor;
        this.cancelListener = cancelListener;
        this.statusChangeListener = statusChangeListener;
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

        // Display appropriate name label
        holder.name.setText(isTutor
                ? "Tutee: " + booking.getTuteeName()
                : "Tutor: " + booking.getTutorName());

        holder.slot.setText("Date: " + booking.getDate() +
                "\nTime: " + booking.getStartTime() + " - " + booking.getEndTime());

        holder.status.setText("Status: " + booking.getStatus());

        // Show cancel button for Tutees only if status is still pending
        if (!isTutor && "pending".equalsIgnoreCase(booking.getStatus())) {
            holder.cancelButton.setVisibility(View.VISIBLE);
            holder.cancelButton.setOnClickListener(v -> {
                if (cancelListener != null) cancelListener.onCancelClick(booking);
            });
        } else {
            holder.cancelButton.setVisibility(View.GONE);
        }

        // Show status dropdown for Tutors
        if (isTutor) {
            holder.statusDropdown.setVisibility(View.VISIBLE);

            ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                    holder.itemView.getContext(),
                    R.array.booking_status_options, // Define in strings.xml
                    android.R.layout.simple_spinner_item
            );
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.statusDropdown.setAdapter(spinnerAdapter);

            // Set current selection based on status
            int selectedIndex = spinnerAdapter.getPosition(booking.getStatus());
            if (selectedIndex >= 0) {
                holder.statusDropdown.setSelection(selectedIndex, false);
            }

            holder.statusDropdown.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, View view, int pos, long id) {
                    String selectedStatus = parent.getItemAtPosition(pos).toString();
                    if (!selectedStatus.equalsIgnoreCase(booking.getStatus())) {
                        booking.setStatus(selectedStatus); // update model
                        if (statusChangeListener != null) {
                            statusChangeListener.onStatusChange(booking, selectedStatus);
                        }
                    }
                }

                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) {}
            });
        } else {
            holder.statusDropdown.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return bookings != null ? bookings.size() : 0;
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView name, slot, status;
        Button cancelButton;
        Spinner statusDropdown;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.bookingTutorName);
            slot = itemView.findViewById(R.id.bookingSlot);
            status = itemView.findViewById(R.id.bookingStatus);
            cancelButton = itemView.findViewById(R.id.cancelBookingBtn);
            statusDropdown = itemView.findViewById(R.id.statusDropdown);
        }
    }
}
