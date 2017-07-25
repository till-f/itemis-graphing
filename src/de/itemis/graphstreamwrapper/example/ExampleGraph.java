package de.itemis.graphstreamwrapper.example;

import de.itemis.graphstreamwrapper.InternalGraph;
import de.itemis.graphstreamwrapper.InternalNode;
import de.itemis.graphstreamwrapper.graphstream.GraphstreamViewCreator;
import de.itemis.graphstreamwrapper.graphstream.layout.hierarchical.HierarchicalLayoutJGraphX;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.swingViewer.DefaultView;

import javax.swing.*;

public class ExampleGraph {

    public static InternalGraph getExampleGraph()
    {
        InternalGraph graph = new InternalGraph();

        InternalNode nodeA = graph.addNode("A", 1.0, 1.0 );
        nodeA.setLabel("Node A");
        nodeA.addAttachment("A1", 0.3, -14).setLabel("Sprite 1");
        nodeA.addAttachment("A2", 0.3, 14).setLabel("Sprite 2");

        InternalNode nodeB = graph.addNode("B", 1.0, 1.0 );
        nodeB.setLabel("Node B");

        InternalNode nodeC = graph.addNode("C", 1.0, 1.0 );
        nodeC.setLabel("Node C");

        InternalNode nodeD = graph.addNode("D", 1.0, 1.0 );
        nodeD.setLabel("Node D");

        InternalNode nodeE = graph.addNode("E", 1.0, 1.0 );
        nodeE.setLabel("Node E");

        InternalNode nodeF = graph.addNode("F", 1.0, 1.0 ); // 2nd root
        nodeF.setLabel("Node F");

        InternalNode nodeG = graph.addNode("G", 1.0, 1.0 );
        nodeG.setLabel("Node G");

        InternalNode nodeH = graph.addNode("H", 1.0, 1.0 );
        nodeH.setLabel("Node H");

        InternalNode node1 = graph.addNode("&1", 1.0, 1.0 );
        node1.setLabel("&");
        node1.setStyleId("operator");

        InternalNode node2 = graph.addNode("&2", 1.0, 1.0 );
        node2.setLabel("&");
        node2.setStyleId("operator");

        InternalNode node3 = graph.addNode("&3", 1.0, 1.0 );
        node3.setLabel("&");
        node3.setStyleId("operator");

        graph.addEdge("A", "&1");
        graph.addEdge("&1", "B");
        graph.addEdge("&1", "C");
        graph.addEdge("C", "&2");
        graph.addEdge("&2", "D");
        graph.addEdge("&2", "E");
        graph.addEdge("A", "&3");
        graph.addEdge("&3", "G");
        graph.addEdge("&3", "H");

        // cross edge
        graph.addEdge("E", "B");

        // from 2nd root
        graph.addEdge("F", "E");

        return graph;
    }

    public static void main(String[] args)
    {
        InternalGraph graph = getExampleGraph();

        GraphstreamViewCreator creator = new GraphstreamViewCreator(graph);
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
                "sprite:selected {" +
                    "stroke-mode: plain;" +
                    "stroke-color: black;" +
                "}" +
                "sprite:clicked {" +
                    "fill-color: black;" +
                "}" +
                "sprite.RAP {" +
                    "fill-color: red;" +
                "}" +
                "sprite.RAP:clicked {" +
                    "fill-color: magenta;" +
                "}" +
                "");

        Layout layout = new HierarchicalLayoutJGraphX(graph);
        DefaultView view = creator.createView(layout);

        JFrame jframe = new JFrame("Graph Example");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setSize(600, 600);
        jframe.add(view);

        // show JFrame
        jframe.setVisible(true);

        view.getCamera().setViewPercent(1.1);
    }

}
