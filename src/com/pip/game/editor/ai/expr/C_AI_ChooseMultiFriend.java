package com.pip.game.editor.ai.expr;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.quest.expr.AbstractExpr;
import com.pip.game.editor.quest.expr.IExpr;

/**
 * ���ʽģ�壬ѡ�������ѡ�
 * @author lighthu
 */
public class C_AI_ChooseMultiFriend extends AbstractExpr {
    public int minDist = -1;
    public int maxDist = -1;
    public int minHp = -1;
    public int maxHp = -1;
    public int minHpPer = -1;
    public int maxHpPer = -1;
    public int orderType = 1;
    public int chooseType = 0;
    public int startIndex = 1;
    public int maxCount = 1;
    public boolean checkTrue = true;

    /**
     * ����ָ��ȫ�ֱ�����ģ�塣
     * @param name ȫ�ֱ�������
     */
    public C_AI_ChooseMultiFriend() {
    }
    
    /**
     * �ж����ģ����һ����������һ��������
     */
    public boolean isCondition() {
        return true;
    }

    /**
     * ȡ�����ɵı��ʽ��
     */
    public String getExpression() {
        String left = "AI_ChooseMultiFriend(" + minDist + "," + maxDist + "," + minHp + "," + maxHp + "," + minHpPer + "," + maxHpPer + "," + orderType + "," + chooseType + "," + startIndex + "," + maxCount + ")";
        if (checkTrue) {
            return left;
        } else {
            return left + " == false";
        }
    }

    /**
     * ��ģ�崴���µı��ʽƬ�Ρ�
     */
    public IExpr createNew(QuestInfo qinfo) {
        return new C_AI_ChooseMultiFriend();
    }

    /**
     * ȡ��ģ�����ơ�
     */
    public String getName() {
        return "ѡ��������...";
    }

    /**
     * ת��Ϊ��Ȼ���Ա�ʾ��
     */
    public String toNatureString() {
        if (checkTrue) {
            return "ѡ�������ѳɹ�";
        } else {
            return "ѡ��������ʧ��";
        }
    }

