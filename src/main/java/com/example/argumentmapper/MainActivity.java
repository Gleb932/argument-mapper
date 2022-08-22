package com.example.argumentmapper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.argumentmapper.exceptions.AuthException;
import com.example.argumentmapper.exceptions.ConnectionException;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AddArgumentMapDialogListener {

    @Inject APIService apiService;
    private ListView mainList;
    private ArrayAdapter<ArgumentMap> listAdapter;
    private final List<ArgumentMap> items = new ArrayList<>();
    private ArgumentMap editingMap;
    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((ArgumentMapperApplication)getApplication()).getApplicationComponent().inject(this);
        mainList = findViewById(R.id.mainList);
        registerForContextMenu(mainList);

        listAdapter = new MainListAdapter(this, items);
        mainList.setAdapter(listAdapter);

        mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArgumentMap selected = ((ArgumentMap)mainList.getItemAtPosition(position));
                openArgumentMap(selected);
            }
        });

        FileManager.setContext(getApplicationContext());
        listAdapter.addAll(FileManager.loadArgumentMaps());
        listAdapter.notifyDataSetChanged();
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Choose action");
        getMenuInflater().inflate(R.menu.map_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int pos = info.position;
        switch (item.getItemId()) {
            case R.id.delete:
                removeArgumentMap(pos);
                return true;
            case R.id.share:
                apiService.authTest().enqueue(new retrofit2.Callback<ResponseBody>()
                {
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        if(t instanceof ConnectionException || t instanceof AuthException)
                        {
                            Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.v(TAG, response.isSuccessful() ? "1":"0");
                    }
                });
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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
        intent.putExtra("map", map);
        editingMap = map;
        this.startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArgumentMap newMap = data.getParcelableExtra("map");
        editingMap.setRoot(newMap.getRoot());
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFinishAddArgumentMapDialog(ArgumentMap item) {
        items.add(item);
        listAdapter.notifyDataSetChanged();
        FileManager.saveMapToFile(item);
    }

    private void removeArgumentMap(int position)
    {
        ArgumentMap map = listAdapter.getItem(position);
        items.remove(position);
        listAdapter.notifyDataSetChanged();
        FileManager.deleteArgumentMap(map);
    }
}
