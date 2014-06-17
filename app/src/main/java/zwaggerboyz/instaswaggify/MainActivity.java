package zwaggerboyz.instaswaggify;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mobeta.android.dslv.DragSortListView;

import java.util.ArrayList;

public class MainActivity extends Activity {
    DragSortListView mListView;
    FilterListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ArrayList<IFilter> items = new ArrayList<IFilter>();
        for (int i = 0; i < 5; i++) {
            items.add(new SaturationFilter());
        }

        adapter = new FilterListAdapter(this, items);
        mListView = (DragSortListView)findViewById(R.id.activity_main_listview);
        mListView.setAdapter(adapter);

        mListView.setRemoveListener(new DragSortListView.RemoveListener() {
            @Override
            public void remove(int item) {
                adapter.remove(item);
                adapter.notifyDataSetChanged();
            }
        });

        mListView.setDropListener(new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {

                Log.v("Main", "DROPPED FROM: " + from + ". TO: " + to);
                adapter.reorder(from, to);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.add_filter) {
            adapter.add();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
