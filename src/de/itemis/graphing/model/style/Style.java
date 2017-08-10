package de.itemis.graphing.model.style;

public abstract class Style implements Cloneable
{
    public static final String DEFAULT_LINE_COLOR_HL = "0099FF";
    public static final Double DEFAULT_LINE_THICKNESS_HL = 3.0;
    public static final Double DEFAULT_LINE_THICKNESS_HLEDGE = 5.0;

    public enum ELineMode { None, Solid, Dotted }

    public enum ELabelAlignment { Center, Above, AtRight, Under, AtLeft }

    protected String lineColor = "000000";
    protected String fillColor = "FFFFFF";
    protected String textColor = "000000";
    protected Double lineThickness = 1.0;
    protected ELineMode lineMode = ELineMode.Solid;
    protected Double fontSize = 12.0;
    protected ELabelAlignment labelAlignment = ELabelAlignment.Center;
    protected Integer zIndex = 1;
    protected Boolean isInLevelForeground = false;

    public Style getCopy()
    {
        try {
            Style clone = (Style)this.clone();
            return clone;
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

    public Integer getzIndex()
    {
        return zIndex + (isInLevelForeground ? 1 : 0);
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

    public void setLabelAlignment(ELabelAlignment labelAlignment)
    {
        this.labelAlignment = labelAlignment;
    }

    public void setIsInLevelForeground(boolean isInLevelForeground)
    {
        this.isInLevelForeground = isInLevelForeground;
    }
}
