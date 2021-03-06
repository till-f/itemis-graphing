package de.itemis.graphing.jgraphx;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import de.itemis.graphing.jgraphx.customization.DefaultMxGraph;
import de.itemis.graphing.jgraphx.customization.DefaultMxGraphComponent;
import de.itemis.graphing.jgraphx.customization.DefaultMxHierarchicalLayout;
import de.itemis.graphing.model.*;
import de.itemis.graphing.model.style.Style;
import de.itemis.graphing.view.AbstractViewManager;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class JGraphXViewManager<T> extends AbstractViewManager<T> implements IGraphListener<T> {

    private final HashMap<GraphElement<T>, mxCell> _graphElementToCell = new HashMap<>();
    private final HashMap<mxCell, GraphElement<T>> _cellToGraphElement = new HashMap<>();
    private final JGraphXStyleConverter _styleConverter = new JGraphXStyleConverter();

    private final DefaultMxGraph _mxGraph;
    private final Object _mxRootNode;
    private final DefaultMxGraphComponent _mxGraphComponent;
    private final mxGraphLayout _mxLayout;

    public JGraphXViewManager(Graph<T> graph)
    {
        this(graph, 15, 15, 30);
    }

    public JGraphXViewManager(Graph<T> graph, double spacingIntraCell, double spacingInterHierarchy, double spacingInterRank)
    {
        super(graph);

        _mxGraph = new DefaultMxGraph();
        _mxRootNode = _mxGraph.getDefaultParent();
        _mxGraphComponent = new DefaultMxGraphComponent(_mxGraph, this);
        _mxLayout = new DefaultMxHierarchicalLayout(_mxGraph, spacingIntraCell, spacingInterHierarchy, spacingInterRank);

        initMxGraph(graph);

        graph.registerGraphListener(this);

        JGraphXMouseListener mouseListener = new JGraphXMouseListener(this, _mxGraphComponent);
        _mxGraphComponent.getGraphControl().addMouseListener(mouseListener);
        _mxGraphComponent.getGraphControl().addMouseWheelListener(mouseListener);
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

    public GraphElement<T> getGraphElement(mxCell cell)
    {
        return _cellToGraphElement.get(cell);
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
            _graphElementToCell.clear();
            _cellToGraphElement.clear();
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
            addAttachment(attachment, true);

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
        mxCell cell = _graphElementToCell.get(element);
        if (cell == null) return;  // this may be called for dangling edges (which have not yet been notified / added before)
        
        _mxGraph.getModel().beginUpdate();
        try
        {
            setStyle(cell, element.getActiveStyle());
        }
        finally
        {
            _mxGraph.getModel().endUpdate();
        }
    }

    @Override
    public void labelChanged(GraphElement<T> element)
    {
        mxCell cell = _graphElementToCell.get(element);
        _mxGraph.getModel().setValue(cell, element.getLabel());
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
        _cellToGraphElement.put(cell, vertex);

        setStyle(cell, vertex.getActiveStyle());

        for (AttachmentBase<T> attachment : vertex.getAttachments())
        {
            addAttachment(attachment, false);
        }

        cell.setGeometry(new mxGeometry(0, 0, vertex.getSize().getWidth(), vertex.getSize().getHeight()));
    }

    private void removeVertex(Vertex<T> vertex)
    {
        for (Edge<T> edge : vertex.getOutgoingEdges())
        {
            removeEdge(edge);
        }
        for (Edge<T> edge : vertex.getIncomingEdges())
        {
            removeEdge(edge);
        }
        removeGraphElement(vertex);
    }

    private void addEdge(Edge<T> edge)
    {
        mxCell sourceCell = _graphElementToCell.get(edge.getFrom());
        mxCell targetCell = _graphElementToCell.get(edge.getTo());
        mxCell cell = (mxCell)_mxGraph.insertEdge(_mxRootNode, edge.getId(), edge.getLabel(), sourceCell, targetCell);
        _graphElementToCell.put(edge, cell);
        _cellToGraphElement.put(cell, edge);

        setStyle(cell, edge.getActiveStyle());
    }

    private void removeEdge(Edge<T> edge)
    {
        removeGraphElement(edge);
    }

    private void addAttachment(AttachmentBase<T> attachment, boolean relayout)
    {
        Vertex<T> parentVertex = attachment.getParent();
        mxCell parentCell = _graphElementToCell.get(parentVertex);

        if (relayout)
        {
            _mxGraph.getModel().setGeometry(parentCell, new mxGeometry(0, 0, parentVertex.getSize().getWidth(), parentVertex.getSize().getHeight()));
            for(AttachmentBase<T> otherAttachment : parentVertex.getAttachments())
            {
                if (otherAttachment instanceof TabularAttachment)
                {
                    mxCell otherAttachmentCell = _graphElementToCell.get(otherAttachment);
                    if (otherAttachmentCell != null)
                    {
                        placeAttachment_Tabular((TabularAttachment<T>) otherAttachment, otherAttachmentCell);
                    }
                }
            }
        }

        mxCell cell = (mxCell)_mxGraph.insertVertex(parentCell, attachment.getId(), attachment.getLabel(), 0, 0, attachment.getSize().getWidth(), attachment.getSize().getHeight());
        _graphElementToCell.put(attachment, cell);
        _cellToGraphElement.put(cell, attachment);

        setStyle(cell, attachment.getActiveStyle());

        if (attachment instanceof TabularAttachment)
        {
            placeAttachment_Tabular((TabularAttachment<T>) attachment, cell);
        }
    }

    private void removeAttachment(AttachmentBase<T> attachment)
    {
        removeGraphElement(attachment);
    }

    private void removeGraphElement(GraphElement<T> element)
    {
        mxCell toRemove = _graphElementToCell.get(element);
        _mxGraph.getModel().remove(toRemove);
        _graphElementToCell.remove(element);
        _cellToGraphElement.remove(toRemove);
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
        _mxGraph.getModel().setStyle(cell, _styleConverter.getStyleCSS(style));
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
