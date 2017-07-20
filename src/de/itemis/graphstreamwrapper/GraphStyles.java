package de.itemis.graphstreamwrapper;

public class GraphStyles {

    public static final String TREE_STYLE = "" +
            "node {" +
                "shape: rounded-box;" +
                "size: 0.4gu,0.4gu;" +
                "fill-mode: plain;" +
                "fill-color: white;" +
                "stroke-mode: plain;" +
                "stroke-color: black;" +
                "text-size: 12;" +
            "}" +
            "node.group {" +
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
                "fill-color: black;" +
            "}" +
            "";
}
