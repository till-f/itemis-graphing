package de.itemis.graphstreamwrapper.example;

import de.itemis.graphstreamwrapper.Graph;
import de.itemis.graphstreamwrapper.Vertex;
import de.itemis.graphstreamwrapper.graphstream.GraphstreamViewCreator;
import de.itemis.graphstreamwrapper.graphstream.layout.hierarchical.HierarchicalLayoutJGraphX;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.swingViewer.DefaultView;

import javax.swing.*;

public class ExampleGraph {

    public static Graph getExampleGraph()
    {
        Graph graph = new Graph();

        Vertex vertexA = graph.addVertex("A", 1.0, 1.0 );
        vertexA.setLabel("vertex A");
        vertexA.addAttachment("A1", 0.3, -14).setLabel("Sprite 1");
        vertexA.addAttachment("A2", 0.3, 14).setLabel("Sprite 2");

        Vertex vertexB = graph.addVertex("B", 1.0, 1.0 );
        vertexB.setLabel("vertex B");

        Vertex vertexC = graph.addVertex("C", 1.0, 1.0 );
        vertexC.setLabel("vertex C");

        Vertex vertexD = graph.addVertex("D", 1.0, 1.0 );
        vertexD.setLabel("vertex D");

        Vertex vertexE = graph.addVertex("E", 1.0, 1.0 );
        vertexE.setLabel("vertex E");

        Vertex vertexF = graph.addVertex("F", 1.0, 1.0 ); // 2nd root
        vertexF.setLabel("vertex F");

        Vertex vertexG = graph.addVertex("G", 1.0, 1.0 );
        vertexG.setLabel("vertex G");

        Vertex vertexH = graph.addVertex("H", 1.0, 1.0 );
        vertexH.setLabel("vertex H");

        Vertex vertex1 = graph.addVertex("&1", 1.0, 1.0 );
        vertex1.setLabel("&");
        vertex1.setStyleId("operator");

        Vertex vertex2 = graph.addVertex("&2", 1.0, 1.0 );
        vertex2.setLabel("&");
        vertex2.setStyleId("operator");

        Vertex vertex3 = graph.addVertex("&3", 1.0, 1.0 );
        vertex3.setLabel("&");
        vertex3.setStyleId("operator");

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
        Graph graph = getExampleGraph();

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
