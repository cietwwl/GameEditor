package com.pip.game.data;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据对象分类目录。
 * @author lighthu
 */
public class DataObjectCategory {
    /**
     * 对应数据对象类。
     */
    public Class dataClass;
    /**
     * 分类名。同一类型数据的分类名不能重复。
     */
    public String name;
    /**
     * 分类对象列表。
     */
    public List<DataObject> objects = new ArrayList<DataObject>();
    
    /**
     * 允许再次包含分类目录
     */
    public List<DataObjectCategory> cates = new ArrayList<DataObjectCategory>();
    
    /**
     * 父分类目录
     */
    public DataObjectCategory parent;
    
    public DataObjectCategory(Class cls) {
        dataClass = cls;
    }
    
    public String toString() {
        if (name == null || name.length() == 0) {
            return "<未分类>";
        }
        return name;
    }
}



    

