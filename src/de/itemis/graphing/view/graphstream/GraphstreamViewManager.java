package de.itemis.graphing.view.graphstream;

import de.itemis.graphing.model.*;
import de.itemis.graphing.view.AbstractViewManager;
import org.graphstream.graph.Element;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

import javax.swing.*;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class GraphstreamViewManager extends AbstractViewManager implements IGraphListener, MouseWheelListener, HierarchyBoundsListener
{
    private final org.graphstream.graph.Graph _gsGraph;
    private final StyleToGraphstreamCSS _styleConverter;
    private SpriteManager _spriteManager;                   // cannot be final due to bug in graphstream (reset of SpriteManager does not work properly)

    private ViewPanel _view = null;
    private Viewer _viewer = null;
    private Layout _layout = null;

    private final double _textThresholdMainText;
    private final double _textThresholdLowPrioText;

    public GraphstreamViewManager(Graph graph)
    {
        this(graph, 25, 55);
    }

    public GraphstreamViewManager(Graph graph, double textThresholdMainText, double textThresholdLowPrioText)
    {
        super(graph);

        _gsGraph = new SingleGraph("Graph");
        _spriteManager = new SpriteManager(_gsGraph);
        _styleConverter = new StyleToGraphstreamCSS();

        _textThresholdMainText = textThresholdMainText;
        _textThresholdLowPrioText = textThresholdLowPrioText;

        setInitialAttributes();
        setLabelState(true, true);

        for(Vertex vertex : graph.getVertexes())
        {
            addVertex(vertex);
        }

        for(Edge edge : graph.getEdges())
        {
            addEdge(edge);
        }
        
        graph.registerGraphListener(this);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // view creation

    public void configure(Layout layout)
    {
        configure(layout, null, null);
    }

    public void configure(Layout layout, Viewer viewer, String viewID)
    {
        NotifyingMouseManager mouseManager = new NotifyingMouseManager(this);

        if (viewer == null)
        {
            viewer = new Viewer(_gsGraph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        }

        final ViewPanel view;
        if (viewID == null)
        {
            view = new DefaultView(viewer, "DefaultView", new org.graphstream.ui.j2dviewer.J2DGraphRenderer());
            viewer.addView(view);
        }
        else
        {
            view = (ViewPanel)viewer.getView(viewID);
        }

        view.setMouseManager(mouseManager);
        view.addMouseWheelListener(this);
        view.addHierarchyBoundsListener(this);

        _view = view;
        _viewer = viewer;
        _layout = layout;

        if (_layout != null)
        {
            _viewer.enableAutoLayout(_layout);
        }
        else
        {
            _viewer.enableAutoLayout();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // IViewManager

    @Override
    public JPanel getView()
    {
        return _view;
    }

    @Override
    public void zoomIn()
    {
        double currentZoom =  _view.getCamera().getViewPercent();
        _view.getCamera().setViewPercent(currentZoom - 0.1 * currentZoom);
        updateLabelState();
    }

    @Override
    public void zoomOut()
    {
        double currentZoom = _view.getCamera().getViewPercent();
        _view.getCamera().setViewPercent(currentZoom + 0.1 * currentZoom);
        updateLabelState();
    }

    @Override
    public void fitView()
    {
        _view.getCamera().resetView();
    }

    public void close()
    {
        _view.removeMouseWheelListener(this);
        _viewer.close();
    }

    // -----------------------------------------------------------------------------------------------------------------
    // MouseWheelListener and HierarchyBoundsListener (size and zoom changing)

    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        int rotation =  e.getWheelRotation();
        double currentZoom = _view.getCamera().getViewPercent();
        double zoomOffset = 0.1 * rotation * currentZoom;
        _view.getCamera().setViewPercent(currentZoom + zoomOffset);

        updateLabelState();
    }

    @Override
    public void ancestorMoved(HierarchyEvent e)
    {

    }

    @Override
    public void ancestorResized(HierarchyEvent e)
    {
        updateLabelState();
    }

    private boolean _showMainText = true;
    private boolean _showLowPrioText = true;
    private void updateLabelState()
    {
        if (_view.getWidth() < 10)
            return;

        try
        {
            // quick distance calculation, relies on 2-dimensional, not rotated rendering
            double distancePX = 5;
            Point3 p1 = _view.getCamera().transformPxToGu(0, 0);
            Point3 p2 = _view.getCamera().transformPxToGu(0, distancePX);
            double distanceGU = Math.abs(p2.y - p1.y);
            double relationPXGU = distancePX / distanceGU;

            boolean showMainText = relationPXGU > _textThresholdMainText;
            boolean showLowPrioText = relationPXGU > _textThresholdLowPrioText;

            if (showMainText != _showMainText || showLowPrioText != _showLowPrioText)
            {
                setLabelState(showMainText, showLowPrioText);
            }
        }
        catch (NullPointerException e)
        {
            // at the very beginning camera might fail to translate pixels. as there is no way to know this in advance we need a catch...
        }
    }

    private void setLabelState(boolean showMainText, boolean showLowPrioText)
    {
        _showMainText = showMainText;
        _showLowPrioText = showLowPrioText;

        String visMain = showMainText ? "text-visibility-mode: normal;" : "text-visibility-mode: hidden;" ;
        String visLowPrio = showLowPrioText ? "text-visibility-mode: normal;" : "text-visibility-mode: hidden;" ;

        _gsGraph.addAttribute("ui.stylesheet", "graph { padding: 0.7gu; } node { " + visMain + " } edge { " + visMain + " arrow-size: 0.1gu,0.04gu; } sprite { " + visMain + " } node.lowpriotxt { " + visLowPrio + " } edge.lowpriotxt { " + visLowPrio + " } sprite.lowpriotxt { " + visLowPrio + " }");
    }

    // -----------------------------------------------------------------------------------------------------------------
    // GraphListener

    @Override
    public void graphCleared()
    {
        _gsGraph.clear();
        _spriteManager = new SpriteManager(_gsGraph);

        setInitialAttributes();
    }

    @Override
    public void vertexAdded(Vertex vertex)
    {
        addVertex(vertex);
    }

    @Override
    public void vertexRemoved(Vertex vertex)
    {
        removeVertex(vertex);
    }

    @Override
    public void edgeAdded(Edge edge)
    {
        addEdge(edge);
    }

    @Override
    public void edgeRemoved(Edge edge)
    {
        removeEdge(edge);
    }

    @Override
    public void attachmentAdded(Attachment attachment)
    {
        // remove all attachments, then insert new set (required for space calculation)
        for (Attachment existingAttachment : attachment.getParent().getAttachments())
        {
            if (existingAttachment != attachment)
                removeAttachment(existingAttachment);
        }
        addAttachments(attachment.getParent());

        if (attachment.isDynamicLayoutAffected())
            _layout.compute();
    }

    @Override
    public void attachmentRemoved(Attachment attachment)
    {
        // remove all attachments, then insert new set (required for space calculation)
        removeAttachment(attachment);
        for (Attachment existingAttachment : attachment.getParent().getAttachments())
        {
            removeAttachment(existingAttachment);
        }
        addAttachments(attachment.getParent());

        if (attachment.isDynamicLayoutAffected())
            _layout.compute();
    }

    @Override
    public void styleChanged(GraphElement element)
    {
        Element gsElement = getGraphstreamElement(element);

        if (gsElement == null)
            return;

        String styleCSS = _styleConverter.getAtciveStyleCSS(element);
        gsElement.setAttribute("ui.style", styleCSS);
    }

    @Override
    public void labelChanged(GraphElement element)
    {
        Element gsElement = getGraphstreamElement(element);

        if (gsElement == null)
            return;

        gsElement.setAttribute("ui.label", element.getLabel());
    }

    @Override
    public void labelPriorityChanged(GraphElement element)
    {
        Element gsElement = getGraphstreamElement(element);

        if (gsElement == null)
            return;

        if (element.isLowPrioLabel())
            gsElement.setAttribute("ui.class", "lowpriotxt");
        else
            gsElement.setAttribute("ui.class", "");
    }

    private Element getGraphstreamElement(GraphElement element)
    {
        Element gsElement;

        if (element instanceof Vertex)
        {
            gsElement = _gsGraph.getNode(element.getId());
        }
        else if (element instanceof Edge)
        {
            gsElement = _gsGraph.getEdge(element.getId());
        }
        else if (element instanceof Attachment)
        {
            gsElement = _spriteManager.getSprite(element.getId());
        }
        else
        {
            throw new IllegalArgumentException("unexpected element type: " + element);
        }

        return gsElement;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // neded for MPS (creation of hotfixed view)

    public org.graphstream.graph.Graph getGraphstreamGraph()
    {
        return _gsGraph;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // dynamic inserting and removing elements

    private void addVertex(Vertex vertex)
    {
        Node gsNode = _gsGraph.addNode(vertex.getId());

        labelChanged(vertex);
        labelPriorityChanged(vertex);
        styleChanged(vertex);

        addAttachments(vertex);
    }

    private void removeVertex(Vertex vertex)
    {
        for (Attachment a : vertex.getAttachments())
        {
            removeAttachment(a);
        }

        _gsGraph.removeNode(vertex.getId());
    }

    private void addEdge(Edge edge)
    {
        org.graphstream.graph.Edge gsEdge = _gsGraph.addEdge(edge.getId(), edge.getFrom().getId(), edge.getTo().getId(), true);

        labelChanged(edge);
        labelPriorityChanged(edge);
        styleChanged(edge);
    }

    private void removeEdge(Edge edge)
    {
        _gsGraph.removeEdge(edge.getId());
    }

    private void addAttachments(Vertex vertex)
    {
        for(Attachment attachment : vertex.getAttachments())
        {
            addAttachment(attachment);
        }
    }

    private void addAttachment(Attachment attachment)
    {
        Vertex vertex = attachment.getParent();

        Sprite sprite = _spriteManager.addSprite(attachment.getId());

        labelChanged(attachment);
        labelPriorityChanged(attachment);
        styleChanged(attachment);

        sprite.attachToNode(vertex.getId());

        // calculate rendering position for the attachment
        // -------------------------------------------------------------------------------------------------------------

        Size cellOffset = vertex.getCellOffset(attachment.getRowIndex(), attachment.getColIndex());
        Size cellSize = vertex.getCellSize(attachment.getRowIndex(), attachment.getColIndex(), attachment.getColSpan(), attachment.getRowSpan());
        Size containerSize = vertex.getAttachmentsSize();

        double x;
        switch (attachment.getHAlignment())
        {
            case Left:
                x = cellOffset.getWidth() + attachment.getSize().getWidth()/2 - containerSize.getWidth()/2;
                break;
            case Center:
                x = cellOffset.getWidth() + cellSize.getWidth()/2 - containerSize.getWidth()/2;
                break;
            case Right:
                x = cellOffset.getWidth() + cellSize.getWidth() - attachment.getSize().getWidth()/2 - containerSize.getWidth()/2;
                break;
            default:
                throw new RuntimeException("unexpected alignment: " + attachment.getHAlignment());
        }

        double y;
        switch (attachment.getVAlignment())
        {
            case Top:
                y = containerSize.getHeight()/2 - (cellOffset.getHeight() + attachment.getSize().getHeight()/2);
                break;
            case Middle:
                y = containerSize.getHeight()/2 - (cellOffset.getHeight() + cellSize.getHeight()/2);
                break;
            case Bottom:
                y = containerSize.getHeight()/2 - (cellOffset.getHeight() + cellSize.getHeight() - attachment.getSize().getHeight()/2);
                break;
            default:
                throw new RuntimeException("unexpected alignment: " + attachment.getVAlignment());
        }

        double distance = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        double radian = Math.atan2(y, x);
        double degree = 360 / (2 * Math.PI) * radian;

        sprite.setPosition(distance, 0.0, degree);
    }

    private void removeAttachment(Attachment attachment)
    {
        _spriteManager.removeSprite(attachment.getId());
    }

    private void setInitialAttributes()
    {
        _gsGraph.addAttribute("ui.quality");
        _gsGraph.addAttribute("ui.antialias");
    }

}
