package com.pip.game.editor.quest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.pip.game.data.ProjectData;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.QuestTrigger;
import com.pip.game.data.quest.pqe.ExpressionList;
import com.pip.game.editor.quest.expr.A_AddItem;
import com.pip.game.editor.quest.expr.A_Dec;
import com.pip.game.editor.quest.expr.A_Empty;
import com.pip.game.editor.quest.expr.A_GetReward;
import com.pip.game.editor.quest.expr.A_Inc;
import com.pip.game.editor.quest.expr.A_Question;
import com.pip.game.editor.quest.expr.A_RemoveItem;
import com.pip.game.editor.quest.expr.A_Set;
import com.pip.game.editor.quest.expr.AbstractNotifyAction;
import com.pip.game.editor.quest.expr.C_AnswerQuestion;
import com.pip.game.editor.quest.expr.C_CloseChat;
import com.pip.game.editor.quest.expr.C_CloseMessage;
import com.pip.game.editor.quest.expr.C_GlobalVar;
import com.pip.game.editor.quest.expr.C_HasItem;
import com.pip.game.editor.quest.expr.C_LocalVar;
import com.pip.game.editor.quest.expr.C_True;
import com.pip.game.editor.quest.expr.C_UseItem;
import com.pip.game.editor.quest.expr.IExpr;
import com.pip.game.editor.quest.flow.AbstractConditionNode;
import com.pip.game.editor.quest.flow.ActionGroupNode;
import com.pip.game.editor.quest.flow.ActionNode;
import com.pip.game.editor.quest.flow.ConditionGroupNode;
import com.pip.game.editor.quest.flow.ConditionNode;
import com.pip.game.editor.quest.flow.Flow;
import com.pip.game.editor.quest.flow.FlowNode;
import com.pip.game.editor.quest.flow.IFlowIterator;
import com.pip.util.SWTUtils;
import com.pip.util.Utils;
import com.swtdesigner.SWTResourceManager;

public class QuestFlowViewer extends Canvas implements DropTargetListener, PaintListener {
    private static final int BOX_WIDTH = 130;
    private static final int X_MARGIN = 40;
    
    // ������Ϣ
    private QuestInfo questInfo;
    // ��������
    private List<Flow> flows = new ArrayList<Flow>();
    // ��ǰ��������ռ�ݵĴ�С
    private Rectangle bestSize;
    // ��ǰ��긡���ϵĽڵ�
    private FlowNode hoverNode;
    // ��ǰ�Ƿ������϶�ģ��
    private boolean isDraggingTemplate;
    // ��ǰѡ�еĽڵ�
    private FlowNode selectedNode;
    // ����
    private Image bufferImg;
    // ���������������
    private QuestDesigner designer;
    
    // �༭ģʽ��0 - ��ͨ������ģʽ��1 - �༭�������ʽģʽ��3 - �༭���������ʽģʽ
    private int mode;
    // �Ƿ�̯ƽ��ʾ
    private boolean flatShow;

    private String stateText = "";
    
	/**
	 * Create the composite
	 * @param parent
	 * @param style
	 */
	public QuestFlowViewer(Composite parent, QuestInfo info, QuestDesigner designer) {
		super(parent, SWT.NO_BACKGROUND);
		setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		addPaintListener(this);
		questInfo = info;
		this.designer = designer;
		
		// �����ȡ
		addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                redraw();
            }

