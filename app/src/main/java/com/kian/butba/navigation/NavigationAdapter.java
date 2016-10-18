package com.kian.butba.navigation;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kian.butba.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by Kian Mistry on 18/10/16.
 */

public class NavigationAdapter extends Adapter<ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private LayoutInflater inflater;

    private Context context;
    private List<NavigationItem> navigationItems = Collections.emptyList();

    public NavigationAdapter(Context context, List<NavigationItem> navigationItems) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.navigationItems = navigationItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER) {
            View view = inflater.inflate(R.layout.navigation_drawer_header, parent, false);
            return new HeaderHolder(view);
        }
        else {
            View view = inflater.inflate(R.layout.navigation_item, parent, false);
            return new ItemHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(holder instanceof HeaderHolder) {

        }
        else if(holder instanceof ItemHolder) {
            ItemHolder itemHolder = (ItemHolder) holder;
            NavigationItem current = navigationItems.get(position - 1);

            itemHolder.getMenuIcon().setImageResource(current.getIconId());
            itemHolder.getMenuTitle().setText(current.getItemTitle());
        }
    }

    @Override
    public int getItemCount() {
        return navigationItems.size() + 1;
    }


    private class HeaderHolder extends ViewHolder {
        public HeaderHolder(View view) {
            super(view);
        }
    }

    private class ItemHolder extends ViewHolder {
        private ImageView menuIcon;
        private TextView menuTitle;

        public ItemHolder(View view) {
            super(view);

            menuIcon = (ImageView) view.findViewById(R.id.menu_icon);
            menuTitle = (TextView) view.findViewById(R.id.menu_title);
        }

        public ImageView getMenuIcon() {
            return menuIcon;
        }

        public TextView getMenuTitle() {
            return menuTitle;
        }
    }
}
