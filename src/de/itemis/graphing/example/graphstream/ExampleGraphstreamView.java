package de.itemis.graphing.example.graphstream;

import de.itemis.graphing.layout.HierarchicalLayoutJGraphX;
import de.itemis.graphing.layout.TreeLayoutAbego;
import de.itemis.graphing.model.Graph;
import de.itemis.graphing.example.ExampleGraph;
import de.itemis.graphing.view.graphstream.GraphstreamViewCreator;
import de.itemis.graphing.view.graphstream.layout.StaticLayout;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.swingViewer.DefaultView;

import javax.swing.*;

public class ExampleGraphstreamView
{
    public static String getExampleStyle()
    {
        return "" +
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
            "";
    }

    public static void main(String[] args)
    {
        Graph graph = ExampleGraph.getExampleGraph();

        GraphstreamViewCreator creator = new GraphstreamViewCreator(graph);
        creator.addStyleCode(getExampleStyle());

        Layout layout = new StaticLayout(graph, new HierarchicalLayoutJGraphX());
        //Layout layout = new StaticLayout(graph, new TreeLayoutAbego());
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
