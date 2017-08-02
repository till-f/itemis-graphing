package de.itemis.graphing.model;

import de.itemis.graphing.model.style.Style;

public interface IStyled
{
    public enum EStyle { Regular, Clicked, Selected }

    /**
     * identical to getStyle(EStyle.Regular)
     */
    Style getStyle();

    /**
     * identical to setStyle(EStyle.Regular, newStyle)
     */
    void setStyle(Style newStyle);

    /**
     * returns the style for the given type (regular, clicked, selected)
     */
    Style getStyle(EStyle styleSelecgtor);

    /**
     * sets the style for the given type (regular, clicked, selected)
     */
    void setStyle(EStyle styleSelector, Style newStyle);

    /**
     * returns the current active style (should be used by view only)
     */
    Style getActiveStyle();

}
