package com.example.argumentmapper.ui;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.argumentmapper.APIService;
import com.example.argumentmapper.ArgumentMap;
import com.example.argumentmapper.ArgumentMapEditor;
import com.example.argumentmapper.ArgumentMapperApplication;
import com.example.argumentmapper.FileManager;
import com.example.argumentmapper.InductiveNode;
import com.example.argumentmapper.MapNode;
import com.example.argumentmapper.OfflineArgumentMapEditor;
import com.example.argumentmapper.OnlineArgumentMapEditor;
import com.example.argumentmapper.R;
import com.example.argumentmapper.TokenExpirationHandler;
import com.example.argumentmapper.exceptions.AuthException;
import com.example.argumentmapper.exceptions.ConnectionException;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dev.bandb.graphview.AbstractGraphAdapter;
import dev.bandb.graphview.graph.Graph;
import dev.bandb.graphview.graph.Node;
import dev.bandb.graphview.layouts.tree.BuchheimWalkerConfiguration;
import dev.bandb.graphview.layouts.tree.BuchheimWalkerLayoutManager;
import dev.bandb.graphview.layouts.tree.TreeEdgeDecoration;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ArgumentMapActivity extends AppCompatActivity implements AddNodeDialogListener, TokenExpirationHandler {
    @Inject
    APIService apiService;
    @Inject
    Gson gson;
    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    FileManager fileManager;
    @Inject
    OkHttpClient okHttpClient;
    private static final String TAG = MainActivity.class.getName();
    private RecyclerView argumentMapView;
    private GraphAdapter graphAdapter;
    private LayoutInflater inflater;
    private ArgumentMap map;
    private boolean editingNode = false;
    private boolean failed = false;
    private dev.bandb.graphview.graph.Node contextVisual;
    private Graph graph;
    private ArgumentMapEditor mapEditor;
    private final static int baseColor = Color.parseColor("#565656");
    private final static int positiveColor = Color.parseColor("#008577");
    private final static int negativeColor = Color.parseColor("#FFA17A");
    private final static int brightestColorConclusion = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        inflater = LayoutInflater.from(this);

        ((ArgumentMapperApplication)getApplication()).getApplicationComponent().inject(this);

        map = getIntent().getParcelableExtra("map");
        Integer sessionID = map.getSessionID();
        if(sessionID != null) {
            mapEditor = new OnlineArgumentMapEditor(this, sharedPreferences, okHttpClient, sessionID);
            apiService.getSessionMap(sessionID).enqueue(new retrofit2.Callback<ResponseBody>()
            {
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if(t instanceof AuthException)
                    {
                        Toast.makeText(ArgumentMapActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        fixToken();
                    }
                    else if(t instanceof ConnectionException)
                    {
                        Toast.makeText(ArgumentMapActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful())
                    {
                        try {
                            String sessionMap = JsonParser.parseString(response.body().string()).getAsJsonObject().get("sessionMap").getAsString();
                            ArgumentMapActivity.this.map.setRoot(gson.fromJson(sessionMap, InductiveNode.class));
                            fillArgumentMap();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else {
                        if(response.code() == 404)
                        {
                            Toast.makeText(ArgumentMapActivity.this, "This session is no longer available", Toast.LENGTH_LONG).show();
                            map.removeSessionID();
                            fileManager.saveMapToFile(map);
                            setResult(RESULT_FIRST_USER);
                            finish();
                        }
                        try {
                            Log.v(TAG, response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }else{
            mapEditor = new OfflineArgumentMapEditor();
            fillArgumentMap();
        }
    }

    @Override
    public void fixToken() {
        if(failed) return;
        failed = true;
        ArgumentMapperApplication app = (ArgumentMapperApplication) getApplication();
        app.redirectToLogin();
        finish();
    }

    private void fillArgumentMap()
    {
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        argumentMapView = findViewById(R.id.treeRecycler);
        graphAdapter = new GraphAdapter();
        BuchheimWalkerConfiguration config = new BuchheimWalkerConfiguration.Builder()
                .setSiblingSeparation(100)
                .setLevelSeparation(100)
                .setSubtreeSeparation(100)
                .setOrientation(BuchheimWalkerConfiguration.ORIENTATION_TOP_BOTTOM)
                .build();
        BuchheimWalkerLayoutManager layoutManager = new BuchheimWalkerLayoutManager(this, config);
        argumentMapView.setLayoutManager(layoutManager);
        Paint edgeStyle = new Paint();
        edgeStyle.setColor(Color.rgb(255,255,255));
        edgeStyle.setStrokeWidth(5f);
        edgeStyle.setStyle(Paint.Style.STROKE);
        edgeStyle.setStrokeJoin(Paint.Join.ROUND);
        edgeStyle.setPathEffect(new CornerPathEffect(10f));
        argumentMapView.addItemDecoration(new TreeEdgeDecoration(edgeStyle));

        graph = new Graph();
        fillGraph(graph, map.getRoot());
        graphAdapter.submitGraph(graph);
        argumentMapView.setAdapter(graphAdapter);
    }

    private void fillGraph(Graph graph, MapNode root)
    {
        List<MapNode> currentLayer = new ArrayList<>();
        currentLayer.add(root);
        List<MapNode> children;
        MapNode parent;
        for(int i = 0; i < currentLayer.size(); i++)
        {
            parent = currentLayer.get(i);
            Node parentVisual = new Node(parent);
            children = parent.getChildren();
            if(children.isEmpty())
            {
                graph.addNode(parentVisual);
                continue;
            }
            for(MapNode child:children) {
                currentLayer.add(child);
                Node childVisual = new Node(child);
                graph.addEdge(parentVisual, childVisual);
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Choose action");
        getMenuInflater().inflate(R.menu.node_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addChild:
                showAddNodeDialog(null);
                return true;
            case R.id.remove:
                removeNode(contextVisual);
                return true;
            default:
                resetDialogStates();
                return super.onContextItemSelected(item);
        }
    }

    public class GraphAdapter extends AbstractGraphAdapter<GraphViewHolder> {
        @NonNull
        @Override
        public GraphViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View nodeView = inflater.inflate(R.layout.node_item, parent, false);
            GraphViewHolder vh = new GraphViewHolder(nodeView);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull GraphViewHolder holder, int position) {
            InductiveNode node = (InductiveNode)getNodeData(position);
            holder.tvName.setText(node.getName());
            int conclusion = node.getConclusion();
            int nodeColor = (Integer) new ArgbEvaluator().evaluate(Math.min(Math.abs(conclusion)/(float)brightestColorConclusion,1), baseColor, ((conclusion < 0)?negativeColor:positiveColor));
            holder.layout.setBackgroundColor(nodeColor);
        }
    }

    class GraphViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView tvName;
        public LinearLayout layout;
        public GraphViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            registerForContextMenu(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            layout = itemView.findViewById(R.id.layout);
        }

        @Override
        public void onClick(View view) {
            contextVisual = ArgumentMapActivity.this.graphAdapter.getNode(getAbsoluteAdapterPosition());
            InductiveNode node = (InductiveNode) contextVisual.getData();
            if(node.getParent() != null) {
                editingNode = true;
                showAddNodeDialog(node);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            contextVisual = ArgumentMapActivity.this.graphAdapter.getNode(getAbsoluteAdapterPosition());
            return false;
        }
    }

    private void showAddNodeDialog(InductiveNode node)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddNodeDialog newFragment = new AddNodeDialog();
        newFragment.setEditingNode(node);
        newFragment.show(fragmentManager, "dialog");
    }

    private void removeNode(Node visualNode)
    {
        if(!mapEditor.removeChild((MapNode)visualNode.getData())) return;
        graph.removeNode(visualNode);
        graphAdapter.notifyDataSetChanged();
    }

    private void resetDialogStates()
    {
        contextVisual = null;
        editingNode = false;
    }

    @Override
    public void onFinishAddNodeDialog(InductiveNode item) {
        if(item != null) {
            if (editingNode) {
                InductiveNode node = (InductiveNode) contextVisual.getData();
                node.setName(item.getName());
                node.setDescription(item.getDescription());
                node.setWeight(item.getWeight());
                graphAdapter.notifyDataSetChanged();
            }else{
                if(mapEditor.addChild((InductiveNode) contextVisual.getData(), item)) {
                    graph.addEdge(contextVisual, new Node(item));
                    graphAdapter.notifyDataSetChanged();
                }
            }
        }
        resetDialogStates();
    }

    @Override
    public void onBackPressed()
    {
        Intent data = new Intent();
        data.putExtra("map", map);
        setResult(RESULT_OK, data);
        fileManager.saveMapToFile(map);
        finish();
    }

    @Override
    protected void onStop()
    {
        fileManager.saveMapToFile(map);
        super.onStop();
    }
}