package de.itemis.graphing.view.graphstream;

import de.itemis.graphing.util.Screen;
import de.itemis.graphing.model.FloatingAttachment;
import de.itemis.graphing.model.GraphElement;
import de.itemis.graphing.model.ISized;
import de.itemis.graphing.model.style.BlockStyle;
import de.itemis.graphing.model.style.EdgeStyle;
import de.itemis.graphing.model.style.Style;

public class StyleToGraphstreamCSS
{
    private final double SCALE = Screen.getScalingFactor();

    public String getAtciveStyleCSS(GraphElement element)
    {
        StringBuilder sb = new StringBuilder();

        Style style = element.getActiveStyle();
        if (style instanceof BlockStyle)
        {
            if (!(element instanceof ISized))
                throw new RuntimeException("unexpected element for block style: " + element);

            BlockStyle blockStyle = (BlockStyle) style;

            String unit = (element instanceof FloatingAttachment && ((FloatingAttachment) element).isPixelCoordinates()) ? "px" : "gu";

            sb.append("shape: " + getGraphstreamShape(blockStyle.getShape()) + ";");
            sb.append("size: " + ((ISized) element).getSize().getWidth() + unit + "," + ((ISized) element).getSize().getHeight() + unit + ";");
            sb.append("fill-mode: plain;");
            sb.append("fill-color: #" + style.getFillColor() + ";");
            sb.append("stroke-color: #" + style.getLineColor() + ";");
            sb.append("stroke-width: " + style.getLineThickness() * SCALE + ";");
            sb.append("stroke-mode: " + getGraphstreamStrokeMode(style.getLineMode()) + ";");
        }
        else if (style instanceof EdgeStyle)
        {
            EdgeStyle edgeStyle = (EdgeStyle) style;

            sb.append("shape: " + getGraphstreamEdgeShape(edgeStyle.getEdgeRouting()) + ";");
            sb.append("arrow-shape: " + getGraphstreamArrowShape(edgeStyle.getShape()) + ";");
            sb.append("fill-color: #" + style.getLineColor() + ";");

            if (style.getLineMode() == Style.ELineMode.None)
            {
                sb.append("stroke-mode: none;");
                sb.append("fill-mode: none;");
                sb.append("size: 0px;");
                sb.append("stroke-width: 0px;");
            }
            else if (style.getLineMode() == Style.ELineMode.Dotted)
            {
                sb.append("stroke-mode: dots;");
                sb.append("fill-mode: plain;");
                sb.append("size: 0px;");
                sb.append("stroke-width: " + style.getLineThickness() * SCALE + "px;");
            }
            else if (style.getLineMode() == Style.ELineMode.Solid && style.getLineThickness() * SCALE > 1.0)
            {
                sb.append("stroke-mode: plain;");
                sb.append("fill-mode: plain;");
                sb.append("size: 0px;");
                sb.append("stroke-width: " + style.getLineThickness() * SCALE + "px;");
            }
            else if (style.getLineMode() == Style.ELineMode.Solid)
            {
                // line thickness <= 1.0 ensured
                sb.append("stroke-mode: plain;");
                sb.append("fill-mode: plain;");
                sb.append("size: " + style.getLineThickness() * SCALE + "px;");
                sb.append("stroke-width: 0px;");
            }
            else
            {
                throw new IllegalArgumentException("unexpected line mode: " + style.getLineMode());
            }

            sb.append("stroke-color: #" + style.getLineColor() + ";");
            sb.append("fill-color: #" + style.getLineColor() + ";");
        }

        sb.append("text-color: #" + style.getTextColor() + ";");
        sb.append("text-size: " + style.getFontSize() * SCALE + ";");
        sb.append("text-padding: " + (int)(3.0 * SCALE + 0.1) + "px,0.0px;");
        sb.append("text-background-mode: plain;");
        sb.append("text-background-color: #" + style.getFillColor() + ";");
        sb.append("text-alignment: " + getGraphstreamTextAlignment(style.getLabelAlignment()) + ";");

        if (element instanceof FloatingAttachment)
        {
            sb.append("z-index: 7;");
        }
        else
        {
            sb.append("z-index: " + style.getzIndex() + ";");
        }

        return sb.toString();
    }

    private String getGraphstreamShape(BlockStyle.EShape shape)
    {
        switch (shape)
        {
            case Box:
                return "box";
            case RoundedBox:
                return "rounded-box";
            case Circle:
                return "circle";
        }

        throw new IllegalArgumentException("invalid shape: " + shape);
    }

    private String getGraphstreamArrowShape(EdgeStyle.EShape shape)
    {
        switch (shape)
        {
            case None:
                return "none";
            case Arrow:
                return "arrow";
            case Circle:
                return "circle";
            case Diamond:
                return "diamond";
        }

        throw new IllegalArgumentException("invalid shape: " + shape);
    }

    private String getGraphstreamEdgeShape(EdgeStyle.EEdgeRouting edgeRouting)
    {
        switch (edgeRouting)
        {
            case Routed:
            case Direct:
                return "line";
            case Cubic:
                return "cubic-curve";
        }

        throw new IllegalArgumentException("invalid edge routing: " + edgeRouting);
    }

    private String getGraphstreamStrokeMode(Style.ELineMode mode)
    {
        switch (mode)
        {
            case None:
                return "none";
            case Solid:
                return "plain";
            case Dotted:
                return "dots";
        }

        throw new IllegalArgumentException("invalid mode: " + mode);
    }

    private String getGraphstreamTextAlignment(Style.ELabelAlignment alignment)
    {
        switch (alignment)
        {
            case Center:
                return "center";
            case Above:
                return "above";
            case AtRight:
                return "at-right";
            case Under:
                return "under";
            case AtLeft:
                return "at-left";
        }

        throw new IllegalArgumentException("invalid mode: " + alignment);
    }

}
