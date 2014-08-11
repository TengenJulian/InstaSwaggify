package zwaggerboyz.instaswaggify;

import android.util.Log;

import java.util.Stack;

import zwaggerboyz.instaswaggify.filters.AbstractFilterClass;
import zwaggerboyz.instaswaggify.viewpager.FilterListAdapter;
import zwaggerboyz.instaswaggify.viewpager.OverlayListAdapter;

/**
 * Created by scoud on 25-6-14.
 */
public class HistoryBuffer {

    private UndoInterface mListener;
    private CircularStack<ActionState> mStack;

    private static final int QUEUE_SIZE = 100;

    private enum ActionType {
        ADD,
        REMOVE,
        REORDER,
        CLEAR,
        SET,
        VALUE_CHANGE
    }

    public HistoryBuffer(UndoInterface listener) {
        mListener = listener;
        mStack = new CircularStack<ActionState>(QUEUE_SIZE);
    }

    public void undo(FilterListAdapter filterListAdapter, OverlayListAdapter overlayListAdapter) {
        ActionState state = mStack.pop();
        Log.i("undoing action:", state.actionType + "");

        if (state.dataType == DataContainer.DataType.FILTER_DATA) {
            filterListAdapter.enableHistory(false);

            switch (state.actionType) {
                case ADD: {
                    filterListAdapter.remove(filterListAdapter.getCount() - 1);
                    break;
                }

                case REMOVE: {
                    AbstractFilterClass filter = state.data.filter;
                    filterListAdapter.insertItem(filter, state.from);
                    break;
                }

                case REORDER: {
                    filterListAdapter.reorder(state.to, state.from);
                    break;
                }

                case CLEAR: {
                    filterListAdapter.setItems(state.data.filters);
                    break;
                }
                case SET: {
                    filterListAdapter.clearFilters();
                    break;
                }
                case VALUE_CHANGE: {
                    filterListAdapter.changeValue(state.from, state.data.intValues);
                    break;
                }
            }

            filterListAdapter.enableHistory(true);
        }
        else {
            overlayListAdapter.enableHistory(false);

            switch (state.actionType) {
                case ADD: {
                    overlayListAdapter.remove(overlayListAdapter.getCount() - 1);
                    break;
                }

                case REMOVE: {
                    Overlay overlay = state.data.overlay;
                    overlayListAdapter.insertItem(overlay, state.from);
                    break;
                }

                case REORDER: {
                    overlayListAdapter.reorder(state.to, state.from);
                    break;
                }

                case CLEAR: {
                    overlayListAdapter.setItems(state.data.overlays);
                    break;
                }

                case SET: {
                    overlayListAdapter.clearOverlays();
                    break;
                }
                case VALUE_CHANGE: {
                    overlayListAdapter.changeValue(state.from, state.data.floatValues);
                    break;
                }
            }

            overlayListAdapter.enableHistory(true);
        }

        if (mStack.size() == 0) {
            mListener.setUndoState(false);
        }
    }

    public void recordRemove(DataContainer data, int index) {
        ActionState state = new ActionState();
        state.actionType = ActionType.REMOVE;
        state.from = index;
        state.data = data;
        state.dataType = data.dataType;

        mStack.push(state);
        mListener.setUndoState(true);
    }

    public void recordAdd(DataContainer.DataType dataType) {
        ActionState state = new ActionState();
        state.actionType = ActionType.ADD;
        state.dataType = dataType;

        mStack.push(state);
        mListener.setUndoState(true);
    }

    public void recordReorder(int from, int to, DataContainer.DataType dataType) {
        ActionState state = new ActionState();
        state.actionType = ActionType.REORDER;
        state.dataType = dataType;
        state.from = from;
        state.to = to;

        mStack.push(state);
        mListener.setUndoState(true);
    }

    public void recordClear(DataContainer dataList) {
        ActionState state = new ActionState();
        state.actionType = ActionType.CLEAR;
        state.data = dataList;
        state.dataType = dataList.dataType;

        mStack.push(state);
        mListener.setUndoState(true);
    }

    public void recordSet(DataContainer dataList) {
        ActionState state = new ActionState();
        state.actionType = ActionType.SET;
        state.data = dataList;
        state.dataType = dataList.dataType;

        mStack.push(state);
        mListener.setUndoState(true);
    }

    public void recordValueChange(DataContainer data, int index) {
        ActionState state = new ActionState();
        state.actionType = ActionType.VALUE_CHANGE;
        state.from = index;
        state.data = data;
        state.dataType = data.dataType;

        mStack.push(state);
        mListener.setUndoState(true);
    }

    private class ActionState {
        ActionType actionType;
        DataContainer data;
        DataContainer.DataType dataType;

        int from, to;

        ActionState(){};
    }

    public interface UndoInterface {
        public void setUndoState(boolean state);
    }

    private class CircularStack<E> {
        Stack<E> stack;

        public CircularStack(int size) {
            stack = new Stack<E>();
        }

        public E pop() {
            return stack.pop();
        }

        public void push(E element) {
            stack.push(element);
        }

        public int size() {
            return stack.size();
        }
    }
}
