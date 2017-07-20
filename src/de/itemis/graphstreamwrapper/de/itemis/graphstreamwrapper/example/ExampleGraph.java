package de.itemis.graphstreamwrapper.de.itemis.graphstreamwrapper.example;

import de.itemis.graphstreamwrapper.GraphCreator;
import de.itemis.graphstreamwrapper.TreeLayout;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.view.View;

import javax.swing.*;

public class ExampleGraph {

    public static GraphCreator getExampleGraphCreator()
    {
        GraphCreator creator = new GraphCreator();
        creator.addStyleCode("" +
                "node {" +
                    "shape: rounded-box;" +
                    "size: 0.4gu,0.4gu;" +
                    "fill-mode: plain;" +
                    "fill-color: white;" +
                    "stroke-mode: plain;" +
                    "stroke-color: black;" +
                    "text-size: 12;" +
                "}" +
                "node.operator {" +
                    "shape: circle;" +
                    "size: 0.2gu,0.2gu;" +
                    "fill-color: gray;" +
                "}" +
                "node:selected {" +
                    "fill-color: yellow;" +
                "}" +
                "node:clicked {" +
                    "fill-color: red;" +
                "}" +
                "edge {" +
                    "fill-color: black;" +
                "}" +
                "sprite {" +
                    "size: 0.05gu, 0.05gu;" +
                    "fill-color: gray;" +
                    "text-alignment: at-right;" +
                    "text-padding: 0.01gu;" +
                    "text-offset: 0.05gu,0gu;" +
                    "text-size: 12;" +
                    "text-background-mode: rounded-box;" +
                    "text-background-color: #FFFFFF;" +
                "}" +
                "sprite.RAP {" +
                    "fill-color: red;" +
                "}" +
                "sprite:selected {" +
                    "stroke-mode: plain;" +
                    "stroke-color: black;" +
                "}" +
                "sprite:clicked {" +
                    "fill-color: black;" +
                "}" +
                "sprite.RAP:clicked {" +
                    "fill-color: magenta;" +
                "}" +
                "");

        creator.addNode("A", "Node A" );

        creator.addNode("&1", "&", "operator" );
        creator.addNode("B", "Node B" );
        creator.addNode("C", "Node C" );
        creator.addNode("&2", "&", "operator" );
        creator.addNode("D", "Node D" );
        creator.addNode("E", "Node E" );
        creator.addEdge("A&1", "A", "&1", true);
        creator.addEdge("&1B", "&1", "B", true);
        creator.addEdge("&1C", "&1", "C", true);
        creator.addEdge("C&2", "C", "&2", true);
        creator.addEdge("&2D", "&2", "D", true);
        creator.addEdge("&2E", "&2", "E", true);
        creator.addNode("&3", "&", "operator" );
        creator.addEdge("A&3", "A", "&3", true);
        creator.addNode("G", "G" );
        creator.addNode("H", "H" );
        creator.addEdge("&3G", "&3", "G", true);
        creator.addEdge("&3H", "&3", "H", true);

        // cross edge
        creator.addEdge("EB", "E", "B", true);

        // dangling node
        creator.addNode("F", "Node F" );
        creator.addEdge("FE", "F", "E", true);

        creator.addSpriteToNode("A1", "A", new Point3(0.3, 0, -14),"Sprite A1", null);
        creator.addSpriteToNode("A2", "A", new Point3(0.3, 0, 14),"Sprite A2", "RAP");

        return creator;
    }

    public static void main(String[] args)
    {
        // graphstream shipped layouts:
        // Force-based: SpringBox (default), Eades84Layout, LinLog
        // Hierarchical: HierarchicalLayout (broken?)

        GraphCreator creator = getExampleGraphCreator();
        DefaultView view = creator.createView(new TreeLayout(), new ExampleSink());

        JFrame jframe = new JFrame("Graph Example");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setSize(600, 600);
        jframe.add(view);

        // show JFrame
        jframe.setVisible(true);

        view.getCamera().setViewPercent(1.1);
    }

}
