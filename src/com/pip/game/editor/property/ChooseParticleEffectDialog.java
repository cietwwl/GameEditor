package com.pip.game.editor.property;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import scryer.ogre.ps.ParticleEffectPlayer;
import scryer.ogre.ps.ParticleSystemManager;
import com.pip.game.data.ProjectData;
import com.pip.game.editor.ParticleEffectManager;
import com.pip.image.workshop.editor.PsPreviewer;

/**
 * ѡ����Ŀ�е�����Ч����
 * @author light.hu
 */
public class ChooseParticleEffectDialog extends Dialog {
    class ListContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            String[] arr = ParticleEffectManager.getParticleEffectNames();
            if(arr == null) {
                return new Object[]{"<��>"};
            }
            Object[] ret = new Object[arr.length + 1];
            ret[0] = "<��>";
            System.arraycopy(arr, 0, ret, 1, arr.length);
            return ret;
        }
        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
    
    protected ListViewer listViewer;
    protected org.eclipse.swt.widgets.List list;
    protected String selectedTemplate = null;
    /**����Ч����ʾ*/
    protected PsPreviewer previewer;
    protected ParticleEffectPlayer currentPlayer;
    private int selIndex; //��ѡ����б�����
    
    
    public String getSelectedTemplate() {
        return selectedTemplate;
    }

    public void setSelectedTemplate(String t) {
        this.selectedTemplate = t;
    }
    
    public int getSelectedIndex() {
        return selIndex;
    }

    /**
     * Create the dialog
     * @param parentShell
     */
    public ChooseParticleEffectDialog(Shell parentShell) {
        super(parentShell);
    }
    
    @Override
    public boolean close() {
    	if (currentPlayer != null) {
            previewer.setInput(null);
            currentPlayer.stop();
            currentPlayer = null;
        }
        return super.close();
    }

    /**
     * Create contents of the dialog
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite parentContainer = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        parentContainer.setLayout(gridLayout);
        
        Composite container = new Composite(parentContainer, SWT.NONE);
        final GridData gd_textPreDesc = new GridData(SWT.FILL, SWT.FILL, true, true);
        container.setLayoutData(gd_textPreDesc);
        gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        container.setLayout(gridLayout);
        
        listViewer = new ListViewer(container, SWT.V_SCROLL | SWT.BORDER);
        listViewer.setContentProvider(new ListContentProvider());
        list = listViewer.getList();
        final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        gridData.heightHint = 150;
        list.setLayoutData(gridData);
        
        listViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(final DoubleClickEvent event) {
                StructuredSelection sel = (StructuredSelection)event.getSelection();
                if (sel.isEmpty()) {
                    return;
                }
                buttonPressed(IDialogConstants.OK_ID);
            }
        });
        
        listViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(final SelectionChangedEvent event) {
                updatePreviewer();
            }
        });
       
        listViewer.setInput(ProjectData.getActiveProject());

        previewer = new PsPreviewer(container, SWT.NONE);
        previewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        
        if (selectedTemplate != null) {
            StructuredSelection sel = new StructuredSelection(selectedTemplate);
            listViewer.setSelection(sel);
        }
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
        return new Point(520, 644);
    }
    
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("ѡ������Ч��");
        newShell.setSize(1200, 800);
    }
    
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            StructuredSelection sel = (StructuredSelection)listViewer.getSelection();
            List list = (List)listViewer.getControl();
            selIndex = list.getSelectionIndex();
            
            if (sel.isEmpty()) {
                selectedTemplate = null;
            } else {
                selectedTemplate = (String)sel.getFirstElement();
                if ("<��>".equals(selectedTemplate)) {
                    selectedTemplate = null;
                }
            }
        }
        if (currentPlayer != null) {
            previewer.setInput(null);
            currentPlayer.stop();
            currentPlayer = null;
        }
        super.buttonPressed(buttonId);
    }

    private void updatePreviewer() {
        StructuredSelection sel = (StructuredSelection)listViewer.getSelection();
        if (sel.isEmpty()) {
            if (currentPlayer != null) {
                currentPlayer.stop();
                currentPlayer = null;
            }
            previewer.setInput(null);
            return;
        }
        String tname = (String)sel.getFirstElement();
        if (currentPlayer != null) {
            currentPlayer.stop();
            currentPlayer = null;
        }
        if ("<��>".equals(tname)) {
            previewer.setInput(null);
        } else {
            currentPlayer = new ParticleEffectPlayer(ParticleEffectManager.getPsManager(), tname, 0, 0);
            currentPlayer.setLoop(true);
            previewer.setInput(currentPlayer );
        }
    }
}
