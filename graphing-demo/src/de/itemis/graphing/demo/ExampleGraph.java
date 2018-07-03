package de.itemis.graphing.demo;

import de.itemis.graphing.model.*;
import de.itemis.graphing.model.style.BlockStyle;
import de.itemis.graphing.model.style.EdgeStyle;
import de.itemis.graphing.model.style.Style;
import de.itemis.graphing.model.style.VertexStyle;

public class ExampleGraph {

    public static Graph<Object> getExampleGraph()
    {
        Graph<Object> graph = new Graph<>();
        fillExampleGraph(graph);
        return graph;
    }

    private static void fillExampleGraph(Graph<Object> graph)
    {
        VertexStyle operatorStyle = VertexStyle.Default();
        operatorStyle.setShape(BlockStyle.EShape.Circle);
        operatorStyle.setFillColor("CCCCCC");
        VertexStyle operatorStyleClicked = VertexStyle.Empty();
        operatorStyleClicked.setLineThickness(Style.DEFAULT_LINE_THICKNESS_HL);

        Vertex<Object> vertexA = graph.addVertex("A", 0, 0);
        vertexA.addAttachment("N1", 15, 10, 0, 0).setLabel("N1");
        vertexA.addAttachment("N2", 15, 10, 0, 1, 2, 1).setLabel("N2");
        vertexA.addAttachment("M1", 15, 10, 1, 0).setLabel("M1");
        vertexA.addAttachment("M2", 12, 8, 1, 1, TabularAttachment.EHAlignment.Center, TabularAttachment.EVAlignment.Middle).setLabel("M2");
        vertexA.addAttachment("M3", 15, 40, 1, 2, 1, 2).setLabel("M3");
        vertexA.addAttachment("S1", 15, 10, 2, 0).setLabel("S1");
        vertexA.addAttachment("S2", 15, 10, 2, 1).setLabel("S2");
        vertexA.getAttachments().forEach(it -> it.setLabelPrio(GraphElement.ELabelPriority.Low));

        Vertex vertexB = graph.addVertex("B", 50, 30);
        vertexB.setLabel("vertex B");

        Vertex vertexC = graph.addVertex("C", 50, 30);
        vertexC.setLabel("vertex C");

        Vertex vertexD = graph.addVertex("D", 50, 30);
        vertexD.setLabel("vertex D");

        Vertex vertexE = graph.addVertex("E", 50, 30);
        vertexE.setLabel("vertex E");

        Vertex vertexF = graph.addVertex("F", 50, 30); // 2nd root
        vertexF.setLabel("vertex F");

        Vertex vertexG = graph.addVertex("G", 50, 30);
        vertexG.setLabel("vertex G");

        Vertex vertexH = graph.addVertex("H", 50, 30);
        vertexH.setLabel("vertex H");

        Vertex vertex1 = graph.addVertex("&1", 20, 20);
        vertex1.setLabel("&");
        vertex1.setStyleRegular(operatorStyle);
        vertex1.setStyleClicked(operatorStyleClicked);
        vertex1.setIsSelectable(false);

        Vertex vertex2 = graph.addVertex("&2", 20, 20);
        vertex2.setLabel("&");
        vertex2.setStyleRegular(operatorStyle);
        vertex2.setStyleClicked(operatorStyleClicked);
        vertex2.setIsSelectable(false);

        Vertex vertex3 = graph.addVertex("&3", 20, 20);
        vertex3.setLabel("&");
        vertex3.setStyleRegular(operatorStyle);
        vertex3.setStyleClicked(operatorStyleClicked);
        vertex3.setIsSelectable(false);

        graph.addEdge("A", "&1");
        graph.addEdge("&1", "B");
        graph.addEdge("&1", "C");
        Edge e = graph.addEdge("C", "&2");
        e.setLabel("edgelabel");
        EdgeStyle es = EdgeStyle.Default();
        es.setArrowOrientation(EdgeStyle.EArrowOrientation.Opposite);
        e.setStyleRegular(es);
        graph.addEdge("&2", "D");
        graph.addEdge("&2", "E");
        graph.addEdge("A", "&3");
        graph.addEdge("&3", "G");
        graph.addEdge("&3", "H");

        // cross edge
        graph.addEdge("E", "B");

        // from 2nd root
        graph.addEdge("F", "E");
    }

}
