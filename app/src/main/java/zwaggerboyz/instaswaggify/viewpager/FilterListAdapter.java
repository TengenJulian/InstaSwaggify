package zwaggerboyz.instaswaggify.viewpager;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

import zwaggerboyz.instaswaggify.HistoryBuffer;
import zwaggerboyz.instaswaggify.R;
import zwaggerboyz.instaswaggify.TexturedSquare;
import zwaggerboyz.instaswaggify.filters.AbstractFilterClass;

/*
 * APP:     InstaSwaggify
 * DATE:    June 2014
 * NAMES:   Mathijs Molenaar, Tristan van Vaalen, David Veenstra, Peter Verkade, Matthijs de Wit,
 *          Arne Zismer
 *
 * FILE:    FilterListAdapter.java
 * This file contains the adapter to change the list of currently selected filters.
 */

public class FilterListAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;
    private List<AbstractFilterClass> mItems;
    private FilterListInterface mListener;
    private HistoryBuffer mHistoryBuffer;

    private class ViewHolder {
        public TextView titleTextView, label1TextView, label2TextView, label3TextView;
        public SeekBar slider1Seekbar, slider2Seekbar, slider3Seekbar;
    }

    public FilterListAdapter(Activity activity, FilterListInterface listener, List<AbstractFilterClass> items, HistoryBuffer historyBuffer) {
        mInflater = activity.getLayoutInflater();
        mItems = items;
        mListener = listener;
        mHistoryBuffer = historyBuffer;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public AbstractFilterClass getItem(int position) {
        return mItems.get(position);
    }

    public void setItems(List<AbstractFilterClass> items) {
        setItems(items, true);
    }

    public void setItems(List<AbstractFilterClass> items, boolean isUndoable) {
        if (isUndoable)
            mHistoryBuffer.updateBuffer(mItems, null);

        mItems = items;
        notifyDataSetChanged();
        mListener.updateImage(mItems);
        if (mItems.size() > 0)
            mListener.filtersNotEmpty();
        else
            mListener.filtersEmpty();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_filter, null);

            viewHolder= new ViewHolder();
            viewHolder.titleTextView = (TextView)convertView.findViewById(R.id.list_item_filter_title);
            viewHolder.label1TextView = (TextView)convertView.findViewById(R.id.list_item_filter_label1);
            viewHolder.label2TextView = (TextView)convertView.findViewById(R.id.list_item_filter_label2);
            viewHolder.label3TextView = (TextView)convertView.findViewById(R.id.list_item_filter_label3);
            viewHolder.slider1Seekbar = (SeekBar)convertView.findViewById(R.id.list_item_filter_seekbar1);
            viewHolder.slider2Seekbar = (SeekBar)convertView.findViewById(R.id.list_item_filter_seekbar2);
            viewHolder.slider3Seekbar = (SeekBar)convertView.findViewById(R.id.list_item_filter_seekbar3);

            viewHolder.slider1Seekbar.setMax(100);
            viewHolder.slider2Seekbar.setMax(100);
            viewHolder.slider3Seekbar.setMax(100);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        final AbstractFilterClass item = getItem(position);
        viewHolder.titleTextView.setText(item.getName());

        /**
         * Toggles visibility of viewHolder elements
         */
        switch (item.getNumValues()) {
            case 0:
                viewHolder.label1TextView.setVisibility(View.GONE);
                viewHolder.label2TextView.setVisibility(View.GONE);
                viewHolder.label3TextView.setVisibility(View.GONE);
                viewHolder.slider1Seekbar.setVisibility(View.GONE);
                viewHolder.slider2Seekbar.setVisibility(View.GONE);
                viewHolder.slider3Seekbar.setVisibility(View.GONE);
                break;
            case 1:
                viewHolder.label1TextView.setVisibility(View.VISIBLE);
                viewHolder.slider1Seekbar.setVisibility(View.VISIBLE);
                viewHolder.label1TextView.setText(item.getLabel(0));
                viewHolder.slider1Seekbar.setProgress(item.getValue(0));

                viewHolder.label2TextView.setVisibility(View.GONE);
                viewHolder.label3TextView.setVisibility(View.GONE);
                viewHolder.slider2Seekbar.setVisibility(View.GONE);
                viewHolder.slider3Seekbar.setVisibility(View.GONE);
                break;
            case 2:
                viewHolder.label1TextView.setVisibility(View.VISIBLE);
                viewHolder.label2TextView.setVisibility(View.VISIBLE);
                viewHolder.label3TextView.setVisibility(View.GONE);
                viewHolder.slider1Seekbar.setVisibility(View.VISIBLE);
                viewHolder.slider2Seekbar.setVisibility(View.VISIBLE);
                viewHolder.slider3Seekbar.setVisibility(View.GONE);

                viewHolder.label1TextView.setText(item.getLabel(0));
                viewHolder.label2TextView.setText(item.getLabel(1));
                viewHolder.slider1Seekbar.setProgress(item.getValue(0));
                viewHolder.slider2Seekbar.setProgress(item.getValue(1));
                break;
            case 3:
                viewHolder.label1TextView.setVisibility(View.VISIBLE);
                viewHolder.label2TextView.setVisibility(View.VISIBLE);
                viewHolder.label3TextView.setVisibility(View.VISIBLE);
                viewHolder.slider1Seekbar.setVisibility(View.VISIBLE);
                viewHolder.slider2Seekbar.setVisibility(View.VISIBLE);
                viewHolder.slider3Seekbar.setVisibility(View.VISIBLE);

                viewHolder.label1TextView.setText(item.getLabel(0));
                viewHolder.label2TextView.setText(item.getLabel(1));
                viewHolder.label3TextView.setText(item.getLabel(2));
                viewHolder.slider1Seekbar.setProgress(item.getValue(0));
                viewHolder.slider2Seekbar.setProgress(item.getValue(1));
                viewHolder.slider3Seekbar.setProgress(item.getValue(2));
                break;
        }

        /**
         * Adds listeners to seek bars. Updates seek bar value on stop track
         */
        if (item.getNumValues() > 0) {
            viewHolder.slider1Seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        item.setValue(0, progress);
                        mListener.updateImage(mItems);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    mHistoryBuffer.updateBuffer(mItems, null);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    //item.setValue(0, seekBar.getProgress());
                    mListener.updateImage(mItems, true);
                }
            });
        }

        if (item.getNumValues() > 1) {
            viewHolder.slider2Seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        item.setValue(1, progress);
                        mListener.updateImage(mItems);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    mHistoryBuffer.updateBuffer(mItems, null);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    //item.setValue(1, seekBar.getProgress());
                    mListener.updateImage(mItems, true);
                }
            });
        }

        if (item.getNumValues() > 2) {
            viewHolder.slider3Seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        item.setValue(2, progress);
                        mListener.updateImage(mItems);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    //item.setValue(2, seekBar.getProgress());
                    mListener.updateImage(mItems, true);
                }
            });
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds(){
        return true;
    }

    public List<AbstractFilterClass> getItems() {
        return mItems;
    }

    /* Removes item at index from filter list */
    public void remove(int index) {
        mHistoryBuffer.updateBuffer(mItems, null);

        mItems.remove(mItems.get(index));

        mListener.updateImage(mItems);
        if (mItems.size() == 0)
            mListener.filtersEmpty();
        notifyDataSetChanged();
    }

    public void reorder(int from, int to) {
        if (from != to) {
            mHistoryBuffer.updateBuffer(mItems, null);

            AbstractFilterClass element = mItems.remove(from);
            mItems.add(to, element);

            notifyDataSetChanged();
            mListener.updateImage(mItems);
        }
    }

    public void addItem(AbstractFilterClass filter) {
        mHistoryBuffer.updateBuffer(mItems, null);
        mItems.add(filter);
        mListener.filtersNotEmpty();
        updateList();
    }

    public void updateList() {
        notifyDataSetChanged();
        mListener.updateImage(mItems);
    }

    public void clearFilters() {
        mHistoryBuffer.updateBuffer(mItems, null);
        mItems.clear();
        notifyDataSetChanged();
        mListener.updateImage(mItems);
        mListener.filtersEmpty();
    }

    public interface FilterListInterface {
        public void updateImage(List<AbstractFilterClass> filters);
        public void updateImage(List<AbstractFilterClass> filters, boolean forceUpdate);
        public void filtersEmpty();
        public void filtersNotEmpty();
        public void addFilterToCompileQueue(TexturedSquare filter);
    }
}


