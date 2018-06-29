package de.itemis.graphing.view.interaction;

import de.itemis.graphing.model.GraphElement;

public interface IClickHandler extends IInteractionHandler
{

    public class ClickParameters
    {
        public enum EButton { LEFT, RIGHT, MIDDLE, OTHER };

        private final boolean _isCtrlPressed;
        private final boolean _isShiftPressed;
        private final boolean _isAltPressed;

        private final EButton _button;

        public ClickParameters(boolean isCtrlPressed, boolean isShiftPressed, boolean isAltPressed, EButton button)
        {
            _isCtrlPressed =isCtrlPressed;
            _isShiftPressed = isShiftPressed;
            _isAltPressed = isAltPressed;
            _button = button;
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

        public EButton getButton()
        {
            return _button;
        }
    }

    void clickBegin(GraphElement element, ClickParameters params);

    void clickEnd(GraphElement element, ClickParameters params);

}