            public void focusLost(FocusEvent e) {
                redraw();
            }
        });
        addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_TAB_NEXT
                        || e.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
                    e.doit = true;
                }
            };
        });
        
        // ��������
        addListener(SWT.KeyDown, new Listener() {
            public void handleEvent(Event event) {
                switch (event.keyCode) {
                    case SWT.DEL:
                        onDelete();
                        break;
                    case SWT.ARROW_UP:
                        traverseUp();
                        break;
                    case SWT.ARROW_RIGHT:
                        traverseRight();
                        break;
                    case SWT.ARROW_LEFT:
                        traverseLeft();
                        break;
                    case SWT.ARROW_DOWN:
                        traverseDown();
                        break;    
                }
            }
        });
        
        // ����ƶ���������hoverNode
        addMouseMoveListener(new MouseMoveListener() {
            public void mouseMove(MouseEvent e) {
                FlowNode node = findNode(e.x, e.y);
                if (hoverNode != node) {
                    hoverNode = node;
                    isDraggingTemplate = false;
                    redraw();
                }
            }
        });
        
        // �������������selectedNode
        addMouseListener(new MouseAdapter() {
            public void mouseDown(MouseEvent e) {
                if (e.button == 1) {
                    FlowNode node = findNode(e.x, e.y);
                    if (node != null && node != selectedNode) {
                        setSelectedNode(node);
                        redraw();
                    }
                }
            }
            
            public void mouseUp(MouseEvent e) {}
        });
        
        // ����DND
		final DropTarget templateDropTarget = new DropTarget(this, DND.DROP_COPY);
		templateDropTarget.addDropListener(this);
		templateDropTarget.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		
		setSize(100, 100);
	}
	
	/**
	 * �༭ģʽ��
	 * @return
	 */
	public int getMode() {
	    return mode;
	}
	
	/**
     * ȡ�����ڱ༭������ͼ��
     * @return
     */
    public List<Flow> getFlows() {
        return flows;
    }
    
    /**
     * �Ƿ�̯ƽ��ʾ��
     * @return
     */
    public boolean isFlatShow() {
        return flatShow;
    }
    
    /**
     * ����̯ƽ��ʾ��־��ֻ��ģʽ0������ģʽ���������ô˱�־��
     * @param value
     */
    public void setFlatShow(boolean value) {
        flatShow = value;
        saveQuest();
        loadFlowsFromQuest();
        layout();
        redraw();
    }
    
	/**
	 * �ӵ�ǰ����Ľű��н������������̡�
	 */
	public void loadFlowsFromQuest() {
	    mode = 0;
        flows = Flow.parseQuest(questInfo, flatShow);
        if (questInfo.triggers.size() > 0) {
            StringBuffer buf = new StringBuffer();
            buf.append("���´������޷���ʶ��(����ɾ��)��\n");
            for (QuestTrigger trigger : questInfo.triggers) {
                buf.append(trigger.condition + " --> " + trigger.action);
                buf.append("\n");
            }
            MessageDialog.openError(getShell(), "����", buf.toString());
        }
        selectedNode = null;
        hoverNode = null;
        isDraggingTemplate = false;
	}
	
	/**
	 * ��һ�����ʽ�ַ����н������������̡�
	 * @param expr
	 */
	public void loadFlowsFromExpression(String expr, int mode) {
	    this.mode = mode;
	    if (expr.trim().length() >= 0) {
	        String[] condition = Utils.splitString(expr.trim(), ';');
	        for(int i = 0; i < condition.length; i++){
	            QuestTrigger dumbTrigger = new QuestTrigger();
	            dumbTrigger.condition = condition[i];
	            dumbTrigger.action = "1";
	            Flow flow = Flow.recognize(dumbTrigger, questInfo);
	            if (flow != null) {
	                flows.add(flow);
	            }
	        }
	    }
        selectedNode = null;
        hoverNode = null;
        isDraggingTemplate = false;
	}
	
	/**
	 * �������������̽������²��֡�
	 */
	public void layout() {
	    // ����������༭ģʽ�����вü���ֻ������һ�����̵���������
	    if (mode == 1 || mode == 3) {
	        if(mode != 3){
	            while (flows.size() > 1) {
	                flows.remove(1);
	            }
	        }
	        if (flows.size() > 0) {
	            for(int i = 0; i < flows.size(); i++){
	                AbstractConditionNode startNode = flows.get(i).getStartNode();
        	        while (true) {
        	            while (startNode.getChildren().size() > 0) {
        	                if (startNode.getChildren().get(0) instanceof AbstractConditionNode) {
        	                    while (startNode.getChildren().size() > 1) {
        	                        startNode.getChildren().remove(1);
        	                    }
        	                    break;
        	                } else {
        	                    startNode.getChildren().remove(0);
        	                }
        	            }
        	            if (startNode.getChildren().size() == 0) {
        	                break;
        	            }
        	            startNode = (AbstractConditionNode)startNode.getChildren().get(i);
        	        }
	            }
	        }
	    }
	    
	    int y = 14;
	    int x = 14;
	    int maxWidth = 0;
	    
	    // ����һ����ʱGC�������������ַ�����С
	    GC bufferGC = null;
        Image tempImg = new Image(getShell().getDisplay(), 1, 1);
        bufferGC = new GC(tempImg);
        bufferGC.setFont(getFont());
	    
        for (Flow flow : flows) {
	        // ��һ�����̽��в���
	        layoutNode(flow.getStartNode(), 0, bufferGC);
	        
	        // ����������̵��ܴ�С
	        ComputeSizeIterator itor1 = new ComputeSizeIterator();
	        flow.iterate(itor1);
	        Rectangle flowBounds = itor1.getBounds();
	        
	        // ��������̵�X��Yֵ���е���
	        AdjustYIterator itor2 = new AdjustYIterator(y - flowBounds.y);
	        flow.iterate(itor2);
            AdjustXIterator itor3 = new AdjustXIterator(x - flowBounds.x);
            flow.iterate(itor3);
	        
            // ����ͳ����Ϣ
            y += flowBounds.height + 30;
            if (maxWidth < flowBounds.width) {
                maxWidth = flowBounds.width;
            }
	    }
        int pw = maxWidth + 14 * 2 + 10;
        int ph = y;
        ScrolledComposite scc = findParent();
        if (scc != null) {
            int parw = scc.getBounds().width;
            int parh = scc.getBounds().height;
            if (pw < parw - scc.getVerticalBar().getSize().x - 5) {
                pw = parw - scc.getVerticalBar().getSize().x - 5;
            }
            if (ph < parh - scc.getHorizontalBar().getSize().y - 5) {
                ph = parh - scc.getHorizontalBar().getSize().y - 5;
            }
        }
	    bestSize = new Rectangle(0, 0, pw, ph);
	    this.setSize(bestSize.width, bestSize.height);

	    bufferGC.dispose();
        tempImg.dispose();
	}
	
	/**
	 * ��һ����㼰���ӽ����в��֣����������ʾλ�á�Ŀǰ�㷨ÿһ�еĹ̶����ΪBOX_WIDTH����֮��ľ�����X_MARGIN��
	 * @param node ���ڵ�
	 * @param level ���ڵ��������еĲ���
	 */
	private void layoutNode(FlowNode node, int level, GC gc) {
	    if (node instanceof ActionGroupNode) {     // ������ڵ�
	        // �������ӽڵ���е������֣������ӽڵ��xλ��Ϊ0��yλ�ô�0��ʼ����
	        int y = 0;
	        for (int i = 0; i < node.getChildren().size(); i++) {
	            FlowNode child = node.getChildren().get(i);
	            layoutNode(child, level + 1, gc);
	            ComputeSizeIterator itor1 = new ComputeSizeIterator();
                Flow.iterate(child, itor1);
                Rectangle childBounds = itor1.getBounds();
                
                // ���ӽڵ�Yֵ���е���
                AdjustYIterator itor2 = new AdjustYIterator(y - childBounds.y);
                Flow.iterate(child, itor2);
                
                // ����Yֵ
                y += childBounds.height + 30;
	        }
	        
	        // ������ڵ㱾��Ĵ�С�չ���ס���е�һ���ӽڵ㼴��
	        if (node.getChildren().size() == 0) {
	            node.setBounds(new Rectangle(0, 0, BOX_WIDTH, 20));
	        } else {
	            int starty = node.getChildren().get(0).getBounds().y;
	            Rectangle endB = node.getChildren().get(node.getChildren().size() - 1).getBounds();
	            int endy = endB.y + endB.height;
	            node.setBounds(new Rectangle(0, starty, BOX_WIDTH, endy - starty));
	        }
	    } else if(node instanceof ConditionGroupNode) {

            // �������ӽڵ���е������֣������ӽڵ��xλ��Ϊ0��yλ�ô�0��ʼ����
            int y = 0;
            for (int i = 0; i < node.getChildren().size(); i++) {
                FlowNode child = node.getChildren().get(i);
                layoutNode(child, level + 1, gc);
                ComputeSizeIterator itor1 = new ComputeSizeIterator();
                Flow.iterate(child, itor1);
                Rectangle childBounds = itor1.getBounds();
                
                // ���ӽڵ�Yֵ���е���
                AdjustYIterator itor2 = new AdjustYIterator(y - childBounds.y);
                Flow.iterate(child, itor2);
                AdjustXIterator itor3 = new AdjustXIterator(BOX_WIDTH + X_MARGIN);
                Flow.iterate(child, itor3);
                
                // ����Yֵ
                y += childBounds.height + 30;
            }
            
	        y = 0;
	         for(ConditionNode conditionNode : ((ConditionGroupNode)node).conditionNodes) {
                  layoutNode(conditionNode, level, gc);
                  
//                  FlowNode child = node.getChildren().get(i);
//                  layoutNode(child, level + 1, gc, startX, startY);

                  ComputeSizeIterator itor1 = new ComputeSizeIterator();
                  Flow.iterate(conditionNode, itor1);
                  Rectangle childBounds = itor1.getBounds();
                  
                  // ���ӽڵ��X��Yֵ���е���
                  AdjustYIterator itor2 = new AdjustYIterator(y - childBounds.y);
                  Flow.iterate(conditionNode, itor2);
//                  AdjustXIterator itor3 = new AdjustXIterator(BOX_WIDTH + X_MARGIN);
//                  Flow.iterate(conditionNode, itor3);
                  
                  // ����Yֵ
                  y += childBounds.height + 30;
              }
	         
	         node.setBounds(new Rectangle(0, 0, BOX_WIDTH, y - 30)); 
	         
//             ComputeSizeIterator itor1 = new ComputeSizeIterator();
//             Flow.iterate(node, itor1);
//             Rectangle childBounds = itor1.getBounds();
//             
//             // ���ӽڵ��X��Yֵ���е���
//             AdjustYIterator itor2 = new AdjustYIterator(y - node.getBounds().height / 2);
//             Flow.iterate(node, itor2);
//             AdjustXIterator itor3 = new AdjustXIterator(BOX_WIDTH + X_MARGIN);
//             Flow.iterate(node, itor3);
             
//             // ����Yֵ
//             y += childBounds.height + 30;

	    } else if (node instanceof ConditionNode || node instanceof ActionNode) {  // ��ͨ�ڵ�
	        // �������ӽڵ���е������֣������ӽڵ��xλ��ΪBOX_WIDTH+X_MARGIN��yλ�ô�0��ʼ����
            int y = 0;
            for (int i = 0; i < node.getChildren().size(); i++) {
                FlowNode child = node.getChildren().get(i);
                layoutNode(child, level + 1, gc);
                ComputeSizeIterator itor1 = new ComputeSizeIterator();
                Flow.iterate(child, itor1);
                Rectangle childBounds = itor1.getBounds();
                
                // ���ӽڵ��X��Yֵ���е���
                AdjustYIterator itor2 = new AdjustYIterator(y - childBounds.y);
                Flow.iterate(child, itor2);
                AdjustXIterator itor3 = new AdjustXIterator(BOX_WIDTH + X_MARGIN);
                Flow.iterate(child, itor3);
                
                // ����Yֵ
                y += childBounds.height + 30;
            }
	        
            // ����ڵ㱾��Ĵ�С�����û���ӽڵ㣬�ڵ����0��0���ɣ�������ӽڵ㣬����ӽڵ���롣
            String[] lines = SWTUtils.formatText(node.toString(), gc, BOX_WIDTH - 4 * 2);
            int needHei = lines.length * gc.getFontMetrics().getHeight() + 8;
            if (node.getChildren().size() == 0) {
                node.setBounds(new Rectangle(0, 0, BOX_WIDTH, needHei)); 
            } else {
                int starty = node.getChildren().get(0).getBounds().y;
                Rectangle endB = node.getChildren().get(node.getChildren().size() - 1).getBounds();
                int endy = endB.y + endB.height;
                node.setBounds(new Rectangle(0, (starty + endy) / 2 - needHei / 2, BOX_WIDTH, needHei));
            }
	    }
	}

    /**
     * ���ƿؼ���
     */
    public void paintControl(PaintEvent e) {
        if (bestSize == null) {
            layout();
        }
        Rectangle clip = new Rectangle(e.x, e.y, e.width, e.height);
        GC bufferGC = null;
        try {
            Point size = getSize();
            if (bufferImg != null && (bufferImg.getBounds().width < clip.width || bufferImg.getBounds().height < clip.height)) {
                bufferImg.dispose();
                bufferImg = null;
            }
            if (bufferImg == null) {
                bufferImg = new Image(getDisplay(), clip.width, clip.height);
            }
            bufferGC = new GC(bufferImg);
            try {
                paintContent(bufferGC, clip);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            e.gc.setClipping(clip);
            e.gc.drawImage(bufferImg, 0, 0, clip.width, clip.height, clip.x, clip.y, clip.width, clip.height);
        } catch (Throwable e1) {
            e1.printStackTrace();
        } finally {
            if (bufferGC != null) {
                bufferGC.dispose();
            }
        }
    }

    /**
     * �ڻ����ϻ������ݡ�
     */
    public void paintContent(GC gc, Rectangle clip) {
	    Point size = getSize();
	    gc.setBackground(getBackground());
	    gc.setFont(getFont());
        gc.fillRectangle(0, 0, clip.width, clip.height);
//	    if (isFocusControl()) {
//    	    gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
//            gc.drawRectangle(0, 0, size.x - 1, size.y - 1);
//	    }

	    // �������еĽڵ�
	    PaintIterator itor = new PaintIterator(gc, clip);
	    for (int i = 0; i < flows.size(); i++) {
	        flows.get(i).iterate(itor);
	        
	        if (i != flows.size() - 1) {
    	        // �����̵��·�����һ���������ָ�
                ComputeSizeIterator itor1 = new ComputeSizeIterator();
                flows.get(i).iterate(itor1);
                Rectangle flowBounds = itor1.getBounds();
    	        gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
    	        int liney = flowBounds.y + flowBounds.height + 15;
    	        gc.setLineStyle(SWT.LINE_DASH);
                gc.drawLine(0 - clip.x, liney - clip.y, clip.width, liney - clip.y);
    	        gc.setLineStyle(SWT.LINE_SOLID);
	        }
	    }
    }
	
    /**
     * ��������֮ǰ���Ƚ���һ�κϷ���У���顣
     */
    public boolean preSaveCheck() {
        // ���Ӽ�飺����õ���AddItem����GetReward�����û���������ô�������������һ�������������������Ұ���һ�������޸Ķ���
        // 20120130�޸ģ������������һ���۳���Ʒ��������������һ���ж���Ʒ���ڵ�������Ҳ���Թ���
        boolean foundError = false;
        for (Flow flow : flows) {
            List<ActionGroupNode> actionGroups = flow.findActionGroups();
            for (ActionGroupNode gnode : actionGroups) {
                boolean hasChangeVar = false;
                boolean hasReward = false;
                boolean hasVarCond = false;
                boolean hasRemoveItem = false;
                boolean hasItemCond = false;
                
                // �����������еĶ������ҳ��Ƿ�������������ͱ����޸Ķ���
                for (FlowNode child : gnode.getChildren()) {
                    if (!(child instanceof ActionNode)) {
                        continue;
                    }
                    ActionNode anode = (ActionNode)child;
                    if (anode.getAction() instanceof A_AddItem  || anode.getAction() instanceof A_GetReward) {
                        hasReward = true;
                    }
                    if (anode.getAction() instanceof A_Dec || anode.getAction() instanceof A_Inc || anode.getAction() instanceof A_Set) {
                        hasChangeVar = true;
                    }
                    if (anode.getAction() instanceof A_RemoveItem) {
                        hasRemoveItem = true;
                    }
                }
                if (hasReward) {
                    // �������������ҳ��Ƿ���������ж�����������Ʒ�ж�����
                    FlowNode parent = gnode.getParent();
                    while (parent != null) {
                        if (parent instanceof ConditionNode) {
                            ConditionNode cn = (ConditionNode)parent;
                            IExpr cond = cn.getCondition();
                            if (cond instanceof C_LocalVar || cond instanceof C_GlobalVar) {
                                hasVarCond = true;
                            }
                            if (cond instanceof C_HasItem) {
                                hasItemCond = true;
                            }
                            if (cond instanceof C_UseItem) {
                                hasItemCond = true;
                            }
                            if (cond instanceof C_CloseChat || cond instanceof C_CloseMessage ||
                                cond instanceof C_AnswerQuestion) {
                                break;
                            }
                        } else {
                            break;
                        }
                        parent = parent.getParent();
                    }
                    if (hasVarCond && hasChangeVar) {
                        // pass
                    } else if (hasItemCond && hasRemoveItem) {
                        // pass
                    } else {
                        foundError = true;
                        break;
                    }
                }
            }
            if (foundError) {
                break;
            }
        }
        if (foundError) {
            String msg = "���ؾ��棡��������������������ʹ����ֱ�Ӹ���ҷ��Ž�����ָ���������ָ����ڱ�ˢ��Σ�գ�����Ҫ�����ʹ��һ����Ʒ���߿��Ʊ�������֤���" +
                "����ֻ�ܱ�ִ��һ�Σ��������û������������������ϸ��������ơ�";
            MessageDialog.openError(getShell(), "����", msg);
            msg = "���Ƿ��������ƣ��������棿";
            if (!MessageDialog.openQuestion(getShell(), "ȷ��", msg)) {
                return false;
            }
        }
        
        // �����̯ƽģʽ������û�ж�Ӧ���ʵĻش�
        if (flatShow) {
            // �ҳ����е�Question Notify ID
            Set<Integer> idMap = new HashSet<Integer>();
            idMap.add(-1);
            for (Flow flow : flows) {
                List<A_Question> qs = flow.findQuestions();
                for (A_Question q : qs) {
                    idMap.add(q.notifyID);
                }
            }
            
            // ��������Իش����⿪ʼ��flow���Ƿ���ڶ�Ӧ��notifyID
            int missCount = 0;
            for (Flow flow : flows) {
                if (flow.getStartNode() instanceof ConditionNode) {
                    IExpr expr = ((ConditionNode)flow.getStartNode()).getCondition();
                    if (expr instanceof C_AnswerQuestion && !idMap.contains(((C_AnswerQuestion)expr).param1)) {
                        missCount++;
                    }
                }
            }
            if (missCount > 0) {
                String msg = "��" + missCount + "���ش���������û���ҵ���Ӧ�����ʣ��Ƿ��������ע�⣺���ѡ���ǣ���Щ���õ��ʴ��������̽���ɾ������";
                if (!MessageDialog.openQuestion(getShell(), "ȷ��", msg)) {
                    return false;
                } else {
                    for (int i = 0; i < flows.size(); i++) {
                        if (flows.get(i).getStartNode() instanceof ConditionNode) {
                            IExpr expr = ((ConditionNode)flows.get(i).getStartNode()).getCondition();
                            if (expr instanceof C_AnswerQuestion && !idMap.contains(((C_AnswerQuestion)expr).param1)) {
                                flows.remove(i);
                                i--;
                            }
                        }
                    }
                    layout();
                    redraw();
                }
            }
        }
        return true;
    }

    /**
	 * ��������
	 */
	public void saveQuest() {
	    questInfo.triggers.clear();
	    for (Flow flow : flows) {
	        flow.save(questInfo);
//	        if(mode == 0) {
//	            //���˵�actionΪ�յ�trigger
//	            for(int i = questInfo.triggers.size() - 1; i >= 0; i--) {
//	                QuestTrigger trigger = questInfo.triggers.get(i);
//	                if("1".equals(trigger.action)) {
//	                    questInfo.triggers.remove(i);
//	                }
// 	            }
//	        }
	    }
	}
	
	/**
	 * �����༭ģʽ�£�����������
	 */
	public String saveCondition() {
	    if (flows.size() == 0) {
	        return "";
	    }
	    StringBuffer buf = new StringBuffer();
	    
	    for(int k = 0; k < flows.size(); k++){
	        int newFlag = -1;
    	    AbstractConditionNode startNode = flows.get(k).getStartNode();
    	    while (true) {
    	        if (buf.length() > 0) {
    	            if(newFlag == -1){
    	                buf.append(";");
    	            }else{
    	                buf.append(", ");
    	            }
    	           
    	        }
    	        newFlag++;
    	        if(startNode instanceof ConditionGroupNode) {
    	            buf.append("(");
    	            int count = ((ConditionGroupNode)startNode).conditionNodes.size();
    	            for(int i=0; i< ((ConditionGroupNode)startNode).conditionNodes.size(); i++) {// ConditionNode conditionNode : ((ConditionGroupNode)startNode).conditionNodes) {
    	                buf.append(((ConditionGroupNode)startNode).conditionNodes.get(i).getCondition().getExpression());
    	                if(i < count - 1) {
    	                    buf.append(", ");	                    
    	                }
    	            }
    	            buf.append(")");
    	            if(startNode.getChildren().size() > 0) {
    	                startNode = (AbstractConditionNode)startNode.getChildren().get(0);
    	            } else {
    	                break;
    	            }
    	        } else {
    	            buf.append(((ConditionNode)startNode).getCondition().getExpression());
    	            if (startNode.getChildren().size() > 0) {
    	                if(startNode.getChildren().get(0) instanceof ConditionGroupNode) {
    	                    startNode = (ConditionGroupNode)startNode.getChildren().get(0);
    	                } else if (startNode.getChildren().get(0) instanceof AbstractConditionNode){
    	                    if(startNode.getChildren().size() > 0) {
    	                        startNode = (AbstractConditionNode)startNode.getChildren().get(0);
    	                    } else {
    	                        break;
    	                    }
    	                } else {
    	                    break;
    	                }
    	                
    	            } else {
    	                break;
    	            }
    	        }
    	    }
    	    
	    }
	    return buf.toString();
	}
	
    // ֪ͨ���������������Ѹı�
    private void notifyModified() {
        Event evt = new Event();
        evt.widget = this;
        this.notifyListeners(SWT.Modify, evt);
    }

    /**
	 * ���õ�ǰѡ�нڵ㣬����֪ͨ����Selection�¼������ߡ�
	 */
	public void setSelectedNode(FlowNode node) {
	    selectedNode = node;
	    Event evt = new Event();
	    evt.widget = this;
	    evt.data = node;
	    this.notifyListeners(SWT.Selection, evt);
	}
	
	/**
	 * ȡ�õ�ǰѡ�нڵ㡣
	 */
	public FlowNode getSelectedNode() {
	    return selectedNode;
	}
	
	// ɾ����ǰѡ�еĽڵ㡣
	private void onDelete() {
	    if (selectedNode == null) {
	        return;
	    }
	    
	    if (selectedNode.getParent() == null) {
            // �����ɾ�����Ǹ��ڵ㣬���ÿ���ӽڵ㶼�ֲ��Ϊһ���µ�Flow������ӽڵ��Ƕ����飬����Ҫ
            // ��ǰ���һ������������ڵ㡣
	        int flowIndex = -1;
	        for (int i = 0; i < flows.size(); i++) {
	            if (flows.get(i).getStartNode() == selectedNode) {
	                flowIndex = i;
	                
	                if(selectedNode.getChildren().size() == 0) {
	                    flows.remove(flowIndex);
	                }
	                
	                break;
	            }
	        }
	        if (flowIndex == -1) {
	            MessageDialog.openError(getShell(), "����", "���ݴ���ѡ�нڵ�û�и��ڵ㡣");
	            return;
	        }
//	        flows.remove(flowIndex);
	        for (FlowNode child : selectedNode.getChildren()) {
	            if (child instanceof ActionGroupNode) {
//	                Flow newFlow = new Flow();
	                Flow newFlow = child.getFlow();
	                ConditionNode fakeNode = new ConditionNode(newFlow);
	                fakeNode.setCondition(new C_True());
	                fakeNode.addChild(child);
	                newFlow.setStartNode(fakeNode);
//	                flows.add(flowIndex, newFlow);
	                flowIndex++;
	            } else if (child instanceof AbstractConditionNode) {
//	                Flow newFlow = new Flow();
	                Flow newFlow = child.getFlow();
	                newFlow.setStartNode((AbstractConditionNode)child);
	                child.setParent(null);
//	                flows.add(flowIndex, newFlow);
	                flowIndex++;
	            }
	        }
	        setSelectedNode(null);
	    } else if (selectedNode instanceof ActionGroupNode || selectedNode.getParent() instanceof ActionGroupNode) {
	        // �����ɾ�������Ƕ�����������е��ӽڵ㣬ɾ������ڵ㼰�������ӽڵ�
	        int index = selectedNode.getParent().getChildren().indexOf(selectedNode);
	        selectedNode.getParent().getChildren().remove(index);
	        if (index < selectedNode.getParent().getChildren().size()) {
	            setSelectedNode(selectedNode.getParent().getChildren().get(index));
	        } else if (index > 0) {
                setSelectedNode(selectedNode.getParent().getChildren().get(index - 1));
	        } else {
                setSelectedNode(null);
	        }
	    } else if (selectedNode.getParent().getParent() != null && selectedNode.getParent().getParent() instanceof ActionGroupNode) {
	        // �����ɾ���Ľڵ�ĸ��ڵ��Ƕ������еĽڵ㣬���ܱ�ɾ����ֻ�ܾ����ܵذ��������
	        if (selectedNode.getParent() instanceof ConditionNode) {
	            ((ActionNode)selectedNode).setAction(new A_Empty());
	            selectedNode.getChildren().clear();
	        } else {
	            return;
	        }
	    } else if(!(selectedNode instanceof ConditionGroupNode) && selectedNode.getParent() != null && selectedNode.getParent() instanceof ConditionGroupNode && ((ConditionGroupNode)selectedNode.getParent()).hasCondition((ConditionNode)selectedNode)) { 
	        //���ɾ���Ľڵ���ConditionGroupNode��Ľڵ�
	        ConditionGroupNode cgn = (ConditionGroupNode)selectedNode.getParent();
	        if(cgn.hasCondition((ConditionNode)selectedNode)) {
	            cgn.conditionNodes.remove(selectedNode);
	            if(cgn.conditionNodes.size() == 1) {
	                //�ý�ʣ��һ�������ڵ��滻�ýڵ�
	                ConditionNode cn = cgn.conditionNodes.get(0);
	                
	                if(cgn.getParent() != null) {
	                    cgn.getParent().getChildren().remove(cgn);	
	                    cgn.getParent().getChildren().add(cn);
	                }	 
	                cn.setParent(cgn.getParent());
	                
	                for(FlowNode fn : cgn.getChildren()) {
	                    cn.addChild(fn);
	                    fn.setParent(cn);
	                }
	                
	                if(cgn.getFlow().getStartNode() == cgn) {
	                    cgn.getFlow().setStartNode(cn);
	                }
	            }
	        }
	    } else {
	        // ʣ�������ǣ���ɾ���ڵ��������е��м�ڵ㣬��ʱֻ��Ҫ�ѱ��ڵ�������ӽڵ㶼�ҵ����ڵ����漴�ɡ�
	        selectedNode.getParent().getChildren().remove(selectedNode);
	        for (FlowNode child : selectedNode.getChildren()) {
	            selectedNode.getParent().addChild(child);
	        }
	        setSelectedNode(selectedNode.getParent());
	        
	        // ����ϲ�����2��ͬ���Ķ����飬�ϲ����ǡ�
	        ActionGroupNode existGroup = null;
	        for (int i = 0; i < selectedNode.getChildren().size(); i++) {
	            if (selectedNode.getChildren().get(i) instanceof ActionGroupNode) {
	                ActionGroupNode group = (ActionGroupNode)selectedNode.getChildren().get(i);
	                if (existGroup == null) {
	                    existGroup = group;
	                } else {
	                    for (FlowNode child : group.getChildren()) {
	                        existGroup.addChild(child);
	                    }
	                    selectedNode.getChildren().remove(i);
	                    i--;
	                }
	            }
	        }
	    }
	    
	    layout();
	    redraw();
	    notifyModified();
	}
	
	// �ƶ���굽��һ���ֵܽڵ�
	private void traverseUp() {
	    if (selectedNode == null) {
	        return;
	    }
	    if (selectedNode.getParent() == null) {
	        return;
	    }
	    int index = selectedNode.getParent().getChildren().indexOf(selectedNode);
	    if (index > 0) {
	        setSelectedNode(selectedNode.getParent().getChildren().get(index - 1));
	        ensureNodeVisible(selectedNode);
	        redraw();
	    }
	}
	
	// �ƶ���굽��һ���ֵܽڵ�
	private void traverseDown() {
	    if (selectedNode == null) {
            return;
        }
        if (selectedNode.getParent() == null) {
            return;
        }
        int index = selectedNode.getParent().getChildren().indexOf(selectedNode);
        if (index < selectedNode.getParent().getChildren().size() - 1) {
            setSelectedNode(selectedNode.getParent().getChildren().get(index + 1));
            ensureNodeVisible(selectedNode);
            redraw();
        }
	}
	
	// �ƶ���굽���ڵ�
	private void traverseLeft() {
	    if (selectedNode == null) {
            return;
        }
        if (selectedNode.getParent() == null) {
            return;
        }
        setSelectedNode(selectedNode.getParent());
        ensureNodeVisible(selectedNode);
        redraw();
	}
	
	// �ƶ���굽��һ���ӽڵ�
	private void traverseRight() {
	    if (selectedNode == null) {
	        return;
	    }
	    if (selectedNode.getChildren().size() == 0) {
	        return;
	    }
	    setSelectedNode(selectedNode.getChildren().get(0));
        ensureNodeVisible(selectedNode);
        redraw();
	}

	// �������ڵĹ�������
	private ScrolledComposite findParent() {
        Composite parent = this.getParent();
        while (!(parent instanceof ScrolledComposite)) {
            parent = parent.getParent();
        }
        return (ScrolledComposite)parent;
	}
	
	// ����������ȷ��ѡ�нڵ�ɼ�
	private void ensureNodeVisible(FlowNode node) {
	    // ����������ڵĴ�С��λ��
	    ScrolledComposite scc = findParent();
	    Point topLeft = scc.getOrigin();
	    int width = scc.getBounds().width;
	    if (scc.getVerticalBar() != null && scc.getVerticalBar().isVisible()) {
	        width -= scc.getVerticalBar().getSize().x;
	    }
	    int height = scc.getBounds().height;
        if (scc.getHorizontalBar() != null && scc.getHorizontalBar().isVisible()) {
            height -= scc.getHorizontalBar().getSize().y;
        }
        
        // �жϵ�ǰ�����Ƿ����ѡ�нڵ㣬����������������֮
        Rectangle nbounds = node.getBounds();
        if (nbounds.x < topLeft.x) {
            topLeft.x = nbounds.x;
        }
        if (nbounds.x + nbounds.width > topLeft.x + width) {
            topLeft.x = nbounds.x + nbounds.width - width;
        }
        if (nbounds.y < topLeft.y) {
            topLeft.y = nbounds.y;
        }
        if (nbounds.y + nbounds.height > topLeft.y + height) {
            topLeft.y = nbounds.y + nbounds.height - height;
        }
        if (!topLeft.equals(scc.getOrigin())) {
            scc.setOrigin(topLeft);
        }
	}
	
	// ����ָ�����λ���ϵĽڵ㣬����Ҳ���������null��
	private FlowNode findNode(int x, int y) {
	    FindNodeIterator itor = new FindNodeIterator(x, y);
	    for (Flow flow : flows) {
	        flow.iterate(itor);
	        if (itor.getFoundNode() != null) {
	            return itor.getFoundNode();
	        }
	    }
	    return null;
	}
	
	/**
	 * ��ǰѡ�е����������ƶ���
	 */
	public void moveFlowUp() {
	    if (selectedNode == null || selectedNode.getFlow() == null) {
	        return;
	    }
	    int index = flows.indexOf(selectedNode.getFlow());
	    if (index < 1) {
	        return;
	    }
	    Flow f1 = flows.get(index - 1);
	    Flow f2 = flows.get(index);
	    flows.set(index, f1);
	    flows.set(index - 1, f2);
        layout();
        redraw();
	}
	
	/**
	 * ��ǰѡ�е����������ƶ���
	 */
	public void moveFlowDown() {
	    if (selectedNode == null || selectedNode.getFlow() == null) {
            return;
        }
        int index = flows.indexOf(selectedNode.getFlow());
        if (index < 0 || index >= flows.size() - 1) {
            return;
        }
        Flow f1 = flows.get(index + 1);
        Flow f2 = flows.get(index);
        flows.set(index, f1);
        flows.set(index + 1, f2);
        layout();
        redraw();
	}
	
	/**
	 * �����֪ͨ�¼��Ķ����ڵ��޸��¼�����Ҫ����֪ͨID�������������޸Ĺ��Ľڵ���ӽڵ��б�
	 * @param expr
	 */
	public void handleNotifyActionChanged(IExpr expr) {
	    FindNotifyActionIterator itor = new FindNotifyActionIterator(expr);
	    for (Flow flow : flows) {
	        flow.iterate(itor);
	        if (itor.getFoundNode() != null) {
	            // �����Ҫ�����������޸Ľڵ��֪ͨID
                AbstractNotifyAction action = (AbstractNotifyAction)expr;
                if (action.notifyID == 0) {
    	            NotifyIDIterator itor2 = new NotifyIDIterator();
                    for (Flow flow2 : flows) {
                        flow2.iterate(itor2);
                    }
                    action.notifyID = itor2.getNextNotifyID();
                }
                
                // �������ӽڵ�
                itor.getFoundNode().updateChildren(flatShow, flows);
	            
	            return;
	        }
	    }
	}

	// ����ķ���ʵ���϶����ݽӿڡ�
	
	/**
	 * ���϶����ݽ��������¼���
	 */
	public void dragEnter(DropTargetEvent event) {}
	
	/**
	 * �϶������뿪�����¼���
	 */
	public void dragLeave(DropTargetEvent event) {}
	
	/**
	 * �϶������޸��¼���
	 */
	public void dragOperationChanged(DropTargetEvent event) {}
	
	
	/**
	 * �϶������ڿؼ����򻬶��¼���
	 */
	public void dragOver(DropTargetEvent event) {
		Point topLeft = toDisplay(0, 0);
		int x = event.x - topLeft.x;
		int y = event.y - topLeft.y;
	    FlowNode node = findNode(x, y);
	    String oldStateText = stateText;
        if (hoverNode != node) {
            hoverNode = node;
            isDraggingTemplate = true;
            redraw();
        } 
        
        if(node != null) {
            if(x <= node.getBounds().x + 20) {
                stateText = "ǰ��";
            } else if(x > node.getBounds().x + 20 && x < node.getBounds().x + 40) {
                stateText = "��";
            } else if(x >= node.getBounds().x + node.getBounds().width - 20){
                stateText = "����";
            } else {
                stateText = "��";
            }
        } else {
            stateText = "�½�Trigger";
        }
        if(stateText.endsWith(oldStateText) == false) {
            redraw();
        }
        
	    event.detail = DND.DROP_NONE;
	    IExpr expr = designer.getSelectedExpr();
	    if (expr == null) {
	        return;
	    }
	    if (hoverNode != null) {
	        // ����϶���ĳ���ڵ��ϣ��ɽڵ��������Ƿ������϶����
	        if (hoverNode.canAccept(expr)) {
	            event.detail = DND.DROP_COPY;
	        }
	    } else {
	        // ����϶����մ�����ֻ�����������ʽ����������½����´���һ������
	        if (expr.isCondition()) {
	            event.detail = DND.DROP_COPY;
	        }
	    }
	}
	
	/**
	 * �϶����ݱ������¼���
	 */
	public void drop(DropTargetEvent event) {
	    stateText = "";
	    
        Point topLeft = toDisplay(0, 0);
        FlowNode node = findNode(event.x - topLeft.x, event.y - topLeft.y);
        if (hoverNode != node) {
            hoverNode = node;
            isDraggingTemplate = false;
            redraw();
        }

        String exprStr = (String)event.data;
	    ExpressionList list = ExpressionList.fromString(exprStr);
	    IExpr expr = ProjectData.getActiveProject().config.templateManager.recognize(list.getExpr(0), questInfo);
	    
	    // ����������һ����֪ͨID�Ķ��������Զ�����֪ͨID
	    if (expr instanceof AbstractNotifyAction) {
	        NotifyIDIterator itor = new NotifyIDIterator();
	        for (Flow flow : flows) {
	            flow.iterate(itor);
	        }
	        ((AbstractNotifyAction)expr).notifyID = itor.getNextNotifyID();
	    }
	    
	    if (hoverNode != null) {
	        // ����϶���ĳ���ڵ��ϣ����ɴ˽ڵ�����������߼�
	        try {
	            
    	        if (hoverNode.accept(expr, event.x - topLeft.x, event.y - topLeft.y, flatShow, flows)) {
        	        layout();
                    redraw();
                    notifyModified();
    	        }
	        } catch (Exception e) {
	            MessageDialog.openError(getShell(), "����", e.toString());
	        }
	    } else {
	        // ����϶��������ʽ���մ�������һ��������
	        if (expr.isCondition()) {
	            Flow newFlow = new Flow();
	            ConditionNode newCond = new ConditionNode(newFlow);
	            newCond.setCondition(expr);
	            newFlow.setStartNode(newCond);
	            flows.add(newFlow);
	            ActionGroupNode gn = new ActionGroupNode(newFlow);
	            ActionNode an = new ActionNode(newFlow);
	            an.setAction(new A_Empty());
	            gn.addChild(an);
	            newCond.addChild(gn);
	            layout();
	            redraw();
	            notifyModified();
                this.ensureNodeVisible(newCond);
	        }
	    }
	    setSelectedNode(selectedNode);
	}
	
	/**
	 * �϶����ݱ�����ǰ�������ݼ�顣
	 */
	public void dropAccept(DropTargetEvent event) {
	    System.out.println(event);
	}

	// ������һЩ����ö�����ࡣ
	
    /**
     * ���ڼ��һ�����̷�Χ��ö������
     * @author lighthu
     */
    class ComputeSizeIterator implements IFlowIterator {
        int minx = Integer.MAX_VALUE;
        int miny = Integer.MAX_VALUE;
        int maxx = Integer.MIN_VALUE;
        int maxy = Integer.MIN_VALUE;
        
        public boolean walk(FlowNode node) {
            if (node.getBounds() != null) {
                Rectangle b = node.getBounds();
                if (b.x < minx) {
                    minx = b.x;
                }
                if (b.y < miny) {
                    miny = b.y;
                }
                if (b.x + b.width > maxx) {
                    maxx = b.x + b.width;
                }
                if (b.y + b.height > maxy) {
                    maxy = b.y + b.height;
                }
            }
            return false;
        }
        
        public Rectangle getBounds() {
            if (minx == Integer.MAX_VALUE) {
                return new Rectangle(0, 0, 0, 0);
            }
            return new Rectangle(minx, miny, maxx - minx, maxy - miny);
        }
    }

    /**
     * ���ڵ������������ж���Yλ�õ�ö������
     * @author lighthu
     */
    class AdjustYIterator implements IFlowIterator {
        int offset;
        
        public AdjustYIterator(int off) {
            offset = off;
        }
        
        public boolean walk(FlowNode node) {
            if (node.getBounds() != null) {
                node.getBounds().y += offset;
            }
            return false;
        }
    }

    /**
     * ���ڵ������������ж���Xλ�õ�ö������
     * @author lighthu
     */
    class AdjustXIterator implements IFlowIterator {
        int offset;
        
        public AdjustXIterator(int off) {
            offset = off;
        }
        
        public boolean walk(FlowNode node) {
            if (node.getBounds() != null) {
                node.getBounds().x += offset;
            }
            return false;
        }
    }

    /**
     * ���ڲ���ָ��λ���ϵĽڵ��ö������
     * @author lighthu
     */
    class FindNodeIterator implements IFlowIterator {
        int x;
        int y;
        FlowNode groupNode;
        FlowNode node;
        
        public FindNodeIterator(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public boolean walk(FlowNode node) {
            if (node.getBounds() != null) {
                Rectangle nbounds = node.getBounds();
                if (node instanceof ActionGroupNode || node instanceof ConditionGroupNode) {
                    if (new Rectangle(nbounds.x - 10, nbounds.y - 10, nbounds.width + 20, nbounds.height + 20).contains(x, y)) {
                        groupNode = node;
                    }
                } else if (nbounds.contains(x, y)) {
                    this.node = node;
                    return true;
                }
            }
            return false;
        }
        
        public FlowNode getFoundNode() {
            if (node != null) {
                return node;
            }
            return groupNode;
        }
    }

    /**
     * ���ڲ��Ҵ�֪ͨ�¼��Ķ����ڵ��ö������
     * @author lighthu
     */
    class FindNotifyActionIterator implements IFlowIterator {
        ActionNode foundNode;
        IExpr expr;
        
        public FindNotifyActionIterator(IExpr expr) {
            this.expr = expr;
        }
        
        public boolean walk(FlowNode node) {
            if (node instanceof ActionNode) {
                ActionNode an = (ActionNode)node;
                if (an.getAction() == expr) {
                    foundNode = an;
                    return true;
                }
            }
            return false;
        }
        
        public ActionNode getFoundNode() {
            return foundNode;
        }
    }
    
    /**
     * ���ڲ�����һ�����ظ�֪ͨID��ö������
     * @author lighthu
     */
    class NotifyIDIterator implements IFlowIterator {
        HashSet<Integer> usedIDs = new HashSet<Integer>();
        
        public boolean walk(FlowNode node) {
            if (node instanceof ActionNode && ((ActionNode)node).getAction() instanceof AbstractNotifyAction) {
                usedIDs.add(((AbstractNotifyAction)((ActionNode)node).getAction()).notifyID);
            }
            return false;
        }
        
        public int getNextNotifyID() {
            int ret = 1;
            while (usedIDs.contains(ret)) {
                ret++;
            }
            return ret;
        }
    }
    
    /**
     * ���ڻ������нڵ��ö������
     * @author lighthu
     */
    class PaintIterator implements IFlowIterator {
        GC gc;
        Rectangle clip;
        
        public PaintIterator(GC gc, Rectangle clip) {
            this.gc = gc;
            this.clip = clip;
        }
        
        public boolean walk(FlowNode node) {
            if (node.getBounds() != null) {
                int lh = gc.getFontMetrics().getHeight();

                // ����ɫ
                Rectangle nbounds = node.getBounds();
                if (node instanceof ActionGroupNode || node instanceof ConditionGroupNode) {
                    nbounds = new Rectangle(nbounds.x - 10, nbounds.y - 10, nbounds.width + 20, nbounds.height + 20);
                } else {
                    nbounds = new Rectangle(nbounds.x, nbounds.y, nbounds.width, nbounds.height);
                }
                if (nbounds.intersects(clip)) {
                    nbounds.x -= clip.x;
                    nbounds.y -= clip.y;
                    if (hoverNode == node) {
                        SWTUtils.drawRoundRect(gc, nbounds, 0xDAF2FC, 0xF9FDFF, 0xEAF7FF);
                        if (isDraggingTemplate && node instanceof AbstractConditionNode && 
                                (node.getParent() == null || !(node.getParent() instanceof ActionGroupNode))) {
                            if((node.getParent() != null && node.getParent() instanceof ConditionGroupNode) || node instanceof AbstractConditionNode) {
                                Rectangle bounds2 = new Rectangle(nbounds.x, nbounds.y, 20, nbounds.height);
                                SWTUtils.drawRoundRect(gc, bounds2, 0xDADADA, 0x00E6E6, 0xF9F9F9);
                                
                                Rectangle bounds3 = new Rectangle(nbounds.x + 20, nbounds.y, 20, nbounds.height);
                                SWTUtils.drawRoundRect(gc, bounds3, 0xDADADA, 0xFF00E6, 0xF9F9F9);                            
                            }
                            Rectangle bounds2 = new Rectangle(nbounds.x + nbounds.width - 20, nbounds.y, 20, nbounds.height);
                            SWTUtils.drawRoundRect(gc, bounds2, 0xDADADA, 0xE6E600, 0xF9F9F9);
                            
                            gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
                            gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
                            gc.drawString(stateText, nbounds.x, nbounds.y + nbounds.height + 2);
                            
                        }
                    } else if (selectedNode == node) {
                        SWTUtils.drawRoundRect(gc, nbounds, 0x9ADFFE, 0xF2F9FE, 0xD6F0FD);
                    } else if (!(node instanceof ActionGroupNode) && !(node instanceof ConditionGroupNode)) {
                        SWTUtils.drawRoundRect(gc, nbounds, 0xDADADA, 0xF9F9F9, 0xE6E6E6);
                    }
                    if (node instanceof ActionGroupNode) {
                        // ���߽�
                        gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
                        gc.setLineStyle(SWT.LINE_DOT);
                        gc.drawRectangle(nbounds);
                        gc.setLineStyle(SWT.LINE_SOLID);
                    } else if(node instanceof ConditionGroupNode) {
                        // ���߽�
                        gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_YELLOW));
                        gc.setLineStyle(SWT.LINE_DOT);
                        gc.drawRectangle(nbounds);
                        gc.setLineStyle(SWT.LINE_SOLID);
                    } else if (node instanceof ConditionNode || node instanceof ActionNode) {
                        // ����������ɫ
                        if (node instanceof ConditionNode) {
                            gc.setForeground(SWTResourceManager.getColor(0x99, 0x00, 0x00));
                        } else {
                            gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
                        }
                        // ������
                        String[] lines = SWTUtils.formatText(node.toString(), gc, nbounds.width - 4 * 2);
                        int x = nbounds.x + 4;
                        int y = nbounds.y + 4;
                        for (int i = 0; i < lines.length; i++) {
                            gc.drawText(lines[i], x, y, true);
                            y += lh;
                        }
                    }
                } else {
                     nbounds.x -= clip.x;
                     nbounds.y -= clip.y;
                }
                
                if(node instanceof ConditionNode && node.getParent() instanceof ConditionGroupNode) {
                    if(((ConditionGroupNode)node.getParent()).hasCondition((ConditionNode)node)) {
                        return false;
                    }                    
                }

                // ��������һ����������
                gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
                gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
                if (node.getParent() != null && !(node.getParent() instanceof ActionGroupNode)) {
                    int startx = nbounds.x - X_MARGIN / 2;
                    int endx = nbounds.x;
                    if (node instanceof ActionGroupNode || node instanceof ConditionGroupNode) {
                        startx += 10;
                    }
                    int thisy = nbounds.y + nbounds.height / 2;
                    gc.drawLine(startx, thisy, endx, thisy);
                    gc.fillPolygon(new int[] { endx, thisy, endx - 4, thisy - 4, endx - 4, thisy + 4});
                }
                
                // ���Ƶ���һ����������
                if (!(node instanceof ActionGroupNode) && node.getChildren().size() > 0) {
                    int startx = nbounds.x + nbounds.width;
                    int endx = startx + X_MARGIN / 2;

                    FlowNode child = node.getChildren().get(0);

                    if(node instanceof ConditionGroupNode) {
                        int starty = child.getBounds().y + child.getBounds().height / 2 - clip.y;
                        child = node.getChildren().get(node.getChildren().size() - 1);
                        int endy = child.getBounds().y + child.getBounds().height / 2 - clip.y;
                        int thisy = node.getChildren().get(0).getBounds().y + node.getChildren().get(0).getBounds().height / 2 - clip.y;
                        endx -= 10;
                        gc.drawLine(startx, thisy, endx, thisy);
                        gc.drawLine(endx, starty, endx, endy);
                    } else {
                        int starty = child.getBounds().y + child.getBounds().height / 2 - clip.y;
                        child = node.getChildren().get(node.getChildren().size() - 1);
                        int endy = child.getBounds().y + child.getBounds().height / 2 - clip.y;
                        int thisy = nbounds.y + nbounds.height / 2;
                        gc.drawLine(startx, thisy, endx, thisy);
                        gc.drawLine(endx, starty, endx, endy);
                    }
                }
            } else if(node instanceof ConditionGroupNode) {
                for(ConditionNode conditionNode : ((ConditionGroupNode)node).conditionNodes) {
                    walk(conditionNode);
                }
            }
            return false;
        }
    }
}
