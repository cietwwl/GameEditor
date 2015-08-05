package com.pip.game.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.pip.game.data.Animation;
import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectConfig;
import com.pip.game.data.ProjectData;
import com.pip.game.editor.util.AnimateHeadIconPreviewer;
import com.pip.game.editor.util.AnimatePreviewer;
import com.pip.util.AutoSelectAll;
import com.pip.util.EFSUtil;
import com.pipimage.image.PipAnimateSet;
import com.pipimage.utils.Utils;

public class AnimationEditor extends DefaultDataObjectEditor {
    private Text textDescription;
    private Text textTitle;
    private Text textID;
    public static final String ID = "com.pip.game.editor.AnimationEditor"; //$NON-NLS-1$
    private CTabFolder tabFolder;
    private CTabItem[] formatTabs;
    private AnimationFileChooser[] formatEditors;
    
    public static int lastSelectedFormat = -1;
    
    /**
     * Create contents of the editor part
     * @param parent
     */
    @Override
    public void createPartControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        container.setLayout(gridLayout);

        final Label label = new Label(container, SWT.NONE);
        label.setText("ID：");

        textID = new Text(container, SWT.BORDER);
        final GridData gd_textID = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textID.setLayoutData(gd_textID);
        textID.addFocusListener(AutoSelectAll.instance);
        textID.addModifyListener(this);

        final Label label_1 = new Label(container, SWT.NONE);
        label_1.setText("名称：");

        textTitle = new Text(container, SWT.BORDER);
        final GridData gd_textTitle = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textTitle.setLayoutData(gd_textTitle);
        textTitle.addFocusListener(AutoSelectAll.instance);
        textTitle.addModifyListener(this);

        final Label label_2 = new Label(container, SWT.NONE);
        label_2.setText("描述：");

        textDescription = new Text(container, SWT.BORDER);
        final GridData gd_textDescription = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textDescription.setLayoutData(gd_textDescription);
        textDescription.addFocusListener(AutoSelectAll.instance);
        textDescription.addModifyListener(this);

        tabFolder = new CTabFolder(container, SWT.NONE);
        tabFolder.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                lastSelectedFormat = tabFolder.getSelectionIndex();
            }
        });
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

        ProjectConfig config = ProjectData.getActiveProject().config;
        formatTabs = new CTabItem[config.animationFormats.size()];
        formatEditors = new AnimationFileChooser[config.animationFormats.size()];
        for (int i = 0; i < config.animationFormats.size(); i++) {
            formatTabs[i] = new CTabItem(tabFolder, SWT.NONE);
            formatTabs[i].setText(config.animationFormats.get(i).title);
            formatEditors[i] = new AnimationFileChooser(tabFolder, this, i);
            formatTabs[i].setControl(formatEditors[i]);
        }
        if (lastSelectedFormat == -1) {
            lastSelectedFormat = 0;
        }
        tabFolder.setSelection(lastSelectedFormat);

        // 设置初始值
        Animation dataDef = (Animation)editObject;
        textID.setText(String.valueOf(dataDef.id));
        textTitle.setText(dataDef.title);
        textDescription.setText(dataDef.description);
        for (int i = 0; i < formatEditors.length; i++) {
            formatEditors[i].setupFile();
            formatEditors[i].setupHeadArea();
        }

        setDirty(false);
        setPartName(this.getEditorInput().getName());
        saveStateToUndoBuffer();
    }

    /**
     * 保存当前编辑数据。
     */
    protected void saveData() throws Exception {
        Animation dataDef = (Animation)editObject;
        
        // 读取输入：对象ID、标题、描述
        try {
            dataDef.id = Integer.parseInt(textID.getText());
        } catch (Exception e) {
            throw new Exception("请输入正确的ID。");
        }
        dataDef.title = textTitle.getText().trim();
        dataDef.description = textDescription.getText();
                
        // 检查输入合法性
        DataObject dobj = ProjectData.getActiveProject().findObject(dataDef.getClass(), dataDef.id);
        if (dobj != null && dobj != getSaveTarget()) {
            throw new Exception("ID重复，请重新输入。");
        }
        if (dataDef.title.length() == 0) {
            throw new Exception("请输入标题。");
        }
    }
    
    /*
     * 头像设置改变，刷新所有头像预览窗口
     */
    public void headAreaChanged() {
        for (int i = 0; i < formatEditors.length; i++) {
            formatEditors[i].setupHeadArea();
        }
    }
}
