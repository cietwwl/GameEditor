package com.pip.game.editor.util;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import com.pip.game.data.ProjectData;
import com.pip.game.data.item.Item;
import com.pip.game.editor.property.ChooseItemDialog;

public class ItemChooser extends Composite {
    private int itemId = -1;
	private Text textID;
	private ModifyListener modifyListener;
	Button browseButton;
	/**
	 * Create the composite
	 * @param parent
	 * @param style
	 */
	public ItemChooser(Composite parent, int style) {
		super(parent, style);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.numColumns = 2;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);

		textID = new Text(this, SWT.BORDER);
		textID.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		textID.setEditable(false);

		browseButton = new Button(this, SWT.NONE);
		browseButton.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
		browseButton.setText("...");
		browseButton.addSelectionListener(new SelectionAdapter(){
		    public void widgetSelected(SelectionEvent e) {
		        selectItem();
		    }
		});
	}
	
	private void selectItem(){
	    ChooseItemDialog itemDialog = new ChooseItemDialog(Display.getCurrent().getActiveShell());
	    if(itemDialog.open() == IDialogConstants.OK_ID){
	        setItemID(itemDialog.getSelectedItem());
	    }
	}
	
	public void setItemID(int id) {
	    Item selItem = ProjectData.getActiveProject().findItemOrEquipment(id);
	    if(selItem != null){	        
	        itemId = id;
	        textID.setText(selItem.toString());
	        textID.setToolTipText(textID.getText());
	        if(modifyListener!=null){
	            modifyListener.modifyText(null);
	        }
	    }
	}
	
	public int getItemID() {
		return itemId;
	}

	/**
	 * will call back as modifyListener.modifyText(null);
	 * @param modifyListener
	 */
    public void setModifyListener(ModifyListener modifyListener) {
        this.modifyListener = modifyListener;
    }
    public void cannotEditable(){
        textID.setText("");
        itemId=-1;
        browseButton.setEnabled(false);
    }
    public void canEditable(){
        browseButton.setEnabled(true);
    }
}
