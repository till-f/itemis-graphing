package de.itemis.graphing.example;

import de.itemis.graphing.model.Attachment;
import de.itemis.graphing.model.Edge;
import de.itemis.graphing.model.Graph;
import de.itemis.graphing.model.Vertex;
import de.itemis.graphing.model.style.BlockStyle;
import de.itemis.graphing.model.style.DefaultVertexStyle;
import de.itemis.graphing.model.style.Style;

public class ExampleGraph {

    public static Graph getExampleGraph()
    {
        Graph graph = new Graph();

        BlockStyle operatorStyle = new DefaultVertexStyle();
        operatorStyle.setFillColor("CCCCCC");
        operatorStyle.setShape(BlockStyle.EShape.Circle);

        Vertex vertexA = graph.addVertex("A", 1.0, 0.5);
        vertexA.setLabel("vertex A");
        vertexA.addAttachment("North", 0.5, 0.2, Attachment.ELocation.East).setLabel("North");
        vertexA.addAttachment("East", 0.5, 0.2, Attachment.ELocation.East).setLabel("East");
        vertexA.addAttachment("South", 0.5, 0.2, Attachment.ELocation.West).setLabel("South");
        vertexA.addAttachment("West", 0.5, 0.2, Attachment.ELocation.West).setLabel("West");

        Vertex vertexB = graph.addVertex("B", 1.0, 0.5);
        vertexB.setLabel("vertex B");

        Vertex vertexC = graph.addVertex("C", 1.0, 0.5);
        vertexC.setLabel("vertex C");

        Vertex vertexD = graph.addVertex("D", 1.0, 0.5);
        vertexD.setLabel("vertex D");

        Vertex vertexE = graph.addVertex("E", 1.0, 0.5);
        vertexE.setLabel("vertex E");

        Vertex vertexF = graph.addVertex("F", 1.0, 0.5); // 2nd root
        vertexF.setLabel("vertex F");

        Vertex vertexG = graph.addVertex("G", 1.0, 0.5);
        vertexG.setLabel("vertex G");

        Vertex vertexH = graph.addVertex("H", 1.0, 0.5);
        vertexH.setLabel("vertex H");

        Vertex vertex1 = graph.addVertex("&1", 0.5, 0.5);
        vertex1.setLabel("&");
        vertex1.setStyle(operatorStyle);

        Vertex vertex2 = graph.addVertex("&2", 0.5, 0.5);
        vertex2.setLabel("&");
        vertex2.setStyle(operatorStyle);

        Vertex vertex3 = graph.addVertex("&3", 0.5, 0.5);
        vertex3.setLabel("&");
        vertex3.setStyle(operatorStyle);

        Edge e = graph.addEdge("A", "&1");
        e.setLabel("refers");

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

}
