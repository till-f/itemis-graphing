package de.itemis.graphing.model.style;

public class AttachmentStyle extends BlockStyle
{
    protected Double pos1 = null;
    protected Double pos2 = null;

    @Override
    public void mergeWith(Style newStyle)
    {
        super.mergeWith(newStyle);

        if (newStyle instanceof AttachmentStyle)
        {
            AttachmentStyle newAttachmentStyle = (AttachmentStyle) newStyle;
            pos1 = (newAttachmentStyle.pos1 == null) ? pos1 : newAttachmentStyle.pos1;
            pos2 = (newAttachmentStyle.pos2 == null) ? pos2 : newAttachmentStyle.pos2;
        }
    }

    public Double getPos1()
    {
        return pos1;
    }

    public Double getPos2()
    {
        return pos2;
    }

    public void setPos1(Double pos1)
    {
        this.pos1 = pos1;
    }

    public void setPos2(Double pos2)
    {
        this.pos2 = pos2;
    }
}
