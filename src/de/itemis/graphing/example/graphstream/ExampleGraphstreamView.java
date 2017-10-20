package de.itemis.graphing.example.graphstream;

import de.itemis.graphing.example.ExampleGraph;
import de.itemis.graphing.layout.TreeLayoutAbego;
import de.itemis.graphing.util.Screen;
import de.itemis.graphing.interactionhandlers.HighlightEdges;
import de.itemis.graphing.interactionhandlers.ShowTooltip;
import de.itemis.graphing.layout.HierarchicalLayoutJGraphX;
import de.itemis.graphing.model.Graph;
import de.itemis.graphing.view.graphstream.GraphstreamViewManager;
import de.itemis.graphing.view.graphstream.layout.StaticLayout;
import org.graphstream.ui.layout.Layout;

import javax.swing.*;

public class ExampleGraphstreamView
{
   public static void main(String[] args)
    {
        System.out.println("Scaling Factor: " + Screen.getScalingFactor());

        double scale = Screen.getScalingFactor();

        Graph graph = ExampleGraph.getExampleGraph();

        GraphstreamViewManager viewManager = new GraphstreamViewManager(graph);
        Layout layout = new StaticLayout(graph, new HierarchicalLayoutJGraphX());
        //Layout layout = new StaticLayout(graph, new TreeLayoutAbego());
        viewManager.registerHandler(new HighlightEdges());
        viewManager.registerHandler(new ShowTooltip());
        viewManager.configure(layout);

        JPanel view = viewManager.getView();
        JFrame jframe = new JFrame("Graph Example");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setSize((int)(600 * scale), (int)(600 * scale));
        jframe.add(view);

        jframe.setVisible(true);
    }
}
