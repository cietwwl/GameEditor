package com.pip.game.editor.area;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.pip.mapeditor.data.MapFile;

public class ChooseColorModeDialog extends Dialog {
    private Combo comboColorMode;
    private MapFile mapFile;
    private int selectedMode;
    private int[] values;
    
    /**
     * Create the dialog
     * @param parentShell
     */
    public ChooseColorModeDialog(Shell parentShell, MapFile mapFile) {
        super(parentShell);
        this.mapFile = mapFile;
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

        comboColorMode = new Combo(container, SWT.READ_ONLY);
        comboColorMode.setVisibleItemCount(10);
        final GridData gd_comboColorMode = new GridData(SWT.FILL, SWT.CENTER, true, false);
        comboColorMode.setLayoutData(gd_comboColorMode);
        String[] items = new String[mapFile.refPalettes.size() + 4];
        values = new int[items.length];
        items[0] = "保留所有颜色";
        values[0] = -2;
        items[1] = "自动优化成256色";
        values[1] = -1;
        for (int i = 0; i < mapFile.refPalettes.size(); i++) {
            items[i + 2] = "地图调色板" + (i + 1);
            values[i + 2] = i;
        }
        items[items.length - 2] = "JPEG压缩(有损)";
        values[items.length - 2] = -3;
        items[items.length - 1] = "压缩纹理(有损)";
        values[items.length - 1] = -4;
        comboColorMode.setItems(items);
        comboColorMode.select(0);
        
        return container;
    }

    /**
     * Create contents of the button bar
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, "确定", true);
        createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
    }

    /**
     * Return the initial size of the dialog
     */
    @Override
    protected Point getInitialSize() {
        return new Point(265, 149);
    }
    
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("选择调色板");
    }
    
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            selectedMode = values[comboColorMode.getSelectionIndex()];
        }
        super.buttonPressed(buttonId);
    }
    
    public int getSelectedMode() {
        return selectedMode;
    }
}
