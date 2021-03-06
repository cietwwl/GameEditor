package com.pip.game.editor.quest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.pip.game.data.Currency;
import com.pip.game.data.DataObject;
import com.pip.game.data.NPCTemplate;
import com.pip.game.data.ProjectData;
import com.pip.game.data.item.DropGroup;
import com.pip.game.data.item.DropItem;
import com.pip.game.data.item.DropNode;
import com.pip.game.data.map.GameMapObject;
import com.pip.game.data.quest.Quest;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.QuestRewardItem;
import com.pip.game.data.quest.QuestRewardSet;
import com.pip.game.data.quest.QuestTarget;
import com.pip.game.data.quest.QuestTrigger;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.ExpressionList;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.DefaultDataObjectEditor;
import com.pip.game.editor.property.ChooseDropGroupDialog;
import com.pip.game.editor.property.ChooseNPCDialog;
import com.pip.game.editor.property.ChooseNPCTemplateDialog;
import com.pip.game.editor.quest.expr.C_TaskFinished;
import com.pip.util.AutoSelectAll;
import com.pip.util.Utils;

public class QuestEditor extends DefaultDataObjectEditor {
	protected Text textUnfinDesc;
	protected Table table;
	/**
	 * 任务目标表的列文本：第一列是条件、第二列是描述；最后一行显示新建选项。
	 */
    class TargetTableLabelProvider extends LabelProvider implements ITableLabelProvider {
    	public String getColumnText(Object element, int columnIndex) {
    		if (element instanceof String) {
    			if (columnIndex == 0) {
    				return "新目标...";
    			} else {
    				return "";
    			}
    		} else {
    			QuestTarget target = (QuestTarget)element;
    			if (columnIndex == 0) {
    				return ExpressionList.toNatureString(target.condition); 
    			} else if (columnIndex == 1) {
    				return target.description;
    			} else {
    			    return target.hint;
    			}
    		}
    	}
    	public Image getColumnImage(Object element, int columnIndex) {
    		return null;
    	}
    }
    /**
     * 任务目标表的内容：每个目标一行，最后一行用空串表示新建选项。
     */
    class TargetTableContentProvider implements IStructuredContentProvider {
    	public Object[] getElements(Object inputElement) {
    		Quest quest = (Quest)inputElement;
    		Object[] ret = new Object[quest.targets.size() + 1];
    		quest.targets.toArray(ret);
    		ret[quest.targets.size()] = "";
    		return ret;
    	}
    	public void dispose() {
    	}
    	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    	}
    }
    /**
     * 奖励树的文本：分四类，奖励集合、奖励项、新建奖励集合("")、新建奖励项("x")
     */
    class RewardTreeLabelProvider extends LabelProvider {
    	public String getText(Object element) {
    		if (element instanceof String) {
    			String str = (String)element;
    			if (str.length() == 0) {
    				return "新建分支..."; 
    			} else {
    				return "添加奖励...";
    			}
    		} else {
    			return element.toString();
    		}
    	}
    	public Image getImage(Object element) {
    		return null;
    	}
    }
    /**
     * 奖励树的数据：顶层是分支列表，""表示新建分支；第二层是分支的奖励项列表，"x"表示新建奖励项。
     */
    class RewardTreeContentProvider implements IStructuredContentProvider, ITreeContentProvider {
    	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    	}
    	public void dispose() {
    	}
    	public Object[] getElements(Object inputElement) {
    		return getChildren(inputElement);
    	}
    	public Object[] getChildren(Object parentElement) {
    		if (parentElement instanceof Quest) {
    			Quest q = (Quest)parentElement;
    			Object[] ret = new Object[q.rewards.size() + 1];
    			q.rewards.toArray(ret);
    			ret[q.rewards.size()] = "";
    			return ret;
    		}
    		if (parentElement instanceof QuestRewardSet) {
    			QuestRewardSet qs = (QuestRewardSet)parentElement;
    			Object[] ret = new Object[qs.rewardItems.size() + 1];
    			qs.rewardItems.toArray(ret);
    			ret[qs.rewardItems.size()] = String.valueOf(qs.id);
    			return ret;
    		}
    		return new Object[0];
    	}
    	public Object getParent(Object element) {
    		if (element instanceof QuestRewardSet || "".equals(element)) {
    			return getEditObject();
    		}
    		if (element instanceof QuestRewardItem) {
    			return ((QuestRewardItem)element).owner;
    		} else {
    			return findReward(Integer.parseInt((String)element));
    		}
    	}
    	public boolean hasChildren(Object element) {
    		return element instanceof QuestRewardSet;
    	}
    }
    
    class TableContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            if(inputElement instanceof List){
                return ((List)inputElement).toArray();
            }
            return new Object[0];
        }
        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
    class TableLabelProvider implements ITableLabelProvider{

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            if(element instanceof DropNode){
                DropNode node = (DropNode)element;
                switch(columnIndex){
                    case 0:{/* 类型 */
                        switch (((DropNode) element).type) {
                        case DropItem.DROP_TYPE_DROPGROUP:
                            return "掉落组";
                        case DropItem.DROP_TYPE_EQUI:
                            return "装备";
                        case DropItem.DROP_TYPE_ITEM:
                            return "物品";
                        case DropItem.DROP_TYPE_MONEY:
                            return "金钱";
                        case DropItem.DROP_TYPE_EXP:
                            return "经验";
                        default:
                            // 扩展货币掉落
                            Currency c = (Currency)ProjectData.getActiveProject().findDictObject(Currency.class, ((DropNode)element).type);
                            return c.title;
                        }
                    }
                    case 1:{/* 名称 */
                        if (node.type == DropItem.DROP_TYPE_ITEM) {
                            return ProjectData.getActiveProject().findItem(node.id).toString();
                        }
                        else if (node.type == DropItem.DROP_TYPE_DROPGROUP) {
                            return ProjectData.getActiveProject().findObject(DropGroup.class, node.id).toString();
                        }
                        else if (node.type == DropItem.DROP_TYPE_EQUI) {
                            return ProjectData.getActiveProject().findEquipment(node.id).toString();
                        }

                        return "";
                    }
                    case 2:{/* 掉落几率 */
                        return String.valueOf(node.getRateString());
                    }
                    case 3:{/* 掉落数量 */
                        return node.quantityMin + "-" + node.quantityMax;
                    }
                    case 4:{/* 任务掉落 */
                        return node.isTask ? "是" : "否";
                    }
                    case 5:{/* 相关任务 */
                        DataObject quest = ProjectData.getActiveProject().findObject(Quest.class, node.taskId);
                        if(quest != null){                            
                            return quest.toString();
                        }
                        break;
                    }
                    case 6:{/* 是否复制 */
                        return node.copy ? "是" : "否";
                    }
                }
            }
            return "";
        }

        public void addListener(ILabelProviderListener listener) {}

        public void dispose() {}

        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        public void removeListener(ILabelProviderListener listener) {}
    }
    
    protected ListViewer npcList;
    protected TableViewer dropTableViewer;
    protected Action addDropNode;
    protected Action delDropNode;
    
    protected Action addNpc;
    protected Action delNpc;
    protected List<NPCTemplate> questNPCList;

    protected QuestDesigner questDesigner;

    protected Button notifyFinishButton;
    protected Button buttonSelectStartNPC, buttonSelectFinishNPC;
    protected Button buttonEditCondition;
    protected Tree rewardTree;
    protected Table targetTable;
    protected Text textPreQuests;
    protected Text textPostDesc;
    protected Text textPreDesc;
    protected Text textAreaID;
    protected TreeViewer rewardTreeViewer;
    protected TableViewer targetTableViewer;
    protected Text textFinishNPC;
    protected Text textStartNPC;
    protected Text textCondition;
    protected Text textRequireFreeBag;
    protected Text textLevel;
    protected Combo comboRepeat;
    protected Combo comboType;
    protected Text textDescription;
    protected Text textTitle;
    protected Text textID;
    protected String additionalCondition = "";
    
    protected Text textRepeatTimeBegin;
    protected Text textRepeatTimeEnd;
    public static final String ID = "com.pip.sanguo.editor.quest.QuestEditor"; //$NON-NLS-1$

    protected QuestInfo questInfo;
    protected Button autoShareButton;
    protected Button buttonValid;
    
    protected Button buttonEditText1;
    protected Button buttonEditText2;
    protected Button buttonEditText3;
    protected Button buttonEditText4;
    
    public Button getAutoShareButton() {
        return autoShareButton;
    }

    public void setAutoShareButton(Button autoShareButton) {
        this.autoShareButton = autoShareButton;
    }
    

    /**
     * Create contents of the editor part
     * @param parent
     */
    @Override
    public void createPartControl(Composite parent) {
        createActions();
        
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FillLayout());

        final CTabFolder tabFolder = new CTabFolder(container, SWT.BOTTOM);

        final CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
        tabItem.setText("基本信息");

        final CTabItem tabItem_1 = new CTabItem(tabFolder, SWT.NONE);
        tabItem_1.setText("设计");
        
        final CTabItem tabItem_2 = new CTabItem(tabFolder, SWT.NONE);
        tabItem_2.setText("掉落设定");
        
        tabFolder.setSelection(tabItem);

        final Composite composite_1 = new Composite(tabFolder, SWT.NONE);
        final GridLayout gridLayout_2 = new GridLayout();
        composite_1.setLayout(gridLayout_2);
        tabItem_1.setControl(composite_1);

        questDesigner = new QuestDesigner(composite_1, SWT.NONE, questInfo, TemplateManager.CONTEXT_SET_QUEST);
        questDesigner.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        questDesigner.addListener(SWT.Modify, new Listener() {
            public void handleEvent(Event evt) {
                setDirty(true);
            }
        });

        final Composite composite = new Composite(tabFolder, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 6;
        composite.setLayout(gridLayout);
        tabItem.setControl(composite);

        final Label idLabel = new Label(composite, SWT.NONE);
        idLabel.setText("ID：");

        textID = new Text(composite, SWT.BORDER);
        final GridData gd_textID = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        textID.setLayoutData(gd_textID);
        textID.addFocusListener(AutoSelectAll.instance);
        textID.addModifyListener(this);

        final Label label_2 = new Label(composite, SWT.NONE);
        label_2.setLayoutData(new GridData());
        label_2.setText("类型：");

        comboType = new Combo(composite, SWT.READ_ONLY);
        comboType.setItems(new String[] {"普通", "场景"});
        final GridData gd_comboType = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        comboType.setLayoutData(gd_comboType);
        comboType.addModifyListener(this);

        final Label label = new Label(composite, SWT.NONE);
        label.setLayoutData(new GridData());
        label.setText("标题：");

        textTitle = new Text(composite, SWT.BORDER);
        final GridData gd_textTitle = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        textTitle.setLayoutData(gd_textTitle);
        textTitle.addFocusListener(AutoSelectAll.instance);
        textTitle.addModifyListener(this);

        final Label label_3 = new Label(composite, SWT.NONE);
        label_3.setLayoutData(new GridData());
        label_3.setText("重复：");

        comboRepeat = new Combo(composite, SWT.READ_ONLY );
        comboRepeat.setItems(new String[] {"不可重复", "每月可完成1次", "每周可完成1次", "每天可完成1次", "无限重复", "时间段重复"});
        comboRepeat.setVisibleItemCount(new String[] {"不可重复", "每月可完成1次", "每周可完成1次", "每天可完成1次", "无限重复", "时间段重复"}.length);
        comboRepeat.select(0);
        comboRepeat.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        comboRepeat.addModifyListener(this);
        
        final Label label_repeatTimeBegin = new Label(composite, SWT.NONE);
        label_repeatTimeBegin.setLayoutData(new GridData());
        label_repeatTimeBegin.setText("重复任务起始时间：");
        
        textRepeatTimeBegin = new Text(composite, SWT.BORDER);
        final GridData gd_textRepeatBegin = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
        textRepeatTimeBegin.setLayoutData(gd_textRepeatBegin);
        textRepeatTimeBegin.addModifyListener(this);
        
        final Label label_repeatTimeEnd = new Label(composite, SWT.NONE);
        label_repeatTimeBegin.setLayoutData(new GridData());
        label_repeatTimeEnd.setText("重复任务终止时间：");
        
        textRepeatTimeEnd = new Text(composite, SWT.BORDER);
        final GridData gd_textRepeatEnd = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
        textRepeatTimeEnd.setLayoutData(gd_textRepeatEnd);
        textRepeatTimeEnd.addModifyListener(this);
        
        
        final Label label_10 = new Label(composite, SWT.NONE);
        label_10.setText("领取提示：\n(接任务时显示)");

        textPreDesc = new Text(composite, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP);
        final GridData gd_textPreDesc = new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1);
        gd_textPreDesc.widthHint = 396;
        gd_textPreDesc.heightHint = 48;
        textPreDesc.setLayoutData(gd_textPreDesc);
        textPreDesc.addModifyListener(this);

        buttonEditText1 = new Button(composite, SWT.NONE);
        buttonEditText1.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                RichTextDialog dlg = new RichTextDialog(getSite().getShell(), questInfo);
                dlg.setText(textPreDesc.getText());
                if (dlg.open() == Dialog.OK) {
                    textPreDesc.setText(dlg.getText());
                }
            }
        });
        final GridData gd_buttonEditText1 = new GridData();
        buttonEditText1.setLayoutData(gd_buttonEditText1);
        buttonEditText1.setText("...");

        final Label label_1 = new Label(composite, SWT.NONE);
        label_1.setText("描述：\n(任务列表显示)");

        textDescription = new Text(composite, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP);
        final GridData gd_textDescription = new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1);
        gd_textDescription.widthHint = 396;
        gd_textDescription.heightHint = 45;
        textDescription.setLayoutData(gd_textDescription);
        textDescription.addModifyListener(this);

        buttonEditText2 = new Button(composite, SWT.NONE);
        buttonEditText2.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                RichTextDialog dlg = new RichTextDialog(getSite().getShell(), questInfo);
                dlg.setText(textDescription.getText());
                if (dlg.open() == Dialog.OK) {
                    textDescription.setText(dlg.getText());
                }
            }
        });
        buttonEditText2.setText("...");

        final Label label_11 = new Label(composite, SWT.NONE);
        label_11.setText("完成提示：\n(交任务时显示)");

        textPostDesc = new Text(composite, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP);
        final GridData gd_textPostDesc = new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1);
        gd_textPostDesc.widthHint = 396;
        gd_textPostDesc.heightHint = 45;
        textPostDesc.setLayoutData(gd_textPostDesc);
        textPostDesc.addModifyListener(this);

        buttonEditText3 = new Button(composite, SWT.NONE);
        buttonEditText3.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                RichTextDialog dlg = new RichTextDialog(getSite().getShell(), questInfo);
                dlg.setText(textPostDesc.getText());
                if (dlg.open() == Dialog.OK) {
                    textPostDesc.setText(dlg.getText());
                }
            }
        });
        final GridData gd_buttonEditText3 = new GridData();
        buttonEditText3.setLayoutData(gd_buttonEditText3);
        buttonEditText3.setText("...");

        final Label label_15 = new Label(composite, SWT.NONE);
        label_15.setText("未完提示：\n(未完成时和NPC\n对话显示)");

        textUnfinDesc = new Text(composite, SWT.WRAP | SWT.V_SCROLL | SWT.MULTI | SWT.BORDER);
        final GridData gd_textUnfinDesc = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
        gd_textUnfinDesc.widthHint = 396;
        gd_textUnfinDesc.heightHint = 45;
        textUnfinDesc.setLayoutData(gd_textUnfinDesc);
        textUnfinDesc.addModifyListener(this);

        buttonEditText4 = new Button(composite, SWT.NONE);
        buttonEditText4.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                RichTextDialog dlg = new RichTextDialog(getSite().getShell(), questInfo);
                dlg.setText(textUnfinDesc.getText());
                if (dlg.open() == Dialog.OK) {
                    textUnfinDesc.setText(dlg.getText());
                }
            }
        });
        final GridData gd_buttonEditText4 = new GridData();
        buttonEditText4.setLayoutData(gd_buttonEditText4);
        buttonEditText4.setText("...");

        final Label label_4 = new Label(composite, SWT.NONE);
        label_4.setText("开始NPC：");

        textStartNPC = new Text(composite, SWT.READ_ONLY | SWT.BORDER);
        final GridData gd_textStartNPC = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textStartNPC.setLayoutData(gd_textStartNPC);

        buttonSelectStartNPC = new Button(composite, SWT.NONE);
        buttonSelectStartNPC.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                selectNPC(0);
            }
        });
        buttonSelectStartNPC.setText("...");

        final Label label_5 = new Label(composite, SWT.NONE);
        label_5.setText("结束NPC：");

        textFinishNPC = new Text(composite, SWT.READ_ONLY | SWT.BORDER);
        final GridData gd_textFinishNPC = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textFinishNPC.setLayoutData(gd_textFinishNPC);

        buttonSelectFinishNPC = new Button(composite, SWT.NONE);
        buttonSelectFinishNPC.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                selectNPC(1);
            }
        });
        buttonSelectFinishNPC.setText("...");

        final Label label_6 = new Label(composite, SWT.NONE);
        label_6.setText("级别：");

        textLevel = new Text(composite, SWT.BORDER);
        final GridData gd_textLevel = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        textLevel.setLayoutData(gd_textLevel);
        textLevel.addFocusListener(AutoSelectAll.instance);
        textLevel.addModifyListener(this);

        final Label label_8 = new Label(composite, SWT.NONE);
        label_8.setText("关联地区：");

        textAreaID = new Text(composite, SWT.BORDER);
        final GridData gd_textAreaID = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        textAreaID.setLayoutData(gd_textAreaID);
        textAreaID.addFocusListener(AutoSelectAll.instance);
        textAreaID.addModifyListener(this);

        final Label label_12 = new Label(composite, SWT.NONE);
        label_12.setText("前置任务：");

        textPreQuests = new Text(composite, SWT.BORDER);
        final GridData gd_textPreQuests = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        textPreQuests.setLayoutData(gd_textPreQuests);
        textPreQuests.addFocusListener(AutoSelectAll.instance);
        textPreQuests.addModifyListener(this);

        final Label label_13 = new Label(composite, SWT.NONE);
        label_13.setLayoutData(new GridData());
        label_13.setText("需求包格：");

        textRequireFreeBag = new Text(composite, SWT.BORDER);
        final GridData gd_textRequireFreeBag = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        textRequireFreeBag.setLayoutData(gd_textRequireFreeBag);
        textRequireFreeBag.addFocusListener(AutoSelectAll.instance);
        textRequireFreeBag.addModifyListener(this);

        final Label label_7 = new Label(composite, SWT.NONE);
        label_7.setText("附加条件：");

        textCondition = new Text(composite, SWT.BORDER);
        textCondition.setEditable(false);
        final GridData gd_textCondition = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        textCondition.setLayoutData(gd_textCondition);
        textCondition.addFocusListener(AutoSelectAll.instance);
        textCondition.addModifyListener(this);

        buttonEditCondition = new Button(composite, SWT.NONE);
        buttonEditCondition.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                onEditCondition();
            }
        });
        final GridData gd_buttonEditCondition = new GridData();
        buttonEditCondition.setLayoutData(gd_buttonEditCondition);
        buttonEditCondition.setText("...");

        buttonValid = new Button(composite, SWT.CHECK);
        buttonValid.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                setDirty(true);
            }
        });
        final GridData gd_buttonValid = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
        buttonValid.setLayoutData(gd_buttonValid);
        buttonValid.setText("是否生效");

        final Label label_9 = new Label(composite, SWT.NONE);
        label_9.setText("任务目标：");
        new Label(composite, SWT.NONE);
        new Label(composite, SWT.NONE);

        final Label label_14 = new Label(composite, SWT.NONE);
        label_14.setText("任务奖励：");
        new Label(composite, SWT.NONE);
        new Label(composite, SWT.NONE);

        targetTableViewer = new TableViewer(composite, SWT.FULL_SELECTION | SWT.BORDER);
        targetTableViewer.addDoubleClickListener(new IDoubleClickListener() {
        	public void doubleClick(final DoubleClickEvent event) {
        		IStructuredSelection sel = (IStructuredSelection)targetTableViewer.getSelection();
        		if (!sel.isEmpty()) {
        			onDoubleClick(targetTableViewer, sel.getFirstElement());
        		}
        	}
        });
        targetTableViewer.setLabelProvider(new TargetTableLabelProvider());
        targetTableViewer.setContentProvider(new TargetTableContentProvider());
        targetTable = targetTableViewer.getTable();
        targetTable.setLinesVisible(true);
        targetTable.setHeaderVisible(true);
        final GridData gd_targetTable = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
        targetTable.setLayoutData(gd_targetTable);
		targetTable.addListener(SWT.KeyDown, new Listener() {
            public void handleEvent(Event event) {
            	event.doit = !handleKey(targetTableViewer, event.keyCode, event.stateMask);
            }
        });

        final TableColumn targetConditionColumn = new TableColumn(targetTable, SWT.NONE);
        targetConditionColumn.setWidth(148);
        targetConditionColumn.setText("条件");

        final TableColumn targetDescColumn = new TableColumn(targetTable, SWT.NONE);
        targetDescColumn.setWidth(221);
        targetDescColumn.setText("描述");

        final TableColumn targetHintColumn = new TableColumn(targetTable, SWT.NONE);
        targetHintColumn.setWidth(221);
        targetHintColumn.setText("提示");
        
        rewardTreeViewer = new TreeViewer(composite, SWT.FULL_SELECTION | SWT.BORDER);
        rewardTreeViewer.addDoubleClickListener(new IDoubleClickListener() {
        	public void doubleClick(final DoubleClickEvent event) {
        		IStructuredSelection sel = (IStructuredSelection)rewardTreeViewer.getSelection();
        		if (!sel.isEmpty()) {
        			onDoubleClick(rewardTreeViewer, sel.getFirstElement());
        		}
        	}
        });
        rewardTreeViewer.setLabelProvider(new RewardTreeLabelProvider());
        rewardTreeViewer.setContentProvider(new RewardTreeContentProvider());
        rewardTree = rewardTreeViewer.getTree();
        final GridData gd_rewardTree = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 3);
        gd_rewardTree.widthHint = 361;
        rewardTree.setLayoutData(gd_rewardTree);
		rewardTree.addListener(SWT.KeyDown, new Listener() {
            public void handleEvent(Event event) {
            	event.doit = !handleKey(rewardTreeViewer, event.keyCode, event.stateMask);
            }
        });

        notifyFinishButton = new Button(composite, SWT.CHECK);
        notifyFinishButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                setDirty(true);
            }
        });
        final GridData gd_notifyFinishButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
        notifyFinishButton.setLayoutData(gd_notifyFinishButton);
        notifyFinishButton.setText("任务目标全部达成后在客户端显示通知消息");

        autoShareButton = new Button(composite, SWT.CHECK);
        autoShareButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                setDirty(true);
            }
        });
        final GridData gd_autoShareButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
        autoShareButton.setLayoutData(gd_autoShareButton);
        autoShareButton.setText("接受任务时自动共享给周围队友");
        
        Composite dropComposite = new Composite(tabFolder, SWT.NONE);
        final GridLayout gridLayout_1 = new GridLayout();
        gridLayout_1.numColumns = 2;
        dropComposite.setLayout(gridLayout_1);
        
        npcList = new ListViewer(dropComposite, SWT.BORDER);
        final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.widthHint = 535;
        npcList.getControl().setLayoutData(gridData);
        npcList.setContentProvider(new IStructuredContentProvider(){
            public Object[] getElements(Object inputElement) {
                if(inputElement instanceof List){
                    return ((List)inputElement).toArray();
                }
                return new Object[0];
            }

            public void dispose() {}

            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
        });
        npcList.setLabelProvider(new LabelProvider(){
            public String getText(Object element){
                if(element != null){
                    return element.toString();
                }
                return "";
            }
        });
        npcList.addSelectionChangedListener(new ISelectionChangedListener(){
            public void selectionChanged(SelectionChangedEvent event) {
                StructuredSelection select = (StructuredSelection)event.getSelection();
                if(!select.isEmpty()){
                    NPCTemplate template = (NPCTemplate)select.getFirstElement();
                    dropTableViewer.setInput(template.dropGroups);
                }
            }
        });
        npcList.addDoubleClickListener(new IDoubleClickListener(){
            public void doubleClick(DoubleClickEvent event) {
                StructuredSelection sel = (StructuredSelection)event.getSelection();
                if(!sel.isEmpty()){
                    onDoubleClick(npcList, sel.getFirstElement());
                }
            }
        });

        dropTableViewer = new TableViewer(dropComposite, SWT.BORDER | SWT.FULL_SELECTION);
        dropTableViewer.setContentProvider(new TableContentProvider());
        dropTableViewer.setLabelProvider(new TableLabelProvider());
        dropTableViewer.addDoubleClickListener(new IDoubleClickListener(){
            public void doubleClick(DoubleClickEvent event) {
                StructuredSelection sel = (StructuredSelection)event.getSelection();
                if(!sel.isEmpty()){
                    onDoubleClick(dropTableViewer, sel.getFirstElement());
                }
            }
        });
        table = dropTableViewer.getTable();
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        table.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true));
        
        final TableColumn newColumnTableColumn = new TableColumn(table, SWT.CENTER);
        newColumnTableColumn.setWidth(100);
        newColumnTableColumn.setText("类型");

        final TableColumn newColumnTableColumn_3 = new TableColumn(table, SWT.CENTER);
        newColumnTableColumn_3.setWidth(100);
        newColumnTableColumn_3.setText("名称");

        final TableColumn newColumnTableColumn_1 = new TableColumn(table, SWT.CENTER);
        newColumnTableColumn_1.setWidth(100);
        newColumnTableColumn_1.setText("掉落几率");

        final TableColumn newColumnTableColumn_2 = new TableColumn(table, SWT.CENTER);
        newColumnTableColumn_2.setWidth(100);
        newColumnTableColumn_2.setText("掉落数量");
        
        final TableColumn newColumnTableColumn_4 = new TableColumn(table, SWT.CENTER);
        newColumnTableColumn_4.setWidth(80);
        newColumnTableColumn_4.setText("任务掉落");
        
        final TableColumn newColumnTableColumn_5 = new TableColumn(table, SWT.CENTER);
        newColumnTableColumn_5.setWidth(100);
        newColumnTableColumn_5.setText("相关任务");
        
        final TableColumn newColumnTableColumn_6 = new TableColumn(table, SWT.CENTER);
        newColumnTableColumn_6.setWidth(80);
        newColumnTableColumn_6.setText("是否共享");

        MenuManager mgrTable = new MenuManager();
        mgrTable.add(addDropNode);
        mgrTable.add(delDropNode);
        Menu menuTable = mgrTable.createContextMenu(dropTableViewer.getControl());
        table.setMenu(menuTable);
        
        MenuManager mgrList = new MenuManager();
        mgrList.add(addNpc);
        mgrList.add(delNpc);
        Menu menuList = mgrList.createContextMenu(npcList.getControl());
        npcList.getControl().setMenu(menuList);
        
        tabItem_2.setControl(dropComposite);

        final Button buttonViewCode = new Button(composite, SWT.NONE);
        buttonViewCode.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                String str = questInfo.generateClientGTL();
                ViewTextDialog dlg = new ViewTextDialog(getSite().getShell());
                dlg.setContent(str);
                dlg.open();
            }
        });
        final GridData gd_buttonViewCode = new GridData(SWT.LEFT, SWT.CENTER, false, false, 6, 1);
        buttonViewCode.setLayoutData(gd_buttonViewCode);
        buttonViewCode.setText("查看客户端任务代码");
        
		
        textRepeatTimeBegin.setEnabled(false);
        textRepeatTimeEnd.setEnabled(false);
        // 设置初始值
        Quest dataDef = (Quest)editObject;
        questDesigner.setup(0, null);
        textID.setText(String.valueOf(dataDef.id));
        comboType.select(dataDef.type);
        textTitle.setText(dataDef.title);
        comboRepeat.select(dataDef.repeatType);
        textRepeatTimeBegin.setText(dataDef.repeatBeginTime);
        textRepeatTimeEnd.setText(dataDef.repeatEndTime);
        textPreDesc.setText(dataDef.preDescription);
        textDescription.setText(dataDef.description);
        textPostDesc.setText(dataDef.postDescription);
        textUnfinDesc.setText(dataDef.unfinishDescription);
        textAreaID.setText(String.valueOf(dataDef.areaID));
        
        StringBuffer startNpcBuffer = new StringBuffer();
        if( dataDef.startNpc == null || dataDef.startNpc.equals("") ){
            textStartNPC.setText(GameMapObject.toString(dataDef.owner, -1)); 
        }else{
            String[] startNpc = Utils.splitString(dataDef.startNpc, ';');
            for(int i = 0; i < startNpc.length; i++){
                Integer npcId =  Utils.parseHex(startNpc[i]); 
                //int npcId = Utils.parseHex(startNpc[i]);
                if(startNpcBuffer.length() != 0){
                    startNpcBuffer.append(';');
                }
                startNpcBuffer.append(GameMapObject.toString(dataDef.owner, npcId));
            }
        }
        textStartNPC.setText(startNpcBuffer.toString());
        //textStartNPC.setText(GameMapObject.toString(dataDef.owner, dataDef.startNpc));
        //textFinishNPC.setText(GameMapObject.toString(dataDef.owner, dataDef.finishNpc));
        
        StringBuffer endNpcBuffer = new StringBuffer();
        if( dataDef.finishNpc == null || dataDef.finishNpc.equals("") ){
            textFinishNPC.setText(GameMapObject.toString(dataDef.owner, -1)); 
        }else{
            String[] endNpc = Utils.splitString(dataDef.finishNpc, ';');
            for(int i = 0; i < endNpc.length; i++){
               // Integer npcId =  Integer.parseInt(endNpc[i]); 
                int npcId = Utils.parseHex(endNpc[i]);
                if(endNpcBuffer.length() != 0){
                    endNpcBuffer.append(';');
                }
                endNpcBuffer.append(GameMapObject.toString(dataDef.owner, npcId));
            }
        }
        textFinishNPC.setText(endNpcBuffer.toString());
        
        textLevel.setText(String.valueOf(dataDef.level));
        String[] conds = parseCondition(dataDef.precondition);
        textPreQuests.setText(conds[0]);
        
        //String[] condss = parseCondition(dataDef.precondition + "," + dataDef.condition);
        additionalCondition = dataDef.condition;
        textCondition.setText(dataDef.condition);
        targetTableViewer.setInput(this.getEditObject());
        rewardTreeViewer.setInput(this.getEditObject());
        notifyFinishButton.setSelection(dataDef.notifyFinish);
        autoShareButton.setSelection(dataDef.autoShare);
        buttonValid.setSelection(dataDef.valid);
        textRequireFreeBag.setText(String.valueOf(dataDef.requireFreeBag));
        new Label(composite, SWT.NONE);
        new Label(composite, SWT.NONE);

        List<DataObject> npcTemList = ProjectData.getActiveProject().getDataListByType(NPCTemplate.class);
        questNPCList = new ArrayList<NPCTemplate>();
        for(DataObject npc : npcTemList){
            NPCTemplate template = (NPCTemplate)npc;
            for(DropNode node : template.dropGroups){
                if(node.isTask && node.taskId == dataDef.id){
                    questNPCList.add((NPCTemplate)template.duplicate());
                    break;
                }
            }
        }
        npcList.setInput(questNPCList);
        

        setDirty(false);
        setPartName(this.getEditorInput().getName());
        saveStateToUndoBuffer();
    }
    
    /**
     * 创建掉落列表菜单
     */
    protected void createActions(){
        addDropNode = new Action("新增"){
            public void run(){
                onAdd(addDropNode);
            }
        };
        
        addNpc = new Action("新增"){
            public void run(){
                onAdd(addNpc);
            }
        };
        
        delDropNode = new Action("删除"){
            public void run(){
                onDelete(delDropNode);
            }
        };
        
        delNpc = new Action("删除"){
            public void run(){
                onDelete(delNpc);
            }
        };
    }
    
    /**
     * 增加操作消息响应
     * @param source
     *      增加动作源对象
     */
    protected void onAdd(Object source){
        if(source == addDropNode){
            StructuredSelection npcSelected = (StructuredSelection)npcList.getSelection();
            if(npcSelected.isEmpty()){
                MessageDialog.openInformation(getSite().getShell(), "提示", "请先选择一个NPC模板！");
            }
            else{                
                ChooseDropGroupDialog dropDialog = new ChooseDropGroupDialog(getSite().getShell());
                
                DropNode current = new DropNode();
                current.quantityMax = 1;
                current.quantityMin = 1;
                current.dropRate = 1000000;
                current.isTask = true;
                current.taskId = editObject.id;
                current.type = -1;
                dropDialog.setSelectedItem(current);
                
                if(dropDialog.open() == IDialogConstants.OK_ID){
                    DropNode node = dropDialog.getSelectedObject();
                    
                    NPCTemplate dataDef = (NPCTemplate)npcSelected.getFirstElement();
                    
                    dataDef.dropGroups.add(node);
                    dropTableViewer.refresh();
                }
            }
        }
        else if(source == addNpc){
            ChooseNPCTemplateDialog templateDialog = new ChooseNPCTemplateDialog(getSite().getShell(), ChooseNPCTemplateDialog.ONE_TYPE);
            if(templateDialog.open() == IDialogConstants.OK_ID){
                int npcId = templateDialog.getSelectedTemplate();
                NPCTemplate template = (NPCTemplate)ProjectData.getActiveProject().findObject(NPCTemplate.class, npcId);
                if(template != null){
                    questNPCList.add((NPCTemplate)template.duplicate());
                    npcList.refresh();
                    setDirty(true);
                }
            }
        }
    }
    
    /**
     * 删除消息响应
     * @param source
     *      删除动作源对象
     */
    private void onDelete(Object source){
        if(source == delNpc){
            StructuredSelection selected = (StructuredSelection)npcList.getSelection();
            if(!selected.isEmpty()){     
                questNPCList.remove(selected.getFirstElement());
                npcList.refresh();
                dropTableViewer.refresh();
                
                setDirty(true);
            }
        }
        else if(source == delDropNode){
            StructuredSelection selDrop = (StructuredSelection)dropTableViewer.getSelection();
            if(!selDrop.isEmpty()){
                StructuredSelection selNpc = (StructuredSelection)npcList.getSelection();
                NPCTemplate template = (NPCTemplate)selNpc.getFirstElement();
                template.dropGroups.remove(selDrop.getFirstElement());
                dropTableViewer.refresh();
                
                setDirty(true);
            }
        }
    }
    

    /**
     * 保存当前编辑数据。
     */
    protected void saveData() throws Exception {
        Quest dataDef = (Quest)editObject;
        
        // 读取输入：对象ID、标题、描述
        try {
            dataDef.id = Integer.parseInt(textID.getText());
        } catch (Exception e) {
            throw new Exception("请输入正确的ID。");
        }
        dataDef.title = textTitle.getText().trim();
        dataDef.preDescription = textPreDesc.getText();
        dataDef.description = textDescription.getText();
        dataDef.postDescription = textPostDesc.getText();
        dataDef.unfinishDescription = textUnfinDesc.getText();
        dataDef.type = comboType.getSelectionIndex();
        dataDef.repeatType = comboRepeat.getSelectionIndex();
        if (dataDef.type != 1) {
            try {
                dataDef.level = Integer.parseInt(textLevel.getText());
            } catch (Exception e) {
                throw new Exception("请输入正确的级别。");
            }
        }
        if (dataDef.type == 1) {
	        try {
	            dataDef.areaID = Integer.parseInt(textAreaID.getText());
	        } catch (Exception e) {
	            throw new Exception("请输入正确的地区ID。");
	        }
        }
        
        String[] repeatTimeBeginString = Utils.splitString(textRepeatTimeBegin.getText(),':');
        if(repeatTimeBeginString.length != 3){
            throw new Exception("请按照起始格式分为3段输入时间");
        }
        for(int i = 0; i < repeatTimeBeginString.length; i++){
            int temp = Integer.parseInt(repeatTimeBeginString[i]);
            if(i == 0){
                if(temp < 0 || temp > 24){
                    throw new Exception("小时输入不合法");
                }
            }else{
                if(temp < 0 || temp > 60){
                    throw new Exception("分和秒输入不合法");
                }
            }
        }
        dataDef.repeatBeginTime = textRepeatTimeBegin.getText();
        
        String[] repeatTimeEndString = Utils.splitString(textRepeatTimeEnd.getText(),':');
        if(repeatTimeEndString.length != 3){
            throw new Exception("请按照起始格式分为3段输入时间");
        }
        for(int i = 0; i < repeatTimeEndString.length; i++){
            int temp = Integer.parseInt(repeatTimeEndString[i]);
            if(i == 0){
                if(temp < 0 || temp > 24){
                    throw new Exception("小时输入不合法");
                }
            }else{
                if(temp < 0 || temp > 60){
                    throw new Exception("分和秒输入不合法");
                }
            }
        }
        dataDef.repeatEndTime = textRepeatTimeEnd.getText();
        
        dataDef.notifyFinish = notifyFinishButton.getSelection();
        dataDef.autoShare = autoShareButton.getSelection();
        dataDef.valid = buttonValid.getSelection();
        if (dataDef.type != 1) {
            try {
                dataDef.requireFreeBag = Integer.parseInt(textRequireFreeBag.getText());
            } catch (Exception e) {
                throw new Exception("请输入正确的需求包格数。");
            }
        }
        
        // 把前置任务合并到任务接受条件里面去
        dataDef.precondition = "";
        try {
            int index = textPreQuests.getText().indexOf(";");
            if(index > 0) {
                //或的关系
                String[] secs = textPreQuests.getText().trim().split(";");
                for (int i = 0; i < secs.length; i++) {
                    if (secs[i].length() == 0) {
                        continue;
                    }
                    int qid = Integer.parseInt(secs[i].trim());
                    if (dataDef.precondition.length() > 0) {
                        dataDef.precondition += "; "; 
                    }
                    dataDef.precondition += "TaskFinished(" + qid + ")";
                }
            } else {
                //与的关系
                String[] secs = textPreQuests.getText().trim().split(",");
                for (int i = 0; i < secs.length; i++) {
                    if (secs[i].length() == 0) {
                        continue;
                    }
                    int qid = Integer.parseInt(secs[i].trim());
                    if (dataDef.precondition.length() > 0) {
                        dataDef.precondition += ", "; 
                    }
                    dataDef.precondition += "TaskFinished(" + qid + ")";
                }
            }
        
        } catch (Exception e) {
        	throw new Exception("请按提示格式输入前置任务ID。");
        }
        
       /*  因为有了并行等多种条件，合成还要拆太麻烦了，
       * 用两个来记录，只在自动生成代码的时候合并
       * dataDef.condition = preCondition;
        if (additionalCondition.trim().length() > 0) {
        	if (dataDef.condition.length() > 0) {
        		dataDef.condition += "&";
        	}
        	dataDef.condition += additionalCondition;
        }*/
        dataDef.condition = additionalCondition;
      
        String finishCondition = "";
        for (QuestTarget target : getQuest().targets) {
        	if (target.condition.trim().length() == 0) {
        		continue;
        	}
        	if (finishCondition.length() > 0) {
    			finishCondition += ", ";
    		}
    		finishCondition += target.condition;
        }
        dataDef.finishCondition = finishCondition;
        
        // 保存任务流程
        if (!questDesigner.preSaveCheck()) {
            throw new Exception("操作已取消。");
        }
        questDesigner.saveQuest();

        // 检查输入合法性
        DataObject dobj = ProjectData.getActiveProject().findObject(dataDef.getClass(), dataDef.id);
        if (dobj != null && dobj != getSaveTarget()) {
            throw new Exception("ID重复，请重新输入。");
        }
        if (dataDef.title.length() == 0) {
            throw new Exception("请输入标题。");
        }
        
        // 检查输入的描述字符串，以及任务目标字符串的合法性
        String[] localVars = questInfo.getVariables();
        PQEUtils pqeUtils = ProjectData.getActiveProject().config.pqeUtils;
        pqeUtils.checkRichTextSyntax(dataDef.preDescription, localVars, true);
        pqeUtils.checkRichTextSyntax(dataDef.description, localVars, true);
        pqeUtils.checkRichTextSyntax(dataDef.postDescription, localVars, true);
        pqeUtils.checkRichTextSyntax(dataDef.unfinishDescription, localVars, true);
        for (QuestTarget target : dataDef.targets) {
            ExpressionList expr = ExpressionList.fromString(target.condition);
            if (expr == null || expr.getExprCount() != 1) {
                throw new Exception("任务目标条件格式错误：" + target.condition);
            }
            expr.checkSyntax(localVars, true);
            if (!expr.isClientSupport(localVars)) {
                throw new Exception("任务目标中只能使用客户端支持的函数。");
            }
            pqeUtils.checkRichTextSyntax(target.description, localVars, false);
            pqeUtils.checkRichTextSyntax(target.hint, localVars, false);
        }
        
        // 检查任务条件合法性
        String[] condition = Utils.splitString(dataDef.condition, ';');
        for(int i = 0; i < condition.length; i++){
            ExpressionList exprList = ExpressionList.fromString(condition[i]);
            if (exprList == null) {
                throw new Exception("任务前提条件格式错误：" + condition[i]);
            }
            exprList.checkSyntax(localVars, true);
        }
       /* ExpressionList exprList = ExpressionList.fromString(dataDef.condition);
        if (exprList == null) {
            throw new Exception("任务前提条件格式错误：" + dataDef.condition);
        }*/
        
        // 检查所有任务触发器的合法性
        Set<String> clientVars = new HashSet<String>();
        Set<String> serverVars = new HashSet<String>();
        for (QuestTrigger trigger : questInfo.triggers) {
            ExpressionList expr1 = ExpressionList.fromString(trigger.condition);
            if (expr1 == null) {
                throw new Exception("触发器条件格式错误：" + trigger.condition);
            }
            expr1.checkSyntax(localVars, true);
            ExpressionList expr2 = ExpressionList.fromString(trigger.action);
            if (expr2 == null) {
                throw new Exception("触发器动作格式错误：" + trigger.action);
            }
            expr2.checkSyntax(localVars, false);
            
            // 收集影响到的变量名
            if (expr1.isClientSupport(localVars) && expr2.isClientSupport(localVars)) {
                expr1.searchAffectLocalVar(clientVars);
                expr2.searchAffectLocalVar(clientVars);
            } else {
                expr1.searchAffectLocalVar(serverVars);
                expr2.searchAffectLocalVar(serverVars);
            }
        }
        
        // 检查是否有客户端和服务器都需要修改的变量
        for (String var : clientVars) {
            if (serverVars.contains(var)) {
                throw new Exception("优化时发现 " + var + " 变量同时被客户端和服务器端修改，请调整逻辑以避免这种情况发生。");
            }
        }
        
        // 保存任务
        questInfo.save();
        
        for(NPCTemplate template : questNPCList){
            /* 把当前页面修改更新到目标 */
            ProjectData.getActiveProject().findObject(NPCTemplate.class, template.id).update(template);
        }
        ProjectData.getActiveProject().saveDataList(NPCTemplate.class);
    }

    /**
     * 保存当前编辑状态以用作UNDO数据，这里只保存任务流程数据。
     */
    protected Object saveState() {
        try {
            questDesigner.saveQuest();
            return questInfo.saveToText();
        } catch (Exception e) {
        }
        return null;
    }
    
    /**
     * 根据以前保存的编辑状态恢复当前编辑状态。这里只需要恢复任务数据。
     */
    protected void loadState(Object stateObj) {
        try {
            questInfo.loadFromText((String)stateObj);
            questDesigner.reloadQuest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        super.init(site, input);
        
        // 载入任务详细信息
        try {
            Quest dataDef = (Quest)editObject;
            questInfo = new QuestInfo(dataDef);
            if (dataDef.source.exists()) {
                questInfo.load();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new PartInitException("关卡格式错误。", e);
        }
    }
    
    // 当前编辑的任务。
    protected Quest getQuest() {
    	return (Quest)getEditObject();
    }

    // 根据ID查找任务分支奖励。
    protected QuestRewardSet findReward(int id) {
		Quest q = (Quest)getEditObject();
		for (QuestRewardSet rs : q.rewards) {
			if (rs.id == id) {
				return rs;
			}
		}
		return null;
    }
    
    // 分析任务接受条件，把条件拆分为前置任务和附加条件两部分。返回一个数组，第一个元素是所有前置任务ID（逗号分隔），第二个
    // 元素是附加条件字符串。
    protected String[] parseCondition(String cond) {
    	String[] ret = new String[] { "", "" };
    	
    	//前置任务是或的关系
    	boolean isOr = cond.indexOf(";") > 0;
    	cond = cond.replace(";", ",");
    	
    	ExpressionList exprList = ExpressionList.fromString(cond);
    	if (exprList == null) {
    		return ret;
    	}
    	
    	C_TaskFinished template = new C_TaskFinished();
    	for (int i = 0; i < exprList.getExprCount(); i++) {
    		Expression expr = exprList.getExpr(i);
    		
    		// 判断表达式是否TaskFinished函数调用
    		if(expr.jjtGetNumChildren() > 0 && expr.jjtGetChild(0) instanceof Expression) {
    		    boolean needContinue = false;
    		    for(int j=0; j<expr.jjtGetNumChildren(); j++) {
                    C_TaskFinished thisCond = (C_TaskFinished)template.recognize(questInfo, (Expression)expr.jjtGetChild(j));
                    if (thisCond != null && thisCond.checkTrue) {
                        if (ret[0].length() > 0) {
                            ret[0] += isOr ? "; " : ", ";
                        }
                        ret[0] += thisCond.taskID;
                        needContinue = true;
                    }
    		    }
    		    if(needContinue) {
    		        continue;    		        
    		    }
 
    		} else {
    	          C_TaskFinished thisCond = (C_TaskFinished)template.recognize(questInfo, expr);
    	            if (thisCond != null && thisCond.checkTrue) {
    	                if (ret[0].length() > 0) {
    	                    ret[0] += isOr ? "; " : ", ";
    	                }
    	                ret[0] += thisCond.taskID;
    	                continue;
    	            }
    		}

    		
    		if (ret[1].length() > 0) {
    			ret[1] += isOr ? "; " : ", ";
    		}
    		ret[1] += expr.toString();
    	}
    	return ret;
    }
    
    // 弹出对话框选择NPC。如果index为0，选择的是任务开始NPC；如果index为1，选择的是任务结束NPC。
    protected void selectNPC(int index) {
        Quest dataDef = (Quest)editObject;
        ChooseNPCDialog dlg = new ChooseNPCDialog(getSite().getShell(), ChooseNPCDialog.MORENPC);
        if (index == 0) {
            String[] npcs = Utils.splitString(dataDef.startNpc, ';');
            for(int i = 0; i < npcs.length; i++){
                try{
                    Integer temp = Utils.parseHex(npcs[i]);
                    npcs[i] = temp.toString();
                }catch(Exception e){
                }
            }
            dlg.setSelectedMoreNPC(npcs);
            
        } else {
            String[] npcs = Utils.splitString(dataDef.finishNpc, ';');
            for(int i = 0; i < npcs.length; i++){
               /* Integer temp = Utils.parseHex(npcs[i]);
                npcs[i] = temp.toString();*/
                try{
                    Integer temp = Utils.parseHex(npcs[i]);
                    npcs[i] = temp.toString();
                }catch(Exception e){
                }
            }
            dlg.setSelectedMoreNPC(npcs);
        }
        
        if (dlg.open() == ChooseNPCDialog.OK) {
            if (index == 0) {
                String showNpc = "";
                List<GameMapObject> npcList = dlg.getSelectMoreNpc();
                String startTempNpc = "";
                if(npcList.size() != 0){
                    for(int i = 0; i < npcList.size(); i++){
                        Integer npcId = npcList.get(i).getGlobalID();
                        if( i!= 0){
                            startTempNpc  = startTempNpc + ';' + npcId.toHexString(npcId);
                            showNpc = showNpc + ';'+ GameMapObject.toString(dataDef.owner, npcId);
                        }else{
                            startTempNpc  = startTempNpc + npcId.toHexString(npcId);
                            showNpc = showNpc + GameMapObject.toString(dataDef.owner, npcId);
                        }
                    }
                }
                textStartNPC.setText(showNpc);
                if(!dataDef.startNpc.equals(startTempNpc)){
                    dataDef.startNpc = startTempNpc;
                    setDirty(true);
                }
            } else {
                String showNpc = "";
                List<GameMapObject> npcList = dlg.getSelectMoreNpc();
                dataDef.finishNpc = "";
                String endTempNpc = "";
                if(npcList.size() != 0){
                    for(int i = 0; i < npcList.size(); i++){
                        Integer npcId = npcList.get(i).getGlobalID();
                        if( i!= 0){
                            endTempNpc = endTempNpc + ';' +  npcId.toHexString(npcId);
                            showNpc = showNpc + ';'+ GameMapObject.toString(dataDef.owner, npcId);
                        }else{
                            endTempNpc = endTempNpc + npcId.toHexString(npcId);
                            showNpc = showNpc + GameMapObject.toString(dataDef.owner, npcId);
                        }
                    }
                }
                textFinishNPC.setText(showNpc);
                if(!dataDef.finishNpc.equals(endTempNpc)){
                    dataDef.finishNpc = endTempNpc;
                    setDirty(true);
                }
            }
        }
    }
    
    // 弹出公式编辑器来编辑触发条件
    private void onEditCondition() {
        String newExpr = ExpressionDialog.open(getSite().getShell(), additionalCondition, questInfo, 3, TemplateManager.CONTEXT_SET_QUEST);
        if (newExpr != null) {
            additionalCondition = newExpr;
            StringBuffer tempString = new StringBuffer();
            String[] condition = Utils.splitString(additionalCondition, ';');
            for(int i = 0; i < condition.length - 1; i++){
                tempString.append(ExpressionList.toNatureString(condition[i]));
                tempString.append(';');
            }
            textCondition.setText(additionalCondition);
            setDirty(true);
        }
    }

    /**
     * 处理Table和Tree控件的双击事件。
     * @param viewer 事件来源
     * @param sel 当前选中对象
     */
    protected void onDoubleClick(Object viewer, Object sel) {
    	if (viewer == targetTableViewer) {
    		if ("".equals(sel)) {
    			// 新建目标
    			QuestTarget newTarget = new QuestTarget(getQuest());
    			if (new QuestTargetDialog(getSite().getShell(), newTarget, questInfo).open() == Dialog.OK) {
    				getQuest().targets.add(newTarget);
    				targetTableViewer.refresh();
    				setDirty(true);
    			}
    		} else {
    			// 编辑选中目标
    			QuestTarget target = (QuestTarget)sel;
    			if (new QuestTargetDialog(getSite().getShell(), target, questInfo).open() == Dialog.OK) {
    				targetTableViewer.refresh(target);
    				setDirty(true);
    			}
    		}
    	} else if (viewer == rewardTreeViewer) {
    		if ("".equals(sel)) {
    			// 新建分支
    			QuestRewardSet rs = new QuestRewardSet(getQuest());
    			rs.id = 1;
    			while (findReward(rs.id) != null) {
					rs.id++;
    			}
    			getQuest().rewards.add(rs);
    			rewardTreeViewer.refresh();
    			setDirty(true);
    		} else if (sel instanceof QuestRewardSet) {
    			// 修改分支属性
    			QuestRewardSet rs = (QuestRewardSet)sel;
    			rs.isFinishReward = !rs.isFinishReward;
    			rewardTreeViewer.refresh(rs);
    			setDirty(true);
    		} else if (sel instanceof String) {
    			// 新建奖励项
    			QuestRewardSet rs = findReward(Integer.parseInt((String)sel));
    			QuestRewardItem qri = new QuestRewardItem(rs);
    			if (new QuestRewardItemDialog(getSite().getShell(), qri, false).open() == Dialog.OK) {
    				if (!checkAddRewardItem(qri)) {
    					return;
    				}
    				rs.rewardItems.add(qri);
    				rewardTreeViewer.refresh(rs);
    				setDirty(true);
    			}
    		} else if (sel instanceof QuestRewardItem) {
    			// 编辑奖励项
    			QuestRewardItem qri = (QuestRewardItem)sel;
    			if (new QuestRewardItemDialog(getSite().getShell(), qri, false).open() == Dialog.OK) {
    				if (!checkAddRewardItem(qri)) {
    					return;
    				}
    				rewardTreeViewer.refresh(qri);
    				setDirty(true);
    			}
    		}
    	}
    	else if(viewer == dropTableViewer){
            DropNode selGroup = (DropNode)sel;
            
            ChooseDropGroupDialog dropDialog = new ChooseDropGroupDialog(getSite().getShell());
            dropDialog.setSelectedItem(selGroup);
            if(dropDialog.open() == IDialogConstants.OK_ID){
                DropNode selNewGroup = dropDialog.getSelectedObject();
                /* 当前选中项和原来不同 */
                if (selNewGroup.equals(selGroup)) {
                    selGroup.update(selNewGroup);
                    setDirty(true);
                }
            }
    	}
    }
    
    // 检查新建的奖励项，除了物品，一个分支里面不能有重复类型的奖励项目。
    protected boolean checkAddRewardItem(QuestRewardItem qri) {
    	if (qri.rewardType == QuestRewardItem.REWARD_ITEM) {
    		return true;
    	}
    	for (QuestRewardItem qri2 : qri.owner.rewardItems) {
    		if (qri2 == qri) {
    			continue;
    		}
    		if (qri2.rewardType == qri.rewardType) {
    			MessageDialog.openError(getSite().getShell(), "错误", "这个分支下面已经有一个此类型的奖励了。");
    			return false;
    		}
    	}
    	return true;
    }
    
    /**
     * 处理Table和Tree控件的特殊按键事件。
     * @param viewer 事件来源
     * @param keyCode 键码
     * @param mask 掩码
     * @return 如果不希望这个事件被控件处理，返回true。
     */
    protected boolean handleKey(Object viewer, int keyCode, int mask) {
    	if (viewer == targetTableViewer) {
    		if (keyCode == SWT.DEL) {
    			// 在任务目标表中按DEL键删除选中的任务目标
    			int sel = targetTable.getSelectionIndex();
    			if (sel != -1 && sel < getQuest().targets.size()) {
    				getQuest().targets.remove(sel);
    				targetTableViewer.refresh();
    				setDirty(true);
					targetTable.setSelection(sel);
    			}
    			return true;
    		}
    	} else if (viewer == rewardTreeViewer) {
    		if (keyCode == SWT.DEL) {
    			// 在任务奖励树中按DEL键删除选中的分支或奖励项
    			IStructuredSelection ssel = (IStructuredSelection)rewardTreeViewer.getSelection();
    			if (ssel.isEmpty()) {
    				return false;
    			}
    			Object sel = ssel.getFirstElement();
    			Object nextSel = null;
    			if (sel instanceof QuestRewardSet) {
    				int index = getQuest().rewards.indexOf(sel);
    				getQuest().rewards.remove(index);
    				rewardTreeViewer.refresh();
    				setDirty(true);
    				
    				if (index >= getQuest().rewards.size()) {
    					nextSel = "";
    				} else {
    					nextSel = getQuest().rewards.get(index);
    				}
    			} else if (sel instanceof QuestRewardItem) {
    				QuestRewardItem qri = (QuestRewardItem)sel;
    				int index = qri.owner.rewardItems.indexOf(qri);
    				qri.owner.rewardItems.remove(index);
    				rewardTreeViewer.refresh(qri.owner);
    				setDirty(true);
    				
    				if (index >= qri.owner.rewardItems.size()) {
    					nextSel = String.valueOf(qri.owner.id);
    				} else {
    					nextSel = qri.owner.rewardItems.get(index);
    				}
    			}
    			rewardTreeViewer.setSelection(new StructuredSelection(nextSel));
    		}
    	}
    	return false;
    }
    
	public void modifyText(final ModifyEvent e) {
		super.modifyText(e);
		if (e.getSource() == comboType) {
			if (comboType.getSelectionIndex() == 1) {
				// 场景任务
				targetTable.setEnabled(false);
				textPreQuests.setEnabled(false);
				textPostDesc.setEnabled(false);
				textPreDesc.setEnabled(false);
				textUnfinDesc.setEnabled(false);
				textAreaID.setEnabled(true);
				buttonSelectStartNPC.setEnabled(false);
				buttonSelectFinishNPC.setEnabled(false);
				buttonEditCondition.setEnabled(false);
				textLevel.setEnabled(false);
				comboRepeat.setEnabled(false);
				textRequireFreeBag.setEnabled(false);
				notifyFinishButton.setEnabled(false);
				autoShareButton.setEnabled(false);
			}else {
				targetTable.setEnabled(true);
				textPreQuests.setEnabled(true);
				textPostDesc.setEnabled(true);
				textPreDesc.setEnabled(true);
                textUnfinDesc.setEnabled(true);
				textAreaID.setEnabled(false);
				buttonSelectStartNPC.setEnabled(true);
				buttonSelectFinishNPC.setEnabled(true);
				buttonEditCondition.setEnabled(true);
				textLevel.setEnabled(true);
				comboRepeat.setEnabled(true);
				textRequireFreeBag.setEnabled(true);
				notifyFinishButton.setEnabled(true);
                autoShareButton.setEnabled(true);
			}
		}else if(e.getSource() == comboRepeat){
		    if(comboRepeat.getSelectionIndex() == 5){
		        textRepeatTimeBegin.setEnabled(true);
		        textRepeatTimeEnd.setEnabled(true);
		    }else{
		        textRepeatTimeBegin.setEnabled(false);
	            textRepeatTimeEnd.setEnabled(false);
		    }
		}
	}
}
