package de.itemis.graphing.model;

import de.itemis.graphing.model.style.Style;

public interface IStyled
{
    Style retrieveStyle();

    void setStyle(Style newStyle);
}