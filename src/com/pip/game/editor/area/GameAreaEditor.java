package com.pip.game.editor.area;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.jdom.Document;

import com.pip.game.data.DataObject;
import com.pip.game.data.GameArea;
import com.pip.game.data.GameAreaInfo;
import com.pip.game.data.MapFormat;
import com.pip.game.data.MonsterGroup;
import com.pip.game.data.NPCTemplate;
import com.pip.game.data.ProjectConfig;
import com.pip.game.data.ProjectData;
import com.pip.game.data.map.GameMapExit;
import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.map.GameMapNPC;
import com.pip.game.data.map.GameMapObject;
import com.pip.game.data.map.GamePatrolPath;
import com.pip.game.data.map.GameRelivePoint;
import com.pip.game.data.map.MultiTargetMapExit;
import com.pip.game.data.pkg.PackageFile;
import com.pip.game.data.pkg.PackageFileItem;
import com.pip.game.data.pkg.PackageUtils;
import com.pip.game.data.quest.Quest;
import com.pip.game.editor.DataListView;
import com.pip.game.editor.DefaultDataObjectEditor;
import com.pip.game.editor.EditorPlugin;
import com.pip.game.editor.GenericChooseDialog;
import com.pip.game.editor.property.ChooseMirrorDialog;
import com.pip.game.editor.property.MirrorSetCellEditor;
import com.pip.game.editor.quest.GameAreaCache;
import com.pip.game.editor.wizard.NewQuestWizard;
import com.pip.image.workshop.WorkshopPlugin;
import com.pip.image.workshop.editor.ImageViewer;
import com.pip.image.workshop.editor.ImageViewerListener;
import com.pip.image.workshop.editor.JPEGMergeOptionDialog;
import com.pip.mango.jni.GLUtils;
import com.pip.mapeditor.MapEditor;
import com.pip.mapeditor.data.GameMap;
import com.pip.mapeditor.data.MapFile;
import com.pip.mapeditor.tool.EmulateWalkTool;
import com.pip.mapeditor.tool.TileConfigTool;
import com.pip.mapeditor.tool.WindowViewTool;
import com.pip.propertysheet.PropertySheetEntry;
import com.pip.propertysheet.PropertySheetViewer;
import com.pip.util.AutoSelectAll;
import com.pip.util.EFSUtil;
import com.pip.util.FileWatcher;
import com.pip.util.IFileModificationListener;
import com.pip.util.Point;
import com.pip.util.Utils;
import com.pipimage.image.CompressTextureOption;
import com.pipimage.image.JPEGMergeOption;
import com.swtdesigner.ResourceManager;

