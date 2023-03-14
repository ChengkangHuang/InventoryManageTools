package ca.mohawkcollege.ordersystem;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class ItemAdapter extends BaseAdapter {

    private static final String TAG = "===ItemAdapter===";

    private final List<Map<String, Object>> mapList;

    public ItemAdapter(List<Map<String, Object>> list) {
        mapList = list;
        Log.d(TAG, "ItemAdapter: " + mapList.size());
    }

    @Override
    public int getCount() {
        return mapList.size();
    }

    @Override
    public Object getItem(int i) {
        return mapList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = View.inflate(viewGroup.getContext(), R.layout.item, null);
            holder = new ViewHolder();
            holder.itemImage = view.findViewById(R.id.itemImageView);
            holder.itemName = view.findViewById(R.id.itemTextView);
            holder.orderButton = view.findViewById(R.id.actionButton);
            holder.minusImageButton = view.findViewById(R.id.minusImageButton);
            holder.plusImageButton = view.findViewById(R.id.plusImageButton);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        ItemRequest item = (ItemRequest) mapList.get(i).get("item");
        holder.itemImage.setImageResource(item.getItemImage());
        holder.itemName.setText(item.getItemName());
        return view;
    }

    static class ViewHolder {
        ImageView itemImage;
        TextView itemName;
        Button orderButton;
        ImageButton minusImageButton;
        ImageButton plusImageButton;
    }
}
