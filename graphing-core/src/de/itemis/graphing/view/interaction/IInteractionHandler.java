package de.itemis.graphing.view.interaction;

import de.itemis.graphing.view.IViewManager;

public interface IInteractionHandler
{

    /**
     * called by ViewManager after the handler has been registered
     * */
    default void setViewManager(IViewManager viewManager)
    {
    }

}
