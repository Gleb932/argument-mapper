package com.example.thoughtscloset;

import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AddArgumentMapDialogListener {

    private ListView mainList;
    private ArrayAdapter<ArgumentMap> listAdapter;
    private final List<ArgumentMap> items = new ArrayList<>();
    private ArgumentMap editingMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainList = findViewById(R.id.mainList);

        listAdapter = new MainListAdapter(this, items);
        mainList.setAdapter(listAdapter);

        mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArgumentMap selected = ((ArgumentMap)mainList.getItemAtPosition(position));
                openArgumentMap(selected);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_button) {
            showAddArgumentMapDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddArgumentMapDialog()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddArgumentMapDialog newFragment = new AddArgumentMapDialog();
        newFragment.show(fragmentManager, "dialog");
    }

    private void openArgumentMap(ArgumentMap map)
    {
        Intent intent = new Intent(this, ArgumentMapActivity.class);
        intent.putExtra("rootNode", map.getRoot());
        editingMap = map;
        this.startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        editingMap.setRoot((InductiveNode) data.getParcelableExtra("rootNode"));
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFinishAddArgumentMapDialog(ArgumentMap item) {
        items.add(item);
    }
}
