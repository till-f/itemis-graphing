package de.itemis.graphstreamwrapper.graphstream;

import de.itemis.graphstreamwrapper.Attachment;
import de.itemis.graphstreamwrapper.Edge;
import de.itemis.graphstreamwrapper.Graph;
import de.itemis.graphstreamwrapper.Vertex;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.Sink;
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

public class GraphstreamViewCreator {

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

    public GraphstreamViewCreator(Graph graph)
    {
        _gsGraph = new SingleGraph("Graph");
        _gsGraph.addAttribute("ui.quality");
        _gsGraph.addAttribute("ui.antialias");
        _spriteManager = new SpriteManager(_gsGraph);

        for(Vertex n : graph.getVertexes())
        {
            addVertex(n);
            for(Attachment a : n.getAttachments())
            {
                addAttachment(a, n, null);
            }
        }

        for(Edge e : graph.getEdges())
        {
            addEdge(e);
            for(Attachment a : e.getAttachments())
            {
                addAttachment(a, null, e);
            }
        }
    }

    public void addStyleCode(String styleCode)
    {
        if (styleCode == null || styleCode.isEmpty())
            throw new IllegalArgumentException("styleCode is mandatory");

        _gsGraph.addAttribute("ui.stylesheet", styleCode);
    }

    private void addVertex(Vertex vertex)
    {
        Node gsNode = _gsGraph.addNode(vertex.getId());

        if (vertex.getLabel() != null)
            gsNode.setAttribute("ui.label", vertex.getLabel());

        if (vertex.getStyleId() != null)
            gsNode.setAttribute("ui.class", vertex.getStyleId());
    }

    private void addEdge(Edge edge)
    {
        org.graphstream.graph.Edge gsEdge = _gsGraph.addEdge(edge.getId(), edge.getFrom().getId(), edge.getTo().getId(), true);

        if (edge.getLabel() != null)
            gsEdge.setAttribute("ui.label", edge.getLabel());

        if (edge.getStyleId() != null)
            gsEdge.setAttribute("ui.class", edge.getStyleId());
    }

    private void addAttachment(Attachment attachment, Vertex toVertex, Edge toEdge)
    {
        Sprite sprite = _spriteManager.addSprite(attachment.getId());

        if (attachment.getLabel() != null)
            sprite.setAttribute("ui.label", attachment.getLabel());

        if (attachment.getStyleId() != null)
            sprite.setAttribute("ui.class", attachment.getStyleId());

        if (toVertex != null)
            sprite.attachToNode(toVertex.getId());
        else if (toEdge != null)
            sprite.attachToEdge(toEdge.getId());

        sprite.setPosition(attachment.getRadius(), 0.0, attachment.getDegree());
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
            viewer = new Viewer(_gsGraph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
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

}
