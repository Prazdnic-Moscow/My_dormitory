package com.example.mydormitory;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class LaundryActivity extends AppCompatActivity {

    private LinearLayout machineTabsLayout, dateSelectionLayout, durationSelectionLayout, myBookingsContainer;
    private LinearLayout timeSelectionLayout, bookingSummaryLayout;
    private Button bookButton;
    private TextView bookingSummaryText;
    private RecyclerView timeSlotsRecyclerView;
    private TimeSlotAdapter adapter;

    private int selectedMachine = 1;
    private String selectedDate = "", selectedStartTime = "";
    private float selectedDuration = 1.0f;
    private List<LaundryBooking> userBookings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry);

        setupToolbar();
        setupComponents();
        setupClickListeners();
        loadSampleData();
        updateTimeSlots();
    }

    /* -------------------- UI -------------------- */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Прачечная");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupComponents() {
        machineTabsLayout = findViewById(R.id.machineTabsLayout);
        dateSelectionLayout = findViewById(R.id.dateSelectionLayout);
        durationSelectionLayout = findViewById(R.id.durationSelectionLayout);
        timeSelectionLayout = findViewById(R.id.timeSelectionLayout);
        bookingSummaryLayout = findViewById(R.id.bookingSummaryLayout);
        bookButton = findViewById(R.id.bookButton);
        bookingSummaryText = findViewById(R.id.bookingSummaryText);
        myBookingsContainer = findViewById(R.id.myBookingsContainer);
        timeSlotsRecyclerView = findViewById(R.id.timeSlotsRecyclerView);
    }

    private void setupClickListeners() {
        setupMachineTabs();
        setupDateButtons();
        setupDurationButtons();
        bookButton.setOnClickListener(v -> bookMachine());
    }

    /* -------------------- Машины, даты, длительность -------------------- */
    private void setupMachineTabs() {
        for (int i = 0; i < machineTabsLayout.getChildCount(); i++) {
            final int machine = i + 1;
            machineTabsLayout.getChildAt(i).setOnClickListener(v -> selectMachine(machine));
        }
    }

    private void setupDateButtons() {
        setupAllButtons(dateSelectionLayout, text -> {
            selectedDate = text;
            updateDateSelection();
            updateTimeSlots();
        });
    }

    private void setupDurationButtons() {
        setupAllButtons(durationSelectionLayout, text -> {
            selectedDuration = Float.parseFloat(text.replace(" ч", "").trim());
            updateDurationSelection();
            updateBookingSummary();
        });
    }

    private void setupAllButtons(LinearLayout parent, java.util.function.Consumer<String> onClick) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof Button) {
                Button btn = (Button) child;
                btn.setOnClickListener(v -> onClick.accept(btn.getText().toString()));
            } else if (child instanceof LinearLayout) {
                setupAllButtons((LinearLayout) child, onClick);
            }
        }
    }

    private void selectMachine(int machine) {
        selectedMachine = machine;
        updateMachineTabs();
        updateTimeSlots();
    }

    private void updateMachineTabs() {
        for (int i = 0; i < machineTabsLayout.getChildCount(); i++) {
            Button tab = (Button) machineTabsLayout.getChildAt(i);
            boolean isSelected = (i + 1 == selectedMachine);
            tab.setBackground(createRoundedBackground(getColor(isSelected ? R.color.blue : R.color.bg_grey)));
            tab.setTextColor(getColor(isSelected ? R.color.white : R.color.grey));
            tab.setAlpha(isSelected ? 1.0f : 0.6f);
        }
    }

    private void updateDateSelection() {
        highlightButtons(dateSelectionLayout, selectedDate, false);
    }

    private void updateDurationSelection() {
        highlightButtons(durationSelectionLayout, "", true);
    }

    private void highlightButtons(LinearLayout parent, String selectedText, boolean isDuration) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof Button) {
                Button btn = (Button) child;
                String btnText = btn.getText().toString().trim();

                boolean isSelected;
                if (isDuration) {
                    String valueStr = btnText.replace(" ч", "").trim();
                    try {
                        float value = Float.parseFloat(valueStr);
                        isSelected = (value == selectedDuration);
                    } catch (NumberFormatException e) {
                        isSelected = false;
                    }
                } else {
                    isSelected = btnText.equals(selectedText);
                }

                btn.setBackground(createRoundedBackground(getColor(isSelected ? R.color.blue : R.color.bg_grey)));
                btn.setTextColor(getColor(isSelected ? R.color.white : R.color.grey));
                btn.setAlpha(isSelected ? 1.0f : 0.6f);
            } else if (child instanceof LinearLayout) {
                highlightButtons((LinearLayout) child, selectedText, isDuration);
            }
        }
    }

    /* -------------------- Сводка и бронирование -------------------- */
    private void updateBookingSummary() {
        if (selectedStartTime.isEmpty()) {
            bookingSummaryText.setText("Выберите время начала");
            bookButton.setText("Выберите время начала");
            return;
        }
        String end = calculateEndTime(selectedStartTime, selectedDuration);
        bookingSummaryText.setText("Бронируете с " + selectedStartTime + " до " + end);
        bookButton.setText("Забронировать Машину #" + selectedMachine + " " +
                selectedDate + " с " + selectedStartTime + " до " + end);
    }

    private void bookMachine() {
        if (selectedStartTime.isEmpty()) {
            Toast.makeText(this, "Выберите время", Toast.LENGTH_SHORT).show();
            return;
        }
        String end = calculateEndTime(selectedStartTime, selectedDuration);
        userBookings.add(new LaundryBooking(selectedMachine, selectedDate,
                selectedStartTime, end, selectedDuration));
        Toast.makeText(this, "Забронировано!", Toast.LENGTH_SHORT).show();
        updateMyBookings();
        updateTimeSlots();
        updateBookingSummary();
    }

    /* -------------------- Слоты времени -------------------- */
    private void updateTimeSlots() {
        List<TimeSlot> slots = generateTimeSlots();
        adapter = new TimeSlotAdapter(slots, time -> {
            if (!isTimeSlotAvailable(time)) return;
            selectedStartTime = time;
            updateBookingSummary();
            adapter.notifyDataSetChanged();
        });
        timeSlotsRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        timeSlotsRecyclerView.setAdapter(adapter);
    }

    private List<TimeSlot> generateTimeSlots() {
        List<TimeSlot> list = new ArrayList<>();
        for (int h = 8; h < 22; h++) {
            for (int m = 0; m < 60; m += 30) {
                String t = String.format("%02d:%02d", h, m);
                list.add(new TimeSlot(t, isTimeSlotAvailable(t)));
            }
        }
        return list;
    }

    private boolean isTimeSlotAvailable(String time) {
        for (LaundryBooking b : userBookings) {
            if (b.getMachineNumber() == selectedMachine && b.getDate().equals(selectedDate)) {
                if (time.compareTo(b.getStartTime()) >= 0 && time.compareTo(b.getEndTime()) < 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private String calculateEndTime(String start, float duration) {
        String[] p = start.split(":");
        int mins = Integer.parseInt(p[0]) * 60 + Integer.parseInt(p[1]) + (int) (duration * 60);
        return String.format("%02d:%02d", mins / 60 % 24, mins % 60);
    }

    /* -------------------- Мои бронирования -------------------- */
    private void updateMyBookings() {
        myBookingsContainer.removeAllViews();
        if (userBookings.isEmpty()) {
            TextView tv = new TextView(this);
            tv.setText("Нет бронирований");
            tv.setTextColor(getColor(R.color.black));
            myBookingsContainer.addView(tv);
        } else {
            for (LaundryBooking b : userBookings) {
                myBookingsContainer.addView(createBookingView(b));
            }
        }
    }

    private View createBookingView(LaundryBooking b) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundResource(R.drawable.booking_item_background);
        int p = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(p, p, p, p);
        layout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));

        TextView[] tvs = {new TextView(this), new TextView(this), new TextView(this)};
        tvs[0].setText("Машина #" + b.getMachineNumber());
        tvs[0].setTextColor(getColor(R.color.black));
        tvs[0].setTextSize(16);
        tvs[0].setTypeface(null, android.graphics.Typeface.BOLD);

        tvs[1].setText(b.getDate());
        tvs[2].setText(b.getStartTime() + " - " + b.getEndTime() + " (" + b.getDuration() + " ч)");
        tvs[1].setTextColor(getColor(R.color.black));
        tvs[2].setTextColor(getColor(R.color.black));
        tvs[1].setTextSize(14);
        tvs[2].setTextSize(14);

        for (TextView tv : tvs) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
            lp.topMargin = tv == tvs[0] ? 0 : p / 4;
            tv.setLayoutParams(lp);
            layout.addView(tv);
        }
        return layout;
    }

    private void loadSampleData() {
        userBookings.add(new LaundryBooking(1, "сб, 13 сент.", "09:30", "12:30", 3.0f));
        updateMyBookings();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /* -------------------- СКРУГЛЁННЫЙ ФОН -------------------- */
    private GradientDrawable createRoundedBackground(int color) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(12 * getResources().getDisplayMetrics().density); // 12dp
        shape.setColor(color);
        return shape;
    }

    /* -------------------- Адаптер времени -------------------- */
    private class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.ViewHolder> {
        private final List<TimeSlot> slots;
        private final java.util.function.Consumer<String> onClick;

        TimeSlotAdapter(List<TimeSlot> slots, java.util.function.Consumer<String> onClick) {
            this.slots = slots;
            this.onClick = onClick;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Button btn = new Button(parent.getContext());
            btn.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
            btn.setPadding(32, 24, 32, 24);
            btn.setTextSize(14);
            btn.setBackground(createRoundedBackground(parent.getContext().getColor(R.color.bg_grey)));
            btn.setTextColor(parent.getContext().getColor(R.color.grey));
            return new ViewHolder(btn);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            TimeSlot slot = slots.get(position);
            Button btn = holder.button;
            btn.setText(slot.time);

            boolean isSelected = slot.time.equals(selectedStartTime) && slot.available;
            boolean isAvailable = slot.available;

            int bgColor = getColor(isSelected ? R.color.blue : R.color.bg_grey);
            int textColor = getColor(isSelected ? R.color.white : R.color.grey);

            btn.setBackground(createRoundedBackground(bgColor));
            btn.setTextColor(textColor);
            btn.setAlpha(isSelected ? 1.0f : 0.6f);
            btn.setEnabled(isAvailable);

            btn.setOnClickListener(v -> {
                if (isAvailable) onClick.accept(slot.time);
            });
        }

        @Override
        public int getItemCount() {
            return slots.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            Button button;
            ViewHolder(Button v) {
                super(v);
                button = v;
            }
        }
    }

    /* -------------------- Модели -------------------- */
    private static class TimeSlot {
        String time;
        boolean available;
        TimeSlot(String time, boolean available) {
            this.time = time;
            this.available = available;
        }
    }

    private static class LaundryBooking {
        int machineNumber;
        String date, startTime, endTime;
        float duration;

        LaundryBooking(int machineNumber, String date, String startTime, String endTime, float duration) {
            this.machineNumber = machineNumber;
            this.date = date;
            this.startTime = startTime;
            this.endTime = endTime;
            this.duration = duration;
        }

        int getMachineNumber() { return machineNumber; }
        String getDate() { return date; }
        String getStartTime() { return startTime; }
        String getEndTime() { return endTime; }
        float getDuration() { return duration; }
    }
}