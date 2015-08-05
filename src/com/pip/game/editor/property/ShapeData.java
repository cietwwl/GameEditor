package com.pip.game.editor.property;

import org.jdom.Element;

import com.pip.game.data.DataObject;
import com.pip.game.data.i18n.I18NContext;
import com.pip.util.Utils;

/**
 * �༭����������
 * @author 	hyfu
 */
public class ShapeData extends DataObject {
	
	public ShapeData() {
        super();
    }

    public static final byte AREA_TYPE_ROUND = 0; //Բ�Σ���Ҫ�뾶����
	
	public static  final byte AREA_TYPE_RECT = 1; //���Σ���Ҫ�������
	
	public static  final byte AREA_TYPE_SECTOR = 2;//���Σ���Ҫ�뾶���ǶȲ���
	
	public static  final byte AREA_TYPE_FRONT_RECT = 3;//��ǰ�����Σ� ��Ҫ������� ~
		
	/**
	 * ��������
	 * @see	#AREA_TYPE_ROUND
	 * @see	#AREA_TYPE_RECT
	 * @see	#AREA_TYPE_SECTOR
	 */
	public byte areaType;
	/**
	 * �������
	 */
	public int[] values = new int[]{0,0};
	
	/**
	 * ê������
	 */
	public byte anchorType; // 0:ָ��ê��, 1:���Լ�Ϊê��
	
	public static  final byte ANCHOR_TYPE_FIX_POINT= 0;//ָ��ê�� 
	
	public static  final byte ANCHOR_TYPE_OWNER_POSITION= 1;//���Լ�Ϊê�� 
	
	@Override
	public void update(DataObject obj) {
		ShapeData oo = (ShapeData) obj;
		id = oo.id;
        title = oo.title;
        description = oo.description;
        setCategoryName(oo.getCategoryName());
        
		areaType = oo.areaType;
		values = oo.values;
		anchorType = oo.anchorType;
	}

	@Override
	public DataObject duplicate() {
		ShapeData ret = new ShapeData();
		ret.update(this);
		return ret;
	}

	@Override
	public void load(Element elem) {
		id = Integer.parseInt(elem.getAttributeValue("id"));
        title = elem.getAttributeValue("title");
        description = elem.getAttributeValue("description");
        setCategoryName(elem.getAttributeValue("category"));
        if (getWholeCategoryName() == null) {
            setCategoryName("");
        }
        
        areaType = Byte.parseByte(elem.getAttributeValue("areaType"));
        values = Utils.stringToIntArray(elem.getAttributeValue("values"), ';');
        if(elem.getAttribute("anchorType")!=null){
        	anchorType = Byte.parseByte(elem.getAttributeValue("anchorType"));
        }
	}

	@Override
	public Element save() {
		Element ret = new Element("area");
        ret.addAttribute("id", String.valueOf(id));
        ret.addAttribute("title", title);
        ret.addAttribute("description", description);
        if (getWholeCategoryName() != null) {
            ret.addAttribute("category", getWholeCategoryName());
        }

        ret.addAttribute("areaType", String.valueOf(areaType));
        ret.addAttribute("values", Utils.intArrayToString(values, ';'));
        
        ret.addAttribute("anchorType", String.valueOf(anchorType));
		return ret;
	}

	@Override
	public boolean depends(DataObject obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean changed(DataObject obj) {
		return !equals(obj);
	}

	@Override
	public boolean i18n(I18NContext context) {
		// TODO Auto-generated method stub
		return false;
	}

	public String toString(){
	    StringBuffer sbuf = new StringBuffer();
	    switch(areaType){
	        case AREA_TYPE_ROUND: 
	            sbuf.append("Բ,");
	            sbuf.append("�뾶:" + values[0]);
	            break;
	        case AREA_TYPE_RECT:
	            sbuf.append("����,");
	            sbuf.append("��x��:" + values[0]).append("x").append(String.valueOf(values[1]));
	            break;
	        case AREA_TYPE_SECTOR:
	            sbuf.append("����");
	            sbuf.append("�뾶:" + values[0]).append(" �Ƕ�:").append(String.valueOf(values[1]));
	            break;      
	       case AREA_TYPE_FRONT_RECT:
	            sbuf.append("��ǰ������");
	            sbuf.append("��x��:" + values[0]).append("x").append(String.valueOf(values[1]));
	            break;
	    }
	    return sbuf.toString();
	}
	
	public static String formatShapeDataToString(ShapeData data){
	    if(data==null) return "";
	    if(data.values==null){
            data.values = new int[]{data.areaType,0,0};
        }
        int[] tmp = new int[data.values.length + 1];
        tmp[0] = data.areaType;
        System.arraycopy(data.values, 0, tmp, 1, data.values.length);
        
        return Utils.intArrayToString(tmp, ',')+"#"+data.anchorType;
	}
	
	public static ShapeData parseShapeDataFromString(String str){
        if(str==null) return null;
        byte anchorType = 0;
        int sharpPos = str.indexOf("#");
        String vStr = str;
        if(sharpPos>0){
        	anchorType  = Byte.parseByte(str.substring(sharpPos+1));
        	vStr = str.substring(0, sharpPos);
        }
        int[] v = Utils.stringToIntArray(vStr, ',');
        if(v.length>0){
            ShapeData tmp = new ShapeData();
            tmp.areaType = (byte)v[0];
            tmp.values = new int[v.length - 1];
            tmp.anchorType = anchorType;
            System.arraycopy(v, 1, tmp.values , 0, tmp.values.length);
            return tmp;
        }
        return null;
    }
}
