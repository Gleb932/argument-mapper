package com.example.thoughtscloset;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import dev.bandb.graphview.AbstractGraphAdapter;
import dev.bandb.graphview.graph.Graph;
import dev.bandb.graphview.graph.Node;
import dev.bandb.graphview.layouts.tree.BuchheimWalkerConfiguration;
import dev.bandb.graphview.layouts.tree.BuchheimWalkerLayoutManager;
import dev.bandb.graphview.layouts.tree.TreeEdgeDecoration;

public class ArgumentMapActivity extends AppCompatActivity implements AddNodeDialogListener {

    private RecyclerView argumentMapView;
    private GraphAdapter graphAdapter;
    private LayoutInflater inflater;
    private InductiveNode root;
    private InductiveNode editingNode;
    private Node contextVisual;
    private Graph graph;
    private final static int baseColor = Color.parseColor("#565656");
    private final static int positiveColor = Color.parseColor("#008577");
    private final static int negativeColor = Color.parseColor("#FFA17A");
    private final static int brightestColorConclusion = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        root = getIntent().getParcelableExtra("rootNode");
        inflater = LayoutInflater.from(this);

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
        fillGraph(graph, root);
        graphAdapter.submitGraph(graph);
        argumentMapView.setAdapter(graphAdapter);
    }

    private void fillGraph(Graph graph, InductiveNode root)
    {
        List<InductiveNode> currentLayer = new ArrayList<>();
        currentLayer.add(root);
        List<InductiveNode> children;
        InductiveNode parent;
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
            for(InductiveNode child:children) {
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
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            layout = (LinearLayout) itemView.findViewById(R.id.layout);
        }

        @Override
        public void onClick(View view) {
            InductiveNode node = (InductiveNode)ArgumentMapActivity.this.graphAdapter.getNode(getAbsoluteAdapterPosition()).getData();
            editingNode = node;
            showAddNodeDialog(node);
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
        if(visualNode.getData() == root) return;
        InductiveNode nodeToRemove = ((InductiveNode)visualNode.getData());
        InductiveNode parent = nodeToRemove.getParent();
        parent.removeChild(nodeToRemove);
        parent.updateConclusion();
        graph.removeNode(visualNode);
        graphAdapter.notifyDataSetChanged();
    }

    private void resetDialogStates()
    {
        contextVisual = null;
        editingNode = null;
    }

    @Override
    public void onFinishAddNodeDialog(InductiveNode item) {
        if(item != null) {
            if (editingNode == null) {
                ((InductiveNode) contextVisual.getData()).addChild(item);
                graph.addEdge(contextVisual, new Node(item));
            }
            item.updateConclusion();
        }
        resetDialogStates();
        graphAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed()
    {
        Intent data = new Intent();
        data.putExtra("rootNode", root);
        setResult(RESULT_OK, data);
        finish();
    }
}