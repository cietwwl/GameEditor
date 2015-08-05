package com.pip.game.editor.ai.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.FunctionCall;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.quest.expr.AbstractExpr;
import com.pip.game.editor.quest.expr.IExpr;

public class A_AI_Summon extends AbstractExpr {

    /**
     * 召唤的NPC所在地图
     */
    int mapId;
    int npcId;
    int x;
    int y;
    /**
     * 巡逻路径1
     */
    int pathId;
    
    public IExpr createNew(QuestInfo qinfo) {
        return new A_AI_Summon();
    }

    public String getExpression() {
        return "Summon(" + mapId + "," + npcId + "," +  x + "," + y + "," + pathId +  ")";
    }

    public String getName() {
        return "召唤NPC（x=y=0表示召唤到目标位置）";
    }

    public boolean isCondition() {
        return false;
    }

    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if(expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("Summon")&& expr.getRightExpr() == null){
            FunctionCall fc = expr.getLeftExpr().getFunctionCall();
            if(fc.getParamCount() != 5){
                return null;
            }
            Expression param1 = fc.getParam(0);
            Expression param2 = fc.getParam(1);
            Expression param3 = fc.getParam(2);
            Expression param4 = fc.getParam(3);
            Expression param5 = fc.getParam(4);
            if(param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER 
                    && param2.getRightExpr() == null && param2.getLeftExpr().type == Expr0.TYPE_NUMBER){
                A_AI_Summon ret = (A_AI_Summon)createNew(null);
                ret.mapId = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
                ret.npcId = PQEUtils.translateNumberConstant(param2.getLeftExpr().value);
                ret.x = PQEUtils.translateNumberConstant(param3.getLeftExpr().value);
                ret.y = PQEUtils.translateNumberConstant(param4.getLeftExpr().value);
                ret.pathId = PQEUtils.translateNumberConstant(param5.getLeftExpr().value);
                return ret;
            }
        }
        return null;
    }

    public String toNatureString() {
        return "召唤怪物(x=y=0表示召唤到目标位置):从地图:" + mapId + "怪物ID:" + npcId+ "到地图位置"+ x + "," + y + "巡逻路径：" + pathId;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] { 
                new TextPropertyDescriptor("mapId", "地图ID"),
                new TextPropertyDescriptor("npcId", "NPC模板ID"),
                new TextPropertyDescriptor("x", "坐标X"),
                new TextPropertyDescriptor("y", "坐标Y"),
                new TextPropertyDescriptor("pathId", "巡逻路径"),
        };
    }

    public Object getPropertyValue(Object id) {
        if("mapId".equals(id)){
            return String.valueOf(mapId);
        }else if("npcId".equals(id)){
            return String.valueOf(npcId);
        }else if("x".equals(id)){
            return String.valueOf(x);
        }else if("y".equals(id)){
            return String.valueOf(y);
        }else if("pathId".equals(id)){
            return String.valueOf(pathId);
        }
        return null;
    }

    public void setPropertyValue(Object id, Object value) {
        if("mapId".equals(id)){
            int newValue = Integer.parseInt((String)value);
            if(newValue != mapId){
                mapId = newValue;
                fireValueChanged();
            }
        }else if("npcId".equals(id)){
            int newValue = Integer.parseInt((String)value);
            if(newValue != npcId){
                npcId = newValue;
                fireValueChanged();
            }
        }else if("x".equals(id)){
            int newValue = Integer.parseInt((String)value);
            if(newValue != x){
                x = newValue;
                fireValueChanged();
            }
        }else if("y".equals(id)){
            int newValue = Integer.parseInt((String)value);
            if(newValue != y){
                y = newValue;
                fireValueChanged();
            }
        }else if("pathId".equals(id)){
            int newValue = Integer.parseInt((String)value);
            if(newValue != pathId){
                pathId = newValue;
                fireValueChanged();
            }
        }

    }

}
