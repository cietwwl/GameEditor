package com.pip.game.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jdom.Element;

import com.pip.game.data.Shop.BuyRequirement;
import com.pip.game.data.i18n.I18NContext;
import com.pip.game.data.item.Item;

public class GiftGroup extends DataObject {
    /**
     * �������ڵ��ࡣ���ڵ����Ͱ�����Сʱ���죬�ܣ��¡�
     */
    public static class CycleDef {
        public static final int CYCLE_HOUR = 0;
        public static final int CYCLE_DAY = 1;
        public static final int CYCLE_WEEK = 2;
        public static final int CYCLE_MONTH = 3;
        public int type = CYCLE_DAY;
        public int amount = 1;
    }
    
    /**
     * ������Ŀ��
     */
    public ProjectData owner;
    /**
     * ��ʼ���ڣ����������ձ�ʾ�����ơ�ֻ���������ֶ���Ч��
     */
    public Date beginDate;
    /**
     * �������ڣ����������ձ�ʾ�����ơ�ֻ���������ֶ���Ч��
     */
    public Date endDate;
    /**
     * �Ƿ���Ч��
     */
    public boolean valid = true;
    /**
     * �����ȡ������-1��ʾ�����ơ�
     */
    public int maxTimes = -1;
    /**
     * ��ȡ���ڡ�
     */
    public CycleDef cycle = new CycleDef();
    /**
     * һ�������������ȡ������-1��ʾ�����ơ�
     */
    public int repeatTimes = 1;
    /**
     * һ��������������ȡ֮����С���ʱ�䣨�룩��-1��ʾ�����ơ�
     */
    public int timeSpace = -1;
    /**
     * һ��������������ȡ�Ŀ�ʼ�ա�-1��ʾ�����ƣ�0��ʾ��һ�졣�������С�ڻ����1�죬���趨�����塣
     */
    public int beginDay = -1;
    /**
     * һ��������������ȡ�Ľ����գ���������-1��ʾ�����ƣ�0��ʾ��һ�졣�������С�ڻ����1�죬���趨�����塣
     */
    public int endDay = -1;
    /**
     * һ��������ÿ��������ȡ�Ŀ�ʼʱ�䣨�֣���0�㿪ʼ���㣩��
     */
    public int beginTime = 0;
    /**
     * һ��������ÿ��������ȡ�Ľ���ʱ�䣨�֣���0�㿪ʼ���㣬����������
     */
    public int endTime = 1440;
    
    // �������Ϣ�п�ʹ�����±�����beginlevel��С����endlevel��󼶱�needitem������Ʒ��
    // giveitem�һ���Ʒ��acount����ȡ������rcount��������ȡ������max�����ȡ������
    // repeat���������ȡ������needtime��ȡʱ�䷶Χ��timespace��С��ȡ�����
    
    /**
     * ������Ϣ������ȡʱ����û�ж�Ӧ�ļ�������ʱ�������Ϣ��
     */
    public String errorMessage = "û�����������Ķһ����á�";
    /**
     * ��Ʒ��ı��⡣
     */
    public String groupMessage = "${beginlevel}��${endlevel}֮�������ȡ��ƷŶ��";
    /**
     * ��Ʒ���⡣
     */
    public String giftMessage = "��${needitem}���Զһ�${giveitem}��";
    /**
     * ��������ȡ��������Ϣ��
     */
    public String maxExceedMessage = "���Ѿ����${acount}���ˣ�ֻ����${max}�Σ���Ǹ����";
    /**
     * һ�������ڳ����������Ƶ���Ϣ��
     */
    public String repeatExceedMessage = "�������Ѿ����${rcount}���ˣ�һ����ֻ�ܸ���${repeat}�Σ����������ɡ�";
    /**
     * ��ȡʱ����û������Ϣ��
     */
    public String timeSpaceMessage = "ÿ${timespace}ֻ����ȡһ��Ŷ��";
    /**
     * û����ȡʱ�����ʾ��Ϣ��
     */
    public String timeErrorMessage = "���ڻ�û����ȡʱ���ģ���ȡʱ����${needtime}����ʱ�����ɡ�";
    /**
     * ������Ʒ�������ʾ��Ϣ��
     */
    public String needItemMessage = "��û���㹻����Ʒ����Ҫ${needitem}���������������Ұɡ�";
    /**
     * ��������������������ʾ��Ϣ��
     */
    public String needVarMessage = "��û�дﵽ�һ�������${needvar}��";
    /**
     * ���ŵ������ɹ�����ʾ��Ϣ��
     */
    public String giveOKMessage = "${giveitem}�Ѿ��ŵ���İ������ˣ��Ͽ�ȥװ�������԰ɣ�";
    /**
     * ������������ʾ��Ϣ��
     */
    public String bagFullMessage = "��������˰�����һ�°��������Ұɡ�";
    
