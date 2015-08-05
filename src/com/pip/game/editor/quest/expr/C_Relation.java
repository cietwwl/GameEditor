package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;

/**
 * 表达式模板：条件的与或。
 * 
 * 目前应该只支持或，因为默认的情况是与
 * 
 * @author ybai
 */
public class C_Relation extends AbstractExpr {
    public final static int C_AND = 0;
    public final static int C_OR = 1;
    
    public final static String CR_AND = "AND";
    public final static String CR_OR = "OR";
    
    private final static String[] r_expression = {CR_AND, CR_OR};
	public int relation;
	
	/**
	 * 构造指定全局变量的模板。
	 * @param name 全局变量名称
	 */
	public C_Relation() {
	    relation = C_OR;
	}
	
	/**
	 * 用模板创建新的表达式片段。
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_Relation();
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
		return r_expression[relation];
	}

	/**
	 * 取得模板名称。
	 */
	public String getName() {
		return "条件关系";
	}

	/**
	 * 识别一个表达式是否匹配本模板。如果匹配，返回一个新的表达式片段对象，否则返回null。
	 */
	public IExpr recognize(QuestInfo qinfo, Expression expr) {    
	    if(expr.getLeftExpr().type == Expr0.TYPE_IDENTIFIER && expr.getRightExpr() == null) {
	        C_Relation ret = new C_Relation();
	        if(CR_AND.equals(expr.getLeftExpr().value)) {
	            ret.relation = 0;
	        } else if ("OR".equals(expr.getLeftExpr().value)) {
	            ret.relation = 1;
	        }
	        return ret;
	    }
		return null;
	}

	/**
	 * 转换为自然语言表示。
	 */
	public String toNatureString() {
		switch (relation) {
		case 0:
			return "与";
		case 1:
			return "或";
		default:
			return "条件未知";
		}
	}
	
	public static boolean checkRelationExpr(String str) {
	    for(int i=0; i<r_expression.length; i++) {
	        if(str.equals(r_expression[i])) {
	            return true;
	        }
	    }
	    return false;
	}

	// 下面是IPropertySource接口的实现

	/**
	 * 取得属性描述符。这个模板只有2个参数：比较性别。
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
//		return new IPropertyDescriptor[] { 
//				new ComboBoxPropertyDescriptor("value", "条件关系", new String[] { "与", "或"})
//		};
	    return new IPropertyDescriptor[0];
	}

	/**
	 * 取得属性当前值。
	 */
	public Object getPropertyValue(Object id) {
		if ("value".equals(id)) {
			return relation;
		}
		return null;
	}

	/**
	 * 设置属性当前值。
	 */
	public void setPropertyValue(Object id, Object value) {
		if ("value".equals(id)) {
			int newRelation = ((Integer)value).intValue();
			if (newRelation != relation) {
			    relation = newRelation;
				fireValueChanged();
			}
		}
	}
}
