package de.itemis.graphstreamwrapper.example;

import de.itemis.graphstreamwrapper.graphstream.GraphstreamViewCreator;
import de.itemis.graphstreamwrapper.graphstream.layout.hierarchical.HierarchicalLayoutJGraphX;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.swingViewer.DefaultView;

import javax.swing.*;

public class ExampleGraph {

    public static GraphstreamViewCreator getExampleGraphCreator()
    {
        GraphstreamViewCreator creator = new GraphstreamViewCreator();
        creator.addStyleCode("" +
                "node {" +
                    "shape: rounded-box;" +
                    "size: 0.4gu,0.4gu;" +
                    "fill-mode: plain;" +
                    "fill-color: #FFFFFF;" +
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
                    "shape: cubic-curve;" +
                    "fill-color: #808080;" +
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
        creator.addNode("B", "Node B" );
        creator.addNode("C", "Node C" );
        creator.addNode("D", "Node D" );
        creator.addNode("E", "Node E" );
        creator.addNode("F", "Node F" ); // 2nd root
        creator.addNode("G", "Node G" );
        creator.addNode("H", "Node H" );
        creator.addNode("&1", "&", "operator" );
        creator.addNode("&2", "&", "operator" );
        creator.addNode("&3", "&", "operator" );
        creator.addEdge("A", "&1", true);
        creator.addEdge("&1", "B", true);
        creator.addEdge("&1", "C", true);
        creator.addEdge("C", "&2", true);
        creator.addEdge("&2", "D", true);
        creator.addEdge("&2", "E", true);
        creator.addEdge("A", "&3", true);
        creator.addEdge("&3", "G", true);
        creator.addEdge("&3", "H", true);

        // cross edge
        creator.addEdge("E", "B", true);

        // from 2nd root
        creator.addEdge("F", "E", true);

        creator.addSpriteToNode("A1", "A", new Point3(0.3, 0, -14),"Sprite A1", null);
        creator.addSpriteToNode("A2", "A", new Point3(0.3, 0, 14),"Sprite A2", "RAP");

        return creator;
    }

    public static void main(String[] args)
    {
        // graphstream shipped layouts:
        // Force-based: SpringBox (default), Eades84Layout, LinLog
        // Hierarchical: HierarchicalLayoutJGraph (broken?)

        GraphstreamViewCreator creator = getExampleGraphCreator();
        DefaultView view = creator.createView(new HierarchicalLayoutJGraphX(creator.getGraph()));

        JFrame jframe = new JFrame("Graph Example");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setSize(600, 600);
        jframe.add(view);

        // show JFrame
        jframe.setVisible(true);

        view.getCamera().setViewPercent(1.1);
    }

}