    /**
     * �һ��鶨�壨���û����𻮷֣���
     */
    public ArrayList<GiftDef> gifts = new ArrayList<GiftDef>();
    
    /**
     * ĳ�û�����һ����塣
     * @author lighthu
     */
    public static class GiftDef {
        /**
         * ����ID��
         */
        public int id = 0;
        /**
         * ��С���𣨰�������
         */
        public int beginLevel = 1;
        /**
         * ��󼶱𣨰�������
         */
        public int endLevel = 100;
        /**
         * ������Ʒ��ֻ֧�ֽ�Ǯ��ս������Ʒ��
         */
        public ArrayList<Shop.BuyRequirement> needItems = new ArrayList<Shop.BuyRequirement>();
        /**
         * ������Ʒ��ֻ֧�ֽ�Ǯ��ս������Ʒ��
         */
        public ArrayList<Shop.BuyRequirement> giveItems = new ArrayList<Shop.BuyRequirement>();
        
        public GiftDef dup() {
            GiftDef ret = new GiftDef();
            ret.id = id;
            ret.beginLevel = beginLevel;
            ret.endLevel = endLevel;
            ret.needItems.clear();
            for (Shop.BuyRequirement item : needItems) {
                ret.needItems.add(item.dup());
            }
            ret.giveItems.clear();
            for (Shop.BuyRequirement item : giveItems) {
                ret.giveItems.add(item.dup());
            }
            return ret;
        }
        
        public String toString() {
            return "[" + beginLevel + "-" + endLevel + "��]";
        }
        
        public void switchProject(ProjectData prj) {
            for (Shop.BuyRequirement br : needItems) {
                br.switchProject(prj);
            }
            for (Shop.BuyRequirement br : giveItems) {
                br.switchProject(prj);
            }
        }
    }
    
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    public GiftGroup(ProjectData owner) {
        this.owner = owner;
    }

    public int getID() {
        return id;
    }
    
    public String toString() {
        return id + ": " + title;
    }

    public boolean equals(Object o) {
        return this == o;
    }
    
    public void update(DataObject obj) {
        GiftGroup oo = (GiftGroup)obj;
        id = oo.id;
        title = oo.title;
        description = oo.description;
        setCategoryName(oo.getCategoryName());
        
        beginDate = oo.beginDate;
        endDate = oo.endDate;
        valid = oo.valid;
        maxTimes = oo.maxTimes;
        cycle.type = oo.cycle.type;
        cycle.amount = oo.cycle.amount;
        repeatTimes = oo.repeatTimes;
        timeSpace = oo.timeSpace;
        beginDay = oo.beginDay;
        endDay = oo.endDay;
        beginTime = oo.beginTime;
        endTime = oo.endTime;
        errorMessage = oo.errorMessage;
        groupMessage = oo.groupMessage;
        giftMessage = oo.giftMessage;
        maxExceedMessage = oo.maxExceedMessage;
        repeatExceedMessage = oo.repeatExceedMessage;
        timeSpaceMessage = oo.timeSpaceMessage;
        timeErrorMessage = oo.timeErrorMessage;
        needItemMessage = oo.needItemMessage;
        needVarMessage = oo.needVarMessage;
        giveOKMessage = oo.giveOKMessage;
        bagFullMessage = oo.bagFullMessage;
        
        gifts.clear();
        for (GiftDef item : oo.gifts) {
            GiftDef newItem = item.dup();
            gifts.add(newItem);
            
            // ��Ŀ�л������ж���������Ҫ����
            if (owner != oo.owner) {
                newItem.switchProject(owner);
            }
        }
    }
    
