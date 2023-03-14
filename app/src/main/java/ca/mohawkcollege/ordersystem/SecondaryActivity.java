package ca.mohawkcollege.ordersystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SecondaryActivity extends AppCompatActivity {

    private static final String TAG = "===SecondaryActivity===";
    private static final int[] imgRes = {R.drawable.rice, R.drawable.avo_slices, R.drawable.avo, R.drawable.cuc, R.drawable.sushi, R.drawable.sashimi, R.drawable.crab, R.drawable.mgo, R.drawable.veg};
    private static final String[] imgName = {"Rice", "Avocado Slices", "Avocado", "Cucumber", "Salmon Sushi", "Salmon Sashimi", "Crab", "Mango", "Vegetables"};
    private List<Map<String, Object>> list;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    GridView gridView;
    ItemAdapter itemAdapter;
    Drawable roundBackground;
    TextView statusTextView;
    TextView quantityTextView;
    Button replayButton;
    ImageButton minusImageButton, plusImageButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_secondary);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        list = new ArrayList<>();
        initData();

        gridView = findViewById(R.id.gridview);
        itemAdapter = new ItemAdapter(list);
        gridView.setAdapter(itemAdapter);

        myRef = database.getReference("itemOrders");
        for (Map<String, Object> itemMap : list) {
            ItemRequest item = (ItemRequest) itemMap.get("item");
            assert item != null;
            myRef.child(item.getItemName()).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    // Setup initial status
                    Log.d(TAG, "onChildAdded: Item -> " + item.getItemName() + " | Key -> " + snapshot.getKey() + " | Value -> " + snapshot.getValue());
                    updateViewsContent(snapshot, item);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Log.d(TAG, "onChildChanged: Item -> " + item.getItemName() + " | Key -> " + snapshot.getKey() + " | Value -> " + snapshot.getValue());
                    updateViewsContent(snapshot, item);
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    Log.d(TAG, "onChildRemoved: " + snapshot.getValue());
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Log.d(TAG, "onChildMoved: " + snapshot.getValue());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, "onCancelled: " + error.getMessage());
                }
            });
        }
    }

    private void initData() {
        for (int i = 0; i < imgName.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("item", new ItemRequest(i, imgName[i], imgRes[i]));
            list.add(map);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setItemStatus(View gridChild, String statusText, int color) {
        statusTextView = gridChild.findViewById(R.id.statusTextView);
        statusTextView.setText(statusText);
        roundBackground = getDrawable(R.drawable.rounded_corners);
        roundBackground.setTint(color);
        statusTextView.setBackground(roundBackground);
    }

    public void setButtonStatus(View gridChild, ItemRequest item, boolean isEnable, int color) {
        replayButton = gridChild.findViewById(R.id.actionButton);
        replayButton.setEnabled(isEnable);
        replayButton.getBackground().setTint(color);
        replayButton.setText("Completed");
        replayButton.setOnClickListener(v -> {
            myRef.child(item.getItemName()).child("empty").setValue(false);
            Toast.makeText(this, "Notification sent > " + item.getItemName(), Toast.LENGTH_SHORT).show();
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
        });
    }

    @SuppressLint("NonConstantResourceId")
    public void setQuantityStatus(View gridChild, ItemRequest item) {
        quantityTextView = gridChild.findViewById(R.id.itemLeftTextView);
        quantityTextView.setText(String.valueOf(item.getQuantity()));
        minusImageButton = gridChild.findViewById(R.id.minusImageButton);
        plusImageButton = gridChild.findViewById(R.id.plusImageButton);
        minusImageButton.setOnClickListener(v -> {
            if (item.getQuantity() > 0) {
                item.setQuantity(item.getQuantity() - 1);
                myRef.child(item.getItemName()).child("quantity").setValue(item.getQuantity());
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                Log.d(TAG, "setQuantity: " + item.getItemName() + " -1");
            }
        });
        plusImageButton.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            myRef.child(item.getItemName()).child("quantity").setValue(item.getQuantity());
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
            Log.d(TAG, "setQuantity: " + item.getItemName() + " +1");
        });
    }

    public void updateViewsContent(@NonNull DataSnapshot snapshot, ItemRequest item) {
        Context context = getApplicationContext();
        View gridViewChild = gridView.getChildAt(item.getItemId());
        switch (Objects.requireNonNull(snapshot.getKey())) {
            case "empty":
                if ((boolean) Objects.requireNonNull(snapshot.getValue())) {
                    item.setEmpty(true);
                    setItemStatus(gridViewChild, "Order Requested", Color.parseColor("#E82020"));
                    setButtonStatus(gridViewChild, item, true, Color.parseColor("#2196F3"));
                    // Play Notification Sound
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(context, notification);
                    r.play();
                } else {
                    item.setEmpty(false);
                    setItemStatus(gridViewChild, "No Order Request", Color.parseColor("#4CAF50"));
                    setButtonStatus(gridViewChild, item, false, Color.parseColor("#807f7e"));
                }
                break;
            case "quantity":
                int quantity = Integer.parseInt(Objects.requireNonNull(snapshot.getValue()).toString());
                if (quantity >= 0) {
                    item.setQuantity(quantity);
                    setQuantityStatus(gridViewChild, item);
                }
                break;
        }
    }

    public void logout(View view) {
        mAuth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}