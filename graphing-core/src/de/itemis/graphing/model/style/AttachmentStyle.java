package de.itemis.graphing.model.style;

public class AttachmentStyle extends BlockStyle
{
    public static AttachmentStyle Default()
    {
        AttachmentStyle style = new AttachmentStyle();
        style.setDefaults();
        return style;
    }

    public static AttachmentStyle Empty()
    {
        return new AttachmentStyle();
    }

    private AttachmentStyle()
    {
        zIndex = 5;
    }
}
