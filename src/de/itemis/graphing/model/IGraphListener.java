package de.itemis.graphing.model;

public interface IGraphListener
{
    void vertexAdded(Vertex vertex);
    void vertexRemoved(Vertex vertex);
    void edgeAdded(Edge vertex);
    void edgeRemoved(Edge vertex);
    void attachmentAdded(Attachment vertex);
    void attachmentRemoved(Attachment vertex);
    void styleChanged(BaseGraphElement element);
}
