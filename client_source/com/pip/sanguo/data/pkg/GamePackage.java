package com.pip.sanguo.data.pkg;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Enumeration;

import com.pip.common.GZIP;
import com.pip.image.ImageSet;
import com.pip.image.PipAnimateSet;

/**
 * �����ؿ���Ϸ���ࡣ�ؿ���Ϸ����һ������ļ�������Ӧ�ð������¼����ļ����ؿ���Ϣ0.stg����ͼ�ļ�*.m����ȷ��ͼtile.pip��tile.ts��
 * �����ļ�l*.ldf��l*.ts����ͼNPC�����ļ�npc.anp����ͼNPC��ײ�����ļ�npc.col���Լ������͹ؿ��йص��ļ���
 */
public class GamePackage extends PackageFile{
    /** ��ȷ��ͼ��� */
    public int tileWidth;
    /** ��ȷ��ͼ�߶� */
    public int tileHeight;
    /** ģ����ͼ��� */
    public int blurTileWidth;
    /** ģ����ͼ�߶� */
    public int blurTileHeight;
    /** �ؿ�ID */
    public int areaID;
    /** �ؿ����� */
    public String title;
    /** �������� */
    public int landFromCount;

    /**
     * ����һ����Ϸ���ļ���
     * @param data
     * @throws IOException
     */
    public GamePackage(byte[] data) throws Exception{
        super(data);
        //. ��ȡ�ؿ�������Ϣ
        ByteArrayInputStream bis = new ByteArrayInputStream(getFile("/0.stg"));
        DataInputStream dis = new DataInputStream(bis);
        tileWidth = dis.readShort();
        tileHeight = dis.readShort();
        blurTileWidth = dis.readShort();
        blurTileHeight = dis.readShort();
        areaID = dis.readShort() & 0xFFFF;
        title = dis.readUTF();
        landFromCount = 0;
        Enumeration emu = nameIndexMap.keys();

        while(emu.hasMoreElements()){
            String fileName = (String)emu.nextElement();

            if(fileName.endsWith(".ldf")){
                landFromCount++;
            }
        }
    }

    /**
     * ����ؿ���ȷ��ͼͼƬ�ļ���
     * @return
     * @throws Exception
     */
    public ImageSet loadTileImage() throws Exception{
        return new ImageSet(getFile("/tile.pip"));
    }
    /**
     * ����ؿ���ȷ��ͼ�����ļ���
     * @return ����2������Ϊ��ͼ������byte���飬��һ�������м�¼ÿһ��ͼ��Ӧ��ͼƬ֡���������ڶ���������
     *     ֡������Ϣ���Ӹ�λ����λ����3������Ϣ��2λ��תֵ��5λ����ͼ��ɫ������1λͨ����־��1��ʾ����ͨ����
     * @throws Exception
     */
    public byte[][] getTileInfo() throws Exception{
        return parseTileInfo(getFile("/tile.ts"));
    }

