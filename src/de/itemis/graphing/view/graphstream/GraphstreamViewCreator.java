package de.itemis.graphing.view.graphstream;

import de.itemis.graphing.model.*;
import de.itemis.graphing.model.style.AttachmentStyle;
import de.itemis.graphing.model.style.Style;
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

import java.awt.Component;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;

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
    private final StyleToGraphstreamCSS _styleConverter;

    public GraphstreamViewCreator(Graph graph)
    {
        _gsGraph = new SingleGraph("Graph");
        _gsGraph.addAttribute("ui.quality");
        _gsGraph.addAttribute("ui.antialias");
        _spriteManager = new SpriteManager(_gsGraph);
        _styleConverter = new StyleToGraphstreamCSS();

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

    private void addVertex(Vertex vertex)
    {
        Node gsNode = _gsGraph.addNode(vertex.getId());

        if (vertex.getLabel() != null)
            gsNode.setAttribute("ui.label", vertex.getLabel());

        String styleCSS = _styleConverter.getStyleString(vertex);
        gsNode.setAttribute("ui.style", styleCSS);
    }

    private void addEdge(Edge edge)
    {
        org.graphstream.graph.Edge gsEdge = _gsGraph.addEdge(edge.getId(), edge.getFrom().getId(), edge.getTo().getId(), true);

        if (edge.getLabel() != null)
            gsEdge.setAttribute("ui.label", edge.getLabel());

        String styleCSS = _styleConverter.getStyleString(edge);
        gsEdge.setAttribute("ui.style", styleCSS);
    }

    private void addAttachment(Attachment attachment, Vertex toVertex, Edge toEdge)
    {
        Sprite sprite = _spriteManager.addSprite(attachment.getId());

        if (attachment.getLabel() != null)
            sprite.setAttribute("ui.label", attachment.getLabel());

        String styleCSS = _styleConverter.getStyleString(attachment);
        sprite.setAttribute("ui.style", styleCSS);

        if (toVertex != null)
        {
            sprite.attachToNode(toVertex.getId());
        }
        else if (toEdge != null)
        {
            sprite.attachToEdge(toEdge.getId());
        }

        Style style = attachment.retrieveStyle();
        double pos1 = attachment.getWidth() / 2;
        double pos2 = 0.0;
        if (style instanceof AttachmentStyle)
        {
            AttachmentStyle attachmentStyle = (AttachmentStyle) style;
            pos1 = attachmentStyle.getPos1() == null ? pos1 : attachmentStyle.getPos1();
            pos2 = attachmentStyle.getPos1() == null ? pos2 : attachmentStyle.getPos1();
        }
        sprite.setPosition(pos1, 0.0, pos2);
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
