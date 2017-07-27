package de.itemis.graphing.model;

import de.itemis.graphing.model.style.Style;

public interface IStyled
{
    public enum EStyle { Regular, Clicked, Selected }

    Style getStyle();

    Style getActiveStyle();

    Style getStyle(EStyle styleSelecgtor);

    void setStyle(EStyle styleSelector, Style newStyle);

    void selectActiveStyle(EStyle styleSelector);
}
