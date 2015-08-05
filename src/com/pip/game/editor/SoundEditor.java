package com.pip.game.editor;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.data.Sound;
import com.pip.util.AutoSelectAll;
import com.pipimage.utils.Utils;

public class SoundEditor extends DefaultDataObjectEditor {

    private Text textSource;
    private Text textDescription;
    private Text textTitle;
    private Text textID;
    
    private static FileDialog browseDialog;
    
    public static final String ID = "com.pip.sanguo.editor.SoundEditor"; //$NON-NLS-1$

    /**
     * Create contents of the editor part
     * @param parent
     */
    @Override
    public void createPartControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 5;
        container.setLayout(gridLayout);

        final Label label = new Label(container, SWT.NONE);
        label.setText("ID��");

        textID = new Text(container, SWT.BORDER);
        final GridData gd_textID = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textID.setLayoutData(gd_textID);
        textID.addFocusListener(AutoSelectAll.instance);
        textID.addModifyListener(this);

        final Label label_1 = new Label(container, SWT.NONE);
        label_1.setLayoutData(new GridData());
        label_1.setText("���ƣ�");

        textTitle = new Text(container, SWT.BORDER);
        final GridData gd_textTitle = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textTitle.setLayoutData(gd_textTitle);
        textTitle.addFocusListener(AutoSelectAll.instance);
        textTitle.addModifyListener(this);
        new Label(container, SWT.NONE);

        final Label label_2 = new Label(container, SWT.NONE);
        label_2.setLayoutData(new GridData());
        label_2.setText("������");

        textDescription = new Text(container, SWT.BORDER);
        final GridData gd_textDescription = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
        textDescription.setLayoutData(gd_textDescription);
        textDescription.addFocusListener(AutoSelectAll.instance);
        textDescription.addModifyListener(this);

        final Label label_3 = new Label(container, SWT.NONE);
        label_3.setText("�ļ���");

        textSource = new Text(container, SWT.READ_ONLY | SWT.BORDER);
        final GridData gd_textSource = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
        textSource.setLayoutData(gd_textSource);

        final Button buttonBrowse = new Button(container, SWT.NONE);
        buttonBrowse.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                onBrowse();
            }
        });
        final GridData gd_buttonBrowse = new GridData();
        buttonBrowse.setLayoutData(gd_buttonBrowse);
        buttonBrowse.setText("���...");
        
        // ���ó�ʼֵ
        updateView();
        
        setDirty(false);
        setPartName(this.getEditorInput().getName());
        saveStateToUndoBuffer();
    }
    
    private void updateView() {
        // ���ó�ʼֵ
        Sound dataDef = (Sound)editObject;
        textID.setText(String.valueOf(dataDef.id));
        textTitle.setText(dataDef.title);
        textDescription.setText(dataDef.description);
        if (dataDef.source != null) {
            textSource.setText(dataDef.source.getAbsolutePath());
        }
    }
    
    /**
     * ���浱ǰ�༭���ݡ�
     */
    protected void saveData() throws Exception {
        Sound dataDef = (Sound)editObject;

        // ��ȡ���룺����ID�����⡢���������͡����𡢼۸�ٺ»����Ӫ������Ч��
        try {
            dataDef.id = Integer.parseInt(textID.getText());
        } catch (Exception e) {
            throw new Exception("��������ȷ��ID��");
        }
        dataDef.title = textTitle.getText().trim();
        dataDef.description = textDescription.getText();
        
        // �������Ϸ���
        DataObject dobj = ProjectData.getActiveProject().findObject(dataDef.getClass(), dataDef.id);
        if (dobj != null && dobj != getSaveTarget()) {
            throw new Exception("ID�ظ������������롣");
        }
        if (dataDef.title.length() == 0) {
            throw new Exception("��������⡣");
        }
    }

    // ��ʾ�Ի���ѡ�������ļ���
    private void onBrowse() {
        Sound dataDef = (Sound)editObject;
        if (browseDialog == null) {
            browseDialog = new FileDialog(getSite().getShell(), SWT.OPEN);
            browseDialog.setFilterExtensions(new String[] { "*.mid", "*.mp3" });
            browseDialog.setFilterNames(new String[] { "MIDI�ļ�(*.mid)", "MP3�ļ�(*.mp3)" });
            if (dataDef.source != null) {
                browseDialog.setFilterPath(dataDef.source.getParent());
            }
        }
        String file = browseDialog.open();
        if (file != null) {
            File newFile;
            if (file.toLowerCase().endsWith(".mid")) {
                newFile = new File(dataDef.owner.baseDir, "Sounds/" + dataDef.id + ".mid");
            } else {
                newFile = new File(dataDef.owner.baseDir, "Sounds/" + dataDef.id + ".mp3");
            }
            try {
                Utils.copyFile(new File(file), newFile);
            } catch (Exception e) {
                MessageDialog.openError(getSite().getShell(), "����", e.toString());
                return;
            }
            dataDef.source = newFile;
            textSource.setText(newFile.getAbsolutePath());
            setDirty(true);
        }
    }
}
