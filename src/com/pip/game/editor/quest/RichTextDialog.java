package com.pip.game.editor.quest;

import java.lang.reflect.Constructor;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectConfig;
import com.pip.game.data.ProjectData;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.ExpressionList;
import com.pip.game.editor.EditorPlugin;
import com.swtdesigner.ResourceManager;

public class RichTextDialog extends Dialog {
    private RichTextEditor editor;
    private String text;
    private QuestInfo questInfo;
    
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * Create the dialog
     * @param parentShell
     */
    public RichTextDialog(Shell parentShell, QuestInfo qinfo) {
        super(parentShell);
        questInfo = qinfo;
    }
    public RichTextDialog(Shell parentShell) {
        super(parentShell);
    }

    /**
     * Create contents of the dialog
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        final GridLayout gridLayout = new GridLayout();
        container.setLayout(gridLayout);

        ProjectConfig config = ProjectData.getActiveProject().config;
        if(config.richTextEditorClass!=null && config.richTextEditorClass.trim().length()>0){
        	 try {
                 String className = config.richTextEditorClass.trim();
                 Class clzz = config.getProjectClassLoader().loadClass(className);
                 Constructor constructor = clzz.getConstructor(Composite.class, QuestInfo.class);
                 editor = (RichTextEditor) constructor.newInstance(container, questInfo);
             }catch (Exception e) {
                 e.printStackTrace();
             }
        }else{
        	editor = new RichTextEditor(container, questInfo);
        }
        editor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        editor.setText(text);
        
        return container;
    }

    /**
     * Create contents of the button bar
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, "ȷ��", true);
        createButton(parent, IDialogConstants.CANCEL_ID, "ȡ��", false);
    }

    /**
     * Return the initial size of the dialog
     */
    @Override
    protected Point getInitialSize() {
        return new Point(600, 400);
    }
    
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("�ı��༭��");
    }

    
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            text = editor.getText();
        }
        super.buttonPressed(buttonId);
    }
    
    public static String open(Shell parentShell, String value, QuestInfo qinfo) {
        RichTextDialog dlg = new RichTextDialog(parentShell, qinfo);
        dlg.setText(value);
        if (dlg.open() == OK) {
            return dlg.getText();
        } else {
            return null;
        }
    }
}
