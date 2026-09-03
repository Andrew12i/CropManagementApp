package com.example.cropmanagementapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cropmanagementapp.db.DatabaseHelper;
import com.example.cropmanagementapp.db.DateUtils;
import com.example.cropmanagementapp.db.ValidationUtils;
import com.example.cropmanagementapp.model.ActivityLog;
import com.example.cropmanagementapp.model.Crop;

import java.util.Calendar;

public class AddActivityLogActivity extends AppCompatActivity {

    private static final String OTHER_OPTION = "Other";

    private Spinner spinnerActivityType;
    private EditText etCustomActivityType, etExpenseAmount, etNotes;
    private Button btnActivityDate, btnSaveActivity;
    private TextView tvFormTitle;

    private String activityDateIso = null;
    private DatabaseHelper dbHelper;
    private long cropId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_activity_log);

        dbHelper = new DatabaseHelper(this);
        cropId = getIntent().getLongExtra("crop_id", -1);

        spinnerActivityType = findViewById(R.id.spinnerActivityType);
        etCustomActivityType = findViewById(R.id.etCustomActivityType);
        etExpenseAmount = findViewById(R.id.etExpenseAmount);
        btnActivityDate = findViewById(R.id.btnActivityDate);
        btnSaveActivity = findViewById(R.id.btnSaveActivity);
        etNotes = findViewById(R.id.etNotes);
        tvFormTitle = findViewById(R.id.tvFormTitle);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.activity_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActivityType.setAdapter(adapter);

        spinnerActivityType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean isOther = OTHER_OPTION.equals(spinnerActivityType.getSelectedItem().toString());
                etCustomActivityType.setVisibility(isOther ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        Crop crop = dbHelper.getCrop(cropId);
        if (crop != null) {
            tvFormTitle.setText("Log Activity — " + crop.getCropName());
        }

        btnActivityDate.setOnClickListener(v -> showDatePicker());
        btnSaveActivity.setOnClickListener(v -> saveActivity());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    activityDateIso = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    btnActivityDate.setText(DateUtils.toDisplayFormat(activityDateIso));
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void saveActivity() {
        if (cropId == -1) {
            Toast.makeText(this, "Missing crop reference. Please go back and try again.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (activityDateIso == null) {
            Toast.makeText(this, "Please select a date for this activity", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedType = spinnerActivityType.getSelectedItem().toString();
        String activityType;

        if (OTHER_OPTION.equals(selectedType)) {
            activityType = etCustomActivityType.getText().toString().trim();
            if (TextUtils.isEmpty(activityType)) {
                etCustomActivityType.setError("Please describe the activity");
                etCustomActivityType.requestFocus();
                return;
            }
            if (!ValidationUtils.containsLetter(activityType)) {
                etCustomActivityType.setError("Activity must include letters, not just numbers");
                etCustomActivityType.requestFocus();
                return;
            }
        } else {
            activityType = selectedType;
        }

        String expenseAmount = etExpenseAmount.getText().toString().trim();
        if (!TextUtils.isEmpty(expenseAmount) && !expenseAmount.matches("\\d+(\\.\\d+)?")) {
            etExpenseAmount.setError("Enter a valid amount, e.g. 500 or 500.50");
            etExpenseAmount.requestFocus();
            return;
        }

        String notes = etNotes.getText().toString().trim();

        ActivityLog log = new ActivityLog();
        log.setCropId(cropId);
        log.setActivityType(activityType);
        log.setActivityDate(activityDateIso);
        log.setNotes(notes);
        log.setExpenseAmount(expenseAmount);

        long id = dbHelper.addActivity(log);
        if (id > 0) {
            Toast.makeText(this, "Activity logged", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Could not save activity. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}