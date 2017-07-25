package de.itemis.graphstreamwrapper;

import de.itemis.graphstreamwrapper.graphstream.layout.PanZoomMouseManager;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.Sink;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import org.graphstream.ui.view.util.MouseManager;

import java.awt.*;
import java.awt.event.MouseWheelListener;

public class GraphCreator {

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

    private final Graph _graph;
    private final SpriteManager _spriteManager;

    public GraphCreator()
    {
        _graph = new SingleGraph("Graph");
        _graph.addAttribute("ui.quality");
        _graph.addAttribute("ui.antialias");
        _spriteManager = new SpriteManager(_graph);
    }

    public GraphCreator(Graph graph)
    {
        _graph = graph;
        _spriteManager = new SpriteManager(_graph);
    }

    public Graph getGraph()
    {
        return _graph;
    }

    public SpriteManager getSpriteManager()
    {
        return _spriteManager;
    }

    public void addStyleCode(String styleCode)
    {
        if (styleCode == null || styleCode.isEmpty())
            throw new IllegalArgumentException("styleCode is mandatory");

        _graph.addAttribute("ui.stylesheet", styleCode);
    }

    public Node addNode(String nodeID)
    {
        return addNode(nodeID, null, null);
    }

    public Node addNode(String nodeID, String label)
    {
        return addNode(nodeID, label, null);
    }

    public Node addNode(String nodeID, String label, String styleClass)
    {
        return addNode(nodeID, label, null, 0.5, 0.5);
    }

    public Node addNode(String nodeID, String label, String styleClass, double width, double height)
    {
        if (nodeID == null || nodeID.isEmpty())
            throw new IllegalArgumentException("nodeID is mandatory");

        Node node = _graph.addNode(nodeID);

        if (label != null)
            node.setAttribute("ui.label", label);

        if (styleClass != null)
            node.setAttribute("ui.class", styleClass);

        node.setAttribute("ui.width", width);
        node.setAttribute("ui.height", height);

        return node;
    }

    public Edge addEdge(String fromID, String toID)
    {
        return addEdge(fromID, toID, null, null, false, null);
    }

    public Edge addEdge(String fromID, String toID, boolean directed)
    {
        return addEdge(fromID, toID, null, null, directed, null);
    }

    public Edge addEdge(String fromID, String toID, String label)
    {
        return addEdge(fromID, toID, label, null, false, null);
    }

    public Edge addEdge(String fromID, String toID, String label, boolean directed)
    {
        return addEdge(fromID, toID, label, null, directed, null);
    }

    public Edge addEdge(String fromID, String toID, String label, String styleClass, boolean directed)
    {
        return addEdge(fromID, toID, label, null, directed, null);
    }

    public Edge addEdge(String fromID, String toID, String label, String styleClass, boolean directed, String edgeID)
    {
        if (fromID == null || fromID.isEmpty() || toID == null || toID.isEmpty())
            throw new IllegalArgumentException("edgeID, fromID and toID are mandatory");

        if (edgeID == null || edgeID.isEmpty())
            edgeID = fromID + "." + toID;

        Edge edge = _graph.addEdge(edgeID, fromID, toID, directed);
        
        if (label != null)
            edge.setAttribute("ui.label", label);

        if (styleClass != null)
            edge.setAttribute("ui.class", styleClass);

        return edge;
    }

    public Sprite addSpriteToNode(String spriteID, String nodeID, Point3 position, String label, String styleClass)
    {
        if (nodeID == null || nodeID.isEmpty())
            throw new IllegalArgumentException("nodeID is mandatory");

        return addSprite_internal(spriteID, position, label, styleClass, nodeID, true, false);
    }

    public Sprite addSpriteToEdge(String spriteID, String edgeID, Point3 position, String label, String styleClass)
    {
        if (edgeID == null || edgeID.isEmpty())
            throw new IllegalArgumentException("edgeID is mandatory");

        return addSprite_internal(spriteID, position, label, styleClass, edgeID, false, true);
    }

    private Sprite addSprite_internal(String spriteID, Point3 position, String label, String styleClass, String elementID, boolean toNode, boolean toEdge)
    {
        if (spriteID == null || spriteID.isEmpty() || position == null)
            throw new IllegalArgumentException("spriteID and position are mandatory");

        Sprite sprite = _spriteManager.addSprite(spriteID);

        if (label != null)
            sprite.setAttribute("ui.label", label);

        if (styleClass != null)
            sprite.setAttribute("ui.class", styleClass);

        if (toNode)
            sprite.attachToNode(elementID);
        else if (toEdge)
            sprite.attachToEdge(elementID);

        sprite.setPosition(position.x, position.y, position.z);

        return sprite;
    }

    public DefaultView createView(Layout layout)
    {
        return (DefaultView) createView(layout, null, null, null, null);
    }

    public DefaultView createView(Layout layout, Sink sink)
    {
        return (DefaultView) createView(layout, sink, null, null, null);
    }

    public DefaultView createView(Layout layout, Sink sink, MouseManager mouseManager)
    {
        return (DefaultView) createView(layout, sink, mouseManager, null, null);
    }

    public View createView(Layout layout, Sink sink, MouseManager mouseManager, Viewer viewer, String viewID)
    {
        if (viewer == null)
        {
            viewer = new Viewer(_graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        }

        // sink is used to handle events
        if (sink != null)
        {
            final Flag flag = new Flag();
            final ViewerPipe pipeFromViewer = viewer.newViewerPipe();

            pipeFromViewer.addSink(sink);

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
            mouseManager = new PanZoomMouseManager(false, false);
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

}