    /**
     * ������ͼ��Ϣ�����ļ���
     * @param data
     * @return ����2������Ϊ��ͼ������byte���飬��һ�������м�¼ÿһ��ͼ��Ӧ��ͼƬ֡���������ڶ���������
     *     ֡������Ϣ���Ӹ�λ����λ����3������Ϣ��2λ��תֵ��5λ����ͼ��ɫ������1λͨ����־��1��ʾ����ͨ����
     * @throws Exception
     */
    private byte[][] parseTileInfo(byte[] data) throws Exception{
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bis);
        int tcount = dis.readShort();
        byte[][] ret = new byte[2][tcount];
        for(int i = 0; i < tcount; i++){
            ret[0][i] = dis.readByte();
            ret[1][i] = dis.readByte();
        }
        return ret;
    }
    /**
     * �������е��ε�ͼƬ�ļ���
     * @return 
     * @throws Exception
     */
    public LandformImage[] loadAllLandformImage() throws Exception{
        LandformImage[] result = new LandformImage[landFromCount];

        for(int i = 0; i < result.length; i++){
            result[i] = loadLandformImage(i);
        }

        return result;
    }

    /**
     * �������е����ļ�����ͼ�����ļ�
     * @return
     * @throws Exception
     */
    public byte[][][] loadAllLandformTileInfof() throws Exception{
        byte[][][] result = new byte[landFromCount][][];

        for(int i = 0; i < result.length; i++){
            result[i] = loadLandformTileInfo(i);
        }

        return result;
    }

    /**
     * ����һ�����ε�ͼƬ�ļ���
     * @param lid
     * @return 
     * @throws Exception
     */
    public LandformImage loadLandformImage(int lid) throws Exception{
        return new LandformImage(getFile("/l" + lid + ".ldf"));
    }
    /**
     * ����һ�����ε���ͼ�����ļ���
     * @param lid
     * @return ����2������Ϊ��ͼ������byte���飬��һ�������м�¼ÿһ��ͼ��Ӧ��ͼƬ֡���������ڶ���������
     *     ֡������Ϣ���Ӹ�λ����λ����3������Ϣ��2λ��תֵ(δ��)��5λ����ͼ��ɫ������1λͨ����־��1��ʾ����ͨ����
     */
    public byte[][] loadLandformTileInfo(int lid) throws Exception{
        return parseTileInfo(getFile("/l" + lid + ".ts"));
    }
    /**
     * �����ͼNPC���������ļ���
     * @return
     */
    public PipAnimateSet loadNPCAnimates() throws Exception{
        return parseAnimatesPackage(getFile("/npc.anp"));
    }
    /**
     * ����һ������Ķ����ļ�����������ļ���һ��PackageFile��ʽ���ļ������е�һ���ļ�ΪCTN������������ÿһ������
     * ���õ���PIPͼƬ�ļ���
     * @param fileData
     * @return
     */
    public PipAnimateSet parseAnimatesPackage(byte[] fileData) throws Exception{
        PackageFile pkg = new PackageFile(fileData);
        int fcount = pkg.fileContents.length;
        ImageSet[] imgs = new ImageSet[fcount - 1];
        for(int i = 1; i < fcount; i++){
            imgs[i - 1] = new ImageSet(pkg.fileContents[i]);
        }
        return new PipAnimateSet(imgs, pkg.fileContents[0]);
    }
    /**
     * �����ͼNPC���赲�������á�
     * @return ����4�����Ⱥ͵�ͼNPC����������ͬ��short���飬��һ���������м�¼ÿ��NPC��ײ�����Xֵ���ڶ�����¼Yֵ��������
     *     ��¼��ȡ����ĸ���¼�߶ȣ��������ݶ�����������յ�ͼNPC�ķ���λ�õġ�
     * @throws Exception
     */
    public short[][][] loadNPCCollision() throws Exception{
        ByteArrayInputStream bis = new ByteArrayInputStream(getFile("/npc.col"));
        DataInputStream dis = new DataInputStream(bis);
        int count = dis.readShort();
        short[][][] ret = new short[4][count][];
        for(int i = 0; i < count; i++){
        	int subCount = dis.readUnsignedByte();
        	
        	ret[0][i] = new short[subCount];
            ret[1][i] = new short[subCount];
            ret[2][i] = new short[subCount];
            ret[3][i] = new short[subCount];
            
        	for(int j = 0; j < subCount; j++){
	            ret[0][i][j] = dis.readShort();
	            ret[1][i][j] = dis.readShort();
	            ret[2][i][j] = (short)(dis.readByte() & 0xFF);
	            ret[3][i][j] = (short)(dis.readByte() & 0xFF);
        	}
        }
        return ret;
    }
    /**
     * ����һ����ͼ�ļ���
     * @param mapID ��ͼID
     * @return
     * @throws Exception
     */
    public GameMap loadMap(int mapID) throws Exception{
        ByteArrayInputStream bis = new ByteArrayInputStream(GZIP.inflate(getFile("/" + mapID + ".m")));
        DataInputStream dis = new DataInputStream(bis);
        GameMap ret = new GameMap(this);
        ret.load(dis);
        ret.id |= areaID << 4;
        return ret;
    }
}
