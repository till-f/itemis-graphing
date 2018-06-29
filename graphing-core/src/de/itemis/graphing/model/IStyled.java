package de.itemis.graphing.model;

import de.itemis.graphing.model.style.Style;

public interface IStyled
{
    /**
     * returns a copy of the current style for REGULAR
     */
    Style getStyleRegular();

    /**
     * returns a copy of the current style for CLICKED
     */
    Style getStyleClicked();

    /**
     * returns a copy of the current style for SELECTED
     */
    Style getStyleSelected();

    /**
     * sets a copy of the given style for REGULAR
     */
    void setStyleRegular(Style newStyle);

    /**
     * sets a copy of the given style for CLICKED
     */
    void setStyleClicked(Style newStyle);

    /**
     * sets a copy of the given style for SELECTED
     */
    void setStyleSelected(Style newStyle);

    /**
     * sets a copy of the given style for REGULAR, CLICKED, SELECTED
     */
    void setAllStyles(Style newStyle);

    /**
     * applies highlighting with a given style
     */
    void pushHighlighting(Style hightlightingStyle);

    /**
     * removes last applied highlighting (throws if no highlighting is applied)
     */
    void popHighlighting();

    /*
     * clears all highlighting (never throws)
     */
    void clearHighlighting();

    /**
     * returns the current active style (not a copy!). should not be used by user code.
     */
    Style getActiveStyle();

}
