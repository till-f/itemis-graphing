package de.itemis.graphing.view;

import de.itemis.graphing.model.GraphElement;

public interface IClickHandler
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

    void clickBegin(GraphElement element, ClickParameters params);

    void clickEnd(GraphElement element, ClickParameters params);

}
