package com.pip.game.editor.skill;

import java.io.File;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.pip.game.data.DataObject;
import com.pip.game.data.EffectSoundConfig;
import com.pip.game.data.ProjectData;
import com.pip.game.data.Sound;
import com.pip.image.workshop.editor.ImageViewerListener;

public class ChooseSkillAnimationDialog extends Dialog implements ImageViewerListener {
    private static final String NO_SOUND = "û������";
    
    class ContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            List<DataObject> list = ProjectData.getActiveProject().getDataListByType(Sound.class);
            Object[] ret = new Object[list.size() + 1];
            ret[0] = NO_SOUND;
            for (int i = 0; i < list.size(); i++) {
                ret[i + 1] = list.get(i);
            }
            return ret;
        }
        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
    private Combo comboSound;
    private int selectedFrame = -1;
    private SkillAnimationSelector previewer;
    private boolean updating = false;;
    private EffectSoundConfig soundConfig;
    private ComboViewer soundViewer;
    private int skillAniGrpIndex;
    
    public int getSelectedFrame() {
        return selectedFrame;
    }

    public void setSelectedFrame(int t) {
        this.selectedFrame = t;
    }
    
    /**
     * Create the dialog
     * @param parentShell
     */
    public ChooseSkillAnimationDialog(Shell parentShell, int skillAniGrpIndex) {
        super(parentShell);
        this.skillAniGrpIndex = skillAniGrpIndex;
        File f = new File(ProjectData.getActiveProject().baseDir, "client_res/sound.data");
        soundConfig = new EffectSoundConfig(f);
    }
    
    private Sound findSoundByName(String fileName) {
        List<DataObject> sounds = ProjectData.getActiveProject().getDataListByType(Sound.class);
        for (DataObject dobj : sounds) {
            Sound sound = (Sound)dobj;
            if (sound.source != null && sound.source.getName().equals(fileName)) {
                return sound;
            }
        }
        return null;
    }
    
    /**
     * Create contents of the dialog
     * @param parent
     */
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        container.setLayout(gridLayout);

        previewer = new SkillAnimationSelector(container, SWT.NONE, skillAniGrpIndex);
        previewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        previewer.setInput(this);
        previewer.setSelectedFrame(selectedFrame);
        previewer.setImageViewerListener(this);

        final Label label = new Label(container, SWT.NONE);
        label.setText("��Ӧ������");

        soundViewer = new ComboViewer(container, SWT.READ_ONLY);
        soundViewer.setContentProvider(new ContentProvider());
        soundViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(final SelectionChangedEvent event) {
                if (updating) {
                    return;
                }
                int currentFrame = previewer.getSelectedFrame();
                StructuredSelection sel = (StructuredSelection)soundViewer.getSelection();
                if (currentFrame != -1 && !sel.isEmpty()) {
                    Object obj = sel.getFirstElement();
                    if (NO_SOUND.equals(obj)) {
                        soundConfig.setConfig(currentFrame, null);
                    } else {
                        soundConfig.setConfig(currentFrame, ((Sound)obj).source.getName());
                    }
                    soundConfig.save();
                }
            }
        });
        comboSound = soundViewer.getCombo();
        comboSound.setVisibleItemCount(20);
        final GridData gd_comboSound = new GridData(SWT.FILL, SWT.CENTER, true, false);
        comboSound.setLayoutData(gd_comboSound);
        soundViewer.setInput(this);

        frameSelectionChanged(previewer, 0);
        
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
        return new Point(1200, 720);
    }
    
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("ѡ���ܶ���");
    }
    
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            selectedFrame = previewer.getSelectedFrame();
        }
        super.buttonPressed(buttonId);
    }

    public void areaSelected(Object source) {
    }

    public void contentChanged(Object source) {
    }

    public void frameDoubleClicked(Object source, int frame) {
    }

    public void frameSelectionChanged(Object source, int newFrame) {
        newFrame = previewer.getSelectedFrame();
        String fname = soundConfig.getConfig(newFrame);
        updating = true;
        if (fname != null) {
            Sound sound = findSoundByName(fname);
            if (sound != null) {
                soundViewer.setSelection(new StructuredSelection(sound));
            } else {
                soundViewer.setSelection(new StructuredSelection(NO_SOUND));
            }
        } else {
            soundViewer.setSelection(new StructuredSelection(NO_SOUND));
        }
        updating = false;
    }
}
