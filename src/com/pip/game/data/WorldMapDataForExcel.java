package com.pip.game.data;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.Sheet;

public class WorldMapDataForExcel {
    /**�����ͼID*/
    private final static byte WORLD_ID     =        0;
    /**��ͼID*/
    private final static byte MAP_ID       =        1;
    /**��ӪID*/
    private final static byte CAMP_ID      =        2;
    /**��ͼ����*/
    private final static byte MAP_NAME     =        3;
    /**��Ӫ����*/
    private final static byte CAMP_NAME    =        4;
    /**�ʺϵȼ�*/
    private final static byte LEVEL        =        5;
    /**��ͼ����*/
    private final static byte XX           =        6;
    /**��ͼ����*/
    private final static byte YY           =        7;
    /**ͨ�з���UP*/
    private final static byte UP           =        8;
    /**ͨ�з���DOWN*/
    private final static byte DOWN         =        9;
    /**ͨ�з���LEFT*/
    private final static byte LEFT         =        10;
    /**ͨ�з���RIGHT*/
    private final static byte RIGHT        =        11;
    /**�����ͼ��ʶ*/
    private final static byte SHARE_ID     =        12;
    /**mapID������*/
    private final static byte MAPID_STEP   = 2;
    /**worldID������*/
    private final static byte WORLDID_STEP = 12;
    
