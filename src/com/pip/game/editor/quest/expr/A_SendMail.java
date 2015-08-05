package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.map.GameMapNPC;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.FunctionCall;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.EditorApplication;
import com.pip.game.editor.property.ItemPropertyDescriptor;
import com.pip.game.editor.property.NPCPropertyDescriptor;
import com.pip.game.editor.property.RichTextPropertyDescriptor;

public class A_SendMail extends AbstractExpr {
    public String npcname;
    public String title;
    public String content;
    public int itemID;
    public int count;
    
    



    public A_SendMail() {
        npcname = "";
        title = "";
        content = "";
        itemID = -1;
        count = 0;
        

        
    }

    public IExpr createNew(QuestInfo qinfo) {
        // TODO Auto-generated method stub
        return new A_SendMail();
    }

    public String getExpression() {
        // TODO Auto-generated method stub
        return "SendMail("+"\"" + PQEUtils.reverseConv(npcname)  +"\""+ ",\"" +PQEUtils.reverseConv(title) + "\""+",\""+ PQEUtils.reverseConv(content)+ "\","+itemID + ", " + count +")";
    }

    public String getName() {
        // TODO Auto-generated method stub
        return "发送信件";
    }

    public boolean isCondition() {
        // TODO Auto-generated method stub
        return false;
    }

    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        // TODO Auto-generated method stub
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC
                && expr.getLeftExpr().getFunctionCall().funcName.equals("SendMail") && expr.getRightExpr() == null) {
            FunctionCall fc = expr.getLeftExpr().getFunctionCall();
            if (fc.getParamCount() != 5) {
                return null;
            }
            Expression param1 = fc.getParam(0);
            Expression param2 = fc.getParam(1);
            Expression param3 = fc.getParam(2);
            Expression param4 = fc.getParam(3);
            Expression param5 = fc.getParam(4);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_STRING

                    && param2.getRightExpr() == null && param2.getLeftExpr().type == Expr0.TYPE_STRING
                    && param3.getRightExpr() == null && param3.getLeftExpr().type == Expr0.TYPE_STRING
                    && param4.getRightExpr() == null && param4.getLeftExpr().type == Expr0.TYPE_NUMBER
                    && param5.getRightExpr() == null && param5.getLeftExpr().type == Expr0.TYPE_NUMBER) {
                A_SendMail ret = (A_SendMail) createNew(qinfo);
                ret.npcname = PQEUtils.translateStringConstant(param1.getLeftExpr().value);
                ret.title = PQEUtils.translateStringConstant(param2.getLeftExpr().value);
                ret.content = PQEUtils.translateStringConstant(param3.getLeftExpr().value);
                ret.itemID = PQEUtils.translateNumberConstant(param4.getLeftExpr().value);
                ret.count = PQEUtils.translateNumberConstant(param5.getLeftExpr().value);

                return ret;
            }
        }
        return null;
    }

    public String toNatureString() {
        return npcname + "发送邮件：" + title;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        // TODO Auto-generated method stub
        return new IPropertyDescriptor[] { 
                new TextPropertyDescriptor("npcname", "发件人"),
                new RichTextPropertyDescriptor("title", "标题", questInfo),
                new RichTextPropertyDescriptor("content", "内容", questInfo),
                new ItemPropertyDescriptor("itemID", "物品"),
                new TextPropertyDescriptor("count", "数量") };
        
    }

    public Object getPropertyValue(Object id) {
        // TODO Auto-generated method stub
        
        if ("npcname".equals(id)) {
            return npcname;
        }
        else if("title".equals(id)){
            return title;
        }
        else if ("content".equals(id)) {
            return content;
        }
        else if ("itemID".equals(id)) {
            return itemID;
        }
        else if ("count".equals(id)) {
            return String.valueOf(count);
        }
        
        return null;
    }

    public void setPropertyValue(Object id, Object value) {
        // TODO Auto-generated method stub
        
        if ("npcname".equals(id)) {
            String  newValue = (String)value;
            if (newValue != npcname) {
                npcname = newValue;
                fireValueChanged();
            }
        }
        else if ("title".equals(id)) {
            String newValue = (String) value;
            if (newValue != title) {
                title = newValue;
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
  
        else if ("itemID".equals(id)) {
            int newValue = ((Integer) value).intValue();
            if (newValue != itemID) {
                itemID = newValue;
                fireValueChanged();
            }
        }
        else if ("count".equals(id)) {
            int newValue = Integer.parseInt((String)value);
            if (newValue != count) {
                count = newValue;
                fireValueChanged();
            }
        }
      

    }

}
