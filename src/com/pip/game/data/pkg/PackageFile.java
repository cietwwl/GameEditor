package com.pip.game.data.pkg;

import java.io.*;
import java.util.*;

/**
 * ����ļ��ࡣ
 */
public class PackageFile {
	/** �������� */
	public String name;
	/** �汾�� */
	protected int version;
	/** �������ļ� */
	protected ArrayList<PackageFileItem> files;

	/**
	 * ����һ���յĴ���ļ��� 
	 */
	public PackageFile() {
		files = new ArrayList<PackageFileItem>();
	}
	
	/**
	 * ��ָ���ļ������롣
	 * @param f ����ļ�·��
	 * @throws IOException
	 */
	public void load(File f) throws IOException {
		FileInputStream fis = new FileInputStream(f);
		load(fis);
		fis.close();
	}
	
	@Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for(PackageFileItem item:files){
            sb.append(item.name+": ");
            if(item.refHashCode!=0){
                sb.append("hash code:"+item.refHashCode+"\n");
            }else if(item.name.equals("npc.anp")){
                sb.append("\n");
                DataInputStream bai = new DataInputStream(new ByteArrayInputStream(item.data));
                for(int i=0; i<item.data.length/4; i++){
                    try {
                        sb.append("hash code:"+bai.readInt());
                    }
                    catch (IOException e) {
                        sb.append(e);
                    }
                }
                sb.append("\n");
            }else{
                sb.append("data length:"+item.data.length+"\n");
            }
        }
	    return sb.toString();
    }

    /**
	 * ��InputStream�����롣
	 * @param is
	 * @throws IOException
	 */
	public void load(InputStream is) throws IOException {
		files.clear();
		DataInputStream dis = new DataInputStream(is);
		name = dis.readUTF();
		version = dis.readInt();
		int fileCount = dis.readShort();
		String[] fileNames = new String[fileCount];
		for (int i = 0; i < fileCount; i++) {
            fileNames[i] = dis.readUTF();
			byte libFlag = dis.readByte();
			PackageFileItem item = new PackageFileItem();
			item.name = fileNames[i];
			if (libFlag == 1) {
			    item.refHashCode = dis.readInt();
			} else {
                int length = dis.readInt();
//    			if (length > 10000000) {
//    				throw new IOException("too large file");
//    			}
    			byte[] fileData = new byte[length];
    			dis.readFully(fileData);
    			item.data = fileData;
			}
			files.add(item);
        }
	}
	
	/**
	 * ���浽�ļ���
	 * @param f
	 * @throws IOException
	 */
	public void save(File f) throws IOException {
		FileOutputStream fos = new FileOutputStream(f);
		save(fos);
		fos.close();
		System.out.println("save pkg at: "+f.getAbsolutePath());
	}
	
	/**
	 * ���浽OutputStream��
	 * @param os
	 * @throws IOException
	 */
	public void save(OutputStream os) throws IOException {
		DataOutputStream dos = new DataOutputStream(os);
		if (name == null) {
		    name = "null";
		}
		dos.writeUTF(name);
		dos.writeInt(version);
		int fileCount = files.size();
		dos.writeShort(fileCount);
		for (int i = 0; i < fileCount; i++) {
			dos.writeUTF(files.get(i).name);
			if (files.get(i).refHashCode != 0) {
			    dos.writeByte(1);  // lib mode flag
			    dos.writeInt(files.get(i).refHashCode);
			} else {
			    dos.writeByte(0);
			    byte[] fileData = files.get(i).data;
    			dos.writeInt(fileData.length);
    			dos.write(fileData);
			}
		}
	}

	/**
	 * �оٴ���ļ��а������ļ���
	 */
	public PackageFileItem[] getFiles() {
		PackageFileItem[] ret = new PackageFileItem[files.size()];
		files.toArray(ret);
		return ret;
	}

	/**
	 * ȡ�ô���ļ��а������ļ�����
	 */
	public int getFileCount() {
		return files.size();
	}

	/**
	 * �õ������ļ���С�ĺ͡�
	 */
	public int getTotalSize() {
	    int ret = 0;
	    for (PackageFileItem pfi : files) {
	        ret += pfi.data.length;
	    }
	    return ret;
	}
	
	/**
	 * �����±�ȡ��һ���ļ���
	 */
	public PackageFileItem getFile(int index) {
		return files.get(index);
	}
	
	/**
	 * ����һ���ļ���
	 */
	public byte[] findFile(String name) {
		for (PackageFileItem pki : files) {
			if (pki.name.equals(name)) {
				return pki.data;
			}
		}
		return null;
	}
	
	/**
	 * �Ѵ���ļ��е�һ���ļ�������
	 * @param index �ļ��±�
	 * @param newname ���ļ���
	 */
	public void renameFile(int index, String newname) {
		files.get(index).name = newname;
	}
	
	/**
	 * �����ļ������һ���ļ���
	 * @param name �ļ���
	 * @param data �ļ�����
	 */
	public void addFile(String name, byte[] data) {
		for (int i = 0; i < files.size(); i++) {
			if (files.get(i).name.equals(name)) {
				files.get(i).data = data;
				return;
			}
		}
		PackageFileItem newItem = new PackageFileItem();
		newItem.name = name;
		newItem.data = data;
		files.add(newItem);
	}
	
	/**
	 * �����ļ������һ�����ļ����á�
	 * @param name �ļ���
	 * @param refHashCode ����hash����
	 */
	public void addFile(String name, int refHashCode) {
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i).name.equals(name)) {
                files.get(i).refHashCode = refHashCode;
                return;
            }
        }
        PackageFileItem newItem = new PackageFileItem();
        newItem.name = name;
        newItem.refHashCode = refHashCode;
        files.add(newItem);
    }
	
	/**
	 * ɾ��һ���ļ���
	 * @param index �ļ��±�
	 */
	public void removeFile(int index) {
		files.remove(index);
	}

	/**
	 * ȡ�ô���ļ�����
	 */
	public String getName() {
		return name;
	}

	/**
	 * ���ô���ļ�����
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * ȡ�ð汾�š�
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * ���ð汾�š�
	 */
	public void setVersion(int version) {
		this.version = version;
	}
	
	/**
	 * ��¡����ļ���
	 */
	public PackageFile clone() {
	    PackageFile ret = new PackageFile();
	    ret.name = name;
	    ret.version = version;
	    for (PackageFileItem item : files) {
	        PackageFileItem newItem = new PackageFileItem();
	        newItem.name = item.name;
	        newItem.data = new byte[item.data.length];
	        System.arraycopy(item.data, 0, newItem.data, 0, item.data.length);
	        ret.files.add(newItem);
	    }
	    return ret;
	}
}
