package com.pip.game.editor.ai.expr;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.ProjectData;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.editor.property.SkillPropertyDescriptor;
import com.pip.game.editor.quest.expr.AbstractExpr;
import com.pip.game.editor.quest.expr.IExpr;
import com.pip.util.Utils;

/**
 * ���ʽģ�壬�������ʩ��һ�����ܡ�
 * @author lighthu
 */
public class A_AI_CastSkill extends AbstractExpr {
    public int[] skillIDs = new int[10];
    public int[] skillLevels = new int[10];

    /**
     * ����ָ��ȫ�ֱ�����ģ�塣
     * @param name ȫ�ֱ�������
     */
    public A_AI_CastSkill() {
    }
    
    /**
     * �ж����ģ����һ����������һ��������
     */
    public boolean isCondition() {
        return false;
    }

    /**
     * ȡ�����ɵı��ʽ��
     */
    public String getExpression() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < skillIDs.length; i++) {
            if (skillIDs[i] > 0) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append((skillIDs[i] << 16) | skillLevels[i]);
            }
        }
        return "AI_CastSkill(\"" + sb.toString() + "\")";
    }

    /**
     * ��ģ�崴���µı��ʽƬ�Ρ�
     */
    public IExpr createNew(QuestInfo qinfo) {
        return new A_AI_CastSkill();
    }

    /**
     * ȡ��ģ�����ơ�
     */
    public String getName() {
        return "����ʩ�ż���...";
    }

    /**
     * ת��Ϊ��Ȼ���Ա�ʾ��
     */
    public String toNatureString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < skillIDs.length; i++) {
            if (skillIDs[i] > 0) {
                if (sb.length() > 0) {
                    sb.append(" | ");
                }
                sb.append(SkillConfig.toString(ProjectData.getActiveProject(), skillIDs[i]));
            }
        }
        return "����ʩ�ż��ܣ�" +  sb.toString();
    }
    
    /**
     * ������ʽΪid1,id2,id3�ļ����б�����skillIDs��skillLevels���顣
     * @param str
     */
    protected void parseSkillList(String str) {
        String[] ids = Utils.splitString(str, ',');
        for (int i = 0; i < ids.length; i++) {
            try {
                int id = Integer.parseInt(ids[i]);
                skillIDs[i] = id >> 16;
                skillLevels[i] = id & 0xFFFF;
            } catch (Exception e) {
            }
        }
    }

    /**
     * ʶ��һ�����ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı��ʽƬ�ζ��󣬷��򷵻�null��
     */
    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("AI_CastSkill") && expr.getRightExpr() == null) {
            if (expr.getLeftExpr().getFunctionCall().getParamCount() != 1) {
                return null;
            }
            Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(0);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_STRING) {
                A_AI_CastSkill ret = (A_AI_CastSkill)createNew(qinfo);
                ret.parseSkillList(PQEUtils.translateStringConstant(param1.getLeftExpr().value));
                return ret;
            }
        }
        return null;
    }

    // ������IPropertySource�ӿڵ�ʵ��

    /**
     * ȡ�����������������ģ����1��������������
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        List<IPropertyDescriptor> list = new ArrayList<IPropertyDescriptor>();
        for (int i = 0; i < skillIDs.length; i++) {
            list.add(new SkillPropertyDescriptor("skillid" + i, "����"));
            list.add(new TextPropertyDescriptor("skilllevel" + i, "�ȼ�"));
        }
        IPropertyDescriptor[] ret = new IPropertyDescriptor[list.size()];
        list.toArray(ret);
        return ret;
    }

    /**
     * ȡ�����Ե�ǰֵ��
     */
    public Object getPropertyValue(Object id) {
        String idStr = (String)id;
        if (idStr.startsWith("skillid")) {
            int index = Integer.parseInt(idStr.substring("skillid".length()));
            return new Integer(skillIDs[index]);
        } else if (idStr.startsWith("skilllevel")) {
            int index = Integer.parseInt(idStr.substring("skilllevel".length()));
            return String.valueOf(skillLevels[index]);
        }
        return null;
    }

    /**
     * �������Ե�ǰֵ��
     */
    public void setPropertyValue(Object id, Object value) {
        String idStr = (String)id;
        if (idStr.startsWith("skillid")) {
            int index = Integer.parseInt(idStr.substring("skillid".length()));
            int newValue = ((Integer)value).intValue();
            if (newValue != skillIDs[index]) {
                skillIDs[index] = newValue;
                fireValueChanged();
            }
        } else if (idStr.startsWith("skilllevel")) {
            int index = Integer.parseInt(idStr.substring("skilllevel".length()));
            int newValue;
            try {
                newValue = Integer.parseInt((String)value);
            } catch (Exception e) {
                return;
            }
            if (newValue != skillLevels[index]) {
                skillLevels[index] = newValue;
                fireValueChanged();
            }
        }
    }
}
