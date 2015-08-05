package com.pip.sanguo.data.pkg;


import java.io.DataInputStream;


/**
 * ��Ϸ��ͼ�ࡣ
 */
public class GameMap{
    /** �����ؿ����� */
    public GamePackage owner;

    /** ��ͼȫ��ID��*/
    public int id;
    /** ��ͼ���ơ�*/
    public String name;
    /** ��ͼ��ȡ�*/
    public int width;
    /** ��ͼ�߶ȡ� */
    public int height;

    /** ����ID���� */
    public short[] exitIDs;
    /** ����Xλ������ */
    public short[] exitX;
    /** ����Yλ������ */
    public short[] exitY;
    /** ����Ŀ���ͼID���� */
    public int[] exitTargetMaps;
    /** ����Ŀ���ͼ�������� */
    public String[] exitTargetMapNames;
    /** ����Ŀ��Xλ������ */
    public short[] exitTargetX;
    /** ����Ŀ��Yλ������ */
    public short[] exitTargetY;

    /** ���������ͣ�0 - ��ȷ��1 - ģ�� */
    public int backgroundType;
    /** �����������ݣ���ȷ��ģ����ͼ���ô�����洢 */
    public byte[][] mapData;
    /** ģ����ͼ����������� */
    public int randomSeed;
    /** ģ����ͼ�ı������Σ�-1��ʾ�� */
    public int baseLandform;

    /* �����Ǹ�NPC���NPC��Ϣ��ÿһ����3��short������ɣ���һ���������NPC�Ķ���ID���ڶ�������X������������Y */

    /** �����NPC�б���ЩNPC�������֮ǰ���� */
    public short[][] groundNPCs;
    /** �����NPC�б���ЩNPC������һ����ƣ���Ҫ�����ڵ���ϵ */
    public short[][] roleNPCs;
    /** ��ղ�NPC�б���ЩNPC�������֮����� */
    public short[][] skyNPCs;

    public GameMap(GamePackage pkg){
        owner = pkg;
    }

    /**
     * ���ļ������롣
     * @param dis
     * @throws Exception
     */
    public void load(DataInputStream dis) throws Exception{
        id = dis.readByte();
        name = dis.readUTF();
        width = dis.readShort();
        height = dis.readShort();

        // ��ȡ������Ϣ
        int exitCount = dis.readByte() & 0xFF;
        exitIDs = new short[exitCount];
        exitX = new short[exitCount];
        exitY = new short[exitCount];
        exitTargetMaps = new int[exitCount];
        exitTargetMapNames = new String[exitCount];
        exitTargetX = new short[exitCount];
        exitTargetY = new short[exitCount];
        for(int i = 0; i < exitCount; i++){
            exitIDs[i] = dis.readShort();
            exitX[i] = dis.readShort();
            exitY[i] = dis.readShort();
            exitTargetMaps[i] = dis.readInt();
            exitTargetMapNames[i] = dis.readUTF();
            exitTargetX[i] = dis.readShort();
            exitTargetY[i] = dis.readShort();
        }

        // ��ȡ��ͼ�㣬������4�㣺�����㡢����㡢��������ղ�
        int layerCount = dis.readByte();
        if(layerCount != 4){
            throw new IllegalArgumentException();
        }

        // ��ȡ������
        byte layerType = dis.readByte();
        if(layerType == 0){
            backgroundType = 0;
            int cw = width / owner.tileWidth;
            int ch = height / owner.tileHeight;
            mapData = new byte[ch][cw];
            for(int i = 0; i < ch; i++){
                dis.readFully(mapData[i]);
            }
        }else if(layerType == 1){
            backgroundType = 1;
            randomSeed = dis.readInt();
            baseLandform = dis.readByte();
            int cw = width / owner.blurTileWidth;
            int ch = height / owner.blurTileHeight;
            mapData = new byte[ch][cw];
            for(int i = 0; i < ch; i++){
                dis.readFully(mapData[i]);
            }
        }else{
            throw new IllegalArgumentException();
        }

        // ��ȡNPC��
        for(int i = 0; i < 3; i++){
            layerType = dis.readByte();
            dis.skipBytes(1); //�Ƿ�Ϊ����㣬�����̶�Ϊ010
            if(layerType != 2){
                throw new IllegalArgumentException();
            }
            short[][] npcs = loadNPCList(dis);
            if(i == 0){
                groundNPCs = npcs;
            }else if(i == 1){
                roleNPCs = npcs;
            }else{
                skyNPCs = npcs;
            }
        }
    }

    // ��ȡһ��NPC�������
    private short[][] loadNPCList(DataInputStream dis) throws Exception{
        int count = dis.readShort();
        short[][] ret = new short[4][count];
        for(int i = 0; i < count; i++){
            ret[0][i] = dis.readShort();
            ret[1][i] = dis.readShort();
            ret[2][i] = dis.readShort();
            ret[3][i] = 0;
        }
        return ret;
    }