    public DataObject duplicate() {
        GiftGroup ret = new GiftGroup(owner);
        ret.update(this);
        return ret;
    }
    
    @Override
    public boolean changed(DataObject obj) {
        return changed(this, obj);
    }
    
    public void load(Element elem) {
        id = Integer.parseInt(elem.getAttributeValue("id"));
        title = elem.getAttributeValue("title");
        setCategoryName(elem.getAttributeValue("category"));
        if (getWholeCategoryName() == null) {
            setCategoryName("");
        }
        
        try {
            beginDate = dateFormat.parse(elem.getAttributeValue("begindate"));
        } catch (Exception e) {
            beginDate = null;
        }
        try {
            endDate = dateFormat.parse(elem.getAttributeValue("enddate"));
        } catch (Exception e) {
            endDate = null;
        }
        valid = "true".equals(elem.getAttributeValue("valid"));
        maxTimes = Integer.parseInt(elem.getAttributeValue("max"));
        
        // �����ַ��������һ���ַ���ʾ���ͣ�h,d,w,m
        String cycleStr = elem.getAttributeValue("cycle");
        char suffix = cycleStr.charAt(cycleStr.length() - 1);
        switch (suffix) {
        case 'h':
            cycle.type = CycleDef.CYCLE_HOUR;
            break;
        case 'd':
            cycle.type = CycleDef.CYCLE_DAY;
            break;
        case 'w':
            cycle.type = CycleDef.CYCLE_WEEK;
            break;
        case 'm':
            cycle.type = CycleDef.CYCLE_MONTH;
            break;
        default:
            throw new IllegalArgumentException();
        }
        cycle.amount = Integer.parseInt(cycleStr.substring(0, cycleStr.length() - 1));

        repeatTimes = Integer.parseInt(elem.getAttributeValue("repeat"));
        timeSpace = Integer.parseInt(elem.getAttributeValue("timespace"));
        beginDay = Integer.parseInt(elem.getAttributeValue("beginday"));
        endDay = Integer.parseInt(elem.getAttributeValue("endday"));
        beginTime = Integer.parseInt(elem.getAttributeValue("begintime"));
        endTime = Integer.parseInt(elem.getAttributeValue("endtime"));
        
        errorMessage = elem.getChildText("message_error");
        groupMessage = elem.getChildText("message_group");
        giftMessage = elem.getChildText("message_gift");
        maxExceedMessage = elem.getChildText("message_count");
        repeatExceedMessage = elem.getChildText("message_repeat");
        timeSpaceMessage = elem.getChildText("message_timespace");
        timeErrorMessage = elem.getChildText("message_time");
        needItemMessage = elem.getChildText("message_item");
        needVarMessage = elem.getChildText("message_var");
        giveOKMessage = elem.getChildText("message_give");
        bagFullMessage = elem.getChildText("message_bag");
        
        gifts.clear();
        List list = elem.getChildren("gift");
        for (int i = 0; i < list.size(); i++) {
            gifts.add(loadGift((Element)list.get(i)));
        }
    }
    
    private GiftDef loadGift(Element elem) {
        GiftDef ret = new GiftDef();
        ret.id = Integer.parseInt(elem.getAttributeValue("id"));
        ret.beginLevel = Integer.parseInt(elem.getAttributeValue("beginlevel"));
        ret.endLevel = Integer.parseInt(elem.getAttributeValue("endlevel"));
        List list = elem.getChildren("needitem");
        for (int i = 0; i < list.size(); i++) {
            Shop.BuyRequirement item = loadItem((Element)list.get(i));
            if (item != null) {
                ret.needItems.add(item);
            }
        }
        list = elem.getChildren("giveitem");
        for (int i = 0; i < list.size(); i++) {
            Shop.BuyRequirement item = loadItem((Element)list.get(i));
            if (item != null) {
                ret.giveItems.add(item);
            }
        }
        return ret;
    }
    
