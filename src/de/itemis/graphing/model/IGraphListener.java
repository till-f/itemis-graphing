package de.itemis.graphing.model;

public interface IGraphListener
{
    void vertexAdded(Vertex vertex);
    void vertexRemoved(Vertex vertex);
    void edgeAdded(Edge edge);
    void edgeRemoved(Edge edge);
    void attachmentAdded(Attachment attachment);
    void attachmentRemoved(Attachment attachment);
    void styleChanged(BaseGraphElement element);
    void labelChanged(BaseGraphElement element);
}