public class GameAreaEditor extends DefaultDataObjectEditor implements ImageViewerListener, SelectionListener,
        Runnable, IFileModificationListener {

    class QuestListContentProvider implements IStructuredContentProvider {
        // ֧��2��input��Integer��ʾmapid��GameMapNPC��ʾNPC
        public Object[] getElements(Object inputElement) {
            java.util.List retList = new ArrayList();
            if (inputElement instanceof Integer) {
                int mapID = ((Integer)inputElement).intValue();
                for (Quest q : sceneQuests) {
                    if (q.type == 1) {
                        retList.add(q);
                    } else {
                        int[] npcs = q.getStartNpc();
                        boolean match = false;
                        for (int n : npcs) {
                            if (n != -1 && (n >> 12) == mapID) {
                                match = true;
                                break;
                            }
                        }
                        if (!match) {
                            npcs = q.getEndNpc();
                            for (int n : npcs) {
                                if (n != -1 && (n >> 12) == mapID) {
                                    match = true;
                                    break;
                                }
                            }
                        }
                        if (match) {
                            retList.add(q);
                        }
                    }
                }
                retList.add("�½���������...");
            } else if (inputElement instanceof GameMapNPC) {
                int npcID = ((GameMapNPC)inputElement).getGlobalID();
                for (Quest q : sceneQuests) {
                    if (q.type == 0) {
                        int[] npcs = q.getStartNpc();
                        boolean match = false;
                        for (int n : npcs) {
                            if (n != -1 && n == npcID) {
                                match = true;
                                break;
                            }
                        }
                        if (!match) {
                            npcs = q.getEndNpc();
                            for (int n : npcs) {
                                if (n != -1 && n == npcID) {
                                    match = true;
                                    break;
                                }
                            }
                        }
                        if (match) {
                            retList.add(q);
                        }
                    }
                }
                retList.add("�½�����...");
            }
            return retList.toArray();
        }
        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
    
    class NPCTemplateListProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            java.util.List<DataObject> list = ((ProjectData) inputElement).getDataListByType(NPCTemplate.class);
            java.util.List<DataObject> retList = new ArrayList<DataObject>();
            for (int i = 0; i < list.size(); i++) {
                NPCTemplate t = (NPCTemplate) list.get(i);
                if (matchCondition(t)) {
                    retList.add(t);
                }
            }
            Object[] ret = retList.toArray();
            Arrays.sort(ret, new Comparator<Object>() {
                public int compare(java.lang.Object arg0, java.lang.Object arg1) {
                    NPCTemplate npc1 = (NPCTemplate)arg0;
                    NPCTemplate npc2 = (NPCTemplate)arg1;
                    if (npc1.id < npc2.id) {
                        return -1;
                    } else if (npc1.id == npc2.id) {
                        return 0;
                    } else {
                        return 1;
                    }
                }
                
                public boolean equals(java.lang.Object arg0) {
                    return false;
                }
            });
            return ret;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    private boolean matchCondition(NPCTemplate t) {
        npcSearchText = npcSearch.getText();
        if (npcSearchText == null || npcSearchText.length() == 0) {
            return true;
        }
        if (t.title.indexOf(npcSearchText) >= 0 || String.valueOf(t.id).indexOf(npcSearchText) >= 0) {
            return true;
        }
        return false;
    }

    class MonsterGroupListProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            return ((ProjectData) inputElement).getDataListByType(MonsterGroup.class).toArray();
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    protected Composite propertyContainer;
    protected ListViewer npcTemplateListViewer;
    protected List npcTemplateList;
    private ListViewer monsterGroupListViewer;
    protected List monsterGroupList;
    private Text textSource;
    public Text textDescription;
    public Text textTitle;
    public Text textID;
    public Text textRefArea;
    public Button checkFullAnimate;
    public static final String ID = "com.pip.game.editor.area.GameAreaEditor"; //$NON-NLS-1$

    protected MapFile currentMapFile;
    protected MapFormat currentFormat;
    protected GameAreaInfo areaInfo;

    protected GameMapViewer mapView;
    private ToolBar pageToolBar;
    private ArrayList<ToolItem> pageItems;
    protected PropertySheetViewer propEditor;
    protected ListViewer questListViewer;
    private List questList;

    private java.util.List<Quest> sceneQuests = new ArrayList<Quest>();

    protected boolean playAnimate = GLUtils.glEnabled;
    private boolean disposed;
    private Thread animateThread;
    private Display display;

    protected ToolItem pickupItem;
    protected ToolItem npcItem;
    protected ToolItem patrolPathItem;
    protected ToolItem windowItem;
    protected ToolItem exitItem;
    protected ToolItem vehicleItem;
    private ToolItem playerItem, playerItemSky;

    private ToolItem rangeItem;

    private FileDialog mapFileDialog;

    protected Text npcSearch;
    private String npcSearchText;
    protected ToolBar toolBar;
    
    protected Text vehicleSearch;
    protected ListViewer vehicleTemplateListViewer;
    protected List vehicleTemplateList;

    protected Combo comboMapFormat;
    protected Text[] mapRefTexts;
    protected Button[] mapRefUpdateButtons;
    protected Button[] mapRefEditButtons;
    protected Button[] mapRefGenPackageButtons;
    protected ToolItem relivePointItem;
    protected ToolItem groundPassItem;
    protected ToolItem skyPassItem;
    protected ToolItem safeAreaItem;
    protected ToolItem eyeSightItem;
    protected ToolItem emulateItem;
    protected ToolItem playItem;
    protected Button mirrorSetButton;
    protected ToolItem exportNPCItem;
    
    /**
     * Create contents of the editor part
     * 
     * @param parent
     */
    @Override
    public void createPartControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FillLayout());

        final CTabFolder tabFolder = new CTabFolder(container, SWT.BOTTOM);

        final CTabItem tabItem1 = new CTabItem(tabFolder, SWT.NONE);
        tabItem1.setText("�����༭");
        tabFolder.setSelection(tabItem1);

        final CTabItem tabItem2 = new CTabItem(tabFolder, SWT.NONE);
        tabItem2.setText("������Ϣ");

        final Composite composite2 = new Composite(tabFolder, SWT.NONE);
        final GridLayout gridLayout_1 = new GridLayout();
        gridLayout_1.numColumns = 5;
        composite2.setLayout(gridLayout_1);
        tabItem2.setControl(composite2);

        final Label label = new Label(composite2, SWT.NONE);
        label.setText("ID��");

        textID = new Text(composite2, SWT.BORDER);
        final GridData gd_textID = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
        textID.setLayoutData(gd_textID);
        textID.addFocusListener(AutoSelectAll.instance);
        textID.addModifyListener(this);

        final Label label_1 = new Label(composite2, SWT.NONE);
        label_1.setText("���⣺");

        textTitle = new Text(composite2, SWT.BORDER);
        final GridData gd_textTitle = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
        textTitle.setLayoutData(gd_textTitle);
        textTitle.addFocusListener(AutoSelectAll.instance);
        textTitle.addModifyListener(this);

        final Label label_2 = new Label(composite2, SWT.NONE);
        label_2.setText("������");

        textDescription = new Text(composite2, SWT.BORDER);
        final GridData gd_textDescription = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
        textDescription.setLayoutData(gd_textDescription);
        textDescription.addFocusListener(AutoSelectAll.instance);
        textDescription.addModifyListener(this);
        
        final Label label_21 = new Label(composite2, SWT.NONE);
        label_21.setText("�ο��ؿ���");

        textRefArea = new Text(composite2, SWT.BORDER);
        textRefArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
        textRefArea.addFocusListener(AutoSelectAll.instance);
        textRefArea.addModifyListener(this);
        
        checkFullAnimate = new Button(composite2, SWT.CHECK);
        checkFullAnimate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
        checkFullAnimate.setText("���ʱ�ѱ����õĶ������嵼��");
        checkFullAnimate.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                setDirty(true);
            }
        });

        final Label label_3 = new Label(composite2, SWT.NONE);
        label_3.setText("Ŀ¼��");

        textSource = new Text(composite2, SWT.BORDER);
        textSource.setEditable(false);
        final GridData gd_textSource = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        textSource.setLayoutData(gd_textSource);
        textSource.addFocusListener(AutoSelectAll.instance);
        textSource.addModifyListener(this);
        
        final Button browseButton = new Button(composite2, SWT.NONE);
        browseButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
        browseButton.setText("��Ŀ¼...");
        browseButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                File f = ((GameArea)getEditObject()).source;
                String cmd = "explorer.exe \"" + f.getAbsolutePath() + "\"";
                try {
                    Runtime.getRuntime().exec(cmd);
                } catch (Exception e1) {
                }
            }
        });

        final Composite tabContainer1 = new Composite(tabFolder, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 4;
        tabContainer1.setLayout(gridLayout);
        tabItem1.setControl(tabContainer1);

        createLeftBar(tabContainer1);

        final SashForm sashForm = new SashForm(tabContainer1, SWT.NONE);
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
        mapView = new GameMapViewer(sashForm, SWT.NONE);
        mapView.setImageViewerListener(this);
        new GameRelivePointTool(this, mapView);

        SashForm sashForm2 = new SashForm(sashForm, SWT.VERTICAL);
        sashForm2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        propertyContainer = new Composite(sashForm2, SWT.NONE);
//        Composite miniMapArea = new Composite(sashForm2, SWT.NONE);
//        createMiniMapArea(miniMapArea);
        
        questListViewer = new ListViewer(sashForm2, SWT.V_SCROLL | SWT.BORDER);
        questListViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(final DoubleClickEvent arg0) {
                StructuredSelection sel = (StructuredSelection)questListViewer.getSelection();
                if (sel.isEmpty()) {
                    return;
                }
                Object selObj = sel.getFirstElement();
                try {
                    if ("�½���������...".equals(selObj)) {
                        new NewQuestWizard(1, getEditObject().id).run();
                       
                        // ˢ���б�
                        findQuests();
                        questListViewer.refresh();
                    } else if ("�½�����...".equals(selObj)) {
                        new NewQuestWizard(0, ((GameMapNPC)questListViewer.getInput()).getGlobalID()).run();

                        // ˢ���б�
                        findQuests();
                        questListViewer.refresh();
                    } else {
                        Quest q = (Quest)selObj;
                        DataListView.tryEditObject(q);
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                    MessageDialog.openError(getSite().getShell(), "����", e1.toString());
                }
            }
        });
        questListViewer.setContentProvider(new QuestListContentProvider());
        questList = questListViewer.getList();
        final GridData gd_questList = new GridData(SWT.FILL, SWT.FILL, true, false);
        gd_questList.heightHint = 200;
        questList.setLayoutData(gd_questList);

        sashForm2.setWeights(new int[] { 3, 1 });
        
        final GridLayout gridLayout_2 = new GridLayout();
        gridLayout_2.verticalSpacing = 0;
        gridLayout_2.marginWidth = 0;
        gridLayout_2.marginHeight = 0;
        gridLayout_2.horizontalSpacing = 0;
        propertyContainer.setLayout(gridLayout_2);

        propEditor = new PropertySheetViewer(propertyContainer, SWT.BORDER, false);
        PropertySheetEntry rootEntry = new PropertySheetEntry();
        propEditor.setRootEntry(rootEntry);
        propEditor.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        ((GridData) propEditor.getControl().getLayoutData()).exclude = false;
        ((Tree) propEditor.getControl()).setHeaderVisible(true);

        npcSearch = new Text(propertyContainer, SWT.BORDER);
        npcSearch.setText("");
        final GridData gd_npcSearch = new GridData(SWT.FILL,SWT.TOP,true,false);
        npcSearch.setLayoutData(gd_npcSearch);
        gd_npcSearch.exclude = true;
        npcSearch.addModifyListener(this);
        npcSearch.setVisible(false);

        npcTemplateListViewer = new ListViewer(propertyContainer, SWT.BORDER | SWT.V_SCROLL);
        npcTemplateListViewer.setContentProvider(new NPCTemplateListProvider());
        npcTemplateListViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(final SelectionChangedEvent event) {
                updateTool();
            }
        });
        npcTemplateList = npcTemplateListViewer.getList();
        npcTemplateList.setBounds(0, 0, 127, 314);
        npcTemplateList.setVisible(false);
        final GridData gd_npcTemplateList = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd_npcTemplateList.exclude = true;
        npcTemplateList.setLayoutData(gd_npcTemplateList);
        npcTemplateListViewer.setInput(ProjectData.getActiveProject());
        
        vehicleSearch = new Text(propertyContainer, SWT.BORDER);
        vehicleSearch.setText("");
        final GridData gd_vehicleSearch = new GridData(SWT.FILL,SWT.TOP,true,false);
        vehicleSearch.setLayoutData(gd_vehicleSearch);
        gd_vehicleSearch.exclude = true;
        vehicleSearch.setVisible(false);

        
        vehicleTemplateListViewer = new ListViewer(propertyContainer,
                SWT.BORDER | SWT.V_SCROLL);
      
        vehicleTemplateList = vehicleTemplateListViewer.getList();
        vehicleTemplateList.setBounds(0, 0, 127, 324);
        vehicleTemplateList.setVisible(false);

        final GridData gd_vehicleTemplateList = new GridData(SWT.FILL,
                SWT.FILL, true, true);
        gd_vehicleTemplateList.exclude = true;
        vehicleTemplateList.setLayoutData(gd_vehicleTemplateList);

        // ѡ�������
        if (ProjectData.getActiveProject().getIndexByType(MonsterGroup.class) >= 0) {
            monsterGroupListViewer = new ListViewer(propertyContainer, SWT.BORDER | SWT.V_SCROLL);
            monsterGroupListViewer.setContentProvider(new MonsterGroupListProvider());
            monsterGroupListViewer.addSelectionChangedListener(new ISelectionChangedListener() {
                public void selectionChanged(final SelectionChangedEvent event) {
                    updateTool();
                }
            });

            monsterGroupList = monsterGroupListViewer.getList();
            monsterGroupList.setBounds(0, 0, 127, 324);
            final GridData gd_monsterGroupList = new GridData(SWT.FILL, SWT.FILL, true, true);
            gd_monsterGroupList.exclude = true;
            monsterGroupList.setLayoutData(gd_monsterGroupList);
            monsterGroupListViewer.setInput(ProjectData.getActiveProject());
        }

        propertyContainer.layout();

        sashForm.setWeights(new int[] { 3, 1 });
        new Label(tabContainer1, SWT.NONE);

        pageToolBar = new ToolBar(tabContainer1, SWT.NONE);
        pageToolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // ѡ���ͼ��ʽ
        ProjectConfig config = ProjectData.getActiveProject().config;
        
        mirrorSetButton = new Button(tabContainer1, SWT.NONE);
        mirrorSetButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                int i = getActiveMapIndex();
                GameMapInfo mi = areaInfo.maps.get(i);
                ChooseMirrorDialog dlg = new ChooseMirrorDialog(getSite().getShell(), mi.mirrorNames, mapView.getMirrorSet());
                if (dlg.open() == Dialog.OK) {
                    mapView.setMirrorSet(dlg.getMirrorSet());
                    mirrorSetButton.setText("��ʾ��" + MirrorSetCellEditor.getMirrorSetText(mi, mapView.getMirrorSet()));
                }
            }
        });
        final GridData gd_mirrorSetButton = new GridData(SWT.FILL, SWT.CENTER, true, false);
        mirrorSetButton.setLayoutData(gd_mirrorSetButton);
        mirrorSetButton.setText("");
        
        comboMapFormat = new Combo(tabContainer1, SWT.READ_ONLY);
        final GridData gd_comboMapFormat = new GridData(SWT.FILL, SWT.CENTER, false, false);
        comboMapFormat.setLayoutData(gd_comboMapFormat);
        String[] formats = new String[config.mapFormats.size()];
        for (int i = 0; i < formats.length; i++) {
            formats[i] = config.mapFormats.get(i).title;
        }
        comboMapFormat.setItems(formats);
        comboMapFormat.select(0);
        comboMapFormat.addModifyListener(new ModifyListener() {
            public void modifyText(final ModifyEvent e) {
                onFormatChanged(comboMapFormat.getSelectionIndex());
            }
        });

        // ���汾��ͼ����
        mapRefTexts = new Text[config.mapFormats.size()];
        mapRefUpdateButtons = new Button[config.mapFormats.size()];
        mapRefEditButtons = new Button[config.mapFormats.size()];
        mapRefGenPackageButtons = new Button[config.mapFormats.size()];
        for (int i = 0; i < config.mapFormats.size(); i++) {
            MapFormat format = config.mapFormats.get(i);
            final Label label_4 = new Label(composite2, SWT.NONE);
            label_4.setText(format.title + "��");

            mapRefTexts[i] = new Text(composite2, SWT.BORDER);
            mapRefTexts[i].setEditable(false);
            mapRefTexts[i].setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

            mapRefUpdateButtons[i] = new Button(composite2, SWT.NONE);
            mapRefUpdateButtons[i].setText("ѡ��...");
            mapRefUpdateButtons[i].addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(final SelectionEvent e) {
                    for (int i = 0; i < mapRefUpdateButtons.length; i++) {
                        if (mapRefUpdateButtons[i] == e.widget) {
                            importMapFile(i);
                        }
                    }
                }
            });
            
            mapRefEditButtons[i] = new Button(composite2, SWT.NONE);
            mapRefEditButtons[i].setText("�༭...");
            mapRefEditButtons[i].addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(final SelectionEvent e) {
                    for (int i = 0; i < mapRefEditButtons.length; i++) {
                        if (mapRefEditButtons[i] == e.widget) {
                            onEditMap(i);
                        }
                    }
                }
            });
            
            mapRefGenPackageButtons[i] = new Button(composite2, SWT.NONE);
            mapRefGenPackageButtons[i].setText("����pkg");
            mapRefGenPackageButtons[i].addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(final SelectionEvent e) {
                    for (int i = 0; i < mapRefEditButtons.length; i++) {
                        if (mapRefGenPackageButtons[i] == e.widget) {
                            onGeneratePackage(i);
                        }
                    }
                }
            });
        }
        
        final Button syncButton = new Button(composite2, SWT.NONE);
        syncButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
        syncButton.setText("ͬ��ͨ��������");
        syncButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                onSyncPassable();
            }
        });

        display = getSite().getShell().getDisplay();
        animateThread = new Thread(this);
        animateThread.start();

        // ���ó�ʼֵ
        boolean changed = verifyMapInfo();
        GameArea dataDef = (GameArea) editObject;
        createPageButtons();
        activePageChanged();
        textID.setText(String.valueOf(dataDef.id));
        textTitle.setText(dataDef.title);
        textDescription.setText(dataDef.description);
        textRefArea.setText(String.valueOf(dataDef.refAreaID));
        checkFullAnimate.setSelection(dataDef.packingFullAnimate);
        textSource.setText(dataDef.source.getAbsolutePath());
        for (int i = 0; i < mapRefTexts.length; i++) {
            if (dataDef.maps[i] != null) {
                mapRefTexts[i].setText(dataDef.maps[i].toString());
            }
        }
        int i = getActiveMapIndex();
        GameMapInfo mi = areaInfo.maps.get(i);
        mirrorSetButton.setText("��ʾ��" + MirrorSetCellEditor.getMirrorSetText(mi, mapView.getMirrorSet()));
        
        if (!changed) {
            setDirty(false);
        }

        setPartName(this.getEditorInput().getName() + ":" + dataDef.source.getName());
        saveStateToUndoBuffer();
    }

    private void createMiniMapArea(Composite miniMapArea) {
        miniMapArea.setLayout(new GridLayout(2,false));
        ImageViewer miniMapViewer = new ImageViewer(miniMapArea, SWT.NONE);
        String file = "E:/workspace/Macaque/devResource/huaGuoShan.png";
        Image input = new Image(getSite().getShell().getDisplay(), file);
        miniMapViewer.setInput(input);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.horizontalSpan = 2;
        miniMapViewer.setLayoutData(gd);
        
        Label label = new Label(miniMapArea, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        label.setText(input.getBounds().width+"x"+input.getBounds().height);
        
        
        Button button = new Button(miniMapArea, SWT.PUSH);
        button.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
        button.setText("ѡ��С��ͼ");
    }

    /**
     * @param tabContainer1
     */
    public void createLeftBar(final Composite tabContainer1) {
        toolBar = new ToolBar(tabContainer1, SWT.VERTICAL);
        toolBar.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

        pickupItem = new ToolItem(toolBar, SWT.RADIO);
        pickupItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                updateTool();
            }
        });
        pickupItem.setToolTipText("ѡ�񹤾�");
        pickupItem.setSelection(true);
        pickupItem.setImage(ResourceManager.getPluginImage(EditorPlugin.getDefault(), "icons/mapeditor/pickup.gif"));

        npcItem = new ToolItem(toolBar, SWT.RADIO);
        npcItem.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                updateTool();
            }
        });

        npcItem.setToolTipText("NPC����");
        npcItem.setImage(ResourceManager.getPluginImage(EditorPlugin.getDefault(), "icons/mapeditor/npc.gif"));

        exitItem = new ToolItem(toolBar, SWT.RADIO);
        exitItem.setImage(ResourceManager.getPluginImage(EditorPlugin.getDefault(), "icons/mapeditor/exit.gif"));
        exitItem.setToolTipText("���͵㹤��");
        exitItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                updateTool();
            }
        });
        
            

        patrolPathItem = new ToolItem(toolBar, SWT.RADIO);
        patrolPathItem.setToolTipText("Ѳ��·������");
        patrolPathItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                updateTool();
            }
        });
        patrolPathItem.setImage(ResourceManager.getPluginImage(EditorPlugin.getDefault(), "icons/mapeditor/path.gif"));

        vehicleItem = new ToolItem(toolBar,SWT.RADIO); 
        vehicleItem.setEnabled(false);

        relivePointItem = new ToolItem(toolBar, SWT.RADIO);
        relivePointItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                updateTool();
            }
        });
        relivePointItem.setToolTipText("����㹤��");
        relivePointItem.setImage(ResourceManager.getPluginImage(EditorPlugin.getDefault(), "icons/mapeditor/relive.gif"));
        
        /*final ToolItem newItemToolItem1 = new ToolItem(toolBar, SWT.PUSH);
        newItemToolItem1.setDisabledImage(ResourceManager.getPluginImage(EditorPlugin.getDefault(),
                "icons/mapeditor/sep.gif"));
        newItemToolItem1.setImage(ResourceManager.getPluginImage(EditorPlugin.getDefault(), "icons/mapeditor/sep.gif"));
        newItemToolItem1.setEnabled(false);*/

        groundPassItem = new ToolItem(toolBar, SWT.RADIO);
        groundPassItem.setToolTipText("����ͨ���Թ���");
        groundPassItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                updateTool();
            }
        });
        groundPassItem.setImage(ResourceManager.getPluginImage(WorkshopPlugin.getDefault(), "icons/mapeditor/passable.gif"));
        
        skyPassItem = new ToolItem(toolBar, SWT.RADIO);
        skyPassItem.setToolTipText("���ͨ���Թ���");
        skyPassItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                updateTool();
            }
        });
        skyPassItem.setImage(ResourceManager.getPluginImage(WorkshopPlugin.getDefault(), "icons/mapeditor/skyPassabel.gif"));
        
        safeAreaItem = new ToolItem(toolBar, SWT.RADIO);
        safeAreaItem.setToolTipText("��ȫ������");
        safeAreaItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                updateTool();
            }
        });
        safeAreaItem.setImage(ResourceManager.getPluginImage(WorkshopPlugin.getDefault(), "icons/mapeditor/safeArea.gif"));
        
        eyeSightItem = new ToolItem(toolBar, SWT.RADIO);
        eyeSightItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                updateTool();
            }
        });
        eyeSightItem.setToolTipText("�����ڵ�����");
        eyeSightItem.setImage(ResourceManager.getPluginImage(WorkshopPlugin.getDefault(), "icons/mapeditor/eyesight.gif"));
        
        windowItem = new ToolItem(toolBar, SWT.RADIO);
        windowItem.setToolTipText("ģ���ֻ���Ļ�鿴");
        windowItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                updateTool();
            }
        });
        windowItem.setImage(ResourceManager.getPluginImage(WorkshopPlugin.getDefault(), "icons/mapeditor/window.gif"));

        emulateItem = new ToolItem(toolBar, SWT.RADIO);
        emulateItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                updateTool();
            }
        });
        emulateItem.setImage(ResourceManager.getPluginImage(WorkshopPlugin.getDefault(), "icons/mapeditor/character.gif"));
        emulateItem.setToolTipText("ģ����������");
            
        final ToolItem newItemToolItem = new ToolItem(toolBar, SWT.PUSH);
        newItemToolItem.setDisabledImage(ResourceManager.getPluginImage(EditorPlugin.getDefault(),
                "icons/mapeditor/sep.gif"));
        newItemToolItem.setImage(ResourceManager.getPluginImage(EditorPlugin.getDefault(), "icons/mapeditor/sep.gif"));
        newItemToolItem.setEnabled(false);

        playItem = new ToolItem(toolBar, SWT.CHECK);
        playItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                playAnimate = playItem.getSelection();
            }
        });
        playItem.setToolTipText("���Ŷ���");
        playItem.setImage(ResourceManager.getPluginImage(EditorPlugin.getDefault(), "icons/mapeditor/play.gif"));
        playItem.setSelection(playAnimate);

        final ToolItem hideNPCToolItem = new ToolItem(toolBar, SWT.CHECK);
        hideNPCToolItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                mapView.setShowMapNPC(!hideNPCToolItem.getSelection());
            }
        });
        hideNPCToolItem.setToolTipText("���ص�ͼNPC");
        hideNPCToolItem.setImage(ResourceManager.getPluginImage(WorkshopPlugin.getDefault(),
                "icons/mapeditor/shownpc.gif"));

        final ToolItem newItemToolItem_1 = new ToolItem(toolBar, SWT.PUSH);
        newItemToolItem_1.setEnabled(false);
        newItemToolItem_1.setDisabledImage(ResourceManager.getPluginImage(WorkshopPlugin.getDefault(),
                "icons/mapeditor/sep.gif"));
        newItemToolItem_1.setImage(ResourceManager.getPluginImage(WorkshopPlugin.getDefault(),
                "icons/mapeditor/sep.gif"));

        final ToolItem editMapToolItem = new ToolItem(toolBar, SWT.PUSH);
        editMapToolItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                onEditMap(currentFormat.id);
            }
        });
        editMapToolItem.setToolTipText("�༭��ͼ");
        editMapToolItem.setImage(ResourceManager.getPluginImage(EditorPlugin.getDefault(),
                "icons/mapeditor/editmap.gif"));

        final ToolItem exportToolItem = new ToolItem(toolBar, SWT.PUSH);
        exportToolItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                onExport();
            }
        });
        exportToolItem.setToolTipText("������Դ");
        exportToolItem.setImage(ResourceManager.getPluginImage(WorkshopPlugin.getDefault(), "icons/disk.gif"));
        
        exportNPCItem = new ToolItem(toolBar,SWT.RADIO);
        exportNPCItem.addSelectionListener(new SelectionAdapter() {
           public void widgetSelected(final SelectionEvent e){
               exportNPCItem();
           } 
        });
        exportNPCItem.setToolTipText("����NPC���ּ�����");
        exportNPCItem.setImage(ResourceManager.getPluginImage(WorkshopPlugin.getDefault(), "icons/disk.gif"));

        /*final ToolItem pathFinderToolItem = new ToolItem(toolBar, SWT.PUSH);
        pathFinderToolItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                mapView.setTool(new PathFinderTool(GameAreaEditor.this, mapView));
            }
        });
        pathFinderToolItem.setToolTipText("����·������");
        pathFinderToolItem.setImage(ResourceManager.getPluginImage(WorkshopPlugin.getDefault(),
                "icons/mapeditor/passable.gif"));

        final ToolItem eyesightToolItem = new ToolItem(toolBar, SWT.PUSH);
        eyesightToolItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                mapView.setTool(new TestEyesightTool(GameAreaEditor.this, mapView));
            }
        });
        eyesightToolItem.setToolTipText("���������ڵ�");
        eyesightToolItem.setImage(ResourceManager.getPluginImage(WorkshopPlugin.getDefault(),
                "icons/mapeditor/eyesight.gif"));

        rangeItem = new ToolItem(toolBar, SWT.PUSH);
        rangeItem.setToolTipText("NPC��Ұ��׷����Χ��ʾ");
        rangeItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                mapView.setShowNpcRange(!mapView.getShowNpcRange());
                mapView.redraw();
            }
        });
        rangeItem.setImage(ResourceManager.getPluginImage(EditorPlugin.getDefault(), "icons/mapeditor/rangeinfo.gif"));

        playerItem = new ToolItem(toolBar, SWT.RADIO);
        playerItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                mapView.player.skyBlock = false;
                mapView.player.show = !mapView.player.show;
                if (!mapView.player.show) {
                    playerItem.setSelection(false);
                }
                updateTool();
                mapView.redraw();
            }
        });

        playerItem.setToolTipText("��ײ���");
        playerItem.setImage(ResourceManager.getPluginImage(EditorPlugin.getDefault(), "icons/mapeditor/npc.gif"));

        playerItemSky = new ToolItem(toolBar, SWT.RADIO);
        playerItemSky.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                mapView.player.skyBlock = true;
                mapView.player.show = !mapView.player.show;
                if (!mapView.player.show) {
                    playerItemSky.setSelection(false);
                }
                updateTool();
                mapView.redraw();
            }
        });

        playerItemSky.setToolTipText("��ղ���ײ���");
        playerItemSky.setImage(ResourceManager.getPluginImage(EditorPlugin.getDefault(), "icons/mapeditor/npc.gif"));*/
    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        super.init(site, input);

        // �����ͼ�ļ��͵�ͼ��Ϣ�ļ�
        try {
            GameArea dataDef = (GameArea) editObject;
            currentFormat = dataDef.owner.config.mapFormats.get(0);
            currentMapFile = new MapFile();
            File mapf = dataDef.getFile(0);
            currentMapFile.load(mapf);
            areaInfo = new GameAreaInfo(dataDef);
            if (new File(dataDef.source, "info.xml").exists()) {
                areaInfo.load();
            } else {
                areaInfo.save();
            }

            // ����ļ��ı仯
            FileWatcher.watch(mapf, this);
            
            // �ҳ���������
            findQuests();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new PartInitException("�ؿ���ʽ����", e);
        }
    }
    
    // ����ڱ༭�����޸��˵�ͼͨ�������û��߰�ȫ�����ã����޸ĺ�����ݱ��浽GameMapInfo��
    public void saveTileInfo() {
        for (int i = 0; i < areaInfo.maps.size(); i++) {
            GameMapInfo mi = areaInfo.maps.get(i);
            GameMap gm = currentMapFile.getMaps().get(i);
            if (mi.tileInfo != null && !Utils.byteArr2Equals(mi.tileInfo, gm.tileInfo)) {
                mi.tileInfo = Utils.dupByteArr2(gm.tileInfo);
            }
        }
    }

    // ������ͼ������Ϣ��ʹ��͵�ͼ�ļ�һ��
    private boolean verifyMapInfo() {
        boolean changed = false;
        GameArea dataDef = (GameArea) editObject;
        if (areaInfo.maps.size() > currentMapFile.getMaps().size()) {
            while (areaInfo.maps.size() > currentMapFile.getMaps().size()) {
                areaInfo.maps.remove(areaInfo.maps.size() - 1);
                changed = true;
            }
        } else if (areaInfo.maps.size() < currentMapFile.getMaps().size()) {
            while (areaInfo.maps.size() < currentMapFile.getMaps().size()) {
//                GameMapInfo newInfo = new GameMapInfo(dataDef);
                GameMapInfo newInfo = null;
                if(ProjectData.getActiveProject().config.gameMapInfoClass != null && ProjectData.getActiveProject().config.gameMapInfoClass.trim().length() > 0){
                    try {
                        String className = ProjectData.getActiveProject().config.gameMapInfoClass.trim();
                        ProjectConfig config = ProjectData.getActiveProject().config;
                        Class clzz = config.getProjectClassLoader().loadClass(className);
                        Constructor cons = clzz.getConstructor(dataDef.getClass());
                        newInfo = (GameMapInfo) cons.newInstance(dataDef);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    newInfo = new GameMapInfo(dataDef);
                }
                newInfo.name = "δ��������";
                areaInfo.maps.add(newInfo);
                changed = true;
            }
        }
        for (int i = 0; i < areaInfo.maps.size(); i++) {
            GameMapInfo mi = areaInfo.maps.get(i);
            GameMap gm = currentMapFile.getMaps().get(i);
            mi.id = i;
            
            // ����ͼ�ڵ���Ϣ�����GameMapInfo���У���GameMapInfo��Ϊ׼
            if (mi.tileInfo != null) {
                int oldgh = mi.tileInfo.length;
                int oldgw = mi.tileInfo[0].length;
                int newgh = gm.tileInfo.length;
                int newgw = gm.tileInfo[0].length;
                if (oldgh != newgh || oldgw != newgw) {
                    // ��С�����仯������NPCλ����Ҫ������ͨ����������Ҫ����
                    int npcxoff = ((newgw - oldgw) / 2) * gm.parent.getCellSize();
                    int npcyoff = ((newgh - oldgh) / 2) * gm.parent.getCellSize();
                    for (GameMapObject gmo : mi.objects) {
                        gmo.x += npcxoff;
                        gmo.y += npcyoff;
                    }
                    
                    // �����µ�ͨ���������飬���Ѿ���������
                    int gxoff = (newgw - oldgw) / 2;
                    int gyoff = (newgh - oldgh) / 2;
                    byte[][] newti = Utils.dupByteArr2(gm.tileInfo);
                    int cpx = Math.abs(Math.min(0, gxoff));
                    int cpw = Math.min(oldgw, newgw);
                    int cpy = Math.abs(Math.min(0, gyoff));
                    int cph = Math.min(oldgh, newgh);
                    for (int y = cpy; y < cpy + cph; y++) {
                        System.arraycopy(mi.tileInfo[y], cpx, newti[y + gyoff], cpx + gxoff, cpw);
                    }
                    mi.tileInfo = newti;
                    changed = true;
                    MessageDialog.openError(getSite().getShell(), "����", "��ע�⣺������С�����仯�����Զ������ؿ��е�NPCλ�ú�ͨ�������ݣ����飡");
                }
                // �ͻ�����ʾ�ڵ��������ݣ�����16������map�ļ��е�Ϊ׼
                new PackageUtils().copyVisibility(gm, mi.tileInfo);
                gm.tileInfo = Utils.dupByteArr2(mi.tileInfo);
            }
        }
        if (changed) {
            setDirtyWithoutUndo();
        }
        return changed;
    }

    /**
     * ���浱ǰ�༭���ݡ�
     */
    protected void saveData() throws Exception {
        GameArea dataDef = (GameArea) editObject;
        saveTileInfo();
        GameAreaCache.clearAreaInfo(dataDef.id);

        // ��ȡ���룺����ID�����⡢����
        try {
            dataDef.id = Integer.parseInt(textID.getText());
        } catch (Exception e) {
            throw new Exception("��������ȷ��ID��");
        }
        dataDef.title = textTitle.getText().trim();
        dataDef.description = textDescription.getText();
        try {
            dataDef.refAreaID = Integer.parseInt(textRefArea.getText());
        } catch (Exception e) {
            throw new Exception("��������ȷ�Ĳο��ؿ�ID��");
        }
        dataDef.packingFullAnimate = checkFullAnimate.getSelection();

        // �������Ϸ���
        DataObject dobj = ProjectData.getActiveProject().findObject(dataDef.getClass(), dataDef.id);
        if (dobj != null && dobj != getSaveTarget()) {
            throw new Exception("ID�ظ������������롣");
        }
        if (dataDef.title.length() == 0) {
            throw new Exception("��������⡣");
        }

        areaInfo.save();
        
        // ����ͻ��������ļ���ά�ְ汾��
        if (dataDef.owner.config.autoGeneratePackage) {
            for (int i = 0; i < dataDef.maps.length; i++) {
                if (dataDef.maps[i] == null) {
                    continue;
                }
                MapFile mapFile;
                if (i == currentFormat.id) {
                    mapFile = currentMapFile;
                } else {
                    mapFile = new MapFile();
                    mapFile.load(dataDef.getFile(i));
                }
                MapFormat format = dataDef.owner.config.mapFormats.get(i);
                PackageFile pkgtemp = new PackageFile();
                pkgtemp.setName(String.valueOf(dataDef.id));
                pkgtemp.setVersion(0);
                new PackageUtils().makeClientPackage(dataDef, mapFile, areaInfo, pkgtemp, (float)format.scale, 
                        dataDef.maps[i].colorMode, dataDef.maps[i].jpegOption, dataDef.maps[i].compressTextureOption);
                pkgtemp.save(new File(dataDef.source, dataDef.getID() + format.pkgName + ".pkg"));
            }
        }
    }

    /**
     * ���浱ǰ�༭״̬��һ������������Ӧ���Ǵ˷�����
     */
    protected Object saveState() {
        try {
            saveTileInfo();
            return areaInfo.saveToXML();
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * ������ǰ����ı༭״̬�ָ���ǰ�༭״̬��������Ӧ���Ǵ˷�����
     */
    protected void loadState(Object stateObj) {
        try {
            areaInfo.loadFromXML((Document) stateObj);
        } catch (Exception e) {
        }
        activePageChanged();
    }

    @Override
    public void dispose() {
        super.dispose();
        disposed = true;
        try {
            animateThread.join();
        } catch (Exception e) {
        }
        FileWatcher.unwatch(this);
    }

    // ������ͼҳǩ
    private void createPageButtons() {
        pageItems = new ArrayList<ToolItem>();
        for (int i = 0; i < currentMapFile.getMaps().size(); i++) {
            ToolItem item = new ToolItem(pageToolBar, SWT.RADIO);
            item.setImage(ResourceManager.getPluginImage(WorkshopPlugin.getDefault(), "icons/mapeditor/" + (i + 1)
                    + ".gif"));
            if (i == 0) {
                item.setSelection(true);
            }
            item.addSelectionListener(this);
            pageItems.add(item);
        }
    }

    // ˢ�µ�ͼҳǩ
    private void refreshPageButtons() {
        boolean resetSel = false;
        while (pageItems.size() > currentMapFile.getMaps().size()) {
            int index = pageItems.size() - 1;
            ToolItem ti = pageItems.get(index);
            if (ti.getSelection()) {
                resetSel = true;
            }
            ti.dispose();
            pageItems.remove(index);
        }
        while (pageItems.size() < currentMapFile.getMaps().size()) {
            int index = pageItems.size();
            ToolItem item = new ToolItem(pageToolBar, SWT.RADIO);
            item.setImage(ResourceManager.getPluginImage(WorkshopPlugin.getDefault(), "icons/mapeditor/" + (index + 1)
                    + ".gif"));
            item.addSelectionListener(this);
            pageItems.add(item);
        }
        if (resetSel && pageItems.size() > 0) {
            pageItems.get(0).setSelection(true);
        }
        pageToolBar.layout();
    }

    // ȡ�õ�ǰѡ�еĵ�ͼ������
    protected int getActiveMapIndex() {
        for (int i = 0; i < pageItems.size() && i < currentMapFile.getMaps().size(); i++) {
            if (pageItems.get(i).getSelection()) {
                return i;
            }
        }
        return -1;
    }

    // ȡ�õ�ǰѡ�еĵ�ͼ
    public GameMap getActiveMap() {
        for (int i = 0; i < pageItems.size() && i < currentMapFile.getMaps().size(); i++) {
            if (pageItems.get(i).getSelection()) {
                return currentMapFile.getMaps().get(i);
            }
        }
        return null;
    }

    // ���µ�ͼ����
    protected void updateTool() {

        boolean supportMonsterGroup = false;
        npcSearch.setVisible(false);
        if (!npcItem.getSelection()) {
            npcTemplateList.setVisible(false);
        }
        propEditor.getControl().setVisible(false);
        
        ((GridData) npcTemplateList.getLayoutData()).exclude = true;
        ((GridData)npcSearch.getLayoutData()).exclude=true;
        ((GridData) propEditor.getControl().getLayoutData()).exclude = true;

        if (ProjectData.getActiveProject().getIndexByType(MonsterGroup.class) >= 0) {
            supportMonsterGroup = true;
        }
        if (pickupItem.getSelection()) {

            // ��ʾ���Դ���
            ((GridData) propEditor.getControl().getLayoutData()).exclude = false;
            propEditor.getControl().setVisible(true);
            propertyContainer.layout();
            if (supportMonsterGroup) {
                ((GridData) monsterGroupList.getLayoutData()).exclude = true;
                monsterGroupList.setVisible(false);
            }

            // ����ʰȡ����
            mapView.setTool(new GamePickupTool(this, mapView));
        }
        else if (npcItem.getSelection()) {
            // ��ʾNPC�б�
            npcSearch.setVisible(true);
            npcTemplateList.setVisible(true);
            ((GridData) npcTemplateList.getLayoutData()).exclude = false;   
            ((GridData)npcSearch.getLayoutData()).exclude = false;

            propertyContainer.layout();

            if (supportMonsterGroup) {
                ((GridData) monsterGroupList.getLayoutData()).exclude = true;
                monsterGroupList.setVisible(false);
            }

            // ����NPC����
            StructuredSelection sel = (StructuredSelection) npcTemplateListViewer.getSelection();
            if (sel.isEmpty()) {
                mapView.setTool(null);
            }
            else {
                NPCTemplate template = (NPCTemplate) sel.getFirstElement();
                mapView.setTool(new GameMapNPCTool(this, mapView, template));
            }
        }
        else if (exitItem.getSelection()) {
            // �������ڹ���
            propEditor.getControl().setVisible(true);
            ((GridData) propEditor.getControl().getLayoutData()).exclude = false;
            mapView.setTool(new GameMapExitTool(this, mapView));
        }
        else if (patrolPathItem.getSelection()) {
            // ����Ѳ��·������
            propEditor.getControl().setVisible(true);
            ((GridData) propEditor.getControl().getLayoutData()).exclude = false;
            mapView.setTool(new GamePatrolPathTool2(this, mapView));
        }
        else if (relivePointItem.getSelection()) {
            // ��������㹤��
            propEditor.getControl().setVisible(true);
            ((GridData) propEditor.getControl().getLayoutData()).exclude = false;
            mapView.setTool(new GameRelivePointTool(this, mapView));
        } else if (windowItem.getSelection()) {
            mapView.setTool(new WindowViewTool(mapView));
        } else if (groundPassItem.getSelection()) {
            mapView.setTool(new GameTileConfigTool(mapView, TileConfigTool.CONFIG_MASK_GROUND, false, "��ɫ�����ʾ����ͨ��"));
        } else if (skyPassItem.getSelection()) {
            mapView.setTool(new TileConfigTool(mapView, TileConfigTool.CONFIG_MASK_SKY, false, "��ɫ�����ʾ����ͨ��"));
        } else if (safeAreaItem.getSelection()) {
            mapView.setTool(new TileConfigTool(mapView, TileConfigTool.CONFIG_MASK_SAFE_AREA, false, "��ɫ�����ʾ��ȫ��"));
        } else if (eyeSightItem.getSelection()) {
            mapView.setTool(new TileConfigTool(mapView, TileConfigTool.CONFIG_MASK_SIGHT, true, "��ɫ�����ʾ�����ڵ�"));
        } else if (emulateItem.getSelection()) {
            File f = new File(System.getProperty("user.home"), "imageworkshop/role.cts");
            if (!f.exists()) {
                MessageDialog.openError(getSite().getShell(), "����", "Ҫʹ�ô˹��ߣ��������û�Ŀ¼��׼��imageworkshop/role.cts�����ļ���");
                return;
            }
            try {
                mapView.setTool(new EmulateWalkTool(mapView));
                if (!playAnimate) {
                    playAnimate = true;
                    playItem.setSelection(true);
                }
            } catch (Exception e) {
                MessageDialog.openError(getSite().getShell(), "����", e.toString());
            }
        }
        setEditingObject(null);
    }
    
    /**
     * ���õ�ǰ��ʾ���ԵĶ���
     * 
     * @param obj
     *            ��ǰѡ�еĶ���null��ʾʲô��û��ѡ��
     */
    public void setEditingObject(GameMapObject obj) {
        if (obj == null) {
            if (getActiveMapIndex() != -1) {
                propEditor.setInput(new Object[] { new GameMapPropertySource(this, areaInfo.maps.get(getActiveMapIndex())) });
                Integer mid = areaInfo.maps.get(getActiveMapIndex()).getGlobalID();
                if (questListViewer.getInput() == null || !questListViewer.getInput().equals(mid)) {
                    questListViewer.setInput(areaInfo.maps.get(getActiveMapIndex()).getGlobalID());
                }
            } else {
                propEditor.setInput(new Object[0]);
                questListViewer.setInput(null);
            }
        } else if (obj instanceof GameMapExit) {
            propEditor.setInput(new Object[] { new GameMapExitPropertySource(this, (GameMapExit) obj) });
            questListViewer.setInput(null);
        } else if (obj instanceof GameMapNPC) {
            propEditor.setInput(new Object[] { new GameMapNPCPropertySource(this, (GameMapNPC) obj) });
            questListViewer.setInput(obj);
        } else if (obj instanceof GamePatrolPath) {
            propEditor.setInput(new Object[] { new GamePatrolPathPropertySource(this, (GamePatrolPath) obj) });
            questListViewer.setInput(null);
        } else if (obj instanceof GameRelivePoint) {
            propEditor.setInput(new Object[] { new GameRelivePointPropertySource(this, (GameRelivePoint) obj) });
            questListViewer.setInput(null);
        } else if (obj instanceof MultiTargetMapExit){
            propEditor.setInput(new Object[] { new MultiTargetMapExitPropertySource(this, (MultiTargetMapExit) obj) });
            questListViewer.setInput(null);
        }
    }

    public void areaSelected(Object source) {
    }

    public void frameDoubleClicked(Object source, int frame) {
    }

    public void frameSelectionChanged(Object source, int newFrame) {
    }

    public void contentChanged(Object source) {
        if (source == mapView) {
            setDirty(true);
            if (mapView.getTool() instanceof TileConfigTool) {
                // ͨ���Ըı��ˣ������ǰ��ͼ��Ϣû��tileInfo���ã�������
                int i = getActiveMapIndex();
                GameMapInfo mi = areaInfo.maps.get(i);
                GameMap gm = currentMapFile.getMaps().get(i);
                if (mi.tileInfo == null) {
                    mi.tileInfo = Utils.dupByteArr2(gm.tileInfo);
                }
            }
        }
    }

    public void widgetDefaultSelected(SelectionEvent e) {
    }

    // ҳ�水ť�����¼���
    public void widgetSelected(SelectionEvent e) {
        activePageChanged();
    }

    // ѡ�е�ͼ�ı��¼���
    private void activePageChanged() {
        GameMap map = getActiveMap();
        mapView.setInput(map, areaInfo.maps.get(getActiveMapIndex()), currentFormat);
        updateTool();
        mapView.refresh();
    }

    /**
     * �����޸ı�־������Ӱ��UNDO Buffer��
     */
    public void setDirtyWithoutUndo() {
        lockUndoBuffer = true;
        setDirty(true);
        lockUndoBuffer = false;
    }

    /**
     * �ı��޸ĺ������޸ı�־��
     */
    public void modifyText(final ModifyEvent e) {
        if (e.widget == npcSearch) {
            npcSearchText = npcSearch.getText();
            StructuredSelection sel = (StructuredSelection) npcTemplateListViewer.getSelection();
            Object selObj = sel.isEmpty() ? null : sel.getFirstElement();
            npcTemplateListViewer.refresh();
            if (selObj != null) {
                sel = new StructuredSelection(selObj);
                npcTemplateListViewer.setSelection(sel);
            }
        } else {
            setDirtyWithoutUndo();
        }
    }

    // �༭��ͼ�ļ�
    protected void onEditMap(int formatID) {
        GameArea dataDef = (GameArea) editObject;
        File mapf = dataDef.getFile(formatID);
        if (mapf == null) {
            return;
        }
        if (!mapf.exists()) {
            MessageDialog.openError(getSite().getShell(), "����", "Ŀ���ļ������ڣ�");
            return;
        }

        // ����Ƿ��Ѿ��򿪱༭��������Ѿ����򼤻�����
        IEditorPart editor = null;
        IFileStore fileStore = EFS.getLocalFileSystem().getStore(new Path((mapf.getAbsolutePath())));
        FileStoreEditorInput input = new FileStoreEditorInput(fileStore);
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        editor = page.findEditor(input);
        if (editor != null) {
            page.activate(editor);
        } else {
            try {
                page.openEditor(input, MapEditor.ID);
            } catch (Exception e) {
                MessageDialog.openError(getSite().getShell(), "����", e.toString());
            }
        }
    }
    
    // ���ɵ�����ʽ��pkg�ļ�
    protected void onGeneratePackage(int formatID) {
        try {
            GameArea dataDef = (GameArea) editObject;
            File mapf = dataDef.getFile(formatID);
            if (mapf == null) {
                return;
            }
            MapFile mapFile;
            if (formatID == currentFormat.id) {
                mapFile = currentMapFile;
            } else {
                mapFile = new MapFile();
                mapFile.load(mapf);
            }
            
            // ��������˲ο��ؿ�������Ҫ���ο��ؿ��Ķ����Ƿ���ĺͱ��ؿ���ȫ��ͬ
            if (dataDef.refAreaID != 0) {
                if (dataDef.packingFullAnimate) {
                    MapFile.includeFullAnimate = true;
                }
                java.util.List<Point> thisPoints = mapFile.getOrderedAnimateRefs();
                if (dataDef.packingFullAnimate) {
                    MapFile.includeFullAnimate = false;
                }
                GameArea refAreaDef = (GameArea)dataDef.owner.findObject(GameArea.class, dataDef.refAreaID);
                if (refAreaDef == null) {
                    throw new Exception("�ο��ؿ������ڣ�");
                }
                File refmapf = refAreaDef.getFile(formatID);
                if (refmapf == null) {
                    throw new Exception("�ο��ؿ�û��ָ�������ʽ�ĵ�ͼ��");
                }
                MapFile refMapFile = new MapFile();
                refMapFile.load(refmapf);
                if (refAreaDef.packingFullAnimate) {
                    MapFile.includeFullAnimate = true;
                }
                java.util.List<Point> refPoints = refMapFile.getOrderedAnimateRefs();
                if (refAreaDef.packingFullAnimate) {
                    MapFile.includeFullAnimate = false;
                }
                if (thisPoints.size() != refPoints.size()) {
                    throw new Exception("���ؿ��Ͳο��ؿ����õĶ�������ȫһ�£�");
                }
                for (int i = 0; i < refPoints.size(); i++) {
                    Point p1 = refPoints.get(i);
                    Point p2 = thisPoints.get(i);
                    if (p1.x != p2.x || p1.y != p2.y) {
                        throw new Exception("���ؿ��Ͳο��ؿ����õĶ�������ȫһ�£�");
                    }
                }
            }
            
            MapFormat format = dataDef.owner.config.mapFormats.get(formatID);
            PackageFile pkgtemp = new PackageFile();
            pkgtemp.setName(String.valueOf(dataDef.id));
            pkgtemp.setVersion(0);
            new PackageUtils().makeClientPackage(dataDef, mapFile, areaInfo, pkgtemp, (float)format.scale, 
                    dataDef.maps[formatID].colorMode, dataDef.maps[formatID].jpegOption, dataDef.maps[formatID].compressTextureOption);
            pkgtemp.save(new File(dataDef.source, dataDef.getID() + format.pkgName + ".pkg"));
            MessageDialog.openInformation(getSite().getShell(), "����pkg", "���ɳɹ���");
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.openError(getSite().getShell(), "����", e.toString());
        }
    }

    // ������ͼ�ļ�
    protected void onExport() {
        DirectoryDialog dlg = new DirectoryDialog(getSite().getShell());
        dlg.setText("�����ؿ���");
        dlg.setMessage("��ѡ�񵼳�Ŀ¼��");
        String newPath = dlg.open();
        if (newPath != null) {
            try {
                GameArea gameArea = (GameArea)getEditObject();
                File dir = new File(newPath, gameArea.title);
                if (dir.exists()) {
                    Utils.deleteDir(dir);
                }
                dir.mkdirs();
                for (int fid = 0; fid < gameArea.maps.length; fid++) {
                    if (gameArea.maps[fid] == null) {
                        continue;
                    }
                    MapFormat format = gameArea.owner.config.mapFormats.get(fid);
                    MapFile mf = new MapFile();
                    mf.load(gameArea.getFile(fid));
                    PackageFile pkgf = new PackageFile();
                    new PackageUtils().makeClientPackage(gameArea, mf, areaInfo, pkgf, (float)format.scale, 
                            gameArea.maps[fid].colorMode, gameArea.maps[fid].jpegOption, gameArea.maps[fid].compressTextureOption);
                    new File(dir, String.valueOf(fid)).mkdirs();
                    for (int i = 0; i < pkgf.getFileCount(); i++) {
                        PackageFileItem item = pkgf.getFile(i);
                        if (item.name.equals("npc.anp")) {
                            // npc.anp��һ���ļ��������
                            PackageFile pf = new PackageFile();
                            pf.load(new ByteArrayInputStream(item.data));
                            for (int j = 0; j < pf.getFileCount(); j++) {
                                PackageFileItem pfi = pf.getFile(j);
                                Utils.saveFileData(new File(dir, fid + "/" + pfi.name), pfi.data);
                            }
                        } else {
                            Utils.saveFileData(new File(dir, fid + "/" + item.name), item.data);
                        }
                    }
                }
                MessageDialog.openInformation(this.getSite().getShell(), "�ɹ�", "�����ɹ���");
            } catch (Exception e) {
                MessageDialog.openError(this.getSite().getShell(), "����", e.toString());
            }
        }
    }
    
    /**
     * �������ڳ���NPC���ּ�����
     */
    protected void exportNPCItem() {
        DirectoryDialog dlg = new DirectoryDialog(getSite().getShell());
        dlg.setText("�����ؿ�NPC���ּ�����");
        dlg.setMessage("��ѡ�񵼳�Ŀ¼��");
        String newPath = dlg.open();
        if(newPath != null) {
            GameArea gameArea = (GameArea)getEditObject();
            File dir = new File(newPath, gameArea.title+".txt");
            if (dir.exists()) {
                deleteFile(dir);
            }
            try {
                dir.createNewFile();
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
            Integer mid = areaInfo.maps.get(getActiveMapIndex()).getGlobalID();
            GameMapInfo mgi = GameMapInfo.findByID(ProjectData.getActiveProject(), mid);
            
            File tmpFile;
            try {
                tmpFile = File.createTempFile("desc", ".txt");
                OutputStreamWriter dos = new OutputStreamWriter(new FileOutputStream(tmpFile));
                    
                for(GameMapObject gmo:mgi.objects){
                    String str = gmo.toString() + "(" + gmo.x + "," + gmo.y + ")" + "\r\n";
//                  if(str.indexOf("���͵�") > -1){
//                      continue; 
//                  }
//                    if(str.indexOf("�����") > -1) {
//                        continue;
//                    }
                    dos.write(str);
                }
                dos.close();
                EFSUtil.copyFile(tmpFile, dir);         
                tmpFile.delete();
                MessageDialog.openInformation(this.getSite().getShell(), "�ɹ�", "�����ɹ���");
            }catch (Exception e) {
                MessageDialog.openError(this.getSite().getShell(), "����", e.toString());
            }
        }
    } 
    
    /**
     * ɾ��һ���ļ�
     * @param dir
     */
    public static void deleteFile(File dir){
        dir.delete();
    }

    /*
     * �����µĵ�ͼ�ļ���
     */
    private void importMapFile(int format) {
        if (mapFileDialog == null) {
            mapFileDialog = new FileDialog(getSite().getShell(), SWT.OPEN);
            mapFileDialog.setFilterPath(ProjectData.getActiveProject().config.getPipLibDir().getAbsolutePath());
            mapFileDialog.setFilterExtensions(new String[] { "*.map" });
            mapFileDialog.setFilterNames(new String[] { "��ͼ�ļ�(*.map)" });
        }
        String path = mapFileDialog.open();
        if (path != null) {
            try {
                GameArea dataDef = (GameArea) editObject;
                MapFile mapFile = new MapFile();
                mapFile.load(new File(path));
                
                if (currentMapFile.getMaps().size() != mapFile.getMaps().size()) {
                    MessageDialog.openError(getSite().getShell(), "����", "ָ���ļ��а����ĳ������͹ؿ���Ŀǰ�ĳ�������һ�¡�");
                    return;
                }
                
                // ѡ����ɫģʽ
                ChooseColorModeDialog dlg = new ChooseColorModeDialog(getSite().getShell(), mapFile);
                if (dlg.open() != ChooseColorModeDialog.OK) {
                    return;
                }
                
                // �����JPEGѹ��������JPEG����
                JPEGMergeOption jpegOption = null;
                if (dlg.getSelectedMode() == -3) {
                    jpegOption = JPEGMergeOptionDialog.choose(null);
                    if (jpegOption == null) {
                        return;
                    }
                }
                
                // �����ѹ�������ʽ������ѹ���������
                CompressTextureOption compTexOption = null;
                if (dlg.getSelectedMode() == -4) {
                    compTexOption = CompressTextureOptionDialogEx.choose();
                    if (compTexOption == null) {
                        return;
                    }
                }
                
                dataDef.setFile(format, new File(path), dlg.getSelectedMode(), jpegOption, compTexOption);
                mapRefTexts[format].setText(dataDef.maps[format].toString());
                ProjectData.getActiveProject().updateObject(editObject, saveTarget);
                if (format == currentFormat.id) {
                    saveTileInfo();
                    currentMapFile = mapFile;
                    verifyMapInfo();
                    refreshPageButtons();
                    activePageChanged();
                    
                    // ���¼����ļ�
                    FileWatcher.unwatch(this);
                    FileWatcher.watch(new File(path), this);
                }
                setDirty(true);
            } catch (Exception e) {
                MessageDialog.openError(getSite().getShell(), "����", e.toString());
            }
        }
    }
    
    /*
     * �޸ĵ�ǰ�鿴�ĵ�ͼ��ʽ��
     */
    private void onFormatChanged(int format) {
        if (format == currentFormat.id) {
            return;
        }
        GameArea gameArea = (GameArea)editObject;
        if (gameArea.maps[format] == null) {
            MessageDialog.openError(getSite().getShell(), "����", "��û��ָ���˰汾�ĵ�ͼ�ļ���");
            return;
        }
        MapFile mapFile = new MapFile();
        try {
            mapFile.load(gameArea.getFile(format));
        } catch (Exception e) {
            MessageDialog.openError(getSite().getShell(), "����", e.toString());
            return;
        }
        saveTileInfo();
        currentMapFile = mapFile;
        currentFormat = gameArea.owner.config.mapFormats.get(format);
        verifyMapInfo();
        refreshPageButtons();
        activePageChanged();
        
        // ���¼����ļ�
        FileWatcher.unwatch(this);
        FileWatcher.watch(gameArea.getFile(format), this);
    }
    
    /*
     * ͬ����ͼ��ͨ�������á�GameMap.tileInfo���顣
     */
    private void onSyncPassable() {
        GameArea gameArea = (GameArea)editObject;
        ArrayList<MapFormat> validFormats = new ArrayList<MapFormat>(); 
        for (int i = 0; i < gameArea.maps.length; i++) {
            File f = gameArea.getFile(i);
            if (f != null) {
                validFormats.add(gameArea.owner.config.mapFormats.get(i));
            }
        }
        GenericChooseDialog dlg = new GenericChooseDialog(getSite().getShell(), "��ʲô�汾Ϊ׼��", validFormats);
        if (dlg.open() != Dialog.OK) {
            return;
        }
        
        int standardFormat = ((MapFormat)dlg.getSelection()).id;
        File mainFile = gameArea.getFile(standardFormat);
        ArrayList<File> otherFiles = new ArrayList<File>();
        for (int i = 0; i < gameArea.maps.length; i++) {
            File f = gameArea.getFile(i);
            if (f != null && !f.equals(mainFile)) {
                otherFiles.add(f);
            }
        }
        if (otherFiles.size() == 0) {
            MessageDialog.openError(getSite().getShell(), "����", "û����Ҫͬ�����ļ���");
        }
        try {
            MapFile mainF = new MapFile();
            mainF.load(mainFile);
            for (File f : otherFiles) {
                MapFile ff = new MapFile();
                ff.load(f);
                if (ff.getMaps().size() != mainF.getMaps().size()) {
                    throw new Exception("��������һ�£�" + f);
                }
                for (int i = 0; i < ff.getMaps().size(); i++) {
                    GameMap srcMap = mainF.getMaps().get(i);
                    GameMap targetMap = ff.getMaps().get(i);
                    if (srcMap.tileInfo.length != targetMap.tileInfo.length ||
                            srcMap.tileInfo[0].length != targetMap.tileInfo[0].length) {
                        throw new Exception("������С��һ�£�" + f);
                    }
                    for (int j = 0; j < srcMap.tileInfo.length; j++) {
                        System.arraycopy(srcMap.tileInfo[j], 0, targetMap.tileInfo[j], 0, srcMap.tileInfo[j].length);
                    }
                }
                ff.save(f);
            }
            MessageDialog.openInformation(getSite().getShell(), "ͬ��", "ͬ���ɹ���");
        } catch (Exception e) {
            MessageDialog.openError(getSite().getShell(), "����", e.toString());
        }
    }

    public void fileModified(File f) {
        GameArea dataDef = (GameArea) editObject;
        File mapf = dataDef.getFile(currentFormat.id);
        if (f.equals(mapf)) {
            // ��ͼ�ļ��仯����������
            MapFile newMapFile = new MapFile();
            try {
                newMapFile.load(mapf);
                saveTileInfo();
                currentMapFile = newMapFile;
            } catch (Exception e) {
            }
            display.asyncExec(new Runnable() {
                public void run() {
                    verifyMapInfo();
                    refreshPageButtons();
                    activePageChanged();
                    setDirty(true);
                }
            });
        }
    }

    // ��������
    public void run() {
        while (!disposed) {
            if (this.playAnimate) {
                mapView.step();
                try {
                    display.asyncExec(new Runnable() {
                        public void run() {
                            try {
                                if (mapView.isDisposed() == false)
                                    mapView.redraw();
                            }
                            catch (Exception e) {
                            }
                        }
                    });
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(100);
            }
            catch (Exception e) {
            }
        }
    }
    
    
    /*
     * �ҳ��͵�ǰ�༭�Ĺؿ��й���������
     */
    protected void findQuests() {
        sceneQuests.clear();
        GameArea ga = (GameArea)getEditObject();
        java.util.List<DataObject> quests = ga.owner.getDataListByType(Quest.class);
        for (DataObject dobj : quests) {
            Quest q = (Quest)dobj;
            if (q.type == 1) {
                if (q.areaID == ga.id) {
                    sceneQuests.add(q);
                }
            } else {
                int[] npcs = q.getStartNpc();
                boolean match = false;
                for (int n : npcs) {
                    if (n != -1 && (n >> 16) == ga.id) {
                        match = true;
                        break;
                    }
                }
                if (!match) {
                    npcs = q.getEndNpc();
                    for (int n : npcs) {
                        if (n != -1 && (n >> 16) == ga.id) {
                            match = true;
                            break;
                        }
                    }
                }
                if (match) {
                    sceneQuests.add(q);
                }
            }
        }
    }
    
}
