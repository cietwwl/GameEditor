package com.pip.game.data;

import java.util.ArrayList;
import java.util.List;

/**
 * ���ݶ������Ŀ¼��
 * @author lighthu
 */
public class DataObjectCategory {
    /**
     * ��Ӧ���ݶ����ࡣ
     */
    public Class dataClass;
    /**
     * ��������ͬһ�������ݵķ����������ظ���
     */
    public String name;
    /**
     * ��������б�
     */
    public List<DataObject> objects = new ArrayList<DataObject>();
    
    /**
     * �����ٴΰ�������Ŀ¼
     */
    public List<DataObjectCategory> cates = new ArrayList<DataObjectCategory>();
    
    /**
     * ������Ŀ¼
     */
    public DataObjectCategory parent;
    
    public DataObjectCategory(Class cls) {
        dataClass = cls;
    }
    
    public String toString() {
        if (name == null || name.length() == 0) {
            return "<δ����>";
        }
        return name;
    }
}



    

