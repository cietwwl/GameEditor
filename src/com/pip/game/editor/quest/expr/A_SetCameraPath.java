package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.FunctionCall;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.property.PathPropertyDescriptor;

public class A_SetCameraPath extends AbstractExpr {
    public int path = -1;
    public int speed =1 ;

    public IExpr createNew(QuestInfo qinfo) {
        // TODO Auto-generated method stub
        return new A_SetCameraPath();
    }

    public String getExpression() {
        // TODO Auto-generated method stub
        return "SetCameraPath(" + path + " , " + speed + ")";
    }

    public String getName() {
        // TODO Auto-generated method stub
        return "设置镜头路径";
    }

    public boolean isCondition() {
        // TODO Auto-generated method stub
        return false;
    }

    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("SetCameraPath") && expr.getRightExpr() == null) {
            FunctionCall fc = expr.getLeftExpr().getFunctionCall();
            if (fc.getParamCount() != 2) {
                return null;
            }
            Expression param1 = fc.getParam(0);
            Expression param2 = fc.getParam(1);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                param2.getRightExpr() == null && param2.getLeftExpr().type == Expr0.TYPE_NUMBER) {
                A_SetCameraPath ret = (A_SetCameraPath)createNew(null);
                ret.path = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
                ret.speed = PQEUtils.translateNumberConstant(param2.getLeftExpr().value);
                return ret;
            }
        }
        return null;
    }

    public String toNatureString() {
        // TODO Auto-generated method stub
        return "路径ID："+path + "速度" +speed;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        // TODO Auto-generated method stub
        return new IPropertyDescriptor[]{
                new PathPropertyDescriptor("path","路径ID"),
                new TextPropertyDescriptor("speed","速度")
        };
        
    }

    public Object getPropertyValue(Object id) {
        // TODO Auto-generated method stub
        if("path".equals(id)){
            if(path == -1){
                return "未指定";
            }else{
            return String.valueOf(path);
            }
        }else if("speed".equals(id)){
            return String.valueOf(speed);
        }
        return null;
    }

    public void setPropertyValue(Object id, Object value) {
        // TODO Auto-generated method stub
        if("path".equals(id)){
            int newValue = (Integer)value;
            if(newValue != path){
                path = newValue;
                fireValueChanged();
            }
        }else if("speed".equals(id)){
            int newValue = Integer.parseInt((String)value);
            if(newValue != speed){
                speed = newValue;
                fireValueChanged();
            }
        }

    }

}
