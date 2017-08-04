package de.itemis.graphing.view;

import de.itemis.graphing.model.GraphElement;

import java.util.Set;

public interface IInteractionListener
{

    public class ClickParameters
    {
        private final boolean _isCtrlPressed;
        private final boolean _isShiftPressed;
        private final boolean _isAltPressed;

        public ClickParameters(boolean isCtrlPressed, boolean isShiftPressed, boolean isAltPressed)
        {
            _isCtrlPressed =isCtrlPressed;
            _isShiftPressed = isShiftPressed;
            _isAltPressed = isAltPressed;
        }

        public boolean isCtrlPressed() {
            return _isCtrlPressed;
        }

        public boolean isShiftPressed() {
            return _isShiftPressed;
        }

        public boolean isAltPressed() {
            return _isAltPressed;
        }
    }

    /**
     * called when left mouse button is clicked.
     */
    void clickBegin(GraphElement element, ClickParameters params);

    /**
     * called when left mouse button is released. element is ensured to be the same as last call to notifyClickBegin().
     */
    void clickEnd(GraphElement element, ClickParameters params);

    /**
     * called when the selection has changed. contains only changes since previous notifications.
     */
    void selectionChanged(Set<GraphElement> selected, Set<GraphElement> unselected);
}
