package com.pip.game.editor.advisor;

import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.ActionBarAdvisor;

public interface IGameActionBarAdvisor {
    
    public void makeActions(IWorkbenchWindow window);
    
    public void fillMenuBar(IMenuManager menuBar);
    
    public void fillCoolBar(ICoolBarManager coolBar);
    
}
