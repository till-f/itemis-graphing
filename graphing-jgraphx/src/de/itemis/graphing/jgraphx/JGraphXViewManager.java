package de.itemis.graphing.jgraphx;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.view.mxGraph;
import de.itemis.graphing.model.*;
import de.itemis.graphing.model.style.Style;
import de.itemis.graphing.view.AbstractViewManager;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.HashMap;

public class JGraphXViewManager<T> extends AbstractViewManager<T> implements IGraphListener<T> {

    private final HashMap<GraphElement, mxCell> _graphElementToCell = new HashMap<>();
    private final StyleToJGraphXCSS _styleConverter = new StyleToJGraphXCSS();

    private final mxGraph _mxGraph;
    private final Object _mxRootNode;
    private final DefaultMxGraphComponent _mxGraphComponent;
    private final mxGraphLayout _mxLayout;

    public JGraphXViewManager(Graph<T> graph)
    {
        super(graph);

        _mxGraph = new mxGraph();
        _mxRootNode = _mxGraph.getDefaultParent();
        _mxGraphComponent = new DefaultMxGraphComponent(_mxGraph);

        mxHierarchicalLayout layout = new mxHierarchicalLayout(_mxGraph);
        layout.setFineTuning(true);
        layout.setIntraCellSpacing(15);
        layout.setInterHierarchySpacing(15);
        layout.setInterRankCellSpacing(30);
        _mxLayout = layout;

        initMxGraph(graph);

        graph.registerGraphListener(this);
    }

    private void initMxGraph(Graph<T> graph)
    {
        _mxGraph.getModel().beginUpdate();
        try
        {
            for (Vertex<T> vertex : graph.getVertexes())
            {
                addVertex(vertex);
            }

            for (Edge<T> edge : graph.getEdges())
            {
                addEdge(edge);
            }
        }
        finally
        {
            _mxGraph.getModel().endUpdate();
        }

        _mxLayout.execute(_mxRootNode);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Component getView()
    {
        return _mxGraphComponent;
    }

    @Override
    public void zoomIn()
    {

    }

    @Override
    public void zoomOut()
    {

    }

    @Override
    public void fitView()
    {
        _mxGraphComponent.fitView();
    }

    @Override
    public void close()
    {

    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void graphCleared()
    {
        _mxGraph.getModel().beginUpdate();
        try
        {
            _mxGraph.removeCells(_mxGraph.getChildCells(_mxGraph.getDefaultParent(), true, true));
        }
        finally
        {
            _mxGraph.getModel().endUpdate();
        }
    }

    @Override
    public void vertexAdded(Vertex<T> vertex)
    {
        _mxGraph.getModel().beginUpdate();
        try
        {
            addVertex(vertex);
        }
        finally
        {
            _mxGraph.getModel().endUpdate();
        }

        _mxLayout.execute(_mxRootNode);
    }

    @Override
    public void vertexRemoved(Vertex<T> vertex)
    {
        _mxGraph.getModel().beginUpdate();
        try
        {
            removeVertex(vertex);
        }
        finally
        {
            _mxGraph.getModel().endUpdate();
        }

        _mxLayout.execute(_mxRootNode);
    }

    @Override
    public void edgeAdded(Edge<T> edge)
    {
        _mxGraph.getModel().beginUpdate();
        try
        {
            addEdge(edge);
        }
        finally
        {
            _mxGraph.getModel().endUpdate();
        }

        _mxLayout.execute(_mxRootNode);
    }

    @Override
    public void edgeRemoved(Edge<T> edge)
    {
        _mxGraph.getModel().beginUpdate();
        try
        {
            removeEdge(edge);
        }
        finally
        {
            _mxGraph.getModel().endUpdate();
        }

        _mxLayout.execute(_mxRootNode);
    }

    @Override
    public void attachmentAdded(AttachmentBase<T> attachment)
    {
        _mxGraph.getModel().beginUpdate();
        try
        {
            addAttachment(attachment);
            mxCell parent = _graphElementToCell.get(attachment.getParent());
            _mxGraph.updateCellSize(parent);
        }
        finally
        {
            _mxGraph.getModel().endUpdate();
        }

        _mxLayout.execute(_mxRootNode);
    }

    @Override
    public void attachmentRemoved(AttachmentBase<T> attachment)
    {
        _mxGraph.getModel().beginUpdate();
        try
        {
            removeAttachment(attachment);
        }
        finally
        {
            _mxGraph.getModel().endUpdate();
        }

        _mxLayout.execute(_mxRootNode);
    }

    @Override
    public void styleChanged(GraphElement<T> element)
    {
        _mxGraph.getModel().beginUpdate();
        try
        {
            setStyle(_graphElementToCell.get(element) , element.getActiveStyle());
        }
        finally
        {
            _mxGraph.getModel().endUpdate();
        }
    }

    @Override
    public void labelChanged(GraphElement<T> element)
    {
    }

    @Override
    public void labelPriorityChanged(GraphElement<T> element)
    {
    }

    // -----------------------------------------------------------------------------------------------------------------

    private void addVertex(Vertex<T> vertex)
    {
        mxCell cell = (mxCell) _mxGraph.insertVertex(_mxRootNode, vertex.getId(), vertex.getLabel(), 0, 0, vertex.getSize().getWidth(), vertex.getSize().getHeight());
        _graphElementToCell.put(vertex, cell);

        setStyle(cell, vertex.getActiveStyle());

        for (AttachmentBase<T> attachment : vertex.getAttachments())
        {
            addAttachment(attachment);
        }

        _mxGraph.updateCellSize(cell);
    }

    private void removeVertex(Vertex<T> vertex)
    {
        mxCell[] toRemove = { _graphElementToCell.get(vertex) };
        _mxGraph.removeCells(toRemove, true);
        _graphElementToCell.remove(vertex);
    }

    private void addEdge(Edge<T> edge)
    {
        mxCell sourceCell = _graphElementToCell.get(edge.getFrom());
        mxCell targetCell = _graphElementToCell.get(edge.getTo());
        mxCell cell = (mxCell)_mxGraph.insertEdge(_mxRootNode, edge.getId(), edge.getLabel(), sourceCell, targetCell);
        _graphElementToCell.put(edge, cell);

        setStyle(cell, edge.getActiveStyle());
    }

    private void removeEdge(Edge<T> edge)
    {
        mxCell sourceCell = _graphElementToCell.get(edge.getFrom());
        mxCell targetCell = _graphElementToCell.get(edge.getTo());

        if (sourceCell == null || targetCell == null) return;

        Object[] edges = _mxGraph.getEdgesBetween(sourceCell, targetCell);
        for (Object mxEdge : edges)
        {
            _mxGraph.getModel().remove(mxEdge);
        }

        _graphElementToCell.remove(edge);
    }

    private void addAttachment(AttachmentBase<T> attachment)
    {
        mxCell parent = _graphElementToCell.get(attachment.getParent());
        mxCell cell = (mxCell)_mxGraph.insertVertex(parent, attachment.getId(), attachment.getLabel(), 0, 0, attachment.getSize().getWidth(), attachment.getSize().getHeight());
        _graphElementToCell.put(attachment, cell);

        setStyle(cell, attachment.getActiveStyle());
    }

    private void removeAttachment(AttachmentBase<T> attachment)
    {
        mxCell[] toRemove = { _graphElementToCell.get(attachment) };
        _mxGraph.removeCells(toRemove, true);
        _graphElementToCell.remove(attachment);
    }

    private void setStyle(mxCell cell, Style style)
    {
        cell.setStyle(_styleConverter.getStyleCSS(style));
    }

}
