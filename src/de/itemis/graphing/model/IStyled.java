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
     * applies highlighting with a given style
     */
    void pushHighlighting(Style hightlightingStyle);

    /**
     * removes highlighting
     */
    void popHighlighting();

    /**
     * returns a copy of the current active style (should be used by view only)
     */
    Style getActiveStyle();

}
