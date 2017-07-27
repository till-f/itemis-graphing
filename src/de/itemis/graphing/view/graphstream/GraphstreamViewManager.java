package de.itemis.graphing.view.graphstream;

import de.itemis.graphing.model.*;
import org.graphstream.graph.Element;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.AttributeSink;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import org.graphstream.ui.view.util.MouseManager;

import java.awt.Component;
import java.awt.event.MouseWheelListener;

public class GraphstreamViewManager implements IGraphListener
{
    public class Flag {

        private boolean _isSet = false;

        public void set()
        {
            _isSet = true;
        }

        public boolean isSet()
        {
            return _isSet;
        }
    }

    private final org.graphstream.graph.Graph _gsGraph;
    private final SpriteManager _spriteManager;
    private final StyleToGraphstreamCSS _styleConverter;

    public GraphstreamViewManager(Graph graph)
    {
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

    public org.graphstream.graph.Graph getGraphstreamGraph()
    {
        return _gsGraph;
    }

    public void addStyleCode(String styleCode)
    {
        if (styleCode == null || styleCode.isEmpty())
            throw new IllegalArgumentException("styleCode is mandatory");

        _gsGraph.addAttribute("ui.stylesheet", styleCode);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // view creation

    public DefaultView createView(Layout layout)
    {
        return (DefaultView) createView(layout, null, null, null, null);
    }

    public DefaultView createView(Layout layout, IViewListener viewListener)
    {
        return (DefaultView) createView(layout, viewListener, null, null, null);
    }

    public DefaultView createView(Layout layout, IViewListener viewListener, MouseManager mouseManager)
    {
        return (DefaultView) createView(layout, viewListener, mouseManager, null, null);
    }

    public View createView(Layout layout, IViewListener viewListener, MouseManager mouseManager, Viewer viewer, String viewID)
    {
        if (viewer == null)
        {
            viewer = new Viewer(_gsGraph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        }

        if (viewListener != null)
        {
            final AttributeSink sink = new ViewListenerSink(_gsGraph, viewListener);

            final Flag flag = new Flag();
            final ViewerPipe pipeFromViewer = viewer.newViewerPipe();

            pipeFromViewer.addAttributeSink(sink);

            Runnable runnable = new Runnable()
            {
                @Override
                public void run() {
                    try {
                        while (true)
                        {
                            if (flag.isSet())
                                return;

                            pipeFromViewer.blockingPump();
                        }
                    } catch (InterruptedException e) {
                        System.out.println("NOTE: graphstream event handler INTERRUPTED");
                    }
                    System.out.println("NOTE: graphstream event handler EXIT");
                }
            };
            final Thread eventListener = new Thread(null, runnable,"graphstream event handler");
            eventListener.start();

            pipeFromViewer.addViewerListener(new ViewerListener()
            {
                @Override
                public void viewClosed(String viewName)
                {
                    flag.set();
                    eventListener.interrupt();
                }

                @Override
                public void buttonPushed(String id) { }

                @Override
                public void buttonReleased(String id) { }
            });
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

        if (mouseManager == null)
        {
            mouseManager = new GraphstreamMouseManager(false, false);
        }
        view.setMouseManager(mouseManager);
        if (mouseManager instanceof MouseWheelListener && view instanceof Component)
        {
            ((Component)view).addMouseWheelListener((MouseWheelListener) mouseManager);
        }

        if (layout != null)
        {
            viewer.enableAutoLayout(layout);
        }
        else
        {
            viewer.enableAutoLayout();
        }

        return view;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // dynamic inserting and removing elements

    private void addVertex(Vertex vertex)
    {
        Node gsNode = _gsGraph.addNode(vertex.getId());

        if (vertex.getLabel() != null)
            gsNode.setAttribute("ui.label", vertex.getLabel());

        String styleCSS = _styleConverter.getStyleString(vertex);
        gsNode.setAttribute("ui.style", styleCSS);

        gsNode.setAttribute("itemis.element", vertex);

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

        String styleCSS = _styleConverter.getStyleString(edge);
        gsEdge.setAttribute("ui.style", styleCSS);

        gsEdge.setAttribute("itemis.element", edge);
    }

    private void removeEdge(Edge edge)
    {
        _gsGraph.removeEdge(edge.getId());
    }

    private void addAttachments(Vertex vertex)
    {
        double[] consumed = new double[4];
        for(Attachment attachment : vertex.getAttachments())
        {
            int ordinal = attachment.getLocation().ordinal();
            consumed[ordinal] += addAttachment(attachment, consumed[ordinal]);
        }
    }

    private double addAttachment(Attachment attachment, final double alreadyConsumedSpace)
    {
        Vertex vertex = attachment.getParent();

        Sprite sprite = _spriteManager.addSprite(attachment.getId());

        if (attachment.getLabel() != null)
            sprite.setAttribute("ui.label", attachment.getLabel());

        String styleCSS = _styleConverter.getStyleString(attachment);
        sprite.setAttribute("ui.style", styleCSS);

        sprite.setAttribute("itemis.element", attachment);

        sprite.attachToNode(vertex.getId());

        // calculate rendering position for the attachment
        double availableSpace = vertex.getAttachmentsSpace(attachment.getLocation());
        double neededSpace;
        if (attachment.getLocation() == Attachment.ELocation.South || attachment.getLocation() == Attachment.ELocation.North)
            neededSpace = attachment.getBaseWidth();
        else
            neededSpace = attachment.getBaseHeight();

        final double spaceOffset = alreadyConsumedSpace - availableSpace/2 + neededSpace/2;
        final double x;
        final double y;
        switch (attachment.getLocation())
        {
            case North:
                x = spaceOffset;
                y = 0.5 * (vertex.getBaseHeight() + attachment.getBaseHeight());
                break;
            case East:
                x = 0.5 * (vertex.getBaseWidth() + attachment.getBaseWidth());
                y = -spaceOffset;
                break;
            case South:
                x = spaceOffset;
                y = -0.5 * (vertex.getBaseHeight() + attachment.getBaseHeight());
                break;
            case West:
                x = -0.5 * (vertex.getBaseWidth() + attachment.getBaseWidth());
                y = -spaceOffset;
                break;
            default:
                throw new IllegalArgumentException("invalid location: " + attachment.getLocation());
        }

        double distance = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        double radian = Math.atan2(y, x);
        double degree = 360 / (2 * Math.PI) * radian;

        sprite.setPosition(distance, 0.0, degree);

        return neededSpace;
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

        String styleCSS = _styleConverter.getStyleString(element);
        gsElement.setAttribute("ui.style", styleCSS);
    }

}
