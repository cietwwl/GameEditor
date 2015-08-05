package com.pip.game.editor.util;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.editor.property.ChooseDataObjectDialog;

public class DataObjectChooser<E extends DataObject> extends Composite {
    private int dataObjectId = -1;
	private Text textID;
	private ModifyListener modifyListener;
	Button browseButton;
	Class<E> clazz;
	String title;
	
	/**
	 * Create the composite
	 * @param parent
	 * @param style
	 */
	public DataObjectChooser(Composite parent, int style, Class<E> clazz, String title) {
		super(parent, style);
		this.clazz = clazz;
		this.title = title;
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
		        selectDataObject();
		    }
		});
	}
	
	private void selectDataObject(){	    
        ChooseDataObjectDialog dlg = new ChooseDataObjectDialog(textID.getShell(), clazz, title);
        if (dlg.open() == Dialog.OK) {
            if(dlg.getSelectedDataObject() != null) {
                setDataObjectID(dlg.getSelectedDataObject().id);                
            } else {
                setDataObjectID(-1);
            }
        }
	}
	
	public void setDataObjectID(int id) {
	    DataObject selDataObject = (DataObject)ProjectData.getActiveProject().findObject(clazz, id);
	    if(selDataObject != null){
	        dataObjectId = id;
	        textID.setText(selDataObject.toString());
	        textID.setToolTipText(textID.getText());
	        if(modifyListener!=null){
	            modifyListener.modifyText(null);
	        }
	    } else {
	        dataObjectId = id;
	        textID.setText("нч");
            if(modifyListener!=null){
                modifyListener.modifyText(null);
            }
	    }
	}
	
	public int getDataObjectID() {
		return dataObjectId;
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
        dataObjectId=-1;
        browseButton.setEnabled(false);
    }
    public void canEditable(){
        browseButton.setEnabled(true);
    }
}
