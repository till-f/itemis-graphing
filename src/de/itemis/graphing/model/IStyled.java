package de.itemis.graphing.model;

import de.itemis.graphing.model.style.Style;

public interface IStyled
{
    public enum EStyle { Regular, Clicked, Selected, Highlighted }

    /**
     * returns a copy of the current regular style
     */
    Style getStyle();

    /**
     * convenience function, identical to setStyle(EStyle.Regular, newStyle)
     */
    void setStyle(Style newStyle);

    /**
     * copies the given style and sets it for the given type
     */
    void setStyle(EStyle styleSelector, Style newStyle);

    /**
     * applies highlighting with a given style
     */
    void beginHighlight(Style hightlightingStyle);

    /**
     * removes highlighting
     */
    void endHighlight();

    /**
     * returns a copy of the current active style (should be used by view only)
     */
    Style getActiveStyle();

}