    private Shop.BuyRequirement loadItem(Element elem) {
        BuyRequirement ret = new BuyRequirement();
        ret.type = Shop.nameToType(elem.getAttributeValue("type"));
        ret.amount = Integer.parseInt(elem.getAttributeValue("amount"));
        if (ret.type == Shop.TYPE_ITEM) {
            int itemID = Integer.parseInt(elem.getAttributeValue("itemid"));
            ret.item = owner.findItemOrEquipment(itemID);
            if (ret.item == null) {
                return null;
            }
        }
        return ret;
    }
    
    public Element save() {
        Element ret = new Element("giftgroup");
        ret.addAttribute("id", String.valueOf(id));
        ret.addAttribute("title", title);
        if (getWholeCategoryName() != null) {
            ret.addAttribute("category", getWholeCategoryName());
        }
        
        if (beginDate == null) {
            ret.addAttribute("begindate", "");
        } else {
            ret.addAttribute("begindate", dateFormat.format(beginDate));
        }
        if (endDate == null) {
            ret.addAttribute("enddate", "");
        } else {
            ret.addAttribute("enddate", dateFormat.format(endDate));
        }
        ret.addAttribute("valid", valid ? "true" : "false");
        ret.addAttribute("max", String.valueOf(maxTimes));
        
        String cycleStr = String.valueOf(cycle.amount);
        switch (cycle.type) {
        case CycleDef.CYCLE_HOUR:
            cycleStr += "h";
            break;
        case CycleDef.CYCLE_DAY:
            cycleStr += "d";
            break;
        case CycleDef.CYCLE_WEEK:
            cycleStr += "w";
            break;
        case CycleDef.CYCLE_MONTH:
            cycleStr += "m";
            break;
        }
        ret.addAttribute("cycle", cycleStr);
        
        ret.addAttribute("repeat", String.valueOf(repeatTimes));
        ret.addAttribute("timespace", String.valueOf(timeSpace));
        ret.addAttribute("beginday", String.valueOf(beginDay));
        ret.addAttribute("endday", String.valueOf(endDay));
        ret.addAttribute("begintime", String.valueOf(beginTime));
        ret.addAttribute("endtime", String.valueOf(endTime));

        appendElem(ret, "message_error", errorMessage);
        appendElem(ret, "message_group", groupMessage);
        appendElem(ret, "message_gift", giftMessage);
        appendElem(ret, "message_count", maxExceedMessage);
        appendElem(ret, "message_repeat", repeatExceedMessage);
        appendElem(ret, "message_timespace", timeSpaceMessage);
        appendElem(ret, "message_time", timeErrorMessage);
        appendElem(ret, "message_item", needItemMessage);
        appendElem(ret, "message_var", needVarMessage);
        appendElem(ret, "message_give", giveOKMessage);
        appendElem(ret, "message_bag", bagFullMessage);
        
        for (GiftDef gift : gifts) {
            ret.addContent(saveGift(gift));
        }
        return ret;
    }
    
    private void appendElem(Element parent, String name, String text) {
        Element elem = new Element(name);
        elem.setText(text);
        parent.addContent(elem);
    }
    
    private Element saveGift(GiftDef gift) {
        Element ret = new Element("gift");
        ret.addAttribute("id", String.valueOf(gift.id));
        ret.addAttribute("beginlevel", String.valueOf(gift.beginLevel));
        ret.addAttribute("endlevel", String.valueOf(gift.endLevel));
        for (Shop.BuyRequirement br : gift.needItems) {
            ret.addContent(saveItem(br, "needitem"));
        }
        for (Shop.BuyRequirement br : gift.giveItems) {
            ret.addContent(saveItem(br, "giveitem"));
        }
        return ret;
    }
    
    private Element saveItem(BuyRequirement req, String name) {
        Element ret = new Element(name);
        ret.addAttribute("type", Shop.typeToName(req.type));
        ret.addAttribute("amount", String.valueOf(req.amount));
        if (req.type == Shop.TYPE_ITEM) {
            ret.addAttribute("itemid", String.valueOf(req.item.id));
        }
        return ret;
    }
    
