package com.pip.game.editor;

import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import scryer.core.ScryerApplication;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "com.pip.sanguo.editor.perspective";
	
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}

	@Override
    public void postStartup() {
        super.postStartup();
        ScryerApplication.getInstance().startup();
    }
	
    public boolean preShutdown() {
        ScryerApplication.getInstance().shutdown();
        getWorkbenchConfigurer().getWorkbench().getActiveWorkbenchWindow().getActivePage().savePerspective();
        return super.preShutdown();
    }
    
    
}
