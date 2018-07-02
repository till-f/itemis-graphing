package de.itemis.graphing.model;

public interface IGraphListener<T>
{
    void graphCleared();
    void vertexAdded(Vertex<T> vertex);
    void vertexRemoved(Vertex<T> vertex);
    void edgeAdded(Edge<T> edge);
    void edgeRemoved(Edge<T> edge);
    void attachmentAdded(AttachmentBase<T> attachment);
    void attachmentRemoved(AttachmentBase<T> attachment);
    void styleChanged(GraphElement<T> element);
    void labelChanged(GraphElement<T> element);
    void labelPriorityChanged(GraphElement<T> element);
}
