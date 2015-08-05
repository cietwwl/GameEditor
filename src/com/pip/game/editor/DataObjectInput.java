package com.pip.game.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.pip.game.data.DataObject;
import com.pip.game.data.IDataCalculator;

/**
 * ���ڱ༭һ�����ݶ�����������
 * @author lighthu
 */
public class DataObjectInput implements IEditorInput {
    protected DataObject dataObject;;
    
    public DataObjectInput(DataObject m) {
        dataObject = m;
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