    /**
     * ����ģ����ͼ�ĵ�ͼ���ݡ�
     * @param landforms ��ͼ���е����е���
     */
    public int[][][] createBlurMapBuffer(LandformImage[] landforms){
        int rows = mapData.length;
        int cols = mapData[0].length;

        // ��ͼ���ݳ�ʼ��Ϊȫ͸����-1��
        int[][][] mapDataBuffer = new int[rows][cols][3];
        int[] clear = new int[]{
                        -1, -1, -1
        };

        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                System.arraycopy(clear, 0, mapDataBuffer[i][j], 0, 3);
            }
        }

        // ����л������Σ��������������ε�100%��(1111)
        if(baseLandform != -1){
            Random rand = new Random(randomSeed);
            LandformImage image = landforms[baseLandform];
            image.generateSearchTable();
            for(int i = 0; i < rows; i++){
                for(int j = 0; j < cols; j++){
                    int[] tile = image.getTile(rand, 0x0F);
                    if(tile[0] != -1){
                        mapDataBuffer[i][j] = new int[]{
                                        makeLayerBits(baseLandform, tile[0], tile[1]), -1, -1
                        };
                    }
                }
            }
        }

        // ���յ��ε�����˳��ʼ�̵�ͼ
        for(int lf = 0; lf < landforms.length; lf++){
            if(lf == baseLandform){
                continue;
            }
            Random rand = new Random(randomSeed);
            byte[][] tmpData = makeLayer(mapData, lf);
            LandformImage image = landforms[lf];
            image.generateSearchTable();
            for(int i = 0; i < rows; i++){
                for(int j = 0; j < cols; j++){
                    int[] tile = image.getTile(rand, tmpData[i + 1][j + 1]);
                    if(tile[0] != -1){
                        if(tmpData[i + 1][j + 1] == 0x0F){
                            // �������������Tile�Ѿ���ռ100%�ˣ��򸲸���ǰ������Tile
                            mapDataBuffer[i][j] = new int[]{
                                            makeLayerBits(lf, tile[0], tile[1]), -1, -1
                            };
                        }else{
                            // �����ռ������ϲ�
                            mergeGridData(mapDataBuffer[i][j], makeLayerBits(lf, tile[0], tile[1]));
                        }
                    }
                }
            }
        }

        return mapDataBuffer;
    }

    // �������յ�ͼ������һ�����һ�����β������
    private int makeLayerBits(int lfid, int tileid, int transit){
        return (lfid << 16) | (transit << 8) | tileid;
    }

    // ��һ����θ�����ݺϲ������յ�ͼ������
    private void mergeGridData(int[] cell, int newLayer){
        for(int i = 0; i < cell.length; i++){
            if(cell[i] == -1){
                cell[i] = newLayer;
                break;
            }
        }
    }

    /**
     * ����һ�����Ρ�
     * @param w ���
     * @param h �߶�
     * @return
     */
    public static byte[][] makeRectangle(int w, int h){
        byte[][] ret = new byte[h][w];
        for(int i = 0; i < h; i++){
            for(int j = 0; j < w; j++){
                if(i == 0){
                    if(j == 0){
                        ret[i][j] = 1; // 0001
                    }else if(j == w - 1){
                        ret[i][j] = 2; // 0010
                    }else{
                        ret[i][j] = 3; // 0011
                    }
                }else if(i == h - 1){
                    if(j == 0){
                        ret[i][j] = 4; // 0100
                    }else if(j == w - 1){
                        ret[i][j] = 8; // 1000
                    }else{
                        ret[i][j] = 12; // 1100
                    }
                }else{
                    if(j == 0){
                        ret[i][j] = 5; // 0101
                    }else if(j == w - 1){
                        ret[i][j] = 10; // 1010
                    }else{
                        ret[i][j] = 15; // 1111
                    }
                }
            }
        }
        return ret;
    }

    /**
     * ����ģ��ͼ�����������ָ��һ�ֵ��εĸ�����ݡ�
     * @param data ģ��ͼ������
     * @param lfid �������
     * @return
     */
    public static byte[][] makeLayer(byte[][] data, int lfid){
        int rows = data.length;
        int cols = data[0].length;
        byte[][] ret = new byte[rows + 2][cols + 2];
        byte[][] sg = makeRectangle(3, 3);
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                if(data[i][j] != lfid){
                    continue;
                }
                ret[i][j] |= sg[0][0];
                ret[i][j + 1] |= sg[0][1];
                ret[i][j + 2] |= sg[0][2];
                ret[i + 1][j] |= sg[1][0];
                ret[i + 1][j + 1] |= sg[1][1];
                ret[i + 1][j + 2] |= sg[1][2];
                ret[i + 2][j] |= sg[2][0];
                ret[i + 2][j + 1] |= sg[2][1];
                ret[i + 2][j + 2] |= sg[2][2];
            }
        }
        return ret;
    }
}
