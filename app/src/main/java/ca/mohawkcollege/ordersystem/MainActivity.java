package ca.mohawkcollege.ordersystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "===MainActivity===";
    private static final int[] imgRes = {R.drawable.rice, R.drawable.avo_slices, R.drawable.avo, R.drawable.cuc, R.drawable.mgo, R.drawable.crab, R.drawable.veg};
    private static final String[] imgName = {"Rice", "Avocado Slices", "Avocado", "Cucumber", "Mango", "Crab", "Vegetables"};
    private static List<Map<String, Object>> list;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    GridView gridView;
    ItemAdapter itemAdapter;
    TextView statusTextView;
    Drawable roundBackground;
    Button sendButton;

    @SuppressLint({"MissingInflatedId", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        list = new ArrayList<>();
        initData();

        gridView = findViewById(R.id.gridview);
        itemAdapter = new ItemAdapter(list);
        gridView.setAdapter(itemAdapter);

        gridView.setOnItemClickListener((adapterView, view, i, l) -> {
            ItemRequest item = (ItemRequest) list.get(i).get("item");
            if (item != null) {
                item.setEmpty(true);
                myRef.child(item.getItemName()).setValue(item);
            }
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
        });

        myRef = database.getReference("itemOrders");
        myRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    ItemRequest item = itemSnapshot.getValue(ItemRequest.class);
                    if (item != null) {
                        View child = gridView.getChildAt(item.getItemId());
                        if (item.isEmpty()) {
                            setItemStatus(child, "Ordered", Color.parseColor("#FFC107"));
                            setButtonStatus(child, item, false, Color.parseColor("#807f7e"));
                        } else {
                            setItemStatus(child, "Ready to Order", Color.parseColor("#4CAF50"));
                            setButtonStatus(child, item, true, Color.parseColor("#03A9F4"));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void initData() {
        for (int i = 0; i < imgRes.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("item", new ItemRequest(i, imgName[i], imgRes[i], false, 10));
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
        sendButton = gridChild.findViewById(R.id.actionButton);
        sendButton.setEnabled(isEnable);
        sendButton.getBackground().setTint(color);
        sendButton.setText("Order");
        sendButton.setOnClickListener(v -> {
            item.setEmpty(true);
            myRef.child(item.getItemName()).setValue(item);
            Toast.makeText(this, "Order sent > " + item.getItemName(), Toast.LENGTH_SHORT).show();
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
        });
    }

    public void logout(View view) {
        mAuth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}