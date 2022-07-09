package com.example.thoughtscloset;

import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AddItemDialogListener{

    private RecyclerView mainList;
    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<RecyclerViewItem> items = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainList = findViewById(R.id.mainList);
        mainList.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        mainList.setLayoutManager(layoutManager);

        recyclerAdapter = new MainListAdapter(items, this);
        mainList.setAdapter(recyclerAdapter);
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

            showAddItemDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void showAddItemDialog()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddItemDialog newFragment = new AddItemDialog();
        newFragment.show(fragmentManager, "dialog");
    }

    @Override
    public void onFinishAddItemDialog(RecyclerViewItem item) {
        items.add(item);
    }
}
