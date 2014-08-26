package zwaggerboyz.instaswaggify.viewpager;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import zwaggerboyz.instaswaggify.DataContainer;
import zwaggerboyz.instaswaggify.HistoryBuffer;
import zwaggerboyz.instaswaggify.filters.AbstractFilterClass;

/**
 * Created by zeta on 8/11/14.
 */
public abstract class ListAdapter <E> extends BaseAdapter{
    protected LayoutInflater mInflater;
    protected List<E> mItems;
    protected ListChangeListener mListener;
    protected HistoryBuffer mHistoryBuffer;
    protected boolean historyEnabled = true;

    protected DataContainer.DataType dataType;

    public ListAdapter(Activity activity, ListChangeListener listener, List<E> items, HistoryBuffer historyBuffer) {
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
    public E getItem(int position) {
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

    public List<E> getItems() {
        return mItems;
    }

    public void setItems(List<E> items) {
        if (historyEnabled) {
            mHistoryBuffer.recordSet(new DataContainer(new ArrayList(mItems), dataType));
        }

        mItems = items;
        notifyDataSetChanged();
        mListener.updateImage(mItems);

        if (mItems.size() == 0)
            mListener.listEmpty();
        else
            mListener.listNotEmpty();
    }

    /* Removes item at index from filter list */
    public void remove(int index) {
        E element = mItems.remove(index);

        if (historyEnabled) {
            mHistoryBuffer.recordRemove(new DataContainer(element, dataType), index);
        }

        mListener.updateImage(mItems);
        if (mItems.size() == 0)
            mListener.listEmpty();
        notifyDataSetChanged();
    }

    public void reorder(int from, int to) {
        if (from != to) {
            if (historyEnabled) {
                mHistoryBuffer.recordReorder(from, to, dataType);
            }

            E element = mItems.remove(from);
            mItems.add(to, element);

            updateList();
        }
    }

    public void addItem(E element) {
        if (historyEnabled) {
            mHistoryBuffer.recordAdd(dataType);
        }

        mItems.add(element);
        mListener.prepareElement(element);
        mListener.listNotEmpty();
        updateList();
    }

    public void insertItem(E element, int index) {
        mItems.add(index, element);
        mListener.listNotEmpty();
        updateList();
    }

    public void changeValue(int index, float[] values) {
        E element = mItems.get(index);

        changeValues(element, values);
        mListener.listNotEmpty();
        updateList();
    }

    public void clear() {
        if (historyEnabled) {
            mHistoryBuffer.recordClear(new DataContainer(new ArrayList(mItems), dataType));
        }

        mItems.clear();
        mListener.updateImage(mItems);
        mListener.listEmpty();
        updateList();
    }

    public void updateList() {
        notifyDataSetChanged();
        mListener.updateImage(mItems);
    }

    abstract public void changeValues(E element, float[] values);

    public void enableHistory (Boolean bool) {
        historyEnabled = bool;
    }

    static abstract public class ListChangeListener {
        abstract public void prepareElement(Object element);
        abstract public void updateImage(List elements);
        abstract public void listEmpty();
        abstract public void listNotEmpty();
    }

}
