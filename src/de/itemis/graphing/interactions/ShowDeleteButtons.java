//package de.itemis.graphing.interactions;
//
//import de.itemis.graphing.model.*;
//import de.itemis.graphing.model.style.AttachmentStyle;
//import de.itemis.graphing.model.style.BlockStyle;
//
//import java.util.Set;
//
//public class ShowDeleteButtons implements IClickHandler
//{
//    private final Size _size;
//    private final double _padding;
//    private final TabularAttachment.ELocation _location;
//
//    private final AttachmentStyle regularStyle = new AttachmentStyle();
//    private final AttachmentStyle clickedStyle;
//
//    private TabularAttachment _deleteAttachment = null;
//
//    public ShowDeleteButtons(double width, double height, double padding, TabularAttachment.ELocation location)
//    {
//        _size = new Size(width, height);
//        _padding = padding;
//        _location = location;
//
//        regularStyle.setShape(BlockStyle.EShape.Circle);
//        regularStyle.setFillColor("F4D2D2");
//
//        clickedStyle = (AttachmentStyle) regularStyle.getCopy();
//        clickedStyle.setLineColor("AA0000");
//        clickedStyle.setLineThickness(Graph.DEFAULT_HL_LINE_THICKNESS);
//    }
//
//    @Override
//    public void notifyClickBegin(GraphElement element, ClickParameters params)
//    {
//        if (_deleteAttachment != null && (_deleteAttachment != element || element == null))
//            _deleteAttachment.getParent().removeAttachment(_deleteAttachment.getId());
//    }
//
//    @Override
//    public void notifyClickEnd(GraphElement element, ClickParameters params)
//    {
//        if (element instanceof Vertex)
//        {
//            Vertex vertex = (Vertex) element;
//            _deleteAttachment = vertex.addAttachment("DELETE_BUTTON", _size.getX(), _size.getY(), _padding, _location);
//            _deleteAttachment.setLabel("-");
//            _deleteAttachment.setStyle(IStyled.EStyle.Regular, regularStyle);
//            _deleteAttachment.setStyle(IStyled.EStyle.Clicked, clickedStyle);
//        }
//
//        if (element instanceof TabularAttachment && element.getId().equals("DELETE_BUTTON"))
//        {
//            element.getGraph().removeVertex(((TabularAttachment) element).getParent().getId());
//            _deleteAttachment = null;
//        }
//    }
//
//    @Override
//    public void selectionCompleted(Set<GraphElement> selected, Set<GraphElement> unselected)
//    {
//    }
//}
