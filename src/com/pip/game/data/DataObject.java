package com.pip.game.data;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jdom.*;

import com.pip.game.data.i18n.I18NContext;
import com.pip.game.data.quest.Quest;
import com.pip.game.data.quest.QuestTrigger;
import com.pip.game.data.quest.QuestVariable;
import com.pip.util.Utils;

/**
 *  ���б༭��֧�ֵ����ݶ���Ĺ��ýӿڡ�
 * @author lighthu
 */
public abstract class DataObject implements Comparable {
    /**
     * ����ID��
     */
    public int id;
    /**
     * ����/���֡�
     */
    public String title = "";
    /**
     * ������
     */
    public String description = "";
    /**
     * �����������֡�
     */
    private String categoryName = "";
    
    /**
     * ���б��е�λ�ã����ڷ�����ģʽ���򣩡�
     */
    public int editorIndex;
    
    /**
     * �������͡�
     */
    public DataObjectCategory cate;
    
    public IDataCalculator DataCalc;
    
    public int getId() {
        return id;
    }
    
    /**
     * ȡ���б�����ʾ�����֡�
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * ȡ�ñ�ע��Ϣ
     */
    public String getComments() {
        return "";
    }
    
    /**
     * �������ݶ���
     * @param obj �༭����ǰ��������
     * @return ��������б仯������true
     */
    public abstract void update(DataObject obj);
    /**
     * ���ƶ��������ڱ༭��
     */
    public abstract DataObject duplicate();
    /**
     * ��XML��ǩ������������ԡ�
     * @param elem
     */
    public abstract void load(Element elem);
    /**
     * �����һ��XML��ǩ��
     * @throws Exception 
     */
    public abstract Element save() ;
    /**
     * �жϱ������Ƿ�����������һ������
     */
    public abstract boolean depends(DataObject obj);
        
    /**
     * �ж϶����Ƿ��б���֪ͨ�������ĸı䡣
     */
    public abstract boolean changed(DataObject obj);

    /**
     * ͬ�����Ƚϴ�С��
     */
    public int compareTo(Object o) {
        if (o == null || !(o instanceof DataObject)) {
            return -1;
        }
        DataObject oo = (DataObject)o;
        if (id < oo.id) {
            return -1;
        } else if (id == oo.id) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * ���浽byte���飨XML��
     * @return
     */
    public byte[] toByteArray() throws Exception {
        Element elem = save();
        Document doc = new Document(elem);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Utils.saveDOM(doc, bos);
        return bos.toByteArray();
    }
    
    /*
     * ��XML�Ƕ��ж����������Ƿ񲻵ȡ�
     */
    protected static boolean changed(DataObject o1, DataObject o2) {
        try {
            byte[] b1 = o1.toByteArray();
            byte[] b2 = o2.toByteArray();
            return !Arrays.equals(b1, b2);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    //���ȫ·��������
    public String getWholeCategoryName() {
        DataObjectCategory _cate = this.cate;
        String cateName = categoryName;
        while(_cate != null) {
            _cate = _cate.parent;
            
            if(_cate != null)
                cateName = _cate.name + "," + cateName;            
        }
        
        return cateName;
    }
    
    // ȡ�ø����������
    public String getRootCategoryName() {
        DataObjectCategory _cate = this.cate;
        String cateName = categoryName;
        while(_cate != null) {
            _cate = _cate.parent;
            
            if(_cate != null)
                cateName = _cate.name;            
        }
        
        return cateName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }
    
    /**
     * �������������Խ��й��ʻ������������Ҫ���ʻ����ַ���������ȡ������context�в��ҷ�������
     * @param context
     * @return �����ĳ�����Ա��滻������true�����򷵻�false��
     */
    public abstract boolean i18n(I18NContext context);
}
