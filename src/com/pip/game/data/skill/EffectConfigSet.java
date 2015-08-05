package com.pip.game.data.skill;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import com.pip.game.data.ProjectData;

/**
 * һ��Ч���ļ��ϡ�ÿ��ID��Ч��ֻ����1����
 * @author lighthu
 */
public class EffectConfigSet {
    protected int levelCount;
    public  List<EffectConfig> effects = new ArrayList<EffectConfig>();
    
    /**
     * ȡ�ü�������
     * @return
     */
    public int getLevelCount() {
        return levelCount;
    }
    
    /**
     * ���ü�������
     * @return
     */
    public void setLevelCount(int value) {
        levelCount = value;
        for (EffectConfig eff : effects) {
            eff.setLevelCount(value);
        }
    }
    
    /**
     * ȡ�õ�ǰ�����а���������Ч����
     */
    public EffectConfig[] getAllEffects() {
        EffectConfig[] ret = new EffectConfig[effects.size()];
        effects.toArray(ret);
        int idAdjust = hasGeneralEffect() ? 1 : 0;
        for (int id = 0; id < ret.length; id++) {
            ret[id].effectID = id - idAdjust;
        }
        return ret;
    }

    /**
     * �ж�ĳһ���͵�Ч���Ƿ��Ѿ����ڡ�
     * @param type
     * @return
     */
    public boolean exists(int type) {
        for (EffectConfig eff : effects) {
            if (eff.getType() == type) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * ��ա�
     */
    public void clear() {
        effects.clear();
    }
    
    /**
     * �������Ͳ���Ч����
     * @param type
     * @return
     */
    public EffectConfig findEffect(int type) {
        for (EffectConfig eff : effects) {
            if (eff.getType() == type) {
                return eff;
            }
        }
        return null;
    }
    
    /**
     * �ж�Ч���������Ƿ������ȱʡЧ��������У���ôЧ��ID��������-1������Ч��ID����������
     */
    public boolean hasGeneralEffect() {
        return effects.size() > 0 && effects.get(0).getType() < 0;
    }
    
    /**
     * ����һ��Ч����Ч�������е�ID��ͨ������Ч����IDΪ-1����һ����ͨЧ����IDΪ0��
     * @param eff
     * @return
     */
    public int getEffectID(EffectConfig eff) {
        int idAdjust = hasGeneralEffect() ? 1 : 0;
        for (int i = 0; i < effects.size(); i++) {
            if (effects.get(i) == eff) {
                return i - idAdjust;
            }
        }
        return -1;
    }
    
    /**
     * ����ָ�������������Ч����
     * @param types
     * @return
     */
    public List<EffectConfig> findEffects(int[] types) {
        List<EffectConfig> ret = new ArrayList<EffectConfig>();
        for (EffectConfig eff : effects) {
            for (int i = 0; i < types.length; i++) {
                if (eff.getType() == types[i]) {
                    ret.add(eff);
                    break;
                }
            }
        }
        return ret;
    }
    
    /**
     * ���һ����Ч����
     * @param eff
     */
    public void addEffect(EffectConfig eff) {
        effects.add(eff);
    }
    
    /**
     * �������Ч����
     */
    public void addGeneralEffect(EffectConfig eff) {
        effects.add(0, eff);
    }
    
    /**
     * ɾ��һ�����͵�Ч����
     * @param type
     */
    public void removeEffect(int type) {
        Iterator<EffectConfig> itor = effects.iterator();
        while (itor.hasNext()) {
            if (itor.next().getType() == type) {
                itor.remove();
            }
        }
    }
    
    /**
     * ȡ�����в�����
     * @return
     */
    public List<EffectParamRef> getAllParams() {
        List<EffectParamRef> ret = new ArrayList<EffectParamRef>();
        int idAdjust = hasGeneralEffect() ? 1 : 0;
        for (int id = 0; id < effects.size(); id++) {
            EffectConfig eff = effects.get(id);
            for (int i = 0; i < eff.getParamCount(); i++) {
                ret.add(new EffectParamRef(eff, i, id - idAdjust));
            }
        }
        return ret;
    }
    
    /**
     * ȡ��ĳ��λ�õĲ���������
     * @param index 0��ʼ���±�
     * @return
     */
    public EffectParamRef getParamAt(int index) {
        int idAdjust = hasGeneralEffect() ? 1 : 0;
        int start = 0;
        for (int id = 0; id < effects.size(); id++) {
            EffectConfig eff = effects.get(id);
            if (index < eff.getParamCount() + start) {
                return new EffectParamRef(eff, index - start, id - idAdjust);
            }
            start += eff.getParamCount();
        }
        return null;
    }
    
    /**
     * ���ƶ���
     */
    public EffectConfigSet duplicate() {
        EffectConfigSet ret = new EffectConfigSet();
        for (EffectConfig eff : effects) {
            ret.addEffect((EffectConfig)eff.clone());
        }
        ret.levelCount = levelCount;
        return ret;
    }
    
    /**
     * ��XML��ǩ������������ԡ�
     * @param elem
     */
    public void load(ProjectData proj, Element elem, int maxLevel) throws Exception {
        effects.clear();
        List list = elem.getChildren("effect");
        for (int i = 0; i < list.size(); i++) {
            addEffect(proj.effectConfigManager.load((Element)list.get(i), maxLevel));
        }
    }
    
    /**
     * �����һ��XML��ǩ��
     */
    public Element save() {
        Element ret = new Element("effects");
        for (EffectConfig eff : effects) {
            if (eff.getType() >=0 ) {
                ret.addContent(eff.save());
            }
        }
        return ret;
    }
}
