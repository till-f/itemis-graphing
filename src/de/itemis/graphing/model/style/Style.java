package de.itemis.graphing.model.style;

import de.itemis.graphing.model.GraphElement;

public abstract class Style implements Cloneable
{
    public enum ELineMode { None, Solid, Dotted }

    public enum ELabelAlignment { Center, Above, AtRight, Under, AtLeft }

    private GraphElement _parent = null;

    protected String lineColor = "000000";
    protected String fillColor = "FFFFFF";
    protected String textColor = "000000";
    protected Double lineThickness = 1.0;
    protected ELineMode lineMode = ELineMode.Solid;
    protected Double fontSize = 12.0;
    protected ELabelAlignment labelAlignment = ELabelAlignment.Center;
    protected Integer zIndex = 1;
    protected Boolean isInLevelForeground = false;
    protected Boolean isLowPrioText = false;

    public Style getCopy()
    {
        try {
            Style clone = (Style)this.clone();
            clone.setParent(null);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setParent(GraphElement parent)
    {
        _parent = parent;
    }

    public String getLineColor()
    {
        return lineColor;
    }

    public String getFillColor()
    {
        return fillColor;
    }

    public String getTextColor()
    {
        return textColor;
    }

    public Double getLineThickness()
    {
        return lineThickness;
    }

    public ELineMode getLineMode()
    {
        return lineMode;
    }

    public Double getFontSize()
    {
        return fontSize;
    }

    public ELabelAlignment getLabelAlignment()
    {
        return labelAlignment;
    }

    public Boolean isInLevelForeground()
    {
        return isInLevelForeground;
    }

    public Boolean isLowPrioText()
    {
        return isLowPrioText;
    }

    public Integer getzIndex()
    {
        return zIndex + (isInLevelForeground ? 1 : 0);
    }

    public void setLineColor(String lineColor)
    {
        this.lineColor = lineColor;
        updated();
    }

    public void setFillColor(String fillColor)
    {
        this.fillColor = fillColor;
        updated();
    }

    public void setTextColor(String textColor)
    {
        this.textColor = textColor;
        updated();
    }

    public void setLineThickness(Double lineThickness)
    {
        this.lineThickness = lineThickness;
        updated();
    }

    public void setLineMode(ELineMode lineMode)
    {
        this.lineMode = lineMode;
        updated();
    }

    public void setFontSize(Double fontSize)
    {
        this.fontSize = fontSize;
        updated();
    }

    public void setLabelAlignment(ELabelAlignment labelAlignment)
    {
        this.labelAlignment = labelAlignment;
        updated();
    }

    public void setIsInLevelForeground(boolean isInLevelForeground)
    {
        this.isInLevelForeground = isInLevelForeground;
    }

    public void setIsLowPrioText(Boolean isLowPrioText)
    {
        this.isLowPrioText = isLowPrioText;
    }

    protected void updated()
    {
        if (_parent != null)
            _parent.styleChanged();
    }
}
