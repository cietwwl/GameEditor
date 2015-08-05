package com.pip.game.data.forbid;

import java.util.Vector;

import org.jdom.Element;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.data.i18n.I18NContext;

public class ForbidSkill extends DataObject{
    public Vector<Integer> forbitSkills = new Vector<Integer>();
    
    public ProjectData owner;
    public ForbidSkill(ProjectData owner){
        this.owner=owner;
    }
    public void addForbidSkillId(int forbidSkillId) {
        forbitSkills.add(forbidSkillId);
//        forbitItems.addElement(forbidItemId);
    }
    
    public void removeForbidSkillId(int forbidSkillId) {
        forbitSkills.remove(new Integer(forbidSkillId));
    }
    
    @Override
    public boolean changed(DataObject obj) {
        // TODO Auto-generated method stub
        return changed(this,obj);
    }

    @Override
    public boolean depends(DataObject obj) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public DataObject duplicate() {
        ForbidSkill forbidSkill=new ForbidSkill(owner);
        forbidSkill.forbitSkills = forbitSkills;
        return forbidSkill;
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
                forbitSkills.add(new Integer(Integer.parseInt(idss[i])));
            }
        }
        
    }

    @Override
    public Element save() {
        Element ret=new Element("forbidSkill");
        ret.addAttribute("id",String.valueOf(id));
        ret.addAttribute("title",title);
        if(getWholeCategoryName()!=null){
            ret.addAttribute("category",getWholeCategoryName());
        }
        
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<forbitSkills.size();i++){
            if(sb.length() > 0) {
                sb.append(",");
            }
            
            sb.append(String.valueOf(forbitSkills.elementAt(i)));
        }
        
        ret.addAttribute("ids", sb.toString());
        
        return ret;
    }

    @Override
    public void update(DataObject obj) {
        ForbidSkill forbidSkill=(ForbidSkill)obj;
        forbitSkills = forbidSkill.forbitSkills;
        
    }
    public String toString(){
        return id + ":" + title;      
    }

    /**
     * 对这个对象的属性进行国际化处理，如果有需要国际化的字符串，则提取出来到context中查找翻译结果。
     * @param context
     * @return 如果有某个属性被替换，返回true，否则返回false。
     */
    public boolean i18n(I18NContext context) {
        return false;
    }
}
