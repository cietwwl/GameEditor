package com.pip.game.editor.system.expr;

import java.util.HashMap;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.property.RichTextPropertyDescriptor;
import com.pip.game.editor.quest.expr.AbstractExpr;
import com.pip.game.editor.quest.expr.IExpr;

/**
 * @file C_SYSTEM_AddChat.java
 * @author zxyu
 * @version 1.0.0
 * @date 2012-8-17
 **/
public class A_SYSTEM_AddChat extends AbstractExpr {
    
    public static HashMap<String, String> systemChats = new HashMap<String, String>();
    
    public String value = "";
    public String key = "";
    
    public A_SYSTEM_AddChat(){
        
    }

    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("SYSTEM_AddChat")) {
            if (expr.getLeftExpr().getFunctionCall().getParamCount() != 2) {
                return null;
            }
            Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(0);
            Expression param2 = expr.getLeftExpr().getFunctionCall().getParam(1);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_STRING
                    || param2.getRightExpr() == null && param2.getLeftExpr().type == Expr0.TYPE_STRING
                    ) {
                A_SYSTEM_AddChat ret = (A_SYSTEM_AddChat)createNew(qinfo);
                ret.key = PQEUtils.translateStringConstant(param1.getLeftExpr().value);
                ret.value = PQEUtils.translateStringConstant(param2.getLeftExpr().value);
                return ret;
            }
        }
        return null;
    }

    public IExpr createNew(QuestInfo qinfo) {
        return new A_SYSTEM_AddChat();
    }

    public boolean isCondition() {
        return true;
    }

    public String getName() {
        return "Ìí¼Ó×Ö·û´®";
    }

    public String getExpression() {
        return "SYSTEM_AddChat(\"" + PQEUtils.reverseConv(key) + "\",\"" + PQEUtils.reverseConv(value) + "\")";
    }

    public String toNatureString() {
        return "Ìí¼Ó×Ö·û´®key:" + key + " value:" + value;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] {
                new TextPropertyDescriptor("key", "KEY"),
                new RichTextPropertyDescriptor("value", "VALUE", questInfo)
        };
    }

    public Object getPropertyValue(Object id) {
        if ("key".equals(id)) {
            return key;
        }
        if ("value".equals(id)) {
            return value;
        }
        return null;
    }

    public void setPropertyValue(Object id, Object value) {
        if("key".equals(id)){
            key = (String)value;
        }
        if("value".equals(id)){
            this.value = (String)value;
        }
    }
    
}