    /**
     * ʶ��һ�����ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı��ʽƬ�ζ��󣬷��򷵻�null��
     */
    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("AI_ChooseMultiFriend")) {
            if (expr.getLeftExpr().getFunctionCall().getParamCount() != 10) {
                return null;
            }
            Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(0);
            Expression param2 = expr.getLeftExpr().getFunctionCall().getParam(1);
            Expression param3 = expr.getLeftExpr().getFunctionCall().getParam(2);
            Expression param4 = expr.getLeftExpr().getFunctionCall().getParam(3);
            Expression param5 = expr.getLeftExpr().getFunctionCall().getParam(4);
            Expression param6 = expr.getLeftExpr().getFunctionCall().getParam(5);
            Expression param7 = expr.getLeftExpr().getFunctionCall().getParam(6);
            Expression param8 = expr.getLeftExpr().getFunctionCall().getParam(7);
            Expression param9 = expr.getLeftExpr().getFunctionCall().getParam(8);
            Expression param10 = expr.getLeftExpr().getFunctionCall().getParam(9);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                    param2.getRightExpr() == null && param2.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                    param3.getRightExpr() == null && param3.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                    param4.getRightExpr() == null && param4.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                    param5.getRightExpr() == null && param5.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                    param6.getRightExpr() == null && param6.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                    param7.getRightExpr() == null && param7.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                    param8.getRightExpr() == null && param8.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                    param9.getRightExpr() == null && param9.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                    param10.getRightExpr() == null && param10.getLeftExpr().type == Expr0.TYPE_NUMBER
                    ) {
                C_AI_ChooseMultiFriend ret = (C_AI_ChooseMultiFriend)createNew(qinfo);
                ret.minDist = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
                ret.maxDist = PQEUtils.translateNumberConstant(param2.getLeftExpr().value);
                ret.minHp = PQEUtils.translateNumberConstant(param3.getLeftExpr().value);
                ret.maxHp = PQEUtils.translateNumberConstant(param4.getLeftExpr().value);
                ret.minHpPer = PQEUtils.translateNumberConstant(param5.getLeftExpr().value);
                ret.maxHpPer = PQEUtils.translateNumberConstant(param6.getLeftExpr().value);
                ret.orderType = PQEUtils.translateNumberConstant(param7.getLeftExpr().value);
                ret.chooseType = PQEUtils.translateNumberConstant(param8.getLeftExpr().value);
                ret.startIndex = PQEUtils.translateNumberConstant(param9.getLeftExpr().value);
                ret.maxCount = PQEUtils.translateNumberConstant(param10.getLeftExpr().value);
                if (expr.getRightExpr() == null) {
                    return ret;
                } else if (expr.getRightExpr().type == Expr0.TYPE_IDENTIFIER && expr.getRightExpr().value.equals("false")) {
                    ret.checkTrue = false;
                    return ret;
                }
            }
        }
        return null;
    }

    // ������IPropertySource�ӿڵ�ʵ��

    /**
     * ȡ�����������������ģ����1��������������
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] {
                new TextPropertyDescriptor("minDist", "��С����(��)"),
                new TextPropertyDescriptor("maxDist", "������(��)"),
                new TextPropertyDescriptor("minHp", "��СѪ��"),
                new TextPropertyDescriptor("maxHp", "���Ѫ��"),
                new TextPropertyDescriptor("minHpPer", "��СѪ���ٷֱ�"),
                new TextPropertyDescriptor("maxHpPer", "���Ѫ���ٷֱ�"),
                new ComboBoxPropertyDescriptor("orderType", "�����׼", new String[] { "����", "Ѫ��", "Ѫ���ٷֱ�" }),
                new ComboBoxPropertyDescriptor("chooseType", "ѡ��ʽ", new String[] { "�Ӹߵ���ѡ��", "�ӵ͵���ѡ��", "���ѡ��" }),
                new TextPropertyDescriptor("startIndex", "��ʼ���(1-N)"),
                new TextPropertyDescriptor("maxCount", "�������"),
                new ComboBoxPropertyDescriptor("checkTrue", "���ɹ�", new String[] { "��", "��" })
        };
    }

    /**
     * ȡ�����Ե�ǰֵ��
     */
    public Object getPropertyValue(Object id) {
        if ("minDist".equals(id)) {
            if (minDist < 0) {
                return "-1";
            } else {
                return  String.valueOf(minDist / 8.0);
            }
        } else if ("maxDist".equals(id)) {
            if (maxDist < 0) {
                return "-1";
            } else {
                return String.valueOf(maxDist / 8.0);
            }
        } else if ("minHp".equals(id)) {
            return String.valueOf(minHp);
        } else if ("maxHp".equals(id)) {
            return String.valueOf(maxHp);
        } else if ("minHpPer".equals(id)) {
            return String.valueOf(minHpPer);
        } else if ("maxHpPer".equals(id)) {
            return String.valueOf(maxHpPer);
        } else if ("orderType".equals(id)) {
            return new Integer(orderType - 1);
        } else if ("chooseType".equals(id)) {
            return new Integer(chooseType);
        } else if ("startIndex".equals(id)) {
            return String.valueOf(startIndex);
        } else if ("maxCount".equals(id)) {
            return String.valueOf(maxCount);
        } else if ("checkTrue".equals(id)) {
            return checkTrue ? 0 : 1;
        }
        return null;
    }

    /**
     * �������Ե�ǰֵ��
     */
    public void setPropertyValue(Object id, Object value) {
        if ("minDist".equals(id)) {
            int newValue;
            try {
                double dv = Double.parseDouble((String)value);
                if (dv < 0) {
                    newValue = -1;
                } else {
                    newValue = (int)(dv * 8);
                }
            } catch (Exception e) {
                return;
            }
            if (newValue != minDist) {
                minDist = newValue;
                fireValueChanged();
            }
        } else if ("maxDist".equals(id)) {
            int newValue;
            try {
                double dv = Double.parseDouble((String)value);
                if (dv < 0) {
                    newValue = -1;
                } else {
                    newValue = (int)(dv * 8);
                }
            } catch (Exception e) {
                return;
            }
            if (newValue != maxDist) {
                maxDist = newValue;
                fireValueChanged();
            }
        } else if ("minHp".equals(id)) {
            int newValue;
            try {
                newValue = Integer.parseInt((String)value);
            } catch (Exception e) {
                return;
            }
            if (newValue != minHp) {
                minHp = newValue;
                fireValueChanged();
            }
        } else if ("maxHp".equals(id)) {
            int newValue;
            try {
                newValue = Integer.parseInt((String)value);
            } catch (Exception e) {
                return;
            }
            if (newValue != maxHp) {
                maxHp = newValue;
                fireValueChanged();
            }
        } else if ("minHpPer".equals(id)) {
            int newValue;
            try {
                newValue = Integer.parseInt((String)value);
                if (newValue > 100) {
                    newValue = 100;
                }
            } catch (Exception e) {
                return;
            }
            if (newValue != minHpPer) {
                minHpPer = newValue;
                fireValueChanged();
            }
        } else if ("maxHpPer".equals(id)) {
            int newValue;
            try {
                newValue = Integer.parseInt((String)value);
                if (newValue > 100) {
                    newValue = 100;
                }
            } catch (Exception e) {
                return;
            }
            if (newValue != maxHpPer) {
                maxHpPer = newValue;
                fireValueChanged();
            }
        } else if ("orderType".equals(id)) {
            int newValue = ((Integer)value).intValue() + 1;
            if (newValue != orderType) {
                orderType = newValue;
                fireValueChanged();
            }
        } else if ("chooseType".equals(id)) {
            int newValue = ((Integer)value).intValue();
            if (newValue != chooseType) {
                chooseType = newValue;
                fireValueChanged();
            }
        } else if ("startIndex".equals(id)) {
            int newValue;
            try {
                newValue = Integer.parseInt((String)value);
                if (newValue < 1) {
                    newValue = 1;
                } else if (newValue > 100) {
                    newValue = 100;
                }
            } catch (Exception e) {
                return;
            }
            if (newValue != startIndex) {
                startIndex = newValue;
                fireValueChanged();
            }
        } else if ("maxCount".equals(id)) {
            int newValue;
            try {
                newValue = Integer.parseInt((String)value);
                if (newValue < 1) {
                    newValue = 1;
                } else if (newValue > 100) {
                    newValue = 100;
                }
            } catch (Exception e) {
                return;
            }
            if (newValue != maxCount) {
                maxCount = newValue;
                fireValueChanged();
            }
        } else if ("checkTrue".equals(id)) {
            boolean newValue = ((Integer)value).intValue() == 0;
            if (newValue != checkTrue) {
                checkTrue = newValue;
                fireValueChanged();
            }
        }
    }
}
