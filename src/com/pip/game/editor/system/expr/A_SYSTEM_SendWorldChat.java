package com.pip.game.editor.system.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.quest.expr.AbstractExpr;
import com.pip.game.editor.quest.expr.IExpr;

/**
 * @file C_SYSTEM_SendWorldChat.java
 * @author zxyu
 * @version 1.0.0
 * @date 2012-8-6
 **/
public class A_SYSTEM_SendWorldChat extends AbstractExpr {
    public String key = "";
    
    public A_SYSTEM_SendWorldChat(){
        
    }

    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("SYSTEM_SendWorldChat")) {
            if (expr.getLeftExpr().getFunctionCall().getParamCount() != 1) {
                return null;
            }
            Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(0);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_STRING
                    ) {
                A_SYSTEM_SendWorldChat ret = (A_SYSTEM_SendWorldChat)createNew(qinfo);
                ret.key = PQEUtils.translateStringConstant(param1.getLeftExpr().value);
                return ret;
            }
        }
        return null;
    }

    public IExpr createNew(QuestInfo qinfo) {
        return new A_SYSTEM_SendWorldChat();
    }

    public boolean isCondition() {
        return true;
    }

    public String getName() {
        return "发送世界聊";
    }

    public String getExpression() {
        return "SYSTEM_SendWorldChat(\"" + PQEUtils.reverseConv(key) + "\")";
    }

    public String toNatureString() {
        return "发送世界聊:" + key;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] {
                new TextPropertyDescriptor("key", "发送内容的KEY")
        };
    }

    public Object getPropertyValue(Object id) {
        if ("key".equals(id)) {
            return key;
        }
        return null;
    }

    public void setPropertyValue(Object id, Object value) {
        if("key".equals(id)){
            key = (String)value;
        }
    }
    
}
