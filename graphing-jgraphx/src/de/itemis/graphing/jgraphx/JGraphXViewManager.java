package de.itemis.graphing.jgraphx;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import de.itemis.graphing.model.*;
import de.itemis.graphing.view.AbstractViewManager;

import java.awt.*;
import java.util.HashMap;

public class JGraphXViewManager<T> extends AbstractViewManager<T> implements IGraphListener<T> {

    private final HashMap<Vertex, mxCell> _vertexToCell = new HashMap<Vertex, mxCell>();
    private final mxGraph _mxGraph;
    private final mxGraphLayout _mxLayout;
    private final mxGraphComponent _mxSwingComponent;
    private final Object _defaultParent;

    public JGraphXViewManager(Graph<T> graph)
    {
        super(graph);

        _mxGraph = new mxGraph();
        _defaultParent = _mxGraph.getDefaultParent();

        mxHierarchicalLayout layout = new mxHierarchicalLayout(_mxGraph);
        layout.setFineTuning(true);
        layout.setIntraCellSpacing(10);
        layout.setInterHierarchySpacing(10);
        layout.setInterRankCellSpacing(30);
        _mxLayout = layout;

        _mxSwingComponent = new mxGraphComponent(_mxGraph);
        _mxSwingComponent.setConnectable(false);
        _mxSwingComponent.setAutoExtend(true);

        initMxGraph(graph);

        graph.registerGraphListener(this);
    }

    private void initMxGraph(Graph<T> graph)
    {
        _mxGraph.getModel().beginUpdate();
        try
        {
            for (Vertex<T> n : graph.getVertexes())
            {
                mxCell cell = (mxCell) _mxGraph.insertVertex(_defaultParent, n.getId(), n, 0, 0, n.getSize().getWidth(), n.getSize().getHeight());
                _mxGraph.updateCellSize(cell);
                _vertexToCell.put(n, cell);
            }

            for (Vertex<T> source : graph.getVertexes())
            {
                for (Vertex<T> target : source.getTargets())
                {
                    mxCell sourceCell = _vertexToCell.get(source);
                    mxCell targetCell = _vertexToCell.get(target);
                    _mxGraph.insertEdge(_defaultParent, null, null, sourceCell, targetCell);
                }
            }
        }
        finally
        {
            _mxGraph.getModel().endUpdate();
        }

        _mxLayout.execute(_defaultParent);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Component getView()
    {
        return _mxSwingComponent;
    }

    @Override
    public void zoomIn() {

    }

    @Override
    public void zoomOut() {

    }

    @Override
    public void fitView() {

    }

    @Override
    public void close() {

    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void graphCleared() {

    }

    @Override
    public void vertexAdded(Vertex<T> vertex) {

    }

    @Override
    public void vertexRemoved(Vertex<T> vertex) {

    }

    @Override
    public void edgeAdded(Edge<T> edge) {

    }

    @Override
    public void edgeRemoved(Edge<T> edge) {

    }

    @Override
    public void attachmentAdded(AttachmentBase<T> attachment) {

    }

    @Override
    public void attachmentRemoved(AttachmentBase<T> attachment) {

    }

    @Override
    public void styleChanged(GraphElement<T> element) {

    }

    @Override
    public void labelChanged(GraphElement<T> element) {

    }

    @Override
    public void labelPriorityChanged(GraphElement<T> element) {

    }
}