    public static void save(String filePath){
        try{
            Workbook workbook = Workbook.getWorkbook(new File(filePath));
            System.out.println("���빤����");
            System.out.println("������������" + workbook.getNumberOfSheets());
            Sheet sheet = workbook.getSheet(0);
            System.out.println("����������" + sheet.getName());
            int rows = sheet.getRows();
            int cols = sheet.getColumns();
            System.out.println("������������" + rows);
            System.out.println("������������" + cols);
            
            String[][] cells = new String[rows - 1][cols];
            for(int i=1; i<rows; i++){
                for(int j=0; j<cols; j++){
                    cells[i - 1][j] = sheet.getCell(j, i).getContents();
                    System.out.print((sheet.getCell(j, i)).getContents() + " ");
                }
                System.out.println();
            }
            System.out.println("���빤��������");
            
            Vector<String> keyMapID = new Vector<String>();
            Vector<String> keyMapID_curShare = new Vector<String>();
            for(int i=0; i<cells.length; i++){
                if(cells[i][SHARE_ID].equals("false")){
                    keyMapID.addElement(cells[i][MAP_ID]);
                    keyMapID.addElement(cells[i][WORLD_ID]);
                }
                else{
                    keyMapID_curShare.addElement(cells[i][MAP_ID]);
                    keyMapID_curShare.addElement(cells[i][WORLD_ID]);
                }
            }
            Vector<String> keyMapID_share = new Vector<String>();
            for(int i=0; i<keyMapID_curShare.size(); i+=MAPID_STEP){
                if(keyMapID_share.size()==0){
                    keyMapID_share.addElement(keyMapID_curShare.elementAt(i));
                    keyMapID_share.addElement(setShareID(keyMapID_curShare.elementAt(i + 1)));
                }
                else{
                    boolean _canAdd = true;
                    for(int j=0; j<keyMapID_share.size(); j+=MAPID_STEP){
                        if(keyMapID_curShare.elementAt(i).equals(keyMapID_share.elementAt(j))){
                            _canAdd = false;
                            break;
                        }
                    }
                    if(_canAdd){
                        keyMapID_share.addElement(keyMapID_curShare.elementAt(i));
                        keyMapID_share.addElement(setShareID(keyMapID_curShare.elementAt(i + 1)));
                    }
                }
            }
            System.out.println("��Ҫ��������(2��)��" + keyMapID_share.size());
            for(int i=0; i<keyMapID_share.size(); i+=MAPID_STEP){
                keyMapID.addElement(keyMapID_share.elementAt(i));
                keyMapID.addElement(keyMapID_share.elementAt(i + 1));
            }
            System.out.println("mapID��������(2��)��" + keyMapID.size());
            System.out.println("mapID�����ݱ������");
            
            Vector<String> keyWorldID = new Vector<String>();
            for(int i=0; i<cells.length; i++){
                keyWorldID.addElement(cells[i][WORLD_ID]);
                keyWorldID.addElement(cells[i][MAP_ID]);
                keyWorldID.addElement(cells[i][CAMP_ID]);
                keyWorldID.addElement(cells[i][MAP_NAME]);
                keyWorldID.addElement(cells[i][CAMP_NAME]);
                keyWorldID.addElement(cells[i][LEVEL]);
                keyWorldID.addElement(cells[i][XX]);
                keyWorldID.addElement(cells[i][YY]);
                keyWorldID.addElement(cells[i][UP]);
                keyWorldID.addElement(cells[i][DOWN]);
                keyWorldID.addElement(cells[i][LEFT]);
                keyWorldID.addElement(cells[i][RIGHT]);
            }
            System.out.println("worldID��������(12��)��" + keyWorldID.size());
            System.out.println("worldID�����ݱ������");

            FileOutputStream fos = new FileOutputStream(new File(new File(filePath).getParent(), "worldMapInfo.dat"));
            DataOutputStream dos = new DataOutputStream(fos);
            
            System.out.println("��¼mapID������");
            dos.writeInt(keyMapID.size());
            dos.writeByte(MAPID_STEP);
            for(int i=0; i<keyMapID.size(); i+=MAPID_STEP){
                dos.writeShort(Short.parseShort(keyMapID.elementAt(i)));//mapID
                dos.writeUTF(keyMapID.elementAt(i + 1));//worldID
            }
            
            System.out.println("��¼worldID������");
            dos.writeInt(keyWorldID.size());
            dos.writeByte(WORLDID_STEP);
            for(int i=0; i<keyWorldID.size(); i+=WORLDID_STEP){
                dos.writeUTF(keyWorldID.elementAt(i + WORLD_ID));
                dos.writeShort(Short.parseShort(keyWorldID.elementAt(i + MAP_ID)));
                dos.writeByte(Byte.parseByte(keyWorldID.elementAt(i + CAMP_ID)));
                dos.writeUTF(keyWorldID.elementAt(i + MAP_NAME));
                dos.writeUTF(keyWorldID.elementAt(i + CAMP_NAME));
                dos.writeUTF(keyWorldID.elementAt(i + LEVEL));
                dos.writeInt(Integer.parseInt(keyWorldID.elementAt(i + XX)));
                dos.writeInt(Integer.parseInt(keyWorldID.elementAt(i + YY)));
                dos.writeUTF(keyWorldID.elementAt(i + UP));
                dos.writeUTF(keyWorldID.elementAt(i + DOWN));
                dos.writeUTF(keyWorldID.elementAt(i + LEFT));
                dos.writeUTF(keyWorldID.elementAt(i + RIGHT));
            }
            
            dos.close();
            fos.close();
            workbook.close();
        }
        catch (BiffException e) {
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
//    /**��*,*,*��ʽ��worldIDת��short*/
//    private static Short setWorldID(String worldID){
//        String[] _sID = worldID.split(",");
//        return (short) (Short.parseShort(_sID[0]) << 11 | Short.parseShort(_sID[1]) << 4 | Short.parseShort(_sID[2]));
//    }
    /**����Ҫ�����worldID���4,*,*�ĸ�ʽ*/
    private static String setShareID(String worldID){
        String[] _curID = worldID.split(",");
        _curID[0] = "4";
        StringBuffer _sbID = new StringBuffer();
        for(int i=0; i<_curID.length; i++){
            _sbID.append(_curID[i] + ",");
        }
        _sbID.deleteCharAt(_sbID.length() - 1);
        System.out.println("ת��ǰ���ݣ�" + worldID + "ת�������ݣ�" + _sbID.toString());
        return _sbID.toString();
    }
}
