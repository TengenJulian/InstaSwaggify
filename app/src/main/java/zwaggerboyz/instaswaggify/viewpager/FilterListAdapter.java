package zwaggerboyz.instaswaggify.viewpager;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import zwaggerboyz.instaswaggify.DataContainer;
import zwaggerboyz.instaswaggify.HistoryBuffer;
import zwaggerboyz.instaswaggify.R;
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

public class FilterListAdapter extends ListAdapter<AbstractFilterClass> {
    private class ViewHolder {
        public TextView titleTextView, label1TextView, label2TextView, label3TextView;
        public SeekBar slider1Seekbar, slider2Seekbar, slider3Seekbar;
    }

    public FilterListAdapter(Activity activity, ListChangeListener listener, List<AbstractFilterClass> items, HistoryBuffer historyBuffer) {
        super(activity, listener, items, historyBuffer);
        dataType = DataContainer.DataType.FILTER_DATA;
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
                viewHolder.slider1Seekbar.setProgress((int)item.getValue(0));

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
                viewHolder.slider1Seekbar.setProgress((int)item.getValue(0));
                viewHolder.slider2Seekbar.setProgress((int)item.getValue(1));
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
                viewHolder.slider1Seekbar.setProgress((int)item.getValue(0));
                viewHolder.slider2Seekbar.setProgress((int)item.getValue(1));
                viewHolder.slider3Seekbar.setProgress((int)item.getValue(2));
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
                    //mHistoryBuffer.updateBuffer(mItems, null);
                    mHistoryBuffer.recordValueChange(new DataContainer(item.getArray(), dataType), position);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    //item.setValue(0, seekBar.getProgress());
                    mListener.updateImage(mItems);
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
                    //mHistoryBuffer.updateBuffer(mItems, null);
                    mHistoryBuffer.recordValueChange(new DataContainer(item.getArray(), dataType), position);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    //item.setValue(1, seekBar.getProgress());
                    mListener.updateImage(mItems);
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
                public void onStartTrackingTouch(SeekBar seekBar) {
                    mHistoryBuffer.recordValueChange(new DataContainer(item.getArray(), dataType), position);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    //item.setValue(2, seekBar.getProgress());
                    mListener.updateImage(mItems);
                }
            });
        }

        return convertView;
    }

    @Override
    public void changeValues(AbstractFilterClass filter, float[] values) {
        filter.setArray(values);
        mListener.listNotEmpty();
        updateList();
    }

    public interface FilterListInterface {
        public void updateImage(List<AbstractFilterClass> filters);
        public void updateImage(List<AbstractFilterClass> filters, boolean forceUpdate);
        public void filtersEmpty();
        public void filtersNotEmpty();
    }
}