    public boolean depends(DataObject obj) {
        if (obj instanceof Item) {
            for (GiftDef g : gifts) {
                for (Shop.BuyRequirement br : g.giveItems) {
                    if (br.item == obj) {
                        return true;
                    }
                }
                for (Shop.BuyRequirement br : g.needItems) {
                    if (br.item == obj) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
 
    /**
     * ����ָ������Ҽ����Ӧ����Ʒ���塣
     * @param level ��Ҽ���
     * @return ���з�����������Ʒ�һ��
     */
    public GiftDef[] findGifts(int level) {
        List<GiftDef> list = new ArrayList<GiftDef>();
        for (GiftDef gd : gifts) {
            if (gd.beginLevel <= level && level <= gd.endLevel) {
                list.add(gd);
            }
        }
        GiftDef[] ret = new GiftDef[list.size()];
        list.toArray(ret);
        return ret;
    }
    
    /**
     * ����ID������Ʒ�һ��
     * @param id ��Ʒ��ID
     * @return �ҵ�����Ʒ�һ�����û���ҵ�����null
     */
    public GiftDef findGift(int id) {
        for (GiftDef gd : gifts) {
            if (gd.id == id) {
                return gd;
            }
        }
        return null;
    }
    
    /**
     * �滻һ���ı���Ϣ�ڵı����������滻����ı���
     * @param pattern �ı���Ϣģ�壨����������
     * @param gift ��Ҽ����Ӧ����Ʒ����
     * @param acount ������ܹ���ȡ����
     * @param rcount ����ұ���������ȡ����
     * @return �滻����ı���
     */
    public String translateText(String pattern, GiftDef gift, int acount, int rcount) {
        StringBuilder sb = new StringBuilder();
        int start = 0;
        while (true) {
            int cur = pattern.indexOf("${", start);
            if (cur == -1) {
                sb.append(pattern.substring(start));
                break;
            }
            int next = pattern.indexOf('}', cur);
            if (next == -1) {
                sb.append(pattern.substring(start));
                break;
            }
            sb.append(pattern.substring(start, cur));
            String token = pattern.substring(cur + 2, next);
            start = next + 1;
            sb.append(translateVar(token, gift, acount, rcount));
        }
        return sb.toString();
    }
    
    /*
     * �����ı���Ϣ�г��ֵı��������õı���������beginlevel��С����endlevel��󼶱�needitem������Ʒ��
     * giveitem�һ���Ʒ��acount����ȡ������rcount��������ȡ������max�����ȡ������repeat���������ȡ������
     * needtime��ȡʱ�䷶Χ��
     */
    private String translateVar(String var, GiftDef gift, int acount, int rcount) {
        if ("beginlevel".equals(var)) {
            return String.valueOf(gift.beginLevel);
        } else if ("endlevel".equals(var)) {
            return String.valueOf(gift.endLevel);
        } else if ("needitem".equals(var)) {
            return itemsToText(gift.needItems);
        } else if ("needvar".equals(var)) {
            return varsToText(gift.needItems);
        } else if ("giveitem".equals(var)) {
            return itemsToText(gift.giveItems);
        } else if ("acount".equals(var)) {
            return String.valueOf(acount);
        } else if ("rcount".equals(var)) {
            return String.valueOf(rcount);
        } else if ("max".equals(var)) {
            return String.valueOf(maxTimes);
        } else if ("repeat".equals(var)) {
            return String.valueOf(repeatTimes);
        } else if ("needtime".equals(var)) {
            return timeToText();
        } else if ("timespace".equals(var)) {
            int h = timeSpace / 3600;
            int m = (timeSpace  % 3600) / 60;
            if (h == 0) {
                return m + "����";
            } else {
                return h + "Сʱ" + m + "����";
            }
        }
        return "";
    }
    
    /*
     * ��Ʒ�б�ת��Ϊ�ַ�����
     */
    private String itemsToText(ArrayList<Shop.BuyRequirement> items) {
        return Shop.BuyRequirement.toString(items, true, true, false);
    }
    
    /*
     * ��������ת��Ϊ�ַ�����
     */
    private String varsToText(ArrayList<Shop.BuyRequirement> items) {
        StringBuilder sb = new StringBuilder();
        for (Shop.BuyRequirement br : items) {
            if (sb.length() > 0) {
                sb.append("��");
            }
            switch (br.type) {
            case Shop.TYPE_VARIABLE:
                sb.append(br.varDesc);
                break;
            }
        }
        return sb.toString();
    }
    
    /*
     * ʱ������ת��Ϊ�ַ�����
     */
    private String timeToText() {
        if (cycle.type == CycleDef.CYCLE_HOUR) {
            // ���������Сʱ����ʱ��������ÿ���ʱ��
            if (beginTime == -1) {
                if (endTime == -1) {
                    return "ȫ��";
                } else {
                    return "ÿ��" + minuteToText(endTime) + "ǰ";
                }
            } else {
                if (endTime == -1) {
                    return "ÿ��" + minuteToText(beginTime) + "��";
                } else {
                    return "ÿ��" + minuteToText(beginTime) + "��" + minuteToText(endTime);
                }
            }
        }
        if (cycle.type == CycleDef.CYCLE_DAY) {
            // ��������
            String text = cycle.amount == 1 ? "ÿ��" : "ÿ" + cycle.amount + "��";
            if (cycle.amount > 1) {
                if (beginDay == -1) {
                    if (endDay != -1) {
                        text += "�еĵ�" + (beginDay + 1) + "��(��)��";
                    }
                } else {
                    if (endDay == -1) {
                        text += "�еĵ�" + (beginDay + 1) + "��(��)ǰ";
                    } else {
                        text += "�еĵ�" + (beginDay + 1) + "��" + (endDay + 1) + "��(��)";
                    }
                }
            }
            if (beginTime == -1) {
                if (endTime == -1) {
                    return text;
                } else {
                    return "��" + text + minuteToText(endTime) + "ǰ";
                }
            } else {
                if (endTime == -1) {
                    return "��" + text + minuteToText(beginTime) + "��";
                } else {
                    return "��" + text + minuteToText(beginTime) + "��" + minuteToText(endTime);
                }
            }
        }
        String weekdays = "��һ����������";
        if (cycle.type == CycleDef.CYCLE_WEEK) {
            // ��������
            String text = cycle.amount == 1 ? "ÿ��" : "ÿ" + cycle.amount + "��";
            if (cycle.amount > 1) {
                if (beginDay == -1) {
                    if (endDay != -1) {
                        text += "�еĵ�" + (beginDay + 1) + "��(��)��";
                    }
                } else {
                    if (endDay == -1) {
                        text += "�еĵ�" + (beginDay + 1) + "��(��)ǰ";
                    } else {
                        text += "�еĵ�" + (beginDay + 1) + "��" + (endDay + 1) + "��(��)";
                    }
                }
            } else {
                if (beginDay == -1) {
                    if (endDay != -1) {
                        text += "�յ���" + weekdays.charAt(endDay);
                    }
                } else {
                    if (endDay == -1) {
                        text += weekdays.charAt(beginDay) + "������";
                    } else {
                        text += weekdays.charAt(beginDay) + "����" + weekdays.charAt(endDay);
                    }
                }
            }
            if (beginTime == -1) {
                if (endTime == -1) {
                    return text;
                } else {
                    return "��" + text + minuteToText(endTime) + "ǰ";
                }
            } else {
                if (endTime == -1) {
                    return "��" + text + minuteToText(beginTime) + "��";
                } else {
                    return "��" + text + minuteToText(beginTime) + "��" + minuteToText(endTime);
                }
            }
        }
        if (cycle.type == CycleDef.CYCLE_MONTH) {
            // ��������
            String text = cycle.amount == 1 ? "ÿ��" : "ÿ" + cycle.amount + "��";
            if (cycle.amount > 1) {
                if (beginDay == -1) {
                    if (endDay != -1) {
                        text += "�еĵ�" + (beginDay + 1) + "��(��)��";
                    }
                } else {
                    if (endDay == -1) {
                        text += "�еĵ�" + (beginDay + 1) + "��(��)ǰ";
                    } else {
                        text += "�еĵ�" + (beginDay + 1) + "��" + (endDay + 1) + "��(��)";
                    }
                }
            } else {
                if (beginDay == -1) {
                    if (endDay != -1) {
                        text += "1�յ�" + (endDay + 1) + "��";
                    }
                } else {
                    if (endDay == -1) {
                        text += (beginDay + 1) + "�յ���ĩ";
                    } else {
                        text += (beginDay + 1) + "�յ�" + (endDay + 1) + "��";
                    }
                }
            }
            if (beginTime == -1) {
                if (endTime == -1) {
                    return text;
                } else {
                    return "��" + text + minuteToText(endTime) + "ǰ";
                }
            } else {
                if (endTime == -1) {
                    return "��" + text + minuteToText(beginTime) + "��";
                } else {
                    return "��" + text + minuteToText(beginTime) + "��" + minuteToText(endTime);
                }
            }
        }
        return "";
    }
    
    private String minuteToText(int min) {
        int hour = min / 60;
        min = min % 60;
        if (min < 10) {
            return hour + ":0" + min;
        } else {
            return hour + ":" + min;
        }
    }
    
    public boolean isValid(Date time){
        if(!valid)
            return valid;
        if(beginDate==null&&endDate==null)
            return true;
        if(beginDate==null)
            return time.getTime()<endDate.getTime();
        if(endDate==null)
            return time.getTime()>beginDate.getTime();
        return time.getTime()>beginDate.getTime()&&time.getTime()<endDate.getTime();
    }
    
    /**
     * ������������Ƿ�֧�������á�
     * @return
     */
    public boolean supportDaySetting() {
        return supportDaySetting(cycle.type, cycle.amount);
    }
    
    
    
    public static boolean supportDaySetting(int type, int amount) {
        if (type == CycleDef.CYCLE_HOUR) {
            return false;
        }
        if (type == CycleDef.CYCLE_DAY) {
            return amount > 1;
        }
        return true;
    }

    /**
     * �������������Խ��й��ʻ������������Ҫ���ʻ����ַ���������ȡ������context�в��ҷ�������
     * @param context
     * @return �����ĳ�����Ա��滻������true�����򷵻�false��
     */
    public boolean i18n(I18NContext context) {
        boolean changed = false;
        String tmp = context.input(title, "GiftGroup");
        if (tmp != null) {
            title = tmp;
            changed = true;
        }
        tmp = context.input(errorMessage, "GiftGroup");
        if (tmp != null) {
            errorMessage = tmp;
            changed = true;
        }
        tmp = context.input(groupMessage, "GiftGroup");
        if (tmp != null) {
            groupMessage = tmp;
            changed = true;
        }
        tmp = context.input(giftMessage, "GiftGroup");
        if (tmp != null) {
            giftMessage = tmp;
            changed = true;
        }
        tmp = context.input(maxExceedMessage, "GiftGroup");
        if (tmp != null) {
            maxExceedMessage = tmp;
            changed = true;
        }
        tmp = context.input(repeatExceedMessage, "GiftGroup");
        if (tmp != null) {
            repeatExceedMessage = tmp;
            changed = true;
        }
        tmp = context.input(timeSpaceMessage, "GiftGroup");
        if (tmp != null) {
            timeSpaceMessage = tmp;
            changed = true;
        }
        tmp = context.input(timeErrorMessage, "GiftGroup");
        if (tmp != null) {
            timeErrorMessage = tmp;
            changed = true;
        }
        tmp = context.input(needItemMessage, "GiftGroup");
        if (tmp != null) {
            needItemMessage = tmp;
            changed = true;
        }
        tmp = context.input(needVarMessage, "GiftGroup");
        if (tmp != null) {
            needVarMessage = tmp;
            changed = true;
        }
        tmp = context.input(giveOKMessage, "GiftGroup");
        if (tmp != null) {
            giveOKMessage = tmp;
            changed = true;
        }
        tmp = context.input(bagFullMessage, "GiftGroup");
        if (tmp != null) {
            bagFullMessage = tmp;
            changed = true;
        }        
        return changed;
    }
}
