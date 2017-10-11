package de.itemis.graphing.model;

public interface IGraphListener
{
    void graphCleared();
    void vertexAdded(Vertex vertex);
    void vertexRemoved(Vertex vertex);
    void edgeAdded(Edge edge);
    void edgeRemoved(Edge edge);
    void attachmentAdded(AttachmentBase attachment);
    void attachmentRemoved(AttachmentBase attachment);
    void styleChanged(GraphElement element);
    void labelChanged(GraphElement element);
    void labelPriorityChanged(GraphElement element);
}
