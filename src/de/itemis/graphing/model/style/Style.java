package de.itemis.graphing.model.style;

public abstract class Style implements Cloneable
{
    public enum ELineMode { None, Solid, Dotted }

    protected String lineColor = "000000";
    protected String fillColor = "FFFFFF";
    protected String textColor = "000000";
    protected Double lineThickness = 1.0;
    protected ELineMode lineMode = ELineMode.Solid;
    protected Double fontSize = 12.0;

    public Style getCopy()
    {
        try {
            return (Style)this.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
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

    public ELineMode getLineMode() {
        return lineMode;
    }

    public Double getFontSize()
    {
        return fontSize;
    }

    public void setLineColor(String lineColor)
    {
        this.lineColor = lineColor;
    }

    public void setFillColor(String fillColor)
    {
        this.fillColor = fillColor;
    }

    public void setTextColor(String textColor)
    {
        this.textColor = textColor;
    }

    public void setLineThickness(Double lineThickness)
    {
        this.lineThickness = lineThickness;
    }

    public void setLineMode(ELineMode lineMode)
    {
        this.lineMode = lineMode;
    }

    public void setFontSize(Double fontSize)
    {
        this.fontSize = fontSize;
    }
}
