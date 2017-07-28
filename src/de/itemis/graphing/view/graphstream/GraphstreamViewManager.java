package de.itemis.graphing.view.graphstream;

import de.itemis.graphing.model.*;
import de.itemis.graphing.model.IInteractionListener;
import de.itemis.graphing.listeners.HighlightInteractions;
import de.itemis.graphing.view.IViewManager;
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
import java.util.List;

public class GraphstreamViewManager implements IGraphListener, IViewManager
{
    private final Graph _graph;

    private final org.graphstream.graph.Graph _gsGraph;
    private final SpriteManager _spriteManager;
    private final StyleToGraphstreamCSS _styleConverter;

    private View _view = null;

    public GraphstreamViewManager(Graph graph)
    {
        _graph = graph;
        _gsGraph = new SingleGraph("Graph");
        _gsGraph.addAttribute("ui.quality");
        _gsGraph.addAttribute("ui.antialias");
        _spriteManager = new SpriteManager(_gsGraph);
        _styleConverter = new StyleToGraphstreamCSS();

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
        configure(layout, null, null, null);
    }

    public void configure(Layout layout, List<IInteractionListener> interactionListeners)
    {
        configure(layout, interactionListeners, null, null);
    }

    public void configure(Layout layout, List<IInteractionListener> interactionListeners, Viewer viewer, String viewID)
    {
        NotifyingMouseManager mouseManager = new NotifyingMouseManager(this);
        mouseManager.registerInteractionListener(new HighlightInteractions());

        if (interactionListeners != null)
        {
            for(IInteractionListener interactionListener : interactionListeners)
            {
                mouseManager.registerInteractionListener(interactionListener);
            }
        }

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

        if (layout != null)
        {
            viewer.enableAutoLayout(layout);
        }
        else
        {
            viewer.enableAutoLayout();
        }

        _view = view;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // IViewManager

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
        _view.getCamera().setViewPercent(1.1);
    }

    @Override
    public JPanel getView()
    {
        if (!(_view instanceof JPanel))
            throw new RuntimeException("GraphstreamViewManager not configured properly.");

        return (JPanel)_view;
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
        double[] consumed = new double[4];
        Attachment previousAttachment = null;
        for(Attachment attachment : vertex.getAttachments())
        {
            int ordinal = attachment.getLocation().ordinal();
            consumed[ordinal] += addAttachment(attachment, consumed[ordinal], previousAttachment);
            previousAttachment = attachment;
        }
    }

    private double addAttachment(Attachment attachment, double alreadyConsumedSpace, Attachment previousAttachment)
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

        double availableSpace = vertex.getAttachmentsSpace(attachment.getLocation());
        double neededSpace;
        if (attachment.getLocation() == Attachment.ELocation.South || attachment.getLocation() == Attachment.ELocation.North)
            neededSpace = attachment.getOuterSize().getWidth();
        else
            neededSpace = attachment.getOuterSize().getHeight();

        double paddingCorrection = 0.0;
        if (previousAttachment != null)
            paddingCorrection = Math.min(attachment.getPadding(), previousAttachment.getPadding());

        final double spaceOffset = alreadyConsumedSpace - availableSpace/2 + neededSpace/2 - paddingCorrection;
        final double x;
        final double y;
        switch (attachment.getLocation())
        {
            case North:
                x = spaceOffset;
                y = 0.5 * (vertex.getInnerSize().getHeight() + attachment.getOuterSize().getHeight());
                break;
            case East:
                x = 0.5 * (vertex.getInnerSize().getWidth() + attachment.getOuterSize().getWidth());
                y = -spaceOffset;
                break;
            case South:
                x = spaceOffset;
                y = -0.5 * (vertex.getInnerSize().getHeight() + attachment.getOuterSize().getHeight());
                break;
            case West:
                x = -0.5 * (vertex.getInnerSize().getWidth() + attachment.getOuterSize().getWidth());
                y = -spaceOffset;
                break;
            default:
                throw new IllegalArgumentException("invalid location: " + attachment.getLocation());
        }

        double distance = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        double radian = Math.atan2(y, x);
        double degree = 360 / (2 * Math.PI) * radian;

        sprite.setPosition(distance, 0.0, degree);

        return neededSpace - paddingCorrection;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // GraphListener

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
        removeVertex(attachment.getParent());
        addVertex(attachment.getParent());
    }

    @Override
    public void attachmentRemoved(Attachment attachment)
    {
        removeVertex(attachment.getParent());
        addVertex(attachment.getParent());
    }

    @Override
    public void styleChanged(BaseGraphElement element)
    {
        Element gsElement = getGraphstreamElement(element);
        String styleCSS = _styleConverter.getAtciveStyleCSS(element);
        gsElement.setAttribute("ui.style", styleCSS);
    }

    private Element getGraphstreamElement(BaseGraphElement element)
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
    // nice little helpers

    public BaseGraphElement getBaseGraphElement(String id)
    {
        return _graph.getElement(id);
    }

    public org.graphstream.graph.Graph getGraphstreamGraph()
    {
        return _gsGraph;
    }

}
