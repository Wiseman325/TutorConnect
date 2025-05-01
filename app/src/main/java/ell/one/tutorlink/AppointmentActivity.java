package ell.one.tutorlink;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
// implements NavigationView.OnNavigationItemSelectedListener
public class AppointmentActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    String[] services =  {"Select service", "Blood pressure", "Allergic disease", "Digestive problems", "Skin problem"};
    String[] bloodTypes =  {"Select blood type", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
    Spinner spinner, bloodTypeSpinner;
    ArrayAdapter<String> arrayAdapter, arrayAdapter1;
    EditText visitReason, symptoms;
    TextView username;
    Button submit;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
        visitReason = findViewById(R.id.reason_edit);
        symptoms = findViewById(R.id.symptoms_edit);
        username = findViewById(R.id.user);
        submit = findViewById(R.id.submit_appointment);
        spinner = findViewById(R.id.select_service);
        bloodTypeSpinner = findViewById(R.id.bloodType);

        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(services));
        ArrayList<String> bloodArrayList = new ArrayList<>(Arrays.asList(bloodTypes));

        arrayAdapter = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,arrayList);
        arrayAdapter1 = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,bloodArrayList);
        spinner.setAdapter(arrayAdapter);
        bloodTypeSpinner.setAdapter(arrayAdapter1);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!parent.getItemAtPosition(position).toString().equals("Select service")){
                    if (!parent.getItemAtPosition(position).toString().equals("Select blood type")) {

                        submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                database = FirebaseDatabase.getInstance();
                                reference = database.getReference("appointments");

                                String name = username.getText().toString();
                                String service = parent.getItemAtPosition(position).toString();
                                String blood_type = parent.getItemAtPosition(position).toString();
                                String visit_reason = visitReason.getText().toString();
                                String symptoms_experienced = symptoms.getText().toString();

                                AppointmentHelperClass appointmentHelperClass = new AppointmentHelperClass(name, service, blood_type, visit_reason, symptoms_experienced);
                                reference.child(name).setValue(appointmentHelperClass);

                                Toast.makeText(getApplicationContext(), "Appointment saved!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        drawerLayout = findViewById(R.id.drawer_layout);
//        NavigationView navigationView = findViewById(R.id.nav_view);
//
//        navigationView.setNavigationItemSelectedListener(this);
//
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
//                R.string.close_nav);
//        drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();
//
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
//            navigationView.setCheckedItem(R.id.nav_home);
//        }
//    }
//
//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.nav_home:
//                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(mainIntent);
//                break;
//
//            case R.id.nav_prof:
//                Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
//                startActivity(profileIntent);
//                break;
//
//            case R.id.nav_comment:
//                Intent commentIntent = new Intent(getApplicationContext(), CommentActivity.class);
//                startActivity(commentIntent);
//                break;
//
//            case R.id.nav_logout:
//                Intent logoutIntent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(logoutIntent);
//                Toast.makeText(this, "Logged out!", Toast.LENGTH_SHORT).show();
//                break;
//        }
//
//        drawerLayout.closeDrawer(GravityCompat.START);
//        return true;
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
    }
}