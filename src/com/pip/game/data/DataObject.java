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
 *  所有编辑器支持的数据对象的公用接口。
 * @author lighthu
 */
public abstract class DataObject implements Comparable {
    /**
     * 对象ID。
     */
    public int id;
    /**
     * 标题/名字。
     */
    public String title = "";
    /**
     * 描述。
     */
    public String description = "";
    /**
     * 所属类型名字。
     */
    private String categoryName = "";
    
    /**
     * 在列表中的位置（用于服务器模式排序）。
     */
    public int editorIndex;
    
    /**
     * 所属类型。
     */
    public DataObjectCategory cate;
    
    public IDataCalculator DataCalc;
    
    public int getId() {
        return id;
    }
    
    /**
     * 取得列表中显示的名字。
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * 取得备注信息
     */
    public String getComments() {
        return "";
    }
    
    /**
     * 保存数据对象。
     * @param obj 编辑器当前输入内容
     * @return 如果数据有变化，返回true
     */
    public abstract void update(DataObject obj);
    /**
     * 复制对象以用于编辑。
     */
    public abstract DataObject duplicate();
    /**
     * 从XML标签中载入对象属性。
     * @param elem
     */
    public abstract void load(Element elem);
    /**
     * 保存成一个XML标签。
     * @throws Exception 
     */
    public abstract Element save() ;
    /**
     * 判断本对象是否依赖于另外一个对象。
     */
    public abstract boolean depends(DataObject obj);
        
    /**
     * 判断对象是否有必须通知服务器的改变。
     */
    public abstract boolean changed(DataObject obj);

    /**
     * 同类对象比较大小。
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
     * 保存到byte数组（XML）
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
     * 从XML角度判断两个对象是否不等。
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
    
    //获得全路径的名字
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
    
    // 取得根分类的名字
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
     * 对这个对象的属性进行国际化处理，如果有需要国际化的字符串，则提取出来到context中查找翻译结果。
     * @param context
     * @return 如果有某个属性被替换，返回true，否则返回false。
     */
    public abstract boolean i18n(I18NContext context);
}
