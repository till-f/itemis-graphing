package de.itemis.graphing.example;

import de.itemis.graphing.model.*;
import de.itemis.graphing.model.style.BlockStyle;
import de.itemis.graphing.model.style.VertexStyle;

public class ExampleGraph {

    public static Graph getExampleGraph()
    {
        Graph graph = new Graph();
        fillExampleGraph(graph);
        return graph;
    }

    public static void fillExampleGraph(Graph graph)
    {
        VertexStyle operatorStyle = graph.getDefaultVertexStyle(IStyled.EStyle.Regular);
        operatorStyle.setShape(BlockStyle.EShape.Circle);
        operatorStyle.setFillColor("CCCCCC");
        VertexStyle operatorStyleClicked = graph.getDefaultVertexStyle(IStyled.EStyle.Clicked);
        operatorStyleClicked.setShape(BlockStyle.EShape.Circle);
        operatorStyleClicked.setFillColor("CCCCCC");
        VertexStyle operatorStyleSelected = graph.getDefaultVertexStyle(IStyled.EStyle.Selected);
        operatorStyleSelected.setShape(BlockStyle.EShape.Circle);
        operatorStyleSelected.setFillColor("CCCCCC");

        Vertex vertexA = graph.addVertex("A", 0.0, 0.0);
        vertexA.addAttachment("N1", 0.3, 0.2, 0, 0).setLabel("N1");
        vertexA.addAttachment("N2", 0.85, 0.2, 0, 1, 2, 1).setLabel("N2");
        vertexA.addAttachment("M1", 0.3, 0.2, 1, 0).setLabel("M1");
        vertexA.addAttachment("M2", 0.25, 0.15, 1, 1, Attachment.EHAlignment.Center, Attachment.EVAlignment.Middle).setLabel("M2");
        vertexA.addAttachment("M3", 0.3, 0.8, 1, 2, 1, 2).setLabel("M3");
        vertexA.addAttachment("S1", 0.3, 0.2, 2, 0).setLabel("S1");
        vertexA.addAttachment("S2", 0.3, 0.2, 2, 1).setLabel("S2");
        //vertexA.addAttachment("S3", 0.3, 0.2, 2, 2).setLabel("S3");

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
        vertex1.setStyle(IStyled.EStyle.Clicked, operatorStyleClicked);
        vertex1.setStyle(IStyled.EStyle.Selected, operatorStyleSelected);
        vertex1.setSelectable(false);

        Vertex vertex2 = graph.addVertex("&2", 0.5, 0.5);
        vertex2.setLabel("&");
        vertex2.setStyle(operatorStyle);
        vertex2.setStyle(IStyled.EStyle.Clicked, operatorStyleClicked);
        vertex2.setStyle(IStyled.EStyle.Selected, operatorStyleSelected);
        vertex2.setSelectable(false);

        Vertex vertex3 = graph.addVertex("&3", 0.5, 0.5);
        vertex3.setLabel("&");
        vertex3.setStyle(operatorStyle);
        vertex3.setStyle(IStyled.EStyle.Clicked, operatorStyleClicked);
        vertex3.setStyle(IStyled.EStyle.Selected, operatorStyleSelected);
        vertex3.setSelectable(false);

        graph.addEdge("A", "&1");
        graph.addEdge("&1", "B");
        graph.addEdge("&1", "C");
        Edge e = graph.addEdge("C", "&2");
        e.setLabel("edgelabel");
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
