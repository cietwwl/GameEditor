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
 * 表达式模板，选择单个盟友。
 * @author lighthu
 */
public class C_AI_ChooseSingleFriend extends AbstractExpr {
    public int minDist = -1;
    public int maxDist = -1;
    public int minHp = -1;
    public int maxHp = -1;
    public int minHpPer = -1;
    public int maxHpPer = -1;
    public int orderType = 1;
    public int index = 1;
    public boolean checkTrue = true;

    /**
     * 构造指定全局变量的模板。
     * @param name 全局变量名称
     */
    public C_AI_ChooseSingleFriend() {
    }
    
    /**
     * 判断这个模板是一个条件还是一个动作。
     */
    public boolean isCondition() {
        return true;
    }

    /**
     * 取得生成的表达式。
     */
    public String getExpression() {
        String left = "AI_ChooseSingleFriend(" + minDist + "," + maxDist + "," + minHp + "," + maxHp + "," + minHpPer + "," + maxHpPer + "," + orderType + "," + index + ")";
        if (checkTrue) {
            return left;
        } else {
            return left + " == false";
        }
    }

    /**
     * 用模板创建新的表达式片段。
     */
    public IExpr createNew(QuestInfo qinfo) {
        return new C_AI_ChooseSingleFriend();
    }

    /**
     * 取得模板名称。
     */
    public String getName() {
        return "选择单个盟友...";
    }

    /**
     * 转换为自然语言表示。
     */
    public String toNatureString() {
        if (checkTrue) {
            return "选择单个盟友成功";
        } else {
            return "选择单个盟友失败";
        }
    }

    /**
     * 识别一个表达式是否匹配本模板。如果匹配，返回一个新的表达式片段对象，否则返回null。
     */
    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("AI_ChooseSingleFriend")) {
            if (expr.getLeftExpr().getFunctionCall().getParamCount() != 8) {
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
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                    param2.getRightExpr() == null && param2.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                    param3.getRightExpr() == null && param3.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                    param4.getRightExpr() == null && param4.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                    param5.getRightExpr() == null && param5.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                    param6.getRightExpr() == null && param6.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                    param7.getRightExpr() == null && param7.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                    param8.getRightExpr() == null && param8.getLeftExpr().type == Expr0.TYPE_NUMBER
                    ) {
                C_AI_ChooseSingleFriend ret = (C_AI_ChooseSingleFriend)createNew(qinfo);
                ret.minDist = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
                ret.maxDist = PQEUtils.translateNumberConstant(param2.getLeftExpr().value);
                ret.minHp = PQEUtils.translateNumberConstant(param3.getLeftExpr().value);
                ret.maxHp = PQEUtils.translateNumberConstant(param4.getLeftExpr().value);
                ret.minHpPer = PQEUtils.translateNumberConstant(param5.getLeftExpr().value);
                ret.maxHpPer = PQEUtils.translateNumberConstant(param6.getLeftExpr().value);
                ret.orderType = PQEUtils.translateNumberConstant(param7.getLeftExpr().value);
                ret.index = PQEUtils.translateNumberConstant(param8.getLeftExpr().value);
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

    // 下面是IPropertySource接口的实现

    /**
     * 取得属性描述符。这个模板有1个参数：参数。
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] {
                new TextPropertyDescriptor("minDist", "最小距离(码)"),
                new TextPropertyDescriptor("maxDist", "最大距离(码)"),
                new TextPropertyDescriptor("minHp", "最小血量"),
                new TextPropertyDescriptor("maxHp", "最大血量"),
                new TextPropertyDescriptor("minHpPer", "最小血量百分比"),
                new TextPropertyDescriptor("maxHpPer", "最大血量百分比"),
                new ComboBoxPropertyDescriptor("orderType", "排序标准", new String[] { "距离", "血量", "血量百分比" }),
                new ComboBoxPropertyDescriptor("indexType", "选择方式", new String[] { "从高到底选择", "从低到高选择", "随机选择" }),
                new TextPropertyDescriptor("index", "选择序号(1-N)"),
                new ComboBoxPropertyDescriptor("checkTrue", "检查成功", new String[] { "是", "否" })
        };
    }

    /**
     * 取得属性当前值。
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
        } else if ("indexType".equals(id)) {
            int it;
            if (index < 0) {
                it = 1;
            } else if (index == 0) {
                it = 2;
            } else {
                it = 0;
            }
            return new Integer(it);
        } else if ("index".equals(id)) {
            return String.valueOf(index);
        } else if ("checkTrue".equals(id)) {
            return checkTrue ? 0 : 1;
        }
        return null;
    }

    /**
     * 设置属性当前值。
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
        } else if ("indexType".equals(id)) {
            int newValue = ((Integer)value).intValue();
            if (newValue == 0) {
                newValue = Math.abs(index);
            } else if (newValue == 1) {
                newValue = -Math.abs(index);
            } else {
                newValue = 0;
            }
            if (newValue != index) {
                index = newValue;
                fireValueChanged();
            }
        } else if ("index".equals(id)) {
            int newValue;
            try {
                newValue = Integer.parseInt((String)value);
                if (index < 0) {
                    newValue = -Math.abs(newValue);
                } else if (index == 0) {
                    newValue = 0;
                } else if (index > 0) {
                    newValue = Math.abs(newValue);
                }
            } catch (Exception e) {
                return;
            }
            if (newValue != index) {
                index = newValue;
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
