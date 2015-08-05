package com.pip.game.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.pip.game.data.DataObject;
import com.pip.game.data.DataObjectCategory;
import com.pip.game.data.ProjectData;

/**
 * 数据对象树形表格数据提供者。每个表格包含一类数据。
 * @author lighthu
 */
public class ProjectDataListProvider implements ITreeContentProvider {
    private Class dataClass;
    private String filterText;
    public boolean needNewCateLabel = true;

    public ProjectDataListProvider(Class cls) {
        dataClass = cls;
    }
    
    public void setFilterText(String text) {
        filterText = text;
    }
    
    public Object[] getElements(Object inputElement) {
        ProjectData proj = ProjectData.getActiveProject();
        if (filterText == null || filterText.length() == 0) {
            // 如果没有设置过滤，则显示所有的分类
            List cates = new ArrayList(proj.getCategoryListByType(dataClass));
            if(needNewCateLabel){
                cates.add("新建分类...");
            }
            return cates.toArray();
        } else {
            List list = new ArrayList();            
            findFilterObject(proj, proj.getCategoryListByType(dataClass), list);
            
            if(needNewCateLabel){
                list.add("新建分类...");
            }
            return list.toArray();
        }
    }
    
    //递归添加过滤的项目
    private void findFilterObject(ProjectData proj, List cates, List list) {
        List<DataObject> dataObjects = new ArrayList<DataObject>();
        
        // 如果设置了过滤，则只显示过滤后又内容的分类
        String ft = filterText.toLowerCase();
        Iterator itor = cates.iterator();
        while (itor.hasNext()) {
            DataObjectCategory cate = (DataObjectCategory)itor.next();
            boolean match = false;
            for (DataObject dobj : cate.objects) {
                if (dobj.toString().toLowerCase().contains(ft)) {
                    list.add(dobj);
                }
            }
            
            if(cate.cates != null) {
                findFilterObject(proj, cate.cates, list);
            }
        }
    }
    
    public void dispose() {}
    
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	public Object[] getChildren(Object parentElement) {
        if(dataClass.getName().endsWith("Equipment")) {
            int ii = 0;
        }
	    if (parentElement instanceof DataObjectCategory) {
	        DataObjectCategory cate = (DataObjectCategory)parentElement;
	        ProjectData proj = ProjectData.getActiveProject();
	        if (filterText == null || filterText.length() == 0) {
	        	//合并两类数据
	            Object[] ret = new Object[cate.objects.size() + cate.cates.size()];
	            System.arraycopy(cate.objects.toArray(), 0, ret, 0, cate.objects.size());
	            System.arraycopy(cate.cates.toArray(), 0, ret, cate.objects.size(), cate.cates.size());
	            return ret;
	            //return cate.objects.toArray();
	        } else {
	            String ft = filterText.toLowerCase();
	            List<DataObject> matchList = new ArrayList<DataObject>();
	            for (DataObject dobj : cate.objects) {
	                if (dobj.toString().toLowerCase().contains(ft)) {
                        matchList.add(dobj);
                    }
	            }
	            return matchList.toArray();
	        }
	    }
		return null;
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
	    return getChildren(element) != null;
	}
}