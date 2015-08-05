package com.pip.game.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.pip.game.data.DataObject;
import com.pip.game.data.IDataCalculator;

public class NullInput implements IEditorInput {
    public NullInput() {
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
        return "";
    }

    public IPersistableElement getPersistable() {
        return null;
    }

    public String getToolTipText() {
        return "";
    }
    
    public boolean equals(Object o) {
        if (o == null || !(o instanceof NullInput)) {
            return false;
        }
        return true;
    }
}
