package com.pip.sanguo.data.pkg;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Hashtable;


/**
 * ����ļ��ࡣ
 */
public class PackageFile{
    /** �������� */
    public String name;
    /** �汾�� */
    public int version;
    /** �������ļ��� */
    public String[] fileNames;
    /** �������ļ����� */
    public byte[][] fileContents;
    /** �ļ������ļ������Ķ��ձ� */
    protected Hashtable nameIndexMap;

    /**
     * ����һ���յ��ļ���
     */
    public PackageFile(){
        name = "";
        fileNames = new String[0];
        fileContents = new byte[0][];
        nameIndexMap = new Hashtable();
    }
    /**
     * ����һ������ļ���
     * @param data
     * @throws IOException
     */
    public PackageFile(byte[] data) throws IOException{
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
        name = in.readUTF(); // name
        version = in.readInt(); // version
        int fileCount = in.readShort();
        fileNames = new String[fileCount];
        nameIndexMap = new Hashtable();
        for(int i = 0; i < fileCount; i++){
            fileNames[i] = in.readUTF();
            nameIndexMap.put("/" + fileNames[i], new Integer(i));
        }
        fileContents = new byte[fileCount][];
        for(int i = 0; i < fileCount; i++){
            int length = in.readInt();
            byte[] fileData = new byte[length];
            in.readFully(fileData);
            fileContents[i] = fileData;
        }
    }

    /**
     * �������ֲ����ļ���
     * @param name
     * @return
     */
    public byte[] getFile(String name){
        Integer index = (Integer)nameIndexMap.get(name);
        if(index == null){
            return null;
        }else{
            return fileContents[index.intValue()];
        }
    }

    /**
     * �ͷ�һ���ļ��Ŀռ䡣
     * @param name
     */
    public void releaseFile(String name){
        Integer index = (Integer)nameIndexMap.get(name);
        if(index != null){
            fileContents[index.intValue()] = null;
        }
    }
    /**
     * ���һ���ļ���
     * @param name
     * @param data
     */
    public void addFile(String name, byte[] data){
        int fileCount = fileNames.length;
        String[] newNames = new String[fileCount + 1];
        System.arraycopy(fileNames, 0, newNames, 0, fileCount);
        newNames[fileCount] = name;
        fileNames = newNames;
        nameIndexMap.put(name, new Integer(fileCount));
        byte[][] newContents = new byte[fileCount + 1][];
        System.arraycopy(fileContents, 0, newContents, 0, fileCount);
        newContents[fileCount] = data;
        fileContents = newContents;
    }
}
