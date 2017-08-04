package de.itemis.graphing.example.graphstream;

import de.itemis.graphing.interactions.DebugInteractionListener;
import de.itemis.graphing.layout.HierarchicalLayoutJGraphX;
import de.itemis.graphing.model.Graph;
import de.itemis.graphing.example.ExampleGraph;
import de.itemis.graphing.interactions.AutomarkLinks;
import de.itemis.graphing.view.graphstream.GraphstreamViewManager;
import de.itemis.graphing.view.graphstream.layout.StaticLayout;
import org.graphstream.ui.layout.Layout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ExampleGraphstreamView
{
   public static void main(String[] args)
    {
        Graph graph = ExampleGraph.getExampleGraph();

        GraphstreamViewManager viewManager = new GraphstreamViewManager(graph);
        Layout layout = new StaticLayout(graph, new HierarchicalLayoutJGraphX());
        viewManager.registerInteractionListener(new AutomarkLinks());
        viewManager.registerInteractionListener(new DebugInteractionListener());
        viewManager.configure(layout);

        JPanel view = viewManager.getView();
        JFrame jframe = new JFrame("Graph Example");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setSize(600, 600);
        jframe.add(view);

        jframe.setVisible(true);
    }
}
