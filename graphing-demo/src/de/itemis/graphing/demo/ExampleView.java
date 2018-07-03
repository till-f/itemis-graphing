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
    public static void main(String[] args)
    {
        Graph<Object> graph = ExampleGraph.getExampleGraph();

        //IViewManager<Object> viewManager = new GraphstreamViewManager<>(graph);
        IViewManager<Object> viewManager = new JGraphXViewManager<>(graph);

        viewManager.registerHandler(new HighlightLinksClickHandler());
        viewManager.registerHandler(new ShowLabelTooltipHoverHandler());
        viewManager.registerHandler(new ShowButtonsExampleHoverHandler());

        Component view = viewManager.getView();
        JFrame jframe = new JFrame("Graph Example");
        jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        double scale = Screen.getScalingFactor();
        jframe.setSize((int)(600 * scale), (int)(600 * scale));
        jframe.add(view);

        jframe.setVisible(true);
    }
}
