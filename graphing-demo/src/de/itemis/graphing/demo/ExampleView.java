package de.itemis.graphing.demo;

import de.itemis.graphing.demo.handlers.ShowButtonsExampleHoverHandler;
import de.itemis.graphing.demo.handlers.ShowLabelTooltipHoverHandler;
import de.itemis.graphing.model.Graph;
import de.itemis.graphing.util.Screen;
import de.itemis.graphing.view.IViewManager;
import de.itemis.graphing.graphstream.GraphstreamViewManager;
import de.itemis.graphing.view.interaction.HighlightLinksClickHandler;
import de.itemis.graphing.jgraphx.JGraphXViewManager;

import javax.swing.*;
import java.awt.*;

public class ExampleView
{
    private enum EAdapter { Graphstream, JGraphT }

    public static void main(String[] args)
    {
        Graph graph = ExampleGraph.getExampleGraph();

        IViewManager viewManager;
        switch (EAdapter.Graphstream)
        {
            case Graphstream:
                viewManager = new GraphstreamViewManager(graph);
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
