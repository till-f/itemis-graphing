package de.itemis.graphing.example.graphstream;

import de.itemis.graphing.example.ExampleGraph;
import de.itemis.graphing.example.handlers.ShowButtonsExampleHoverHandler;
import de.itemis.graphing.example.handlers.ShowLabelTooltipHoverHandler;
import de.itemis.graphing.layout.HierarchicalLayoutJGraphX;
import de.itemis.graphing.model.Graph;
import de.itemis.graphing.util.Screen;
import de.itemis.graphing.view.IViewManager;
import de.itemis.graphing.view.graphstream.GraphstreamViewManager;
import de.itemis.graphing.view.graphstream.layout.StaticLayout;
import de.itemis.graphing.view.interaction.HighlightLinksClickHandler;
import de.itemis.graphing.view.jgraphx.JGraphXViewManager;
import org.graphstream.ui.layout.Layout;

import javax.swing.*;
import java.awt.*;

public class ExampleView
{
    private enum EAdapter { Graphstream, JGraphT }
    private static final EAdapter ADAPTER = EAdapter.JGraphT;

    public static void main(String[] args)
    {
        Graph graph = ExampleGraph.getExampleGraph();

        IViewManager viewManager;

        switch (ADAPTER)
        {
            case Graphstream:
                viewManager = new GraphstreamViewManager(graph);
                Layout layout = new StaticLayout(graph, new HierarchicalLayoutJGraphX());
                //Layout layout = new StaticLayout(graph, new TreeLayoutAbego());
                ((GraphstreamViewManager)viewManager).configure(layout);
                break;
            case JGraphT:
                viewManager = new JGraphXViewManager(graph);
                break;
            default:
                throw new RuntimeException("invalid adapter");
        }

        viewManager.registerHandler(new HighlightLinksClickHandler());
        viewManager.registerHandler(new ShowLabelTooltipHoverHandler());
        viewManager.registerHandler(new ShowButtonsExampleHoverHandler());

        Component view = viewManager.getView();
        JFrame jframe = new JFrame("Graph Example");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        double scale = Screen.getScalingFactor();
        jframe.setSize((int)(600 * scale), (int)(600 * scale));
        jframe.add(view);

        jframe.setVisible(true);
    }
}
