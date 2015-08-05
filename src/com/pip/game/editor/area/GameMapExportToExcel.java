package com.pip.game.editor.area;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.pip.game.data.DataObject;
import com.pip.game.data.GameArea;
import com.pip.game.data.GameAreaInfo;
import com.pip.game.data.ProjectData;
import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.map.GameMapNPC;
import com.pip.game.data.map.GameMapObject;

public class GameMapExportToExcel {
    private static final String[] MAPINFO_TABLE_TITLE = { "�ؿ�ID", "�ؿ���С", "��ͼID", "��ͼ����", "������", "NPC����", "NPC�ļ���С", "NPC�ڴ��С", "NPC����"};

    public GameMapExportToExcel() {
    }

    public void saveGameMapToExcel(String fileName) {
        try {
            WritableWorkbook wwb = Workbook.createWorkbook(new File(fileName));
            ProjectData proj = ProjectData.getActiveProject();
            List<DataObject> areas = null;
            try {
                areas = proj.getDataListByType(GameArea.class);
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }

            if (areas.size() != 0) {
                WritableSheet ws = wwb.createSheet("��ͼ��Ϣ", 0);
                WritableCellFormat wcf = new WritableCellFormat();
                wcf.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);

                for (int col = 0; col < MAPINFO_TABLE_TITLE.length; col++) {
                    Label label = new Label(col, 0, MAPINFO_TABLE_TITLE[col], wcf);
                    ws.addCell(label);
                }

                int row = 1;

                for (int i = 0; i < areas.size(); i++) {
                    GameArea ga = (GameArea) areas.get(i);
                    GameAreaInfo areaInfo = new GameAreaInfo(ga);
                    try {
                        areaInfo.load();
                    }
                    catch (Exception e2) {
                        e2.printStackTrace();
                    }

                    int col = 0;
                    int pkgRow = row;

                    // �ؿ�ID
                    ws.addCell(new Label(col++, row, String.valueOf(ga.getID()), wcf));
                    // �ؿ���С
                    ws.addCell(new Label(col++, row, String.valueOf(getPackageSize(ga)), wcf));

                    List<GameMapInfo> gmis = areaInfo.maps;

                    for (int j = 0; j < gmis.size(); j++) {
                        try {
                            GameMapInfo gmi = gmis.get(j);

                            List<String> mapInfo = new ArrayList<String>();

                            // ��ͼId
                            mapInfo.add(String.valueOf(gmi.getGlobalID()));

                            // ��ͼ����
                            mapInfo.add(gmi.name);

                            // ��������
                            mapInfo.add(String.valueOf(gmi.maxPlayer));

                            int npcCount = 0;
                            int npcFileSize = 0;
                            int npcMemorySize = 0;
                            
                            HashSet<Integer> npcTemplateSet = new HashSet<Integer>();
                            
                            for(GameMapObject gmo : gmi.objects){
                                if(gmo instanceof GameMapNPC){
                                    GameMapNPC gmn = (GameMapNPC)gmo;
                                    
                                    npcCount++;
                                    
                                    if(!npcTemplateSet.contains(gmn.template.id)){
                                        npcFileSize += gmn.template.image.getAnimateFileSize(0);
                                        npcMemorySize += gmn.template.image.getAnimateMemorySize(0);
                                        npcTemplateSet.add(gmn.template.id);
                                    }
                                }
                            }
                            
                            // NPC����
                            mapInfo.add(String.valueOf(npcTemplateSet.size()));
                            
                            // NPC�ļ���С
                            mapInfo.add(String.valueOf(npcFileSize));
                            
                            // NPC�ڴ��С
                            mapInfo.add(String.valueOf(npcMemorySize));
                            
                            // NPC����
                            mapInfo.add(String.valueOf(npcCount));
                            
                            String[] mapInfoLable = new String[mapInfo.size()];
                            mapInfo.toArray(mapInfoLable);

                            for (int k = 0; k < mapInfoLable.length; k++) {
                                Label label = new Label(col + k, row, mapInfoLable[k], wcf);
                                ws.addCell(label);
                            }
                            row++;
                        }
                        catch (Exception e) {
                            e.toString();
                        }
                    }

                    if (row > pkgRow + 1) {
                        ws.mergeCells(0, pkgRow, 0, row - 1);
                        ws.mergeCells(1, pkgRow, 1, row - 1);
                    }

                }

                wwb.write();
                wwb.close();
            }
        }
        catch (RowsExceededException e) {
            e.printStackTrace();
        }
        catch (WriteException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long getPackageSize(GameArea ga) {
        File file = new File(ga.source + "/" + ga.id + ".pkg");
        return file.length();
    }
}
