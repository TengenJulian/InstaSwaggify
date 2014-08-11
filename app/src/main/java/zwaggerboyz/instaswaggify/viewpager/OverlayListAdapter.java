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
import zwaggerboyz.instaswaggify.filters.AbstractFilterClass;

/*
 * APP:     InstaSwaggify
 * DATE:    June 2014
 * NAMES:   Mathijs Molenaar, Tristan van Vaalen, David Veenstra, Peter Verkade, Matthijs de Wit,
 *          Arne Zismer
 *
 * FILE:    OverlayListAdapter.java
 * This file contains the adapter to change the list of currently selected overlays.
 */

public class OverlayListAdapter extends BaseAdapter implements MyGLSurfaceView.onOverlayChangeListener {
    private LayoutInflater mInflater;
    private MyGLSurfaceView mGLSurfaceView;
    private OverlayListInterface mListener;
    private List<Overlay> mItems;
    private HistoryBuffer mHistoryBuffer;
    private boolean historyEnabled = true;

    private class ViewHolder {
        TextView titleTextView;
    }

    public OverlayListAdapter(Activity activity, OverlayListInterface listener, MyGLSurfaceView surfaceView, List<Overlay> items, HistoryBuffer historyBuffer) {
        mInflater = activity.getLayoutInflater();
        mListener = listener;
        mGLSurfaceView = surfaceView;
        mItems = items;
        mHistoryBuffer = historyBuffer;
        mGLSurfaceView.setOnOverlayChangeListener(this);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Overlay getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds(){
        return true;
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

    public List<Overlay> getItems() {
        return mItems;
    }

    /* Removes item at index from filter list */
    public void remove(int index) {
        Overlay overlay =  mItems.remove(index);
        if (historyEnabled){
            mHistoryBuffer.recordRemove(new DataContainer(overlay), index);
        }

        mGLSurfaceView.requestRender();
        if (mItems.size() == 0)
            mListener.overlaysEmpty();
        notifyDataSetChanged();
    }

    public void reorder(int from, int to) {
        if (from != to) {
            if (historyEnabled) {
                mHistoryBuffer.recordReorder(from, to, DataContainer.DataType.OVERLAY_DATA);
            }
            Overlay element = mItems.remove(from);
            mItems.add(to, element);

            mGLSurfaceView.requestRender();
            notifyDataSetChanged();
        }
    }

    public void addItem(Overlay overlay) {
        if (historyEnabled) {
            mHistoryBuffer.recordAdd(DataContainer.DataType.OVERLAY_DATA);
        }
        mItems.add(0, overlay);
        mGLSurfaceView.addToCompileQueue(overlay);
        mGLSurfaceView.requestRender();
        mListener.overlaysNotEmpty();
        notifyDataSetChanged();
    }

    public void insertItem(Overlay overlay, int index) {
        mItems.add(index, overlay);
        mListener.overlaysNotEmpty();
        mGLSurfaceView.requestRender();
        notifyDataSetChanged();
    }

    public void changeValue(int index, float[] values) {
        Overlay overlay = mItems.get(index);
        overlay.setValues(values);
        mListener.overlaysNotEmpty();
        mGLSurfaceView.requestRender();
    }

    public void clearOverlays() {
        if (historyEnabled) {
            mHistoryBuffer.recordClear(new DataContainer(new ArrayList(mItems), DataContainer.DataType.OVERLAY_DATA));
        }

        mItems.clear();
        mGLSurfaceView.requestRender();
        mListener.overlaysEmpty();
        notifyDataSetChanged();
    }

    public void setItems(List<Overlay> items) {
        if (historyEnabled) {
            mHistoryBuffer.recordSet(new DataContainer(
                    new ArrayList(mItems),
                    DataContainer.DataType.FILTER_DATA
            ));
        }

        mItems.clear();
        mItems.addAll(items);

        if (mItems.size() == 0) {
            mListener.overlaysEmpty();
        }
        else {
            mListener.overlaysNotEmpty();
        }

        mGLSurfaceView.requestRender();
        notifyDataSetChanged();
    }

    @Override
    public void updateBuffer() {
    }

    public void requestRender() {
        mGLSurfaceView.requestRender();

    }

    public void enableHistory (Boolean bool) {
        historyEnabled = bool;
    }

    public interface OverlayListInterface {
        public void overlaysEmpty();
        public void overlaysNotEmpty();
    }
}
