package com.pip.game.editor;

import java.io.File;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import com.pip.game.data.ProjectConfig;
import com.pip.game.data.ProjectData;
import com.pip.game.data.WorldMapDataForExcel;
import com.pip.game.data.i18n.I18NProcessor;
import com.pip.game.data.i18n.LocaleConfig;
import com.pip.game.editor.advisor.ActionBarAdvisorAdapter;
import com.pip.game.editor.advisor.IBarAdvisorRegisterAction;
import com.pip.game.editor.equipment.EquipmentExportToExcel;
import com.pip.game.editor.item.ItemExportToExcel;
import com.pip.game.editor.quest.QuestExportToExcel;
import com.pip.game.editor.util.MapExportPng;
import com.pip.game.editor.util.Settings;
import com.pip.image.workshop.DirectoryView;
import com.pip.image.workshop.TileLibView;
import com.pip.image.workshop.TileView;
import com.pip.image.workshop.WorkshopPlugin;
import com.pip.mapeditor.data.ProjectOwner;
import com.pipimage.utils.Utils;
import com.swtdesigner.ResourceManager;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor implements IBarAdvisorRegisterAction {
    private ActionBarAdvisorAdapter actionBarAdvisorAdapter;
    
    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
        
        actionBarAdvisorAdapter = new ActionBarAdvisorAdapter(this);
    }

    protected void makeActions(IWorkbenchWindow window) {
        actionBarAdvisorAdapter.makeActions(window);
    }

    protected void fillMenuBar(IMenuManager menuBar) {
        actionBarAdvisorAdapter.fillMenuBar(menuBar);
    }

    protected void fillCoolBar(ICoolBarManager coolBar) {
        actionBarAdvisorAdapter.fillCoolBar(coolBar);
    }

    public void registerAction(IAction action) {
        this.register(action);        
    }
}
