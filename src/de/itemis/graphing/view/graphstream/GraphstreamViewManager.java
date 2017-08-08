package de.itemis.graphing.view.graphstream;

import de.itemis.graphing.model.*;
import de.itemis.graphing.view.AbstractViewManager;
import org.graphstream.graph.Element;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import javax.swing.JPanel;
import java.awt.Component;

public class GraphstreamViewManager extends AbstractViewManager implements IGraphListener
{
    private final org.graphstream.graph.Graph _gsGraph;
    private final StyleToGraphstreamCSS _styleConverter;
    private SpriteManager _spriteManager;                   // cannot be final due to bug in graphstream (reset of SpriteManager does not work properly)

    private View _view = null;
    private Viewer _viewer = null;
    private Layout _layout = null;

    public GraphstreamViewManager(Graph graph)
    {
        super(graph);

        _gsGraph = new SingleGraph("Graph");
        _spriteManager = new SpriteManager(_gsGraph);
        _styleConverter = new StyleToGraphstreamCSS();

        setInitialAttributes();
        setShowLabels(true);

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

        View view;
        if (viewID == null)
        {
            view = new DefaultView(viewer, "DefaultView", new org.graphstream.ui.j2dviewer.J2DGraphRenderer());
            viewer.addView(view);
        }
        else
        {
            view = viewer.getView(viewID);
        }

        view.setMouseManager(mouseManager);
        if (view instanceof Component)
        {
            ((Component)view).addMouseWheelListener(mouseManager);
        }

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
        if (!(_view instanceof JPanel))
            throw new RuntimeException("GraphstreamViewManager not configured properly, viewer must provide a JPanel as view instance.");

        return (JPanel)_view;
    }

    @Override
    public void zoomIn()
    {
        double currentZoom =  _view.getCamera().getViewPercent();
        _view.getCamera().setViewPercent(currentZoom - 0.1 * currentZoom);
    }

    @Override
    public void zoomOut()
    {
        double currentZoom = _view.getCamera().getViewPercent();
        _view.getCamera().setViewPercent(currentZoom + 0.1 * currentZoom);
    }

    @Override
    public void fitView()
    {
        _view.getCamera().resetView();
        _view.getCamera().setAutoFitView(true);
    }

    @Override
    public void setShowLabels(boolean show)
    {
        if (show)
            _gsGraph.addAttribute("ui.stylesheet", "node { text-visibility-mode: normal; } sprite { text-visibility-mode: normal; } edge { text-visibility-mode: normal; }");
        else
            _gsGraph.addAttribute("ui.stylesheet", "node { text-visibility-mode: hidden; } sprite { text-visibility-mode: hidden; } edge { text-visibility-mode: hidden; }");
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

        if (vertex.getLabel() != null)
            gsNode.setAttribute("ui.label", vertex.getLabel());

        String styleCSS = _styleConverter.getAtciveStyleCSS(vertex);
        gsNode.setAttribute("ui.style", styleCSS);

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

        if (edge.getLabel() != null)
            gsEdge.setAttribute("ui.label", edge.getLabel());

        String styleCSS = _styleConverter.getAtciveStyleCSS(edge);
        gsEdge.setAttribute("ui.style", styleCSS);
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

        if (attachment.getLabel() != null)
            sprite.setAttribute("ui.label", attachment.getLabel());

        String styleCSS = _styleConverter.getAtciveStyleCSS(attachment);
        sprite.setAttribute("ui.style", styleCSS);

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
        _gsGraph.addAttribute("ui.stylesheet", "edge { arrow-size: 0.1gu,0.04gu; }");
    }

}
