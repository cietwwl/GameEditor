package com.pip.game.editor.area;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.pip.util.AutoSelectAll;
import com.pipimage.image.CompressTextureOption;
import com.pipimage.image.JPEGMergeOption;

/**
 * 编辑压缩纹理模式图片选项。
 * @author lighthu
 */
public class CompressTextureOptionDialogEx extends Dialog {
    private Text textBorderWidth;
    private Combo comboTextureSize;
    private Combo comboTextureType;

    private CompressTextureOption editObject;
    
    private static String[] formats = { CompressTextureOption.PVRTC_4BPP, CompressTextureOption.PVRTC_4BPP2, CompressTextureOption.ETC1, CompressTextureOption.ETC2 };
    private static int[] sizes = { 512, 1024, 2048 };
    
    /**
     * Create the dialog
     * @param parentShell
     */
    public CompressTextureOptionDialogEx(Shell parentShell) {
        super(parentShell);
        editObject = new CompressTextureOption(CompressTextureOption.PVRTC_4BPP, 1024, 1024);
        editObject.borderWidth = 2;
    }

    /**
     * Create contents of the dialog
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        container.setLayout(gridLayout);
        
        Label label = new Label(container, SWT.NONE);
        label.setText("\u7EB9\u7406\u7C7B\u578B\uFF1A");
        
        comboTextureType = new Combo(container, SWT.READ_ONLY);
        comboTextureType.setItems(new String[] {"PVRTC4", "\u5206\u79BB\u900F\u660E\u901A\u9053\u7684PVRTC4", "ETC1", "\u5206\u79BB\u900F\u660E\u901A\u9053\u7684ETC1"});
        comboTextureType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        comboTextureType.select(0);
        
        Label label_1 = new Label(container, SWT.NONE);
        label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        label_1.setText("\u6700\u5927\u7EB9\u7406\u5927\u5C0F\uFF1A");
        
        comboTextureSize = new Combo(container, SWT.READ_ONLY);
        comboTextureSize.setItems(new String[] {"512", "1024", "2048"});
        comboTextureSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        comboTextureSize.select(1);

        final Label label_3 = new Label(container, SWT.NONE);
        label_3.setText("\u63CF\u8FB9\u5BBD\u5EA6\uFF1A");

        textBorderWidth = new Text(container, SWT.BORDER);
        textBorderWidth.setText("2");
        textBorderWidth.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textBorderWidth.addFocusListener(AutoSelectAll.instance);

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
        return new Point(352, 198);
    }
    
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("压缩纹理选项");
    }
    
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
		    editObject.format = formats[comboTextureType.getSelectionIndex()];
		    editObject.sizeWidth = sizes[comboTextureSize.getSelectionIndex()];
		    editObject.sizeHeight = editObject.sizeWidth;
			try {
				editObject.borderWidth = Integer.parseInt(textBorderWidth.getText());
				if (editObject.borderWidth < 0 || editObject.borderWidth > 10) {
					throw new Exception();
				}
			} catch (Exception e) {
				MessageDialog.openError(this.getShell(), "错误", "描边宽度：请输入0-10之间的整数。");
				return;
			}
			if (CompressTextureOption.ETC1.equals(editObject.format)) {
                String msg = "ETC1格式不支持透明色，你确定你要使用这个格式吗？";
                if (!MessageDialog.openConfirm(getShell(), "确认", msg)) {
                    return;
                }
            }
		}
		super.buttonPressed(buttonId);
	}

	public static CompressTextureOption choose() {
		CompressTextureOptionDialogEx dlg = new CompressTextureOptionDialogEx(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		if (dlg.open() == Dialog.OK) {
			return dlg.editObject;
		} else {
			return null;
		}
	}
}
