package zwaggerboyz.instaswaggify.viewpager;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import zwaggerboyz.instaswaggify.DataContainer;
import zwaggerboyz.instaswaggify.HistoryBuffer;
import zwaggerboyz.instaswaggify.MyGLSurfaceView;
import zwaggerboyz.instaswaggify.Overlay;
import zwaggerboyz.instaswaggify.R;

/*
 * APP:     InstaSwaggify
 * DATE:    June 2014
 * NAMES:   Mathijs Molenaar, Tristan van Vaalen, David Veenstra, Peter Verkade, Matthijs de Wit,
 *          Arne Zismer
 *
 * FILE:    OverlayListAdapter.java
 * This file contains the adapter to change the list of currently selected overlays.
 */

public class OverlayListAdapter extends ListAdapter<Overlay> {

    private class ViewHolder {
        TextView titleTextView;
    }

    public OverlayListAdapter(Activity activity, ListChangeListener listener, List<Overlay> items, HistoryBuffer historyBuffer) {
        super(activity, listener, items, historyBuffer);
        dataType = DataContainer.DataType.OVERLAY_DATA;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_overlay, null);
            viewHolder = new ViewHolder();
            viewHolder.titleTextView = (TextView)convertView.findViewById(R.id.list_item_overlay_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.titleTextView.setText(getItem(position).getName());

        return convertView;
    }

    @Override
    public void changeValues(Overlay overlay, float[] values) {
        overlay.setValues(values);
        updateList();
    }

    public interface OverlayListInterface {
        public void overlaysEmpty();
        public void overlaysNotEmpty();
    }
}
