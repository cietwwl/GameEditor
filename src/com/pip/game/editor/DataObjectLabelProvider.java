package com.pip.game.editor;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.pip.game.data.DataObject;
import com.pip.game.data.DataObjectCategory;

/**
 * 数据对象表格文本提供者。每个表格包含一类数据，包括ID和名字两列。
 * @author lighthu
 */
public class DataObjectLabelProvider extends LabelProvider implements ITableLabelProvider {
    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof String) {
            if (columnIndex == 0) {
                return (String)element;
            } else {
                return "";
            }
        } else if (element instanceof DataObjectCategory) {
            if (columnIndex == 0) {
                String ret = ((DataObjectCategory)element).name;
                if ("".equals(ret)) {
                    ret = "<未分类>";
                }
                return ret;
            } else {
                return "";
            }
        } else if (element instanceof DataObject) {
            DataObject dobj = (DataObject)element;
            if (columnIndex == 0) {
                return String.valueOf(dobj.id);
            } else if (columnIndex == 1) {
                return dobj.getTitle();
            } else {
                return dobj.getComments();
            }
        } else {
            return element.toString();
        }
    }
    
    public Image getColumnImage(Object element, int columnIndex) {
        if (columnIndex == 0) {
            if(element instanceof DataObjectCategory || element instanceof String) {
                return EditorPlugin.getDefault().getImageRegistry().get("itemtype");
            } else {
                return EditorPlugin.getDefault().getImageRegistry().get("item");
            }
        } 
        return null;
    }
}