package com.pip.game.editor.quest;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;

import com.pip.game.data.ProjectData;
import com.pip.game.data.item.Item;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.QuestVariable;
import com.pip.game.editor.quest.expr.AbstractNotifyAction;
import com.pip.game.editor.quest.expr.C_LocalVar;
import com.pip.game.editor.quest.expr.IExpr;
import com.pip.game.editor.quest.expr.IExprListener;
import com.pip.game.editor.quest.flow.ActionNode;
import com.pip.game.editor.quest.flow.ConditionNode;
import com.pip.game.editor.quest.flow.FlowNode;
import com.pip.propertysheet.PropertySheetEntry;
import com.pip.propertysheet.PropertySheetViewer;

public class QuestDesigner extends Composite implements DragSourceListener, IExprListener {
	private static final String ADD_NEW_VAR = "�½�����...";
	private static final String TASK_VAR_NODE = "�������";
	
    /**
	 * ���ģ���������ݡ�
	 * @author lighthu
	 */
	class TemplateTreeContentProvider implements IStructuredContentProvider, ITreeContentProvider {
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		    getElements(questInfo);
		}
		public void dispose() {}
		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}
		public Object[] getChildren(Object parentElement) {
		    TemplateManager tmgr = ProjectData.getActiveProject().config.templateManager;
			if (parentElement == questInfo) {
				// ���ڵ������е�ģ������
				Object[] ret = new Object[tmgr.TEMPLATE_TYPES.length + 1];
				System.arraycopy(tmgr.TEMPLATE_TYPES, 0, ret, 0, tmgr.TEMPLATE_TYPES.length);
				ret[tmgr.TEMPLATE_TYPES.length] = TASK_VAR_NODE;
				return ret;
			} else if (parentElement == TASK_VAR_NODE) {
				// ��������б�
				Object[] ret = new Object[questInfo.variables.size() + 1];
				for (int i = 0; i < questInfo.variables.size(); i++) {
					ret[i] = new C_LocalVar(questInfo.variables.get(i).name);
				}
				ret[questInfo.variables.size()] = ADD_NEW_VAR;
				return ret;
			} /*else if (parentElement == itemInfo) {
			 // ���ڵ������е�ģ������
                Object[] ret = new Object[TemplateManager.TEMPLATE_TYPES.length + 1];
                System.arraycopy(TemplateManager.TEMPLATE_TYPES, 0, ret, 0, TemplateManager.TEMPLATE_TYPES.length);
                ret[TemplateManager.TEMPLATE_TYPES.length] = TASK_VAR_NODE;
                return ret;
			}*/
			else {
				// ��������
			    List<IExpr> retList = new ArrayList<IExpr>();
				for (int i = 0; i < tmgr.TEMPLATE_TYPES.length; i++) {
					if (tmgr.TEMPLATE_TYPES[i] == parentElement) {
					    // ��ģ����й���
					    for (int j = 0; j < tmgr.TEMPLATES[i].length; j++) {
					        IExpr expr = tmgr.TEMPLATES[i][j];
					        if (flowViewer.getMode() != 0 && !expr.isCondition()) {
					            continue;
					        }
					        if ((tmgr.TEMPLATES_CONTEXT[i][j] & contextMask) == 0) {
					            continue;
					        }
					        retList.add(expr);
					    }
					}
				}
				return retList.toArray();
			}
		}
		public Object getParent(Object element) {
		    TemplateManager tmgr = ProjectData.getActiveProject().config.templateManager;
			if (ADD_NEW_VAR.equals(element)) {
				return TASK_VAR_NODE;
			} else if (element instanceof String) {
				return questInfo;
			} else if (element instanceof C_LocalVar) {
				return TASK_VAR_NODE;
			} else {
				for (int i = 0; i < tmgr.TEMPLATE_TYPES.length; i++) {
					for (IExpr template : tmgr.TEMPLATES[i]) {
						if (template == element) {
							return tmgr.TEMPLATE_TYPES[i];
						}
					}
				}
			}
			return null;
		}
		public boolean hasChildren(Object element) {
			return getChildren(element).length > 0;
		}
	}
	
	private Tree templateTree;
	private TreeViewer templateTreeViewer;
	private PropertySheetViewer propEditor;
	private QuestFlowViewer flowViewer;
	
	private QuestInfo questInfo;
	private int contextMask;
	private Button flatModeButton;
    private Button buttonUp;
    private Button buttonDown;
	
	/**
	 * Create the composite
	 * @param parent
	 * @param style
	 */
	public QuestDesigner(Composite parent, int style, QuestInfo qi, int contextMask) {
		super(parent, style);
		this.questInfo = qi;
		this.contextMask = contextMask;
		
		final GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.numColumns = 2;
		setLayout(gridLayout);

		templateTreeViewer = new TreeViewer(this, SWT.FULL_SELECTION | SWT.BORDER);
		templateTreeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(final DoubleClickEvent event) {
        		Object selObj = getSelectedTemplate();
        		if (selObj != null) {
        			if (selObj.equals(ADD_NEW_VAR)) {
        				onNewVar();
        			} else if (selObj instanceof String) {
						if (templateTreeViewer.getExpandedState(selObj)) {
							templateTreeViewer.collapseToLevel(selObj, 1);
						} else {
							templateTreeViewer.expandToLevel(selObj, 1);
						}
        			}
        		}
			}
		});
		templateTreeViewer.setContentProvider(new TemplateTreeContentProvider());
		templateTree = templateTreeViewer.getTree();
		final GridData gd_templateTree = new GridData(SWT.FILL, SWT.FILL, false, true);
		gd_templateTree.widthHint = 242;
		templateTree.setLayoutData(gd_templateTree);
		templateTree.addListener(SWT.KeyDown, new Listener() {
            public void handleEvent(Event event) {
            	if (event.keyCode == SWT.DEL) {
            		Object selObj = getSelectedTemplate();
	        		if (selObj != null) {
	        			if (selObj instanceof C_LocalVar) {
	        				onDelVar(((C_LocalVar)selObj).getName());
	        			}
	        		}
            	}
            }
        });

	    final ScrolledComposite scrolledComposite = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
	    scrolledComposite.getVerticalBar().setPageIncrement(400);
	    scrolledComposite.getVerticalBar().setMaximum(1000);
	    scrolledComposite.getHorizontalBar().setMaximum(1000);
	    scrolledComposite.getHorizontalBar().setPageIncrement(400);
	    scrolledComposite.getVerticalBar().setIncrement(200);
	    scrolledComposite.getHorizontalBar().setIncrement(200);
	    scrolledComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
	    scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));

		flowViewer = new QuestFlowViewer(scrolledComposite, questInfo, this);
		scrolledComposite.setContent(flowViewer);
		flowViewer.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                // ��FlowViewer��ѡ�нڵ�ı�ʱ�������½ڵ�����Ա༭��
                FlowNode node = (FlowNode)event.data;
                flowNodeSelected(node);
            }
        });
		flowViewer.addListener(SWT.Modify, new Listener() {
            public void handleEvent(Event event) {
                // ��FlowViewer������ݸı�ʱ��֪ͨEditor����Dirty��־
                notifyModified();
            }
        });

		final Composite composite_1 = new Composite(this, SWT.NONE);
		final GridData gd_composite_1 = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd_composite_1.heightHint = 240;
		composite_1.setLayoutData(gd_composite_1);
		composite_1.setLayout(new FillLayout());
		
        propEditor = new PropertySheetViewer(composite_1, SWT.NONE | SWT.BORDER, false);
        PropertySheetEntry rootEntry = new PropertySheetEntry();
        propEditor.setRootEntry(rootEntry);

        // ����DragAndDrop
        final DragSource treeDragSource = new DragSource(templateTree, DND.DROP_COPY);
		treeDragSource.addDragListener(this);
		treeDragSource.setTransfer(new Transfer[] { TextTransfer.getInstance() });

        templateTreeViewer.setInput(questInfo);

	    final Composite composite = new Composite(this, SWT.NONE);
	    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
	    final GridLayout gridLayout_1 = new GridLayout();
	    gridLayout_1.numColumns = 3;
	    composite.setLayout(gridLayout_1);
		
	    flatModeButton = new Button(composite, SWT.CHECK);
	    flatModeButton.addSelectionListener(new SelectionAdapter() {
	        public void widgetSelected(final SelectionEvent e) {
	            flowViewer.setFlatShow(flatModeButton.getSelection());
	        }
	    });
	    flatModeButton.setText("�ش����ⵥ����ʾ");

	    buttonUp = new Button(composite, SWT.NONE);
	    buttonUp.addSelectionListener(new SelectionAdapter() {
	        public void widgetSelected(final SelectionEvent e) {
	            moveFlowUp();
	        }
	    });
	    final GridData gd_buttonUp = new GridData(SWT.RIGHT, SWT.CENTER, true, false);
	    buttonUp.setLayoutData(gd_buttonUp);
	    buttonUp.setText("��");

	    buttonDown = new Button(composite, SWT.NONE);
	    buttonDown.addSelectionListener(new SelectionAdapter() {
	        public void widgetSelected(final SelectionEvent e) {
	            moveFlowDown();
	        }
	    });
	    buttonDown.setText("��");
	}
	
	/**
	 * ���ñ༭ģʽ��
	 * @param mode �༭ģʽ
	 * @param initParam ��ʼ��ʽ
	 */
	public void setup(int mode, String initParam) {
	    if (mode == 0) {
	        flowViewer.loadFlowsFromQuest();
	    } else if (mode == 1 || mode == 3) {
	        flowViewer.loadFlowsFromExpression(initParam, mode);
	        buttonUp.setEnabled(false);
	        buttonDown.setEnabled(false);
	    }
	}
	
	/**
	 * ��������������Ϣ��
	 */
	public void reloadQuest() {
	    flowViewer.loadFlowsFromQuest();
	    flowViewer.layout();
	    flowViewer.redraw();
	    templateTreeViewer.refresh();
	}
	
	/**
     * ��������֮ǰ���Ƚ���һ�κϷ���У���顣
     */
    public boolean preSaveCheck() {
        return flowViewer.preSaveCheck();
    }
    
	/**
     * ��������
     */
    public void saveQuest() {
        flowViewer.saveQuest();
    }
    
    /**
     * �����༭ģʽ�£�����������
     */
    public String saveCondition() {
        return flowViewer.saveCondition();
    }

    /**
     * �����б��ʽ�����ı�֪ͨ����
     */
    public void valueChanged(IExpr source) {
        if (source instanceof AbstractNotifyAction) {
            flowViewer.handleNotifyActionChanged(source);
        }
        flowViewer.layout();
        flowViewer.redraw();
        notifyModified();
    }
    
	// ȡ��ģ�����е�ǰѡ��Ľڵ㡣���û��ѡ���κνڵ㣬����null��
	private Object getSelectedTemplate() {
		IStructuredSelection sel = (IStructuredSelection)templateTreeViewer.getSelection();
		if (sel.isEmpty()) {
			return null;
		} else {
			return sel.getFirstElement();
		}
	}
	
	/**
	 * ȡ��ģ�����е�ǰѡ�еı��ʽģ�塣���û��ѡ���κνڵ㣬����ѡ�еĲ��Ǳ��ʽģ�壬����null��
	 */
	public IExpr getSelectedExpr() {
	    Object obj = getSelectedTemplate();
	    if (obj != null && obj instanceof IExpr) {
	        return (IExpr)obj;
	    }
	    return null;
	}
	
	// �½�һ���������
	private void onNewVar() {
		InputDialog dlg = new InputDialog(getShell(), "�½�����", "������������ƣ�", "var", null);
		if (dlg.open() == InputDialog.OK) {
			String name = dlg.getValue().trim();
			if (name.length() == 0) {
				return;
			}
			if (name.startsWith("_")) {
				MessageDialog.openError(getShell(), "����", "ֻ��ϵͳ���������»��߿�ͷ��");
				return;
			}
			if (name.startsWith("$")) {
			    MessageDialog.openError(getShell(), "����", "$���ű�����Ϊ������������ı�����");
                return;
			}
            if (name.startsWith("v")) {
                MessageDialog.openError(getShell(), "����", "v���ű�����Ϊͨ���������þֲ�������");
                return;
            }
			if (name.indexOf(' ') != -1) {
				MessageDialog.openError(getShell(), "����", "���������ܰ����ո�");
				return;
			}
			// 20111018: �������ƣ�������ֻ����ʹ��Ӣ����ĸ�����ֻ����»���
			for (int i = 0; i < name.length(); i++) {
			    char ch = name.charAt(i);
			    if (ch >= 'a' && ch <= 'z') {
			    } else if (ch >= 'A' && ch <= 'Z') {
			    } else if (ch >= '0' && ch <= '9') {
			    } else if (ch == '_') {
			    } else {
			        MessageDialog.openError(getShell(), "����", "�����ƣ�Ϊ��������Դ���������ֻ��ʹ��Ӣ����ĸ�����ֻ����»��ߡ�");
			        return;
			    }
			}
			
			for (QuestVariable var : questInfo.variables) {
				if (var.name.equals(name)) {
					MessageDialog.openError(getShell(), "����", "����" + name + "�Ѿ������ˡ�");
					return;
				}
			}
			QuestVariable newvar = new QuestVariable();
			newvar.name = name;
			questInfo.variables.add(newvar);
			templateTreeViewer.refresh(TASK_VAR_NODE);
            notifyModified();
		}
	}
	
	// ɾ��ѡ�е��������
	private void onDelVar(String name) {
		int index = -1;
		for (int i = 0; i < questInfo.variables.size(); i++) {
			if (questInfo.variables.get(i).name.equals(name)) {
				index = i;
				break;
			}
		}
		if (index == -1) {
			return;
		}
		questInfo.variables.remove(index);
		templateTreeViewer.refresh(TASK_VAR_NODE);
        notifyModified();
	}
	
	// ֪ͨ���������������Ѹı�
	private void notifyModified() {
	    Event evt = new Event();
	    evt.widget = this;
	    this.notifyListeners(SWT.Modify, evt);
	}

	// ����ķ���ʵ�ִ�ģ�������ϳ����ݵĽӿڡ�
	
	/**
	 * �϶���ʼ�¼���
	 */
	public void dragStart(DragSourceEvent event) {
		Object selObj = getSelectedTemplate();
		if (selObj != null && selObj instanceof IExpr) {
			event.doit = true;
		} else {
			event.doit = false;
		}
	}
	
	/**
	 * �����϶����ݡ�
	 */
	public void dragSetData(DragSourceEvent event) {
		Object selObj = getSelectedTemplate();
		if (selObj != null && selObj instanceof IExpr) {
			IExpr template = (IExpr)selObj;
			event.data = template.createNew(questInfo).getExpression();
		}
	}
	
	/**
	 * �϶������¼��������κβ�����
	 */
	public void dragFinished(DragSourceEvent event) {}

    /**
     * @param node
     */
    private void flowNodeSelected(FlowNode node) {
        try{
            if (node != null && node instanceof ConditionNode) {
                ((ConditionNode)node).getCondition().addListener(QuestDesigner.this);
                propEditor.setInput(new Object[] { ((ConditionNode)node).getCondition() });
            } else if (node != null && node instanceof ActionNode) {
                ((ActionNode)node).getAction().addListener(QuestDesigner.this);
                propEditor.setInput(new Object[] { ((ActionNode)node).getAction() });
            } else {
                propEditor.setInput(new Object[0]);
            }
        }catch(Exception e){
            MessageDialog.openError(getShell(), "Error", "flowNodeSelected Error:\n"+e.toString());
            e.printStackTrace();
        }
    }
    
    protected void moveFlowUp() {
        flowViewer.moveFlowUp();
    }
    
    protected void moveFlowDown() {
        flowViewer.moveFlowDown();
    }
}
