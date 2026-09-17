package com.example.cropmanagementapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cropmanagementapp.db.DatabaseHelper;
import com.example.cropmanagementapp.db.DateUtils;
import com.example.cropmanagementapp.model.Crop;
import com.example.cropmanagementapp.model.IncomeLog;

import java.util.Calendar;
import java.util.Locale;

/**
 * Logs income from selling a crop, with a toggle between entering
 * quantity + rate per unit (auto-computed total) or a flat total amount.
 */
public class AddIncomeActivity extends AppCompatActivity {

    private TextView tvModeQtyRate, tvModeTotal, tvComputedTotal, tvFormTitle;
    private TextView tvStatusReceived, tvStatusPending;
    private LinearLayoutHolder holder; // not used, placeholder removed below

    private android.widget.LinearLayout llQtyRateBlock;
    private EditText etQuantity, etRate, etTotalAmount, etBuyerName, etNotes;
    private Spinner spinnerUnit;
    private Button btnIncomeDate, btnSaveIncome;

    private boolean qtyRateMode = true;
    private boolean fullyReceived = true;
    private String incomeDateIso = null;

    private DatabaseHelper dbHelper;
    private long cropId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);

        dbHelper = new DatabaseHelper(this);
        cropId = getIntent().getLongExtra("crop_id", -1);

        tvFormTitle = findViewById(R.id.tvFormTitle);
        tvModeQtyRate = findViewById(R.id.tvModeQtyRate);
        tvModeTotal = findViewById(R.id.tvModeTotal);
        llQtyRateBlock = findViewById(R.id.llQtyRateBlock);
        etQuantity = findViewById(R.id.etQuantity);
        etRate = findViewById(R.id.etRate);
        etTotalAmount = findViewById(R.id.etTotalAmount);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        tvComputedTotal = findViewById(R.id.tvComputedTotal);
        btnIncomeDate = findViewById(R.id.btnIncomeDate);
        etBuyerName = findViewById(R.id.etBuyerName);
        tvStatusReceived = findViewById(R.id.tvStatusReceived);
        tvStatusPending = findViewById(R.id.tvStatusPending);
        etNotes = findViewById(R.id.etNotes);
        btnSaveIncome = findViewById(R.id.btnSaveIncome);

        ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(this,
                R.array.income_units, android.R.layout.simple_spinner_item);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);

        Crop crop = dbHelper.getCrop(cropId);
        if (crop != null) {
            tvFormTitle.setText("Log Income — " + crop.getCropName());
        }

        tvModeQtyRate.setOnClickListener(v -> setMode(true));
        tvModeTotal.setOnClickListener(v -> setMode(false));
        tvStatusReceived.setOnClickListener(v -> setReceivedStatus(true));
        tvStatusPending.setOnClickListener(v -> setReceivedStatus(false));

        TextWatcher recompute = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateComputedTotal();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        };
        etQuantity.addTextChangedListener(recompute);
        etRate.addTextChangedListener(recompute);

        btnIncomeDate.setOnClickListener(v -> showDatePicker());
        btnSaveIncome.setOnClickListener(v -> saveIncome());

        setMode(true);
        setReceivedStatus(true);
    }

    private void setMode(boolean isQtyRate) {
        qtyRateMode = isQtyRate;
        tvModeQtyRate.setSelected(isQtyRate);
        tvModeTotal.setSelected(!isQtyRate);
        tvModeQtyRate.setTextColor(getResources().getColor(isQtyRate ? R.color.white : R.color.text_primary));
        tvModeTotal.setTextColor(getResources().getColor(!isQtyRate ? R.color.white : R.color.text_primary));

        llQtyRateBlock.setVisibility(isQtyRate ? View.VISIBLE : View.GONE);
        findViewById(R.id.etTotalAmount).setVisibility(isQtyRate ? View.GONE : View.VISIBLE);
        updateComputedTotal();
    }

    private void setReceivedStatus(boolean received) {
        fullyReceived = received;
        tvStatusReceived.setSelected(received);
        tvStatusPending.setSelected(!received);
        tvStatusReceived.setTextColor(getResources().getColor(received ? R.color.white : R.color.text_primary));
        tvStatusPending.setTextColor(getResources().getColor(!received ? R.color.white : R.color.text_primary));
    }

    private void updateComputedTotal() {
        if (qtyRateMode) {
            double qty = parseOrZero(etQuantity.getText().toString());
            double rate = parseOrZero(etRate.getText().toString());
            double total = qty * rate;
            tvComputedTotal.setText(String.format(Locale.getDefault(), "Total income: KES %.2f", total));
        } else {
            double total = parseOrZero(etTotalAmount.getText().toString());
            tvComputedTotal.setText(String.format(Locale.getDefault(), "Total income: KES %.2f", total));
        }
    }

    private double parseOrZero(String text) {
        if (TextUtils.isEmpty(text)) return 0;
        try { return Double.parseDouble(text.trim()); } catch (NumberFormatException e) { return 0; }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    incomeDateIso = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    btnIncomeDate.setText(DateUtils.toDisplayFormat(incomeDateIso));
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void saveIncome() {
        if (cropId == -1) {
            Toast.makeText(this, "Missing crop reference. Please go back and try again.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (incomeDateIso == null) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        String quantity = "", unit = "", rate = "", totalAmount;

        if (qtyRateMode) {
            quantity = etQuantity.getText().toString().trim();
            rate = etRate.getText().toString().trim();
            if (TextUtils.isEmpty(quantity) || parseOrZero(quantity) <= 0) {
                etQuantity.setError("Enter a quantity greater than 0");
                etQuantity.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(rate) || parseOrZero(rate) < 0) {
                etRate.setError("Enter a valid rate");
                etRate.requestFocus();
                return;
            }
            unit = spinnerUnit.getSelectedItem().toString();
            totalAmount = String.valueOf(parseOrZero(quantity) * parseOrZero(rate));
        } else {
            String total = etTotalAmount.getText().toString().trim();
            if (TextUtils.isEmpty(total) || parseOrZero(total) <= 0) {
                etTotalAmount.setError("Enter a total amount greater than 0");
                etTotalAmount.requestFocus();
                return;
            }
            totalAmount = String.valueOf(parseOrZero(total));
        }

        IncomeLog income = new IncomeLog();
        income.setCropId(cropId);
        income.setIncomeDate(incomeDateIso);
        income.setQuantity(quantity);
        income.setUnit(unit);
        income.setRate(rate);
        income.setTotalAmount(totalAmount);
        income.setBuyerName(etBuyerName.getText().toString().trim());
        income.setReceivedStatus(fullyReceived ? "Fully received" : "Pending");
        income.setNotes(etNotes.getText().toString().trim());

        long id = dbHelper.addIncome(income);
        if (id > 0) {
            Toast.makeText(this, "Income logged", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Could not save income. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    // Unused placeholder class removed to keep this valid Java — ignore if IDE flags it.
    private static class LinearLayoutHolder { }
}