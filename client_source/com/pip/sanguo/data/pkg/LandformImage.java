package com.pip.sanguo.data.pkg;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import com.pip.image.ImageSet;

/**
 * ģ����ͼ���Ρ�
 */
public class LandformImage extends ImageSet{
    /** ռ��ȫ�����ӵ���ͼ */
    public static final int TILE_100 = 0;
    /** ռ������75%���ӵ���ͼ */
    public static final int TILE_75 = 1;
    /** ռ���·�50%���ӵ���ͼ */
    public static final int TILE_50H = 2;
    /** ռ���ҷ�50%���ӵ���ͼ */
    public static final int TILE_50V = 3;
    /** ռ�����Ϻ����½Ǹ��ӵ���ͼ */
    public static final int TILE_50S = 4;
    /** ռ����25%���ӵ���ͼ */
    public static final int TILE_25 = 5;

    // ÿ��ͼ���Ӧ�ĵ�������
    protected short[] tileTypes;
    // ÿ��ͼ��ĳ���Ȩ��
    protected short[] tilePrior;

    // ��ͼ���Ͷ�Ӧ��ͼ���ұ�
    protected int[][] frameSearchTable;

    // ��ͬ���͵ĸ��Ӷ�Ӧ����ͼ���������������Ǹ������ͣ���һ�����ӻ���Ϊ4��С��ķ�����ÿһС����1λ
    // ����ʾ�������õ�һ��0-16��������4��С�����У������Ͻǵĸ���ռ���λ�����½ǵ�ռ���λ��
    protected final static int[][] TYPE_MAP = {
                    {
                                    -1, 0
                    }, // 0000
                    {
                                    TILE_25, 0
                    }, // 0001
                    {
                                    TILE_25, 2
                    }, // 0010
                    {
                                    TILE_50H, 0
                    }, // 0011
                    {
                                    TILE_25, 1
                    }, // 0100
                    {
                                    TILE_50V, 0
                    }, // 0101
                    {
                                    TILE_50S, 2
                    }, // 0110
                    {
                                    TILE_75, 0
                    }, // 0111
                    {
                                    TILE_25, 3
                    }, // 1000
                    {
                                    TILE_50S, 0
                    }, // 1001
                    {
                                    TILE_50V, 2
                    }, // 1010
                    {
                                    TILE_75, 2
                    }, // 1011
                    {
                                    TILE_50H, 1
                    }, // 1100
                    {
                                    TILE_75, 1
                    }, // 1101
                    {
                                    TILE_75, 3
                    }, // 1110
                    {
                                    TILE_100, 0
                    }
    // 1111
    };

    
    /**
     * ���ļ������봴����
     * @param data
     */
    public LandformImage(byte[] data) throws Exception{
        super(data);
        int fcount = getFrameCount();
        ByteArrayInputStream bis = new ByteArrayInputStream(data, data.length - 4 * fcount, 4 * fcount);
        DataInputStream dis = new DataInputStream(bis);
        tileTypes = new short[fcount];
        tilePrior = new short[fcount];
        for(int i = 0; i < fcount; i++){
            tileTypes[i] = dis.readShort();
            tilePrior[i] = dis.readShort();
        }
    }
    /**
     * ���ɲ�ͬ�������Ͷ�Ӧ����ͼ�Ĳ��ұ��ڼ���ģ����ͼ֮ǰִ�д˷�����������߲���Ч�ʡ�
     */
    public void generateSearchTable(){
        frameSearchTable = new int[6][];
        int count = tileTypes.length;
        for(int t = 0; t < 6; t++){
            int[] candidates = new int[count];
            int canCount = 0;
            for(int i = 0; i < count; i++){
                if(tileTypes[i] == t){
                    candidates[canCount++] = i;
                }
            }
            frameSearchTable[t] = new int[canCount];
            System.arraycopy(candidates, 0, frameSearchTable[t], 0, canCount);
        }
    }
    /**
     * �����趨�ĳ���Ƶ�����ѡ��һ��ָ�����͵�ģ����ͼ��
     * @param rand �����������
     * @param tileType ģ����ͼ����
     */
    private int randomChooseTile(Random rand, int tileType){
        if(tileType == -1){
            return -1;
        }
        int[] candidates = frameSearchTable[tileType];
        if(candidates.length == 0){
            candidates = frameSearchTable[TILE_100];
        }
        int totalPrior = 0;
        for(int i = 0; i < candidates.length; i++){
            totalPrior += tilePrior[candidates[i]] & 0xFFFF;
        }
        if(totalPrior == 0){
            return -1;
        }
        int point = rand.nextInt(totalPrior);
        for(int i = 0; i < candidates.length; i++){
            point -= tilePrior[candidates[i]] & 0xFFFF;
            if(point <= 0){
                return candidates[i];
            }
        }
        return -1;
    }
    /**
     * ���ݸ�������������ҳ�һ����ͼ��
     * @param rand �����������
     * @param gridType �������ͣ�0-16
     * @return ��������2��Ԫ�أ���Ӧ��ͼ��PipImage�е���������תֵ
     */
    public int[] getTile(Random rand, int gridType){
        int tileIndex = randomChooseTile(rand, TYPE_MAP[gridType][0]);
        return new int[]{
                        tileIndex, TYPE_MAP[gridType][1]
        };
    }
}
