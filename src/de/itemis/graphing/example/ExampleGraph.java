package de.itemis.graphing.example;

import de.itemis.graphing.model.*;
import de.itemis.graphing.model.style.BlockStyle;
import de.itemis.graphing.model.style.VertexStyle;

public class ExampleGraph {

    public static Graph getExampleGraph()
    {
        Graph graph = new Graph();

        VertexStyle operatorStyle = graph.getDefaultVertexStyle(IStyled.EStyle.Regular);
        operatorStyle.setShape(BlockStyle.EShape.Circle);
        operatorStyle.setFillColor("CCCCCC");
        VertexStyle operatorStyleClicked = graph.getDefaultVertexStyle(IStyled.EStyle.Clicked);
        operatorStyleClicked.setShape(BlockStyle.EShape.Circle);
        operatorStyleClicked.setFillColor("CCCCCC");
        VertexStyle operatorStyleSelected = graph.getDefaultVertexStyle(IStyled.EStyle.Selected);
        operatorStyleSelected.setShape(BlockStyle.EShape.Circle);
        operatorStyleSelected.setFillColor("CCCCCC");

        Vertex vertexA = graph.addVertex("A", 1.0, 0.5);
        vertexA.setLabel("vertex A");
        vertexA.addAttachment("N1", 0.3, 0.2,0.03, Attachment.ELocation.North).setLabel("N1");
        vertexA.addAttachment("N2", 0.3, 0.2,0.03, Attachment.ELocation.North).setLabel("N2");
        vertexA.addAttachment("N3", 0.3, 0.2,0.03, Attachment.ELocation.North).setLabel("N3");
        vertexA.addAttachment("E1", 0.3, 0.2, Attachment.ELocation.East).setLabel("E1");
        vertexA.addAttachment("E2", 0.3, 0.2, Attachment.ELocation.East).setLabel("E2");
        vertexA.addAttachment("S1", 0.3, 0.2, Attachment.ELocation.South).setLabel("S1");
        vertexA.addAttachment("S2", 0.3, 0.2, Attachment.ELocation.South).setLabel("S2");
        vertexA.addAttachment("W1", 0.3, 0.2, Attachment.ELocation.West).setLabel("W1");
        vertexA.addAttachment("W2", 0.3, 0.2, Attachment.ELocation.West).setLabel("W2");

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

        return graph;
    }

}
