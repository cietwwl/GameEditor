package com.pip.game.data.item;

import org.jdom.Element;

import com.pip.game.data.DataObject;
import com.pip.game.data.extprop.ExtPropEntries;
import com.pip.game.data.i18n.I18NContext;

public class DropNode extends DataObject{
    public static final int ERROR_VALUE = -1;
    public static final int ERROR_SYMBOL = -2;
    public static final int ERROR_OUT_OF_RANGE = -3;
    public static final int ERROR_GEN = -4;
    
    /** ������ʾ�ȷ�� */
    public static final int RATE_ACCURACY = 1000000;
    
    /**
     * �ڵ����� �˴��Ķ���ͬDropItem.dropType
     * ��Ʒ
     * װ��
     * ������
     * ��Ǯ
     * ����
     * >100 ��ʾ��չ���ң���currency.xml
     */
    public int type;
    
    /**
     * ������Ʒ�������
     */
    public int quantityMax;
    /**
     * ������Ʒ����������
     */
    public int quantityMin;
    /** 
     * �������ֵ
     *  ������� = dropRate/1,000,000
     */
    public int dropRate;
    
    /**
     * �Ƿ����������
     */
    public boolean isTask;
    
    /**
     * ���õ�����һ����������ʱ�򣬴洢�������id
     */
    public int taskId = -1;
    
    /**
     * �Ƿ�����Ҽ临�ơ�
     * true������ - ÿ����Ҷ�����һ��
     * false�������� - ֻ����һ�ݣ����������������ROLL�����
     */
    public boolean copy;
    
    /**
     * ��չ���ԡ�
     */
    public ExtPropEntries extPropEntries = new ExtPropEntries();
    
    public DropNode(int type){
        this.type = type;
    }
    
    public DropNode(){
        
    }

    public boolean depends(DataObject obj) {
        return false;
    }

    public DataObject duplicate() {
        DropNode node = new DropNode();
        node.update(this);
        return node;
    }

    @Override
    public boolean changed(DataObject obj) {
        return changed(this, obj);
    }

    public void load(Element elem) {
        type = Integer.parseInt(elem.getAttributeValue("droptype"));
        id = Integer.parseInt(elem.getAttributeValue("dropid"));
        quantityMax = Integer.parseInt(elem.getAttributeValue("quantityMax"));
        quantityMin = Integer.parseInt(elem.getAttributeValue("quantityMin"));
        isTask = Boolean.parseBoolean(elem.getAttributeValue("isTask"));
        dropRate = Integer.parseInt(elem.getAttributeValue("dropRate"));
        taskId = Integer.parseInt(elem.getAttributeValue("taskId"));
        copy = "1".equals(elem.getAttributeValue("copy"));
        extPropEntries.loadExtData(elem.getChild("extProps"));
    }

    public Element save() {
        Element ret = new Element("dropnode");
        ret.addAttribute("droptype",String.valueOf(type));
        ret.addAttribute("dropid",String.valueOf(id));
        ret.addAttribute("quantityMax",String.valueOf(quantityMax));
        ret.addAttribute("quantityMin",String.valueOf(quantityMin));
        ret.addAttribute("isTask",String.valueOf(isTask));
        ret.addAttribute("dropRate",String.valueOf(dropRate));
        ret.addAttribute("taskId",String.valueOf(taskId));
        ret.addAttribute("copy", copy ? "1" : "0");
        if(extPropEntries.editProps.size()>0){
        	Element el = new Element("extProps");
        	extPropEntries.saveToDom(el);
        	ret.addContent(el);
        }
        return ret;
    }

    public void update(DataObject obj) {
        DropNode node = (DropNode)obj;
        id = node.id;
        type = node.type;
        quantityMax = node.quantityMax;
        quantityMin = node.quantityMin;
        isTask = node.isTask;
        dropRate = node.dropRate;
        taskId = node.taskId;
        copy = node.copy;
        extPropEntries.copyFrom(node.extPropEntries);
    }
    
    /**
     * 
     * @param rate
     * @return
     */
    public int getDropRate(String rate){
        if(rate == null || "".equals(rate)){
            return ERROR_GEN;
        }
        
        /* ʾ����1% = 1/100   1%% = 1/10,000   1%%% = 1/1,000,000 */
        int value = 0;
        int symbolCnt = 0;
        String valStr ;
        
        int index = rate.indexOf("%");
        if(index != -1){
            valStr = rate.substring(0, index);
            String accuracy = rate.substring(index);
            
            //�жϺ����ķ������Ƿ��ǡ�%��
            while(accuracy.length() > 0){
                if(accuracy.endsWith("%")){                    
                    accuracy = accuracy.substring(0 , accuracy.length() - 1);
                    symbolCnt++;
                }
                else{
                    return ERROR_SYMBOL;
                }
            }
        }
        else{
            valStr = rate;
        }
        
        try {
            value = Integer.parseInt(valStr);
        }
        catch (NumberFormatException e) {
            return ERROR_VALUE;
        }
        
        if((symbolCnt > 3) || (symbolCnt == 1 && value > 100) 
                || (symbolCnt == 2 && value > 10000) || (value > 1000000)){
            return ERROR_OUT_OF_RANGE;
        }
        
        return value * (int)Math.pow(100 , 3 - symbolCnt);
    }
    
    
    public String getRateString(){
        if(dropRate % 10000 == 0){
            return dropRate/10000 + "%";
        }
        else if(dropRate % 100 == 0){
            return dropRate/100 + "%%";
        }
        else{
            return String.valueOf(dropRate);
        }
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
