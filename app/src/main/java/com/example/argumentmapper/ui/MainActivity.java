package com.example.argumentmapper.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.argumentmapper.APIService;
import com.example.argumentmapper.ArgumentMap;
import com.example.argumentmapper.ArgumentMapperApplication;
import com.example.argumentmapper.FileManager;
import com.example.argumentmapper.InductiveNode;
import com.example.argumentmapper.R;
import com.example.argumentmapper.exceptions.AuthException;
import com.example.argumentmapper.exceptions.ConnectionException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AddArgumentMapDialogListener, ShareMapDialogProvider, ConnectMapDialogListener {

    @Inject
    APIService apiService;
    @Inject
    FileManager fileManager;
    @Inject Gson gson;
    private ListView mainList;
    private ArrayAdapter<ArgumentMap> listAdapter;
    private final List<ArgumentMap> items = new ArrayList<>();
    private ArgumentMap editingMap;
    private static final String TAG = MainActivity.class.getName();
    private String currentCode;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((ArgumentMapperApplication)getApplication()).getApplicationComponent().inject(this);
        mainList = findViewById(R.id.mainList);
        sharedPreferences = getSharedPreferences("default", Context.MODE_PRIVATE);;
        registerForContextMenu(mainList);

        listAdapter = new MainListAdapter(this, items);
        mainList.setAdapter(listAdapter);

        mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArgumentMap selected = ((ArgumentMap)mainList.getItemAtPosition(position));
                openArgumentMap(selected);
            }
        });

        listAdapter.addAll(fileManager.loadArgumentMaps());
        listAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            ArgumentMap map = data.getParcelableExtra("map");
            editingMap.setRoot(map.getRoot());
        }
        else if(resultCode == RESULT_FIRST_USER) {
            editingMap.removeSessionID();
        }
        listAdapter.notifyDataSetChanged();
        fileManager.saveMapToFile(editingMap);
        editingMap = null;
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
        }else if(id == R.id.connect_button)
        {
            showConnectArgumentMapDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Choose action");
        ArgumentMap map = listAdapter.getItem(((AdapterView.AdapterContextMenuInfo)menuInfo).position);
        if(map.getSessionID() != null)
        {
            getMenuInflater().inflate(R.menu.map_shared_context_menu, menu);
            Integer ownerID = map.getOwnerID();
            String userIDStr = sharedPreferences.getString("user_ID", null);
            if(userIDStr == null) return;
            int userID;
            try{
                userID = Integer.parseInt(userIDStr);
            }catch (NumberFormatException e){ return; }
            if(ownerID != null && ownerID.equals(userID))
            {
                menu.findItem(R.id.stopSharing).setVisible(true);
            }
        }else
        {
            getMenuInflater().inflate(R.menu.map_context_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int pos = info.position;
        ArgumentMap map = listAdapter.getItem(pos);
        int itemID = item.getItemId();
        if(itemID == R.id.delete) {
            removeArgumentMap(pos);
            return true;
        }else if(itemID == R.id.share) {
            String userIDStr = sharedPreferences.getString("user_ID", null);
            apiService.createSession(gson.toJson(map.getRoot(), InductiveNode.class)).enqueue(new retrofit2.Callback<ResponseBody>()
            {
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if(t instanceof AuthException)
                    {
                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        ArgumentMapperApplication app = (ArgumentMapperApplication) getApplication();
                        app.redirectToLogin();
                    }
                    else if(t instanceof ConnectionException)
                    {
                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful())
                    {
                        try {
                            String responseString = response.body().string();
                            int sessionID = JsonParser.parseString(responseString).getAsJsonObject().get("sessionID").getAsInt();
                            map.setSessionID(sessionID);
                            if(userIDStr != null)
                            {
                                map.setOwnerID(Integer.parseInt(userIDStr));
                            }
                            fileManager.saveMapToFile(map);
                            currentCode = String.valueOf(sessionID);
                            listAdapter.notifyDataSetChanged();
                            new ShareMapDialogFragment().show(getSupportFragmentManager(), ShareMapDialogFragment.TAG);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else {
                        try {
                            String jsonResponse = response.errorBody().string();
                            String message = JsonParser.parseString(jsonResponse).getAsJsonObject().get("message").getAsString();
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            return true;
        }else if(itemID == R.id.stopSharing) {
            Integer mapSessionID = map.getSessionID();
            if(mapSessionID == null)
            {
                return true;
            }
            apiService.deleteSession(mapSessionID).enqueue(new retrofit2.Callback<ResponseBody>()
            {
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if(t instanceof AuthException)
                    {
                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        ArgumentMapperApplication app = (ArgumentMapperApplication) getApplication();
                        app.redirectToLogin();
                    }
                    else if(t instanceof ConnectionException)
                    {
                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful())
                    {
                        map.removeSessionID();
                        listAdapter.notifyDataSetChanged();
                        fileManager.saveMapToFile(map);
                    }else{
                        try {
                            Log.v(TAG, response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            return true;
        }else if(itemID == R.id.showCode) {
            currentCode = String.valueOf(map.getSessionID());
            new ShareMapDialogFragment().show(getSupportFragmentManager(), ShareMapDialogFragment.TAG);
            return true;
        }else if(itemID == R.id.disconnect) {
            map.removeSessionID();
            listAdapter.notifyDataSetChanged();
            fileManager.saveMapToFile(map);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void showAddArgumentMapDialog()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddArgumentMapDialog newFragment = new AddArgumentMapDialog();
        newFragment.show(fragmentManager, "dialog");
    }

    private void showConnectArgumentMapDialog()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ConnectMapDialog newFragment = new ConnectMapDialog();
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
    public void onFinishConnectMapDialogListener(int code) {
        apiService.getSessionMap(code).enqueue(new retrofit2.Callback<ResponseBody>()
        {
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if(t instanceof AuthException)
                {
                    Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    ((ArgumentMapperApplication)getApplication()).redirectToLogin();
                }
                else if(t instanceof ConnectionException)
                {
                    Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful())
                {
                    try {
                        JsonObject jsonResponse = JsonParser.parseString(response.body().string()).getAsJsonObject();
                        InductiveNode inductiveNode = gson.fromJson(jsonResponse.get("map_tree").getAsString(), InductiveNode.class);
                        ArgumentMap map = new ArgumentMap(inductiveNode);
                        map.setSessionID(code);
                        map.setOwnerID(jsonResponse.get("owner_id").getAsInt());
                        items.add(map);
                        listAdapter.notifyDataSetChanged();
                        fileManager.saveMapToFile(map);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    if(response.code() == 404)
                    {
                        Toast.makeText(MainActivity.this, "Cannot find the specified session", Toast.LENGTH_LONG).show();
                    }
                    try {
                        Log.v(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onFinishAddArgumentMapDialog(ArgumentMap item) {
        items.add(item);
        listAdapter.notifyDataSetChanged();
        fileManager.saveMapToFile(item);
    }

    private void removeArgumentMap(int position)
    {
        ArgumentMap map = listAdapter.getItem(position);
        items.remove(position);
        listAdapter.notifyDataSetChanged();
        fileManager.deleteArgumentMap(map);
    }

    @Override
    public String getCode() {
        return currentCode;
    }

    @Override
    public void forgetCode() {
        currentCode = null;
    }
}
