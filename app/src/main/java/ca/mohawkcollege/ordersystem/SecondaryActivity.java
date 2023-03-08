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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

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

public class SecondaryActivity extends AppCompatActivity {

    private static final String TAG = "===SecondaryActivity===";
    private final int[] imgRes = {R.drawable.rice, R.drawable.avo_slices, R.drawable.avo, R.drawable.cuc, R.drawable.mgo, R.drawable.crab, R.drawable.veg};
    private final String[] imgName = {"Rice", "Avocado Slices", "Avocado", "Cucumber", "Mango", "Crab", "Vegetables"};
    private List<Map<String, Object>> list;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    GridView gridView;
    resItemAdapter itemAdapter;
    Drawable roundBackground;
    TextView statusTextView;

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
        itemAdapter = new resItemAdapter();
        gridView.setAdapter(itemAdapter);

        gridView.setOnItemClickListener((adapterView, view, i, l) -> {
            ItemRequest item = (ItemRequest) list.get(i).get("item");
            if (item != null) {
                item.setEmpty(false);
                myRef.child(item.getItemName()).setValue(item);
            }
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
        });

        myRef = database.getReference("itemOrders");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    ItemRequest item = itemSnapshot.getValue(ItemRequest.class);
                    if (item != null) {
                        View child = gridView.getChildAt(item.getItemId());
                        if (item.isEmpty()) {
                            setStatus(child, "Order Received", Color.parseColor("#E82020"));
                        } else {
                            setStatus(child, "No Order Request", Color.parseColor("#4CAF50"));
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
    public void setStatus(View gridChild, String text, int color) {
        statusTextView = gridChild.findViewById(R.id.statusTextView);
        statusTextView.setText(text);
        roundBackground = getDrawable(R.drawable.rounded_corners);
        roundBackground.setTint(color);
        statusTextView.setBackground(roundBackground);
    }

    public class resItemAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            resViewHolder holder;
            if (view == null) {
                view = View.inflate(viewGroup.getContext(), R.layout.item, null);
                holder = new resViewHolder();
                holder.itemImage = view.findViewById(R.id.itemImageView);
                holder.itemName = view.findViewById(R.id.itemTextView);
                view.setTag(holder);
            } else {
                holder = (resViewHolder) view.getTag();
            }
            holder.itemImage.setImageResource(imgRes[i]);
            holder.itemName.setText(imgName[i]);
            return view;
        }

        class resViewHolder {
            ImageView itemImage;
            TextView itemName;
        }
    }

    public void logout(View view) {
        mAuth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}