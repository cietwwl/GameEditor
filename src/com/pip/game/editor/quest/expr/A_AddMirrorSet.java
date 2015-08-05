package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.FunctionCall;
import com.pip.game.data.quest.pqe.PQEUtils;

/**
 * 表达式模板：增加相位。
 * @author tzhang
 */
public class A_AddMirrorSet extends AbstractExpr {
    
    public int mapId;
	public int mask;
	
	/**
	 * 构造指定全局变量的模板。
	 * @param name 全局变量名称
	 */
	public A_AddMirrorSet() {
	    mapId = 0;
	    mask = 1;
	}
	
	/**
	 * 判断这个模板是一个条件还是一个动作。
	 */
	public boolean isCondition() {
		return false;
	}

	/**
	 * 用模板创建新的表达式片段。
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new A_AddMirrorSet();
	}

	/**
	 * 取得模板名称。
	 */
	public String getName() {
		return "增加相位";
	}

	/**
	 * 转换为自然语言表示。
	 */
	public String toNatureString() {
		return "增加地图" + mapId + "相位 " +  mask;
	}
	
	/**
	 * 取得生成的表达式。
	 */
	public String getExpression() {
		return "AddMirrorSet(" + mapId + ","+ mask + ")";
	}

	/**
	 * 识别一个表达式是否匹配本模板。如果匹配，返回一个新的表达式片段对象，否则返回null。
	 */
	public IExpr recognize(QuestInfo qinfo, Expression expr) {
		if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("AddMirrorSet") && expr.getRightExpr() == null) {
			FunctionCall fc = expr.getLeftExpr().getFunctionCall();
			//由于在原先的基础上增加了一个地图ID
			if(fc.getParamCount() == 1){
			    Expression param1 = fc.getParam(0);
			    if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER ){
    			    A_AddMirrorSet ret = (A_AddMirrorSet)createNew(qinfo);
    			    ret.mask = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
    			    return ret;
			    }
			}else{
			    if (fc.getParamCount() != 2) {
	                return null;
	            }
    			Expression param1 = fc.getParam(0);
    			Expression param2 = fc.getParam(1);
    			if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER 
    			        && param2.getRightExpr() == null && param2.getLeftExpr().type == Expr0.TYPE_NUMBER ) {
    				A_AddMirrorSet ret = (A_AddMirrorSet)createNew(qinfo);
    				ret.mapId = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
    				ret.mask = PQEUtils.translateNumberConstant(param2.getLeftExpr().value);
    				return ret;
    			}
			}
		}
		return null;
	}

	// 下面是IPropertySource接口的实现

	/**
	 * 取得属性描述符。这个模板有2个参数：字符串参数和整数参数。
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] { 
		        new TextPropertyDescriptor("mapId", "地图ID"),
				new TextPropertyDescriptor("mask", "相位")
		};
	}

	/**
	 * 取得属性当前值。
	 */
	public Object getPropertyValue(Object id) {
		if ("mask".equals(id)) {
			return String.valueOf(mask);
		}else if ("mapId".equals(id)) {
            return String.valueOf(mapId);
        }
		return null;
	}

	/**
	 * 设置属性当前值。
	 */
	public void setPropertyValue(Object id, Object value) {
	    if ("mask".equals(id)) {
			int newValue = Integer.parseInt((String)value);
			if (newValue != mask) {
				mask = newValue;
				fireValueChanged();
			}
		} else if ("mapId".equals(id)) {
            int newValue = Integer.parseInt((String)value);
            if (newValue != mapId) {
                mapId = newValue;
                fireValueChanged();
            }
        }
	}

 
}
