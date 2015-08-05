package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.FunctionCall;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.property.RichTextPropertyDescriptor;

public class A_SendChat extends AbstractExpr {
    public String npcname;
    public String content;
    public int type;
    
    public A_SendChat() {
        npcname = "";
        content = "";
        type = 0;
    }

    public IExpr createNew(QuestInfo qinfo) {
        return new A_SendChat();
    }

    public String getExpression() {
        return "SendChat("+"\"" + PQEUtils.reverseConv(npcname) + "\",\""+ PQEUtils.reverseConv(content)+ "\"," + type + ")";
    }

    public String getName() {
        return "发送消息";
    }

    public boolean isCondition() {
        return false;
    }

    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC
                && expr.getLeftExpr().getFunctionCall().funcName.equals("SendChat") && expr.getRightExpr() == null) {
            FunctionCall fc = expr.getLeftExpr().getFunctionCall();
            if (fc.getParamCount() != 3) {
                return null;
            }
            Expression param1 = fc.getParam(0);
            Expression param2 = fc.getParam(1);
            Expression param3 = fc.getParam(2);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_STRING
                    && param2.getRightExpr() == null && param2.getLeftExpr().type == Expr0.TYPE_STRING
                    && param3.getRightExpr() == null && param3.getLeftExpr().type == Expr0.TYPE_NUMBER
                    ) {
                A_SendChat ret = (A_SendChat) createNew(qinfo);
                ret.npcname = PQEUtils.translateStringConstant(param1.getLeftExpr().value);
                ret.content = PQEUtils.translateStringConstant(param2.getLeftExpr().value);
                ret.type = PQEUtils.translateNumberConstant(param3.getLeftExpr().value);
                return ret;
            }
        }
        return null;
    }

    public String toNatureString() {
        return npcname + "发送消息：" + content;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] { 
                new TextPropertyDescriptor("npcname", "发送人"),
                new RichTextPropertyDescriptor("content", "内容", questInfo),
                new ComboBoxPropertyDescriptor("type", "类型", new String[] { "私聊", "系统消息", "狮子吼" }),
                };
        
    }

    public Object getPropertyValue(Object id) {
        if ("npcname".equals(id)) {
            return npcname;
        }
        else if ("content".equals(id)) {
            return content;
        }
        else if ("type".equals(id)) {
            return new Integer(type);
        }
        return null;
    }

    public void setPropertyValue(Object id, Object value) {
        if ("npcname".equals(id)) {
            String  newValue = (String)value;
            if (newValue != npcname) {
                npcname = newValue;
                fireValueChanged();
            }
        }
        else if ("content".equals(id)) {
            String newValue = (String) value;
            if (newValue != content) {
                content = newValue;
                fireValueChanged();
            }
        }
        else if ("type".equals(id)) {
            int newValue = ((Integer)value).intValue();
            if (newValue != type) {
                type = newValue;
                fireValueChanged();
            }
        }
    }
}
