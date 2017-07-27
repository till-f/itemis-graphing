package de.itemis.graphing.example.graphstream;

import de.itemis.graphing.example.ExampleViewListener;
import de.itemis.graphing.layout.HierarchicalLayoutJGraphX;
import de.itemis.graphing.model.Graph;
import de.itemis.graphing.example.ExampleGraph;
import de.itemis.graphing.view.graphstream.GraphstreamViewManager;
import de.itemis.graphing.view.graphstream.layout.StaticLayout;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.swingViewer.DefaultView;

import javax.swing.*;

public class ExampleGraphstreamView
{


    public static void main(String[] args)
    {
        Graph graph = ExampleGraph.getExampleGraph();

        GraphstreamViewManager viewManager = new GraphstreamViewManager(graph);
        viewManager.addStyleCode("node:selected { fill-color: yellow; } node:clicked { fill-color: red; } sprite:selected { fill-color: yellow; } sprite:clicked { fill-color: red; }");

        Layout layout = new StaticLayout(graph, new HierarchicalLayoutJGraphX());
        DefaultView view = viewManager.createView(layout);

        JFrame jframe = new JFrame("Graph Example");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setSize(600, 600);
        jframe.add(view);

        jframe.setVisible(true);

        //view.getCamera().setViewPercent(1.1);
    }
}
