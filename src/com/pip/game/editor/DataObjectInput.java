package com.pip.game.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.pip.game.data.DataObject;
import com.pip.game.data.IDataCalculator;
import com.pip.game.data.ProjectData;

/**
 * 用于编辑一个数据对象的输入对象。
 * @author lighthu
 */
public class DataObjectInput implements IEditorInput {
    protected DataObject dataObject;
    protected boolean useLongName;
   
	public DataObjectInput(DataObject m) {
		dataObject = m;
		useLongName = ProjectData.getActiveProject().config.useLongName;
    }
    
    public Object getAdapter(Class adapter) {
        return null;
    }

    public boolean exists() {
        return false;
    }

    public ImageDescriptor getImageDescriptor() {
        return EditorPlugin.getDefault().getImageRegistry().getDescriptor("dataobj");
    }

    public String getName() {
    	if(useLongName){
    		return dataObject.toString() + "<"+ProjectData.getActiveProject().config.dataTypeNames.get(dataObject.getClass())+">";
    	}
        return dataObject.toString();
    }

    public IPersistableElement getPersistable() {
        return null;
    }

    public String getToolTipText() {
        return dataObject.toString();
    }
    
    public boolean equals(Object o) {
        if (o == null || !(o instanceof DataObjectInput)) {
            return false;
        }
        return dataObject.equals(((DataObjectInput)o).dataObject);
    }
}
