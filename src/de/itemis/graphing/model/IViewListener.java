package de.itemis.graphing.model;

import java.util.List;

public interface IViewListener
{
    void slectionChanged(List<Vertex> vertexes);
    void attachmentClicked(Attachment attachment);
}
