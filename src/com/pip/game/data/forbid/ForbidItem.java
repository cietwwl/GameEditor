package com.pip.game.data.forbid;

import java.util.Vector;

import org.jdom.Element;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.data.i18n.I18NContext;

/**
 * ������Ʒ��
 * @author jwmeng
 *
 */
public class ForbidItem extends DataObject{
    public Vector<Integer> forbitItems = new Vector<Integer>();
    
    public ProjectData owner;
    
    public ForbidItem(ProjectData owner){
        this.owner=owner;
        
    }
    
    public void addForbidItemId(int forbidItemId) {
        forbitItems.add(forbidItemId);
    }
    
    public void removeForbidItemId(int forbidItemId) {
        forbitItems.remove(new Integer(forbidItemId));
    }
    
    @Override
    public boolean changed(DataObject obj) {
        return changed(this,obj);
    }

    @Override
    public boolean depends(DataObject obj) {
        return false;
    }

    @Override
    public DataObject duplicate() {
       ForbidItem forbidItem=new ForbidItem(owner);
       forbidItem.forbitItems = forbitItems;
        return forbidItem;
    }

    @Override
    public void load(Element elem) {
        id=Integer.parseInt(elem.getAttributeValue("id"));
        title=elem.getAttributeValue("title");
        this.setCategoryName(elem.getAttributeValue("category"));
        if(getWholeCategoryName()==null){
            this.setCategoryName("");
        }
        
        String ids = elem.getAttributeValue("ids");
        if(ids != null && "".equals(ids) == false) {
            String[] idss = ids.split(",");
            for(int i=0;i<idss.length;i++){
                forbitItems.add(new Integer(Integer.parseInt(idss[i])));
            }
        }
        
    }

    @Override
    public Element save() {
        Element ret=new Element("forbidItem");
        ret.addAttribute("id",String.valueOf(id));
        ret.addAttribute("title",title);
        if(getWholeCategoryName()!=null){
            ret.addAttribute("category",getWholeCategoryName());
        }
        
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<forbitItems.size();i++){
            if(sb.length() > 0) {
                sb.append(",");
            }
            
            sb.append(String.valueOf(forbitItems.elementAt(i)));
        }
        
        ret.addAttribute("ids", sb.toString());
        
        return ret;
        
    }

    @Override
    public void update(DataObject obj) {
        ForbidItem forbidItem=(ForbidItem)obj;
        forbitItems = forbidItem.forbitItems;
    }
    
    public String toString(){
        return id + ":" + title;      
    }

    /**
     * �������������Խ��й��ʻ������������Ҫ���ʻ����ַ���������ȡ������context�в��ҷ�������
     * @param context
     * @return �����ĳ�����Ա��滻������true�����򷵻�false��
     */
    public boolean i18n(I18NContext context) {
        return false;
    }
}
