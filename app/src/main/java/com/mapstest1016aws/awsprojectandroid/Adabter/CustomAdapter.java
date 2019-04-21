package com.mapstest1016aws.awsprojectandroid.Adabter;

import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.amazonaws.amplify.generated.graphql.ListDiarysQuery;
import com.mapstest1016aws.awsprojectandroid.R;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private List<ListDiarysQuery.Item> objects;
    private OnItemSelectedListener listener;
    private final boolean withContextMenu;

    class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnCreateContextMenuListener, PopupMenu.OnMenuItemClickListener {

        TextView txt_name,txt_description;

        ViewHolder(View view) {
            super(view);
            txt_name=(TextView)view.findViewById(R.id.txt_name);
            txt_description=(TextView)view.findViewById(R.id.txt_description);
            view.setOnClickListener(this);
            if (withContextMenu) {
                view.setOnCreateContextMenuListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (listener != null) {
                listener.onSelected(objects.get(position));
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.getMenuInflater().inflate(R.menu.custom_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(this);
            popup.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (listener != null) {
                ListDiarysQuery.Item object = objects.get(getAdapterPosition());
                listener.onMenuAction(object, item);
            }
            return false;
        }
    }

    public CustomAdapter(ArrayList<ListDiarysQuery.Item> objects, OnItemSelectedListener listener, boolean withContextMenu) {
        this.listener = listener;
        this.objects = objects;
        this.withContextMenu = withContextMenu;
    }

    public interface OnItemSelectedListener {

        void onSelected(ListDiarysQuery.Item object);

        void onMenuAction(ListDiarysQuery.Item object, MenuItem item);

    }

    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_diary_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CustomAdapter.ViewHolder holder, int position) {
        ListDiarysQuery.Item object = objects.get(position);
        holder.txt_name.setText(object.title());
        holder.txt_description.setText(object.desc());
    }

    @Override
    public int getItemCount() {
         return  objects == null ? 0 : objects.size();
    }
}