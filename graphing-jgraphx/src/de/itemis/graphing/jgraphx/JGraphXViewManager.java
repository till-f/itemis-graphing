package de.itemis.graphing.jgraphx;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import de.itemis.graphing.model.*;
import de.itemis.graphing.model.style.Style;
import de.itemis.graphing.view.AbstractViewManager;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class JGraphXViewManager<T> extends AbstractViewManager<T> implements IGraphListener<T> {

    private final HashMap<GraphElement, mxCell> _graphElementToCell = new HashMap<>();
    private final JGraphXStyleConverter _styleConverter = new JGraphXStyleConverter();

    private final DefaultMxGraph _mxGraph;
    private final Object _mxRootNode;
    private final DefaultMxGraphComponent _mxGraphComponent;
    private final mxGraphLayout _mxLayout;

    public JGraphXViewManager(Graph<T> graph)
    {
        super(graph);

        _mxGraph = new DefaultMxGraph();
        _mxRootNode = _mxGraph.getDefaultParent();
        _mxGraphComponent = new DefaultMxGraphComponent(_mxGraph);
        _mxLayout = new DefaultMxHierarchicalLayout(_mxGraph);

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

        triggerLayout();
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
        _mxGraphComponent.zoomIn();
    }

    @Override
    public void zoomOut()
    {
        _mxGraphComponent.zoomOut();
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

        triggerLayout();
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

        triggerLayout();
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

        triggerLayout();
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

        triggerLayout();
    }

    @Override
    public void attachmentAdded(AttachmentBase<T> attachment)
    {
        _mxGraph.getModel().beginUpdate();
        try
        {
            addAttachment(attachment);

            Vertex<T> vertex = attachment.getParent();
            mxCell cell = _graphElementToCell.get(vertex);
            cell.setGeometry(new mxGeometry(0, 0, vertex.getSize().getWidth(), vertex.getSize().getHeight()));
        }
        finally
        {
            _mxGraph.getModel().endUpdate();
        }

        triggerLayout();
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

        triggerLayout();
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
        mxCell cell = (mxCell) _mxGraph.insertVertex(_mxRootNode, vertex.getId(), vertex.getLabel(), 0, 0, 0, 0);
        _graphElementToCell.put(vertex, cell);

        setStyle(cell, vertex.getActiveStyle());

        for (AttachmentBase<T> attachment : vertex.getAttachments())
        {
            addAttachment(attachment);
        }

        cell.setGeometry(new mxGeometry(0, 0, vertex.getSize().getWidth(), vertex.getSize().getHeight()));
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

        if (attachment instanceof TabularAttachment)
        {
            placeAttachment_Tabular((TabularAttachment<T>) attachment, cell);
        }
    }

    private void removeAttachment(AttachmentBase<T> attachment)
    {
        mxCell[] toRemove = { _graphElementToCell.get(attachment) };
        _mxGraph.removeCells(toRemove, true);
        _graphElementToCell.remove(attachment);
    }

    private void placeAttachment_Tabular(TabularAttachment<T> attachment, mxCell mxCell)
    {
        Vertex vertex = attachment.getParent();
        Size cellOffset = vertex.getCellOffset(attachment.getRowIndex(), attachment.getColIndex());
        Size cellSize = vertex.getCellSize(attachment.getRowIndex(), attachment.getColIndex(), attachment.getColSpan(), attachment.getRowSpan());

        double x;
        switch (attachment.getHAlignment())
        {
            case Left:
                x = cellOffset.getWidth();
                break;
            case Center:
                x = cellOffset.getWidth() + (cellSize.getWidth() - attachment.getSize().getWidth()) / 2;
                break;
            case Right:
                x = cellOffset.getWidth() + cellSize.getWidth() - attachment.getSize().getWidth();
                break;
            default:
                throw new RuntimeException("unexpected alignment: " + attachment.getHAlignment());
        }
        x += vertex.getPadding().getWest();

        double y;
        switch (attachment.getVAlignment())
        {
            case Top:
                y = cellOffset.getHeight();
                break;
            case Middle:
                y = cellOffset.getHeight() + (cellSize.getHeight() - attachment.getSize().getHeight()) / 2;
                break;
            case Bottom:
                y = cellOffset.getHeight() + cellSize.getHeight() - attachment.getSize().getHeight();
                break;
            default:
                throw new RuntimeException("unexpected alignment: " + attachment.getVAlignment());
        }
        y += vertex.getPadding().getNorth();

        mxCell.setGeometry(new mxGeometry(x, y, attachment.getSize().getWidth(), attachment.getSize().getHeight()));
    }

    private void setStyle(mxCell cell, Style style)
    {
        cell.setStyle(_styleConverter.getStyleCSS(style));
    }

    private void triggerLayout()
    {
        if (_mxLayout instanceof mxHierarchicalLayout)
        {
            // mxHierarchicalLayouter cannot handle vertexes with child vertexes correctly. attachments are not supposed to be layouted anyway.
            // only layout top-level vertices (and edges in between)
            // manually filtering edge labels as it seems to be placed together with the edge / connected vertices
            List<Object> topLevelCells = Arrays.stream(_mxGraph.getChildCells(_mxRootNode, false, false))
                    .filter(it -> !((mxCell)it).isEdge())
                    .collect(Collectors.toList());

            ((mxHierarchicalLayout) _mxLayout).execute(_mxRootNode, topLevelCells);
        }
        else
        {
            _mxLayout.execute(_mxRootNode);
        }
    }
}
