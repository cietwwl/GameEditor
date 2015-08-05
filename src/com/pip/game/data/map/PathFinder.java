package com.pip.game.data.map;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.pip.mapeditor.data.AccurateMapLayer;
import com.pip.mapeditor.data.BlurMapLayer;
import com.pip.mapeditor.data.GameMap;
import com.pip.mapeditor.data.IMapLayer;
import com.pip.mapeditor.data.MapNPC;
import com.pip.mapeditor.data.MapNPCLayer;
import com.pip.mapeditor.data.NPCImageInfo;
import com.pip.mapeditor.data.TileInfo;

/**
 * ��ͼѰ·�����ࡣÿ��GameMapInfo���󴴽�һ��PathFinder������ʵ�ֳ���������2��֮��Ѱ·��<br>
 * Ѱ·�㷨��ԭ�����£�<br>
 * 1. ����Ϊ��ͼ�е�ÿ��16x16�ĸ�㣬���������г���1���������ͨ�������㲻��ͨ����<br>
 * 2. ����ÿ����㣬��A*�㷨����������Χ11x11��Χ�ڵ�ÿ������Ƿ���·�����ѷ���������浽�����С������ʽ
 * Ϊbyte���飬ÿ������Ӧһ���ֽڣ��������ʼ����·�����洢������ʼ��ľ��룻�������ʼ��û��·�����洢
 * ���������һ������ʼ����·���ĸ������꣨��11x11��Χ�ڵ����꣬-1��ʾ���Ͻǣ�-121��ʾ���½ǣ���<br>
 * 3. �����ͼ�����������·��ʱ�����Ŀ�������ʼ�㻺���11x11�ķ�Χ�ڣ���ֱ��ͨ��������ҳ����·�������
 * ����11x11��Χ�ڣ�����11x11��Χ�ھ���Ŀ���������Ǹ�����Ϊ��ʱĿ��������·����<br>
 * 4. ���ѡ�е���ʱĿ�굽��ʼ��֮��û�п���·������ôȡ����ʱĿ�����������һ����·���ĵ���Ϊ��ʱĿ�ꡣ<br>
 * 
 * ע��Ϊ���Ч�ʣ���󻺴��б���Ĳ��ǵ���ʼ��ľ��룬���Ǵ���ʼ���ߵ�Ŀ���ĵ�һ����Ŀ���㡣
 * @author lighthu
 */
public class PathFinder {
    /** ����С��Ӧ��λ�� */
    public static final int GRID_BITS = 4;
    /** ����С */
    public static final int GRID_SIZE = 16;
    /** ���淶Χ�뾶 */
    public static final int BUFFER_RANGE = 5;
    /** ���淶Χ�Ŀ�� */
    public static final int BUFFER_MATRIX_SIZE = 11;
    /** �����Ĵ�С */
    public static final int BUFFER_BLOCK_SIZE = 121;
    
    /* ����� */
    protected static Random rand = new Random();

    // ��ͼ����
    protected GameMapInfo map;
    // ʵ�ʵ�ͼ��Ϣ
    protected GameMap gameMap;
    // ��ͼ��ȣ���㣩
    protected int gridWidth;
    // ��ͼ�߶ȣ���㣩
    protected int gridHeight;
    // ��ͼ��ͨ������Ϣ����16x16�ĸ�����
    protected byte[][] passable;
    // ��ͼ��ͨ������Ϣ����4x4�ĸ����㣬ÿ8�����ƴ��1���ֽ�
    protected byte[] passable2;
    // ��ͼ��һ����ӽ���ͼ���ĵĿ�ͨ�����λ�ã����أ�
    protected int godX, godY;
    // ·�����档�ٶ���ͼ��СΪ640x640����ô��40x40����㣬ÿ�����Ļ���������11x11�ֽڣ��ܹ��Ļ������
    // Ϊ40x40x11x11=193600����Լ190K�������1000�ŵ�ͼ��PathFinderռ�õ��ܿռ��Լ200M��
    // Ϊ�˽�ʡ�ռ䣬���һ�������Χ11x11�ķ�Χ�ڵ����и�㶼�ǿ�ͨ���ģ���ô�������Ӧ�Ļ��潫�����档
    // ���ԣ�pathBuffer�еĻ�����Ӧ�ĸ�㲻�������ģ���Ҫ��pathBufferIndex��������
    // ����һ����������������ʼ�㱾��Ͳ���ͨ���������ϲ�Ӧ��������Ϊ��ʼ�㡣Ϊ���ݴ������������Ҳ����
    // ����·�����棬������Χȫ������ͨ�������������ٱ�֤������żȻ���߽���ɽ�Ҳ���߳�����
    protected byte[] pathBuffer;
    // ÿ�������Χ��·��������pathBuffer�е�������Ϊ�˽�ʡ�ռ䣬��������һ��short���洢������������
    // һ���������11x11�ֽڣ��������Ǳ���pathBuffer�е�ֱ��������������ﶼֵ��-1����ʾ������û�л��档
    protected short[] pathBufferIndex;
    // ��ͼ�Ŀ�ͨ������Ϣ����16x16�ĸ����㡣���һ�����Ӵ��κ�һ�����ڿ��Ե����ô�����ʽ��Ϊ��ͨ�
    protected List<byte[][]> reachable;
    // ��ǿͨ����Ϣ��ֻ�е�һ��������Χ8��������>=3�����Ե��������£�������Ӳű���Ϊ�ɵ���
    protected List<byte[][]> reachable2;
    // �����ͼ��ͨ����Ϣ����¼ÿ��4x4�ĸ����Ƿ��ܴ�������ڵ��
    protected byte[][] reachable3;
    
    //A*�㷨·������
    protected int pathLen[][];
    
    protected int openNodes[];
    
    protected int maxOpenNodes;
    
    protected static  byte PATH_FIND[][] = {
            { -1, 0, 2 },
            { 1, 0, 2 },
            { 0, -1, 2 },
            { 0, 1, 2 },
            { -1, -1, 3 },
            { 1, -1, 3 },
            { -1, 1, 3 }, 
            { 1, 1, 3 }
        };
    // ÿ�����������㣬���ǶȲ�Ĵ�С����
    protected static int[][] REPLACEMENT_SEQUENCE;
    static {
        // ���ȼ����ÿ�������м���֮��ĽǶ�
        double[] angles = new double[BUFFER_BLOCK_SIZE];
        for (int i = 0; i < BUFFER_BLOCK_SIZE; i++) {
            int xoff = (i % BUFFER_MATRIX_SIZE) - BUFFER_RANGE;
            int yoff = BUFFER_RANGE - (i / BUFFER_MATRIX_SIZE);
            if (xoff == 0 && yoff == 0) {
                angles[i] = 100;
                continue;
            }
            double sin = yoff / Math.sqrt(xoff * xoff + yoff * yoff);
            double arc = Math.asin(sin);
            if (xoff >= 0) {
                if (yoff < 0) {
                    arc += 2 * Math.PI;
                }
            } else {
                arc = Math.PI - arc;
            }
            angles[i] = arc;
        }
        
        // ����ÿ�������ӽ��Ƕȵĸ��˳��
        REPLACEMENT_SEQUENCE = new int[BUFFER_BLOCK_SIZE][BUFFER_BLOCK_SIZE];
        for (int i = 0; i < BUFFER_BLOCK_SIZE; i++) {
            // �����������������и��֮��ĽǶȲ�ı�
            double[] anglesDup = new double[BUFFER_BLOCK_SIZE];
            for (int j = 0; j < BUFFER_BLOCK_SIZE; j++) {
                REPLACEMENT_SEQUENCE[i][j] = j;
                anglesDup[j] = Math.abs(angles[j] - angles[i]);
                if (anglesDup[j] > Math.PI) {
                    anglesDup[j] = Math.PI * 2 - anglesDup[j];
                }
                
                // ��Ŀ���㵽ԭ���֮��ľ�����Ϊһ��Ȩֵ�������������ǲ��ҵ����ĵ���һ��ֱ���ϵĵ㡣
                int x1 = i % BUFFER_BLOCK_SIZE;
                int y1 = i / BUFFER_BLOCK_SIZE;
                int x2 = j % BUFFER_BLOCK_SIZE;
                int y2 = j / BUFFER_BLOCK_SIZE;
                int dist = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
                anglesDup[j] += Math.sqrt(dist) / 5;
            }
            
            // ���Ƕȴ�С�����˳������
            for (int ii = 0; ii < BUFFER_BLOCK_SIZE; ii++) {
                for (int jj = ii + 1; jj < BUFFER_BLOCK_SIZE; jj++) {
                    if (anglesDup[ii] > anglesDup[jj]) {
                        double dtemp = anglesDup[ii];
                        anglesDup[ii] = anglesDup[jj];
                        anglesDup[jj] = dtemp;
                        int itemp = REPLACEMENT_SEQUENCE[i][ii];
                        REPLACEMENT_SEQUENCE[i][ii] = REPLACEMENT_SEQUENCE[i][jj];
                        REPLACEMENT_SEQUENCE[i][jj] = itemp;
                    }
                }
            }
        }
    }
    
    protected PathFinder() {
        
    }
    
    /**
     * ����һ����ͼ��PathFinder�����潫�ڴ�ʱ������������
     * @param map
     */
    public PathFinder(GameMapInfo map) {
        this.map = map;
        this.gameMap = map.owner.getMapFile().getMaps().get(map.id);
    }
    
    /**
     * �༭���в���ʱ�õ��Ĺ��췽����
     * @param map
     * @param mf
     */
    public PathFinder(GameMapInfo map, GameMap gm) {
        this.map = map;
        this.gameMap = gm;
        buildPathBuffer();
    }
    
    /**
     * �Ѵ����õ�·�����汣��Ϊ�ֽ�����
     */
    public byte[] savePathBuffer() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeInt(gridWidth);
            dos.writeInt(gridHeight);
            dos.writeInt(passable.length);
            dos.writeInt(passable[0].length);
            for (byte[] row : passable) {
                dos.write(row);
            }
            dos.writeInt(passable2.length);
            dos.write(passable2);
            dos.writeInt(godX);
            dos.writeInt(godY);
            dos.writeInt(pathBuffer.length);
            dos.write(pathBuffer);
            dos.writeInt(pathBufferIndex.length);
            for (short s : pathBufferIndex) {
                dos.writeShort(s);
            }
            dos.writeInt(reachable.size());
            for (int i = 0; i < reachable.size(); i++) {
                byte[][] matrix = reachable.get(i);
                dos.writeInt(matrix.length);
                dos.writeInt(matrix[0].length);
                for (byte[] row : matrix) {
                    dos.write(row);
                }
            }
            dos.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        }
    }
    
    /**
     * ��Ԥ�ȱ����·���������������롣
     */
    public void loadPathBuffer(byte[] data) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bis);
        gridWidth = dis.readInt();
        gridHeight = dis.readInt();
        passable = new byte[dis.readInt()][dis.readInt()];
        for (byte[] row : passable) {
            dis.readFully(row);
        }
        passable2 = new byte[dis.readInt()];
        dis.readFully(passable2);
        godX = dis.readInt();
        godY = dis.readInt();
        pathBuffer = new byte[dis.readInt()];
        dis.readFully(pathBuffer);
        pathBufferIndex = new short[dis.readInt()];
        for (int i = 0; i < pathBufferIndex.length; i++) {
            pathBufferIndex[i] = dis.readShort();
        }
        reachable = new ArrayList<byte[][]>();
        int count = dis.readInt();
        for (int i = 0; i < count; i++) {
            byte[][] matrix = new byte[dis.readInt()][dis.readInt()];
            for (byte[] row : matrix) {
                dis.readFully(row);
            }
            reachable.add(matrix);
        }
        buildReachable2();
        buildReachable3();
        maxOpenNodes = (gridWidth + gridHeight) << 4;
        openNodes = new int[maxOpenNodes];
        pathLen = new int[gridWidth][gridHeight];
    }
    
    /**
     * ����·�����档
     */
    public void buildPathBuffer() {
        // ����ͨ������Ϣ
        passable = getPassableMatrix();
        
        // Ϊ�������ռ�
        gridWidth = gameMap.width / GRID_SIZE;
        gridHeight = gameMap.height / GRID_SIZE;
        pathBufferIndex = new short[gridWidth * gridHeight];
        pathBuffer = new byte[gridWidth * gridHeight * BUFFER_BLOCK_SIZE];

        // ɨ�����и�㣬����ÿ�����Ļ��档
        int index = 0;
        short bufferIndex = 0;
        for (int i = 0; i < gridHeight; i++) {
            for (int j = 0; j < gridWidth; j++) {
                // ����һ�����Ļ��棬�������null�����ʾ��������Χ11x11��Χ��ȫ������ͨ��������Ҫ����
                byte[] buf = createGridPathBuffer(i, j);
                if (buf != null) {
                    System.arraycopy(buf, 0, pathBuffer, bufferIndex * BUFFER_BLOCK_SIZE, BUFFER_BLOCK_SIZE);
                    pathBufferIndex[index] = bufferIndex;
                    bufferIndex++;
                } else {
                    pathBufferIndex[index] = -1;
                }
                index++;
            }
        }
        
        // ʵ����Ҫ�Ļ��泤��Ӧ�û����󳤶�ҪС������õ�δ�õĿռ�
        if (bufferIndex < gridWidth * gridHeight) {
            byte[] realBuf = new byte[bufferIndex * BUFFER_BLOCK_SIZE];
            System.arraycopy(pathBuffer, 0, realBuf, 0, realBuf.length);
            pathBuffer = realBuf;
        }
        
        // ����ÿ�����Ŀ�ͨ����
        reachable = new ArrayList<byte[][]>();
        for (GameMapObject gmo : map.objects) {
            if (!(gmo instanceof GameMapExit)) {
                continue;
            }
            reachable.add(getReachableMatrix((GameMapExit)gmo));
        }
        buildReachable2();
        buildReachable3();
        maxOpenNodes = (gridWidth + gridHeight) << 4;
        openNodes = new int[maxOpenNodes];
        pathLen = new int[gridWidth][gridHeight];
    }
    
    /*
     * ������ǿͨ����Ϣ��Ϊ���뿨�����񣩣�ֻ��һ��������Χ8�����ӳ���3�����Ե�����㵽�
     */
    protected void buildReachable2() {
        reachable2 = new ArrayList<byte[][]>();
        for (byte[][] r : reachable) {
            int w = r[0].length;
            int h = r.length;
            byte[][] r2 = new byte[h][w]; 
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    // �����������޷�ͨ����reachable2Ҳ���޷�ͨ��
                    if (r[i][j] == 0) {
                        r2[i][j] = 0;
                        continue;
                    }
                    
                    // ������Χ8�����ӵĿ�ͨ����
                    int passcount = 0;
                    for (int k = i - 1; k <= i + 1; k++) {
                        if (k < 0 || k >= h) {
                            continue;
                        }
                        for (int l = j - 1; l <= j + 1; l++) {
                            if (l < 0 || l >= w) {
                                continue;
                            }
                            if (r[k][l] == 1) {
                                passcount++;
                            }
                        }
                    }
                    r2[i][j] = (byte)passcount;
                }
            }
            reachable2.add(r2);
        }
    }
    
    /*
     * ��������ͨ����Ϣ���ٲ��ұ�4x4�������ڴ�ǽ�жϡ����һ��������Χ����һ�����ӿ�ͨ���������ͨ����
     */
    protected void buildReachable3() {
        int gridw = passable[0].length << 2;
        int gridh = passable.length << 2;
        reachable3 = new byte[gridh][gridw];
        for (GameMapObject gmo : map.objects) {
            if (!(gmo instanceof GameMapExit)) {
                continue;
            }
            byte[][] matrix = getReachableMatrix3((GameMapExit)gmo);
            for (int y = 0; y < gridh; y++) {
                for (int x = 0; x < gridw; x++) {
                    if (matrix[y][x] == 1) {
                        reachable3[y][x] = 1;
                        if (y > 0) {
                            reachable3[y - 1][x] = 1;
                        }
                        if (y < gridh - 1) {
                            reachable3[y + 1][x] = 1;
                        }
                        if (x > 0) {
                            reachable3[y][x - 1] = 1;
                        }
                        if (x < gridw - 1) {
                            reachable3[y][x + 1] = 1;
                        }
                    }
                }
            }
        }
    }
    
    /*
     * Ϊһ����㴴����Χ11x11��Χ�ڵ�·����Ϣ������A*�㷨�������11x11��Χ��ÿ����㵽��ʼ�����̾��롣
     * �������120����㶼�ܹ��������Ҫ���棬ֱ�ӷ���null���ɡ�
     */
    protected byte[] createGridPathBuffer(int row, int col) {
        // �����ʼ�㲻��ͨ�������ü�����
        if (passable[row][col] == 0) {
            return null;
        }
        
        // ���м���Ϊ��ʼ�㣬��������ɢ
        List<int[]> points = new ArrayList<int[]>();        // ������
        byte[][] tempMatrix = new byte[BUFFER_MATRIX_SIZE][BUFFER_MATRIX_SIZE];     // ·�����Ȼ��棬-128��ʾδ������
        for (int i = 0; i < BUFFER_MATRIX_SIZE; i++) {
            Arrays.fill(tempMatrix[i], (byte)-128);
        }
        points.add(new int[] { 0, 0 });
        tempMatrix[BUFFER_RANGE][BUFFER_RANGE] = 0;
        while (points.size() > 0) {
            int[] coord = points.remove(0);
            int x = coord[0];
            int y = coord[1];
            byte dist = tempMatrix[y + BUFFER_RANGE][x + BUFFER_RANGE];
            
            // ���˵������ĸ����Ƿ����ͨ�����������ͨ����������ǰû���߹�����ô��������У����Ұ�����·�����ȱ��Ϊ��ǰ���·������+1
            if (x > -BUFFER_RANGE && canPassGrid(y + row, x + col - 1) && 
                    (tempMatrix[y + BUFFER_RANGE][x + BUFFER_RANGE - 1] == (byte)-128 || tempMatrix[y + BUFFER_RANGE][x + BUFFER_RANGE - 1] > dist + 2)) {
                points.add(new int[] { x - 1, y });
                tempMatrix[y + BUFFER_RANGE][x + BUFFER_RANGE - 1] = (byte)(dist + 2);
            }
            if (x < BUFFER_RANGE && canPassGrid(y + row, x + col + 1) && 
                    (tempMatrix[y + BUFFER_RANGE][x + BUFFER_RANGE + 1] == (byte)-128 || tempMatrix[y + BUFFER_RANGE][x + BUFFER_RANGE + 1] > dist + 2)) {
                points.add(new int[] { x + 1, y });
                tempMatrix[y + BUFFER_RANGE][x + BUFFER_RANGE + 1] = (byte)(dist + 2);
            }
            if (y > -BUFFER_RANGE && canPassGrid(y + row - 1, x + col) && 
                    (tempMatrix[y + BUFFER_RANGE - 1][x + BUFFER_RANGE] == (byte)-128 || tempMatrix[y + BUFFER_RANGE - 1][x + BUFFER_RANGE] > dist + 2)) {
                points.add(new int[] { x, y - 1 });
                tempMatrix[y + BUFFER_RANGE - 1][x + BUFFER_RANGE] = (byte)(dist + 2);
            }
            if (y < BUFFER_RANGE && canPassGrid(y + row + 1, x + col) && 
                    (tempMatrix[y + BUFFER_RANGE + 1][x + BUFFER_RANGE] == (byte)-128 || tempMatrix[y + BUFFER_RANGE + 1][x + BUFFER_RANGE] > dist + 2)) {
                points.add(new int[] { x, y + 1 });
                tempMatrix[y + BUFFER_RANGE + 1][x + BUFFER_RANGE] = (byte)(dist + 2);
            }
            
            // ���4��б��ĸ��Ƿ���Ե���
            if (x > -BUFFER_RANGE && y > -BUFFER_RANGE && canPassGrid(y + row - 1, x + col - 1) &&
                    (canPassGrid(y + row - 1, x + col) || canPassGrid(y + row, x + col - 1)) &&
                    (tempMatrix[y + BUFFER_RANGE - 1][x + BUFFER_RANGE - 1] == (byte)-128 || tempMatrix[y + BUFFER_RANGE - 1][x + BUFFER_RANGE - 1] > dist + 3)) {
                points.add(new int[] { x - 1, y - 1 });
                tempMatrix[y + BUFFER_RANGE - 1][x + BUFFER_RANGE - 1] = (byte)(dist + 3);
            }
            if (x < BUFFER_RANGE && y > -BUFFER_RANGE && canPassGrid(y + row - 1, x + col + 1) && 
                    (canPassGrid(y + row - 1, x + col) || canPassGrid(y + row, x + col + 1)) &&
                    (tempMatrix[y + BUFFER_RANGE - 1][x + BUFFER_RANGE + 1] == (byte)-128 || tempMatrix[y + BUFFER_RANGE - 1][x + BUFFER_RANGE + 1] > dist + 3)) {
                points.add(new int[] { x + 1, y - 1 });
                tempMatrix[y + BUFFER_RANGE - 1][x + BUFFER_RANGE + 1] = (byte)(dist + 3);
            }
            if (x > -BUFFER_RANGE && y < BUFFER_RANGE && canPassGrid(y + row + 1, x + col - 1) && 
                    (canPassGrid(y + row + 1, x + col) || canPassGrid(y + row, x + col - 1)) &&
                    (tempMatrix[y + BUFFER_RANGE + 1][x + BUFFER_RANGE - 1] == (byte)-128 || tempMatrix[y + BUFFER_RANGE + 1][x + BUFFER_RANGE - 1] > dist + 3)) {
                points.add(new int[] { x - 1, y + 1 });
                tempMatrix[y + BUFFER_RANGE + 1][x + BUFFER_RANGE - 1] = (byte)(dist + 3);
            }
            if (x < BUFFER_RANGE && y < BUFFER_RANGE && canPassGrid(y + row + 1, x + col + 1) && 
                    (canPassGrid(y + row + 1, x + col) || canPassGrid(y + row, x + col + 1)) &&
                    (tempMatrix[y + BUFFER_RANGE + 1][x + BUFFER_RANGE + 1] == (byte)-128 || tempMatrix[y + BUFFER_RANGE + 1][x + BUFFER_RANGE + 1] > dist + 3)) {
                points.add(new int[] { x + 1, y + 1 });
                tempMatrix[y + BUFFER_RANGE + 1][x + BUFFER_RANGE + 1] = (byte)(dist + 3);
            }
        }
        
        // ���������ܵ���ĵ㶼�Ѿ�������ˣ�ʣ��Ϊ-128�ľ���ͨ��11x11��Χ�ڵĵ��޷�ֱ�ӵ���ĵ㡣����ÿ���޷�����ĵ㣬����Ҫ����������������
        // ���ҵ�һ�����Ե���ĵ㣬��Ϊ�滻Ŀ�ꡣ
        boolean hasUnpassGrid = false;
        for (int i = 0; i < BUFFER_MATRIX_SIZE; i++) {
            for (int j = 0; j < BUFFER_MATRIX_SIZE; j++) {
                if (tempMatrix[i][j] != (byte)-128) {
                    continue;
                }
                int pt = findReplacement(tempMatrix, i, j);
                tempMatrix[i][j] = (byte)(-1 - pt);
                
                // ͳ���Ƿ��в��ɵ������Ч��
                if (!hasUnpassGrid) {
                    int rx = j + col - BUFFER_RANGE;
                    int ry = i + row - BUFFER_RANGE;
                    if (rx >= 0 && rx < gridWidth && ry >= 0 && ry < gridHeight) {
                        hasUnpassGrid = true;
                    }
                }
            }
        }
        
        // ������е㶼���Ե������Խ�磬����null
        if (!hasUnpassGrid) {
            return null;
        }
        
        // �ѻ���չƽ����
        byte[] ret = new byte[BUFFER_MATRIX_SIZE * BUFFER_MATRIX_SIZE];
        for (int i = 0; i < BUFFER_MATRIX_SIZE; i++) {
            for (int j = 0; j < BUFFER_MATRIX_SIZE; j++) {
                // ����ת��һ�»����ʽ������㷨Ч�ʣ����Ŀ�겻�ɵ�����Ǳ�������Ŀɵ�����λ�ã����Ŀ��ɵ��
                // �������ʼ�㵽�˵�����·���е�һ���ĸ�����ꡣ
                if (tempMatrix[i][j] < 0) {
                    ret[i * BUFFER_MATRIX_SIZE + j] = tempMatrix[i][j];
                } else if (tempMatrix[i][j] < 4) {
                    ret[i * BUFFER_MATRIX_SIZE + j] = (byte)(j + i * BUFFER_MATRIX_SIZE);
                } else {
                    List<int[]> path = findBestPath(tempMatrix, i, j);
                    int[] arr = path.get(path.size() - 1);
                    ret[i * BUFFER_MATRIX_SIZE + j] = (byte)(arr[0] + arr[1] * BUFFER_MATRIX_SIZE);
                }
            }
        }
        return ret;
    }

    /*
     * ���ҵ�������һ��Ŀ�������·���������б��У����һ��Ԫ�رض�����ʼ�����ꡣ�����ʽΪint[] { x, y }��
     */
    protected List<int[]> findBestPath(byte[][] matrix, int row, int col) {
        List<int[]> ret = new ArrayList<int[]>();
        int dist = matrix[row][col];
        while (dist > 0) {
            ret.add(new int[] { col, row });
            int nextdist = dist;
            int nextrow = row, nextcol = col;
            
            // ��
            if (row > 0) {
                int t = matrix[row - 1][col];
                if (t >= 0 && t < nextdist) {
                    nextrow = row - 1;
                    nextdist = t;
                }
            }
            
            // ��
            if (row < BUFFER_MATRIX_SIZE - 1) {
                int t = matrix[row + 1][col];
                if (t >= 0 && t < nextdist) {
                    nextrow = row + 1;
                    nextdist = t;
                }
            }
            
            // ��
            if (col > 0) {
                int t = matrix[row][col - 1];
                if (t >= 0 && t < nextdist) {
                    nextcol = col - 1;
                    nextdist = t;
                }
            }

            // ��
            if (col < BUFFER_MATRIX_SIZE - 1) {
                int t = matrix[row][col + 1];
                if (t >= 0 && t < nextdist) {
                    nextcol = col + 1;
                    nextdist = t;
                }
            }
            
            // ����
            if (row > 0 && col > 0) {
                int t = matrix[row - 1][col - 1];
                if (t >= 0 && t < nextdist) {
                    nextrow = row - 1;
                    nextcol = col - 1;
                    nextdist = t;
                }
            }
            
            // ����
            if (row < BUFFER_MATRIX_SIZE - 1 && col > 0) {
                int t = matrix[row + 1][col - 1];
                if (t >= 0 && t < nextdist) {
                    nextrow = row + 1;
                    nextcol = col - 1;
                    nextdist = t;
                }
            }

            // ����
            if (row > 0 && col < BUFFER_MATRIX_SIZE - 1) {
                int t = matrix[row - 1][col + 1];
                if (t >= 0 && t < nextdist) {
                    nextrow = row - 1;
                    nextcol = col + 1;
                    nextdist = t;
                }
            }

            // ����
            if (row < BUFFER_MATRIX_SIZE - 1 && col < BUFFER_MATRIX_SIZE - 1) {
                int t = matrix[row + 1][col + 1];
                if (t >= 0 && t < nextdist) {
                    nextrow = row + 1;
                    nextcol = col + 1;
                    nextdist = t;
                }
            }
            
            if (nextrow == row && nextcol == col) {
                throw new IllegalArgumentException("Bad path buffer.");
            }
            row = nextrow;
            col = nextcol;
            dist = nextdist;
        }
        return ret;
    }
    
    /*
     * �Ӿ����е�һ������������Ҿ�������Ŀ���ͨ���ĵ㡣���ص������У���һ��Ԫ����Ŀ�������������Ͻ�Ϊ0�����½�Ϊ120��
     */
    protected int findReplacement(byte[][] matrix, int row, int col) {
        // ����Ŀ������ڵķ�����ȡ�����������Ӧ�Ĳ��ұ�
        int pos = col + row * BUFFER_MATRIX_SIZE;
        int[] searchTable = REPLACEMENT_SEQUENCE[pos];

        // �����ұ�ָ����˳�����β��ҵ���һ������ͨ��ĵ�
        boolean found = false;
        int len = searchTable.length;
        for (int i = 0; i < len; i++) {
            if (found) {
                pos = searchTable[i];
                if (matrix[pos / BUFFER_MATRIX_SIZE][pos % BUFFER_MATRIX_SIZE] >= 0) {
                    return pos;
                }
            } else {
                if (searchTable[i] == pos) {
                    found = true;
                }
            }
        }
        return BUFFER_RANGE + BUFFER_RANGE * BUFFER_MATRIX_SIZE;
    }
    
    /*
     * ���һ������Ƿ�����ͨ���������㱻���Ϊ����ͨ�������߸��������곬����ͼ��Χ������false��
     */
    protected boolean canPassGrid(int row, int col) {
        if (row < 0 || row >= passable.length) {
            return false;
        }
        if (col < 0 || col >= passable[0].length) {
            return false;
        }
        return passable[row][col] != 0;
    }
    
    /*
     * ��һ���������ݵľ��������һ�����顣
     */
    protected void fillMatrix(boolean[][] matrix, int x, int y, int w, int h, boolean value) {
        // �淶λ����Ϣ
        if (x >= matrix[0].length || y >= matrix.length) {
            return;
        }
        if (x < 0) {
            w += x;
            x = 0;
        }
        if (y < 0) {
            h += y;
            y = 0;
        }
        if (x + w > matrix[0].length) {
            w = matrix[0].length - x;
        }
        if (y + h > matrix.length) {
            h = matrix.length - y;
        }
        if (w <= 0 || h <= 0) {
            return;
        }
        
        // �������
        for (int i = y; i < y + h; i++) {
            Arrays.fill(matrix[i], x, x + w, value);
        }
    }
    
    /**
     * �ӵ�ͼ�����з���ͨ�������ݡ�
     * @return ÿ������ͨ���ԡ������ͼ��СΪw*h���򷵻�����Ĵ�СӦ����(w/16)*(h/16)��
     */
    protected byte[][] getPassableMatrix() {
        gridWidth = gameMap.width / GRID_SIZE;
        gridHeight = gameMap.height / GRID_SIZE;
        
        int tw = gameMap.parent.getTileWidth();
        int tw2 = tw * 4 / GRID_SIZE;
        int th = gameMap.parent.getTileHeight();
        int th2 = th * 4 / GRID_SIZE;
        int btw = gameMap.parent.getBlurTileWidth();
        int btw2 = btw * 4 / GRID_SIZE;
        int bth = gameMap.parent.getBlurTileHeight();
        int bth2 = bth * 4 / GRID_SIZE;
        
        // ����һ������������ͨ������Ϣ���������Ϊÿ��ʵ�ʸ��16��С��
        boolean[][] gridData = new boolean[gridHeight * 4][gridWidth * 4];
        byte[][] testTileInfo = map.tileInfo;
        if (testTileInfo == null) {
            testTileInfo = gameMap.tileInfo;
        }
        if (gameMap.parent.isLibMode) {
            // ��ģʽ���ڵ��������ݣ��������
            // ��ģʽ���ڵ�������8x8�ĸ���Ϊ��λ��ÿ��������gridData�����ж�Ӧ2x2��Ԫ��
            int cellGrids = gameMap.parent.getCellSize() / 4;
            for (int y = 0; y < testTileInfo.length; y++) {
                for (int x = 0; x < testTileInfo[0].length; x++) {
                    byte block = testTileInfo[y][x];
                    if ((block & 2) != 0) {
                        fillMatrix(gridData, x * cellGrids, y * cellGrids, cellGrids, cellGrids, true);
                    }
                }
            }
        } else {
            for (int i = 0; i < gridData.length; i++) {
                Arrays.fill(gridData[i], true);
            }
            boolean afterGround = false;
            ArrayList<TileInfo> tiles = gameMap.parent.getTileImage().tileInfo;
            for (IMapLayer layer : gameMap.layers) {
                if (layer instanceof AccurateMapLayer) {
                    // ɨ����ͼ���ݣ�����ͨ����Tile���Ϊfalse��ÿ����ȷ��ͼռ8��С���ӡ�
                    short[][] mapData = ((AccurateMapLayer)layer).getLayerData();
                    for (int i = 0; i < mapData.length; i++) {
                        for (int j = 0; j < mapData[i].length; j++) {
                            int cc = mapData[i][j];
                            if (cc == -1) {
                                continue;
                            }
                            TileInfo cinfo = tiles.get(cc);
                            if (cinfo.unpassable) {
                                fillMatrix(gridData, tw2 * j, th2 * i, tw2, th2, false);
                            }
                        }
                    }
                } else if (layer instanceof MapNPCLayer) {
                    if (afterGround) {
                        // ��ղ㲻������ײ����
                        continue;
                    }
                    if (layer == gameMap.groundLayer) {
                        afterGround = true;
                    }
                    for (MapNPC npc : ((MapNPCLayer)layer).getNpcs()) {
                        long key = gameMap.parent.getNPCKey(npc);
                        NPCImageInfo ninfo = gameMap.parent.getNPCs().get(key);
                        if (ninfo == null) {
                            continue;
                        }
                        for (int i = 0; i < ninfo.cx.length; i++) {
                            int startx = ninfo.cx[i] + npc.x;
                            int starty = ninfo.cy[i] + npc.y;
                            int endx = startx + ninfo.cw[i];
                            int endy = starty + ninfo.ch[i];
                            startx = (int)Math.round(startx * 4.0 / GRID_SIZE);
                            starty = (int)Math.round(starty * 4.0 / GRID_SIZE);
                            endx = (int)Math.round(endx * 4.0 / GRID_SIZE);
                            endy = (int)Math.round(endy * 4.0 / GRID_SIZE);
                            fillMatrix(gridData, startx, starty, endx - startx, endy - starty, false);
                        }
                    }
                } else if (layer instanceof BlurMapLayer) {
                    // ɨ����ͼ���ݣ�����ͨ����Tile���Ϊfalse��ÿ��ģ����ͼռ16��С���ӡ�
                    int[][][] mapData = ((BlurMapLayer)layer).getMapData();
                    for (int i = 0; i < mapData.length; i++) {
                        for (int j = 0; j < mapData[i].length; j++) {
                            int[] cc = mapData[i][j];
                            for (int k = cc.length - 1; k >= 0; k--) {
                                if (cc[k] != -1) {
                                    int lfid = cc[k] >> 16;
                                    int tid = cc[k] & 0xFF;
                                    TileInfo cinfo = gameMap.parent.getLandforms().get(lfid).tileInfo.get(tid);
                                    if (cinfo.unpassable) {
                                        fillMatrix(gridData, btw2 * j, bth2 * i, btw2, bth2, false);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // ����ÿ����㣬������16��С���������1�루����һ�룩����ͨ��������Ϊ�����㲻��ͨ����������Ϊ��ͨ��
        int minDistance = Integer.MAX_VALUE;
        int centerX = gridWidth * GRID_SIZE / 2;
        int centerY = gridHeight * GRID_SIZE / 2;
        byte[][] ret = new byte[gridHeight][gridWidth];
        for (int i = 0; i < gridHeight; i++) {
            for (int j = 0; j < gridWidth; j++) {
                int passCount = 0;
                for (int ii = 0; ii < 4; ii++) {
                    for (int jj = 0; jj < 4; jj++) {
                        if (gridData[i * 4 + ii][j * 4 + jj]) {
                            passCount++;
                        }
                    }
                }
                ret[i][j] = (byte)(passCount >= 10 ? 1 : 0);
                if (ret[i][j] != 0) {
                    // ����˸����ľ����ͼ���ĵľ��룬Ѱ�Ҿ������������һ����ͨ�����
                    int gridCenterX = j * GRID_SIZE + GRID_SIZE / 2;
                    int gridCenterY = i * GRID_SIZE + GRID_SIZE  / 2;
                    int distance = (gridCenterX - centerX) * (gridCenterX - centerX) + (gridCenterY - centerY) * (gridCenterY - centerY);
                    if (distance < minDistance) {
                        minDistance = distance;
                        godX = gridCenterX;
                        godY = gridCenterY;
                    }
                }
            }
        }
        
        // ��ͨ��������ͨ������һ�ָ�ʽ������һ��byte�����У�ÿ��4x4�ĸ��ռ1λ��
        passable2 = new byte[(gridHeight * 4 * gridWidth * 4 + 7) / 8];
        int tmp = 0;
        int index = 0;
        int bitIndex = 7;
        for (int i = 0; i < gridHeight * 4; i++) {
            for (int j = 0; j < gridWidth * 4; j++) {
                if (gridData[i][j]) {
                    tmp |= 1 << bitIndex;
                }
                bitIndex--;
                if (bitIndex < 0) {
                    passable2[index] = (byte)tmp;
                    index++;
                    bitIndex = 7;
                    tmp = 0;
                }
            }
        }
        if (bitIndex != 7) {
            passable2[index] = (byte)tmp;
        }
        maxOpenNodes = (gridWidth + gridHeight) << 4;
        openNodes = new int[maxOpenNodes];
        pathLen = new int[gridWidth][gridHeight];
        return ret;
    }
    
    /**
     * ��ͨ���������з���ͨ�������ݡ�
     * @return ÿ�����Ŀ�ͨ���ԡ������ͼ��СΪw*h���򷵻�����Ĵ�СӦ����(w/16)*(h/16)��
     */
    protected byte[][] getReachableMatrix(GameMapExit exit) {
        int gridw = passable[0].length;
        int gridh = passable.length;
        byte[][] ret = new byte[gridh][gridw];
        int ex = exit.x / GRID_SIZE;
        int ey = exit.y / GRID_SIZE;
        
        // �ӳ��ڵ㿪ʼ�����ķ������߷��������пɵ���ĸ�㲢�����
        List<int[]> openList = new ArrayList<int[]>();
        openList.add(new int[] { ex, ey });
        while (openList.size() > 0) {
            int[] p = openList.remove(0);
            int x = p[0];
            int y = p[1];
            if (ret[y][x] == 0) {
                // �������㻹û���߹���������ǣ���������Χ�ĸ������п�ͨ����ھӼ��������
                ret[y][x] = 1;
                if (x > 0 && passable[y][x - 1] == 1) {
                    openList.add(new int[] { x - 1, y });
                }
                if (x < gridw - 1 && passable[y][x + 1] == 1) {
                    openList.add(new int[] { x + 1, y });
                }
                if (y > 0 && passable[y - 1][x] == 1) {
                    openList.add(new int[] { x, y - 1 });
                }
                if (y < gridh - 1 && passable[y + 1][x] == 1) {
                    openList.add(new int[] { x, y + 1 });
                }
            }
        }
        return ret;
    }
    
    /*
     * ��ͨ���������з���ͨ�������ݣ���4x4�ĸ��ӣ���
     * @return ÿ�����Ŀ�ͨ���ԡ������ͼ��СΪw*h���򷵻�����Ĵ�СӦ����(w/4)*(h/4)��
     */
    protected byte[][] getReachableMatrix3(GameMapExit exit) {
        int gridw = passable[0].length << 2;
        int gridh = passable.length << 2;
        byte[][] ret = new byte[gridh][gridw];
        int ex = (exit.x << 2) / GRID_SIZE;
        int ey = (exit.y << 2) / GRID_SIZE;
        
        // �ӳ��ڵ㿪ʼ�����ķ������߷��������пɵ���ĸ�㲢�����
        LinkedList<int[]> openList = new LinkedList<int[]>();
        openList.addLast(new int[] { ex, ey });
        while (openList.size() > 0) {
            int[] p = openList.removeFirst();
            int x = p[0];
            int y = p[1];
            if (ret[y][x] == 0) {
                // �������㻹û���߹���������ǣ���������Χ�ĸ������п�ͨ����ھӼ��������
                ret[y][x] = 1;
                if (x > 0 && canPassGrid2(x - 1, y)) {
                    openList.addLast(new int[] { x - 1, y });
                }
                if (x < gridw - 1 && canPassGrid2(x + 1, y)) {
                    openList.addLast(new int[] { x + 1, y });
                }
                if (y > 0 && canPassGrid2(x, y - 1)) {
                    openList.addLast(new int[] { x, y - 1 });
                }
                if (y < gridh - 1 && canPassGrid2(x, y + 1)) {
                    openList.addLast(new int[] { x, y + 1 });
                }
            }
        }
        return ret;
    }
    
    /**
     * �ж�һ�����Ƿ����ͨ��������Ϊ4���ء�
     * @param x ��ͼ���꣨���أ�
     * @param y ��ͼ���꣨���أ�
     */
    private boolean canPass(int x, int y) {
        int width = passable[0].length << 2;
        int index = (y >> 2) * width + (x >> 2);
        int bitIndex = 7 - (index & 7);
        index >>= 3;
        return (passable2[index] & (1 << bitIndex)) != 0;
    }
    
    /*
     * �ж�һ�������Ƿ����ͨ��������Ϊ4���ء�
     * @param x ��������ϵ������
     * @param y ��������ϵ������
     */
    private boolean canPassGrid2(int x, int y) {
        int width = passable[0].length << 2;
        int index = y * width + x;
        int bitIndex = 7 - (index & 7);
        index >>= 3;
        return (passable2[index] & (1 << bitIndex)) != 0;
    }
    
    /**
     * a*Ѱ·
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @param mask
     * @return
     */
    public int[][] searchPathAStar(int startX, int startY, int endX, int endY ,int mask ){
        int fxg = startX >> GRID_BITS;
        int fyg = startY >> GRID_BITS;
        int txg = endX >> GRID_BITS;
        int tyg = endY >> GRID_BITS;
        if (fxg < 0 || fxg >= gridWidth || fyg < 0 || fyg >= gridHeight) {
            return null;
        }
         
        // �����ʼ�����ֹ����ȣ�����Ѱ·
        if (fxg == txg && fyg == tyg)
        {
            return null;
        }

        // ���Ŀ��㲻��ͨ��������Ѱ·
        if ((passable[tyg][txg]  & mask) == 0)
        {
            return null;
        }
        // ��ʼ��A*�㷨·�����Ȼ���
        for (int k = 0; k < gridWidth; k++)
        {
            Arrays.fill(pathLen[k], 0);
        }
            int openNodeStart = 0;
            int openNodeEnd = 1;
            pathLen[fxg][fyg] = 1;
            openNodes[0] = (int)((fxg << 16) | fyg);
            boolean found = false;
            while (openNodeStart != openNodeEnd) 
            {
                int thisX = (openNodes[openNodeStart] >> 16) & 0xFFFF;
                int thisY = openNodes[openNodeStart] & 0xFFFF;
                int thisLen = pathLen[thisX][thisY];
                openNodeStart++;
                if (openNodeStart >= maxOpenNodes) {
                    openNodeStart = 0;
                }
                if (thisX == txg && thisY == tyg) 
                {
                    found = true;
                    break;
                }
                /*if ((thisX - endX) * (thisX - endX) + (thisY - endY) * (thisY - endY) <= 6400) {
                    endX = thisX;
                    endY = thisY; 
                    found = true;
                    break;
                }*/
                for (int i = 0; i < 8; i++) 
                {
                    int checkX = thisX + PATH_FIND[i][0];
                    int checkY = thisY + PATH_FIND[i][1];
                    int step = PATH_FIND[i][2];
                    if (checkX < 0 || checkX >= gridWidth || checkY < 0 || checkY >= gridHeight) 
                    {
                        continue;
                    }
                    int t = 0;
                    try{
                        
                         t = pathLen[checkX][checkY];
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    if (t == 0 || t > thisLen + step) 
                    {
                        // û���߹������ߴӸ����·���߹���������open
                        if (t == 0 && ((passable[checkY][checkX]  & mask) == 0)) 
                        {
                            // ����ͨ������Ϊ-1
                            pathLen[checkX][checkY] = -1;
                        } 
                        else 
                        {
                            // ������·��������ֵ��������open��
                            pathLen[checkX][checkY] = (short)(thisLen + step);
                            if ((checkY - thisY) * (endY - thisY) >= 0 &&
                                    (checkX - thisX) * (endX - thisX) >= 0) 
                            {
                                openNodeStart--;
                                if (openNodeStart < 0) 
                                {
                                    openNodeStart = maxOpenNodes - 1;
                                }
                                openNodes[openNodeStart] = (int)((checkX << 16) | checkY);
                            } 
                            else 
                            {
                                openNodes[openNodeEnd] = (int)((checkX << 16) | checkY);
                                openNodeEnd++;
                                if (openNodeEnd >= maxOpenNodes) 
                                {
                                    openNodeEnd = 0;
                                }
                                if (openNodeEnd == openNodeStart)
                                {
                                    //ͷβ�����ˣ���Ҫ��չopenNode����
                                    int newLen = maxOpenNodes * 3 / 2;
                                    int[] newarray = new int[newLen];
                                    System.arraycopy(openNodes, 0, newarray, 0, openNodeEnd);
                                    int remainCount = maxOpenNodes - openNodeStart;
                                    System.arraycopy(openNodes, openNodeStart, newarray, newLen - remainCount, remainCount);
                                    openNodeStart = newLen - remainCount;
                                    openNodes = newarray;
                                    maxOpenNodes = newLen;
                                }
                            }
                        }
                    }
                }
            }
            if (found) 
            {
                // �ҵ�·���Ժ󣬴ӽ���������ɨ�裬�ҳ��ӽ����㵽��ʼ��֮������·��������һ������
                int[][] newpath;
                int stepLen = pathLen[txg][tyg];
                int[][] ret = new int[stepLen][];
                for (int j = 0; j < stepLen; j++)
                {
                    ret[j] = new int[2];
                }
                int retp = stepLen - 1;
                while (txg != fxg || tyg != fyg) 
                {
                    if (retp < 0) 
                    {
                        break;
                    }
                    ret[retp][0] = txg ;
                    ret[retp][1] = tyg ;
                    retp--;
                    for (int i = 0; i < 8; i++)
                    {
                        int checkX = txg + PATH_FIND[i][0];
                        int checkY = tyg + PATH_FIND[i][1];
                        int step = PATH_FIND[i][2];
                        if (checkX < 0 || checkX >= gridHeight || checkY < 0 || checkY >= gridWidth) 
                        {
                            continue;
                        }
                        if (pathLen[checkX][checkY]  == stepLen - step) 
                        {
                            txg = checkX;
                            tyg = checkY;
                            stepLen -= step;
                            break;
                        }
                    }
                }
                int[][] ret2 = new int[ret.length -retp - 1][2];
                for(int i = 0 ; i < ret2.length; i ++){
                    System.arraycopy(ret[retp + 1 + i], 0, ret2[i], 0, ret2[i].length);
                }
                // �Ż�·��
                newpath = optimizePath(ret2, mask);
                return newpath;
            }
            else 
            {
                return null;
            }
    }
    // �Ż�·����ȥ��ֱ���ϵ��м��
    int[][] optimizePath(int[][] path, int mask)
    {
        List<Object> optPath = null;
        int oldDx, oldDy, i, j;
        int newPath[][] = null;
        if (path == null)
        {
            return null;
        }

        optPath = new ArrayList<Object>();
        oldDx = oldDy = i = j = 0;

        // ����ͬһֱ���ϵ�·�㣬ֻ�������˵�
        for (i = 0; i < path.length - 1; i++)
        {
            int dx, dy;
            
            dx = path[i][0] - path[i + 1][0];
            dy = path[i][1] - path[i + 1][1];
            if(dx != oldDx || dy != oldDy){
                oldDx = dy;
                oldDy = dy;
                optPath.add(path[i]);
            }
        }

        optPath.add(path[path.length- 1]);

        // ����·���е�·�㣬���������б��·����û���ϰ����õ��м��ֱ�ǹյ�
        for (i = 0; i < optPath.size() - 2; ++i)
        {
            for (j = i + 2; j < optPath.size(); ++j)
            {
                int[] p1, p2;
                
                p1 = (int[]) optPath.get(i);
                p2 = (int[]) optPath.get(j);
                if (availablePath(p1[0], p1[1], p2[0], p2[1], mask))
                {
                    optPath.remove(i + 1);
                    --j;
                }
                else
                {
                    break;
                }
            }
        }

        // �����·��
        newPath = new int[optPath.size()][];
        for (i = 0; i < optPath.size(); i++)
        {
            int[] tempArr;
            
            tempArr = (int[]) optPath.get(i);
            newPath[i] = new int[tempArr.length];
            System.arraycopy(tempArr, 0, newPath[i], 0, newPath[i].length);
        }
        return newPath; 
    }
    
 // �������֮���Ƿ��ֱ��ͨ����
   public boolean availablePath(int x1, int y1, int x2, int y2, int mask)
    {
        // ����ʽֱ�߷���
        // y = (y2 - y1) * (x - x1) / (x2 - x1) + y1
        int minX, maxX, x, y, dy, dx;
        
        minX = Math.min(x1, x2);
        maxX = Math.max(x1, x2);
        dy = y2 - y1;
        dx = x2 - x1;

        for (x = minX; x < maxX; x++)
        {
            int atemp = 0;
            
            y = dy * (x - x1) / dx + y1;
            atemp = passable[y][x];
            if ((atemp & mask) == 0)
            {
                return false;
            }
        }

        return true;
    }
    /**
     * ����·����
     * @param fromx ��ʼ��x���꣨���أ�
     * @param fromy ��ʼ��y���꣨���أ�
     * @param tox Ŀ���x���꣨���أ�
     * @param toy Ŀ���y���꣨���أ�
     * @return ��Ŀ������·���ϵĵ�һ��Ŀ��㣨���أ���
     */
    public int[] findPath(int fromx, int fromy, int tox, int toy) {
        // ת��Ϊ������꣬�������ʼ���Ƿ�Ϸ���
        int fxg = fromx >> GRID_BITS;
        int fyg = fromy >> GRID_BITS;
        int txg = tox >> GRID_BITS;
        int tyg = toy >> GRID_BITS;
        if (fxg < 0 || fxg >= gridWidth || fyg < 0 || fyg >= gridHeight) {
            return new int[] { tox, toy };
        }
        
        // ȡ����ʼ����·����������
        int bindex = pathBufferIndex[fxg + fyg * gridWidth];
        if (bindex == -1) {
            // ���û�л������ݣ�����������������㱾����ͨ����������������Χȫ������ͨ��
            if (passable[fyg][fxg] == 0) {
                // �����ʼ�㲻��ͨ������ô�Ȱ����������Ա�һ����ͨ���㣬������ܶ�����ͨ���������ͼ��Ԥ��ѡ����һ����ͨ��������
                return new int[] { godX, godY };
            } else {
                // ���ȫ������ͨ������ôֱ�Ӷ���Ŀ����ȥ������
                return new int[] { tox, toy };
            }
        }
        bindex *= BUFFER_BLOCK_SIZE;
        
        // ���Ŀ�������ʼ�����Χ11x11�ķ�Χ�ڣ���ȡĿ���Ϊ���㣬����ȡĿ�귽������Զ��һ���ڷ�Χ�ڵĵ���ΪĿ���
        int xoff = txg - fxg;
        int yoff = tyg - fyg;
        if (xoff > BUFFER_RANGE || xoff < -BUFFER_RANGE || yoff > BUFFER_RANGE || yoff < -BUFFER_RANGE) {
            int edge;       // �ж�Ŀ��㷽��0 - 3�ֱ��Ƕ�������
            if (xoff > 0) {
                if (yoff > 0) {
                    if (xoff > yoff) {
                        edge = 0;
                    } else {
                        edge = 1;
                    }
                } else {
                    if (xoff > -yoff) {
                        edge = 0;
                    } else {
                        edge = 3;
                    }
                }
            } else {
                if (yoff > 0) {
                    if (-xoff > yoff) {
                        edge = 2;
                    } else {
                        edge = 1;
                    }
                } else {
                    if (-xoff > -yoff) {
                        edge = 2;
                    } else {
                        edge = 3;
                    }
                }
            }
            switch (edge) {
                case 0:
                    yoff = yoff * BUFFER_RANGE / xoff;
                    xoff = BUFFER_RANGE;
                    break;
                case 1:
                    xoff = xoff * BUFFER_RANGE / yoff;
                    yoff = BUFFER_RANGE;
                    break;
                case 2:
                    yoff = yoff * BUFFER_RANGE / (-xoff);
                    xoff = -BUFFER_RANGE;
                    break;
                case 3:
                    xoff = xoff * BUFFER_RANGE / (-yoff);
                    yoff = -BUFFER_RANGE;
                    break;
            }
        }
        
        // ���Ŀ����㲻��ͨ�������л�Ŀ���ΪĿ��㸽������Ŀ�ͨ����
        int bindex2 = xoff + BUFFER_RANGE + (yoff + BUFFER_RANGE) * BUFFER_MATRIX_SIZE;
        if (pathBuffer[bindex + bindex2] < 0) {
            bindex2 = -(pathBuffer[bindex + bindex2] + 1);
        }
        
        // �����б�����Ŀ����㵽��ʼ��֮�����·���ĵ�һ����ֱ�ӷ��ؾͿ�����
        byte targetCell = pathBuffer[bindex + bindex2];
        int targetX = (targetCell % BUFFER_MATRIX_SIZE) + fxg - BUFFER_RANGE;
        int targetY = (targetCell / BUFFER_MATRIX_SIZE) + fyg - BUFFER_RANGE;
        return new int[] { targetX * GRID_SIZE + GRID_SIZE / 2, targetY * GRID_SIZE + GRID_SIZE / 2 };
    }
    
    /**
     * �ж�һ������Χ��ͨ���ĸ�����
     * @param x ��������
     * @param y ��������
     * @return 0��ʾ�˵㲻��ͨ�>0��ʾ��Χ��ͨ���ĸ������������Լ���
     */
    private byte getPassLevel(int x, int y) {
        try {
            for (byte[][] rinfo : reachable2) {
                if (rinfo[y / GRID_SIZE][x / GRID_SIZE] > 0) {
                    return rinfo[y / GRID_SIZE][x / GRID_SIZE];
                }
            }
        } catch (Exception e) {
        }
        return 0;
    }
    
    private static int[][] SEARCH_GRIDS1 = {
        { -16, -16 },
        { 0, -16 },
        { 16, -16 },
        { -16, 0 },
        { 16, 0 },
        { -16, 16 },
        { 0, 16 },
        { 16, 16 },
    };
    private static int[][] SEARCH_GRIDS2 = {
        { -32, -32 }, 
        { -16, -32 },
        { 0, -32 },
        { 16, -32 },
        { 32, -32 },
        { -32, -16 }, 
        { 32, -16 },
        { -32, 0 }, 
        { 32, 0 },
        { -32, 16 }, 
        { 32, 16 },
        { -32, 32 }, 
        { -16, 32 },
        { 0, 32 },
        { 16, 32 },
        { 32, 32 },
    };
    
    /**
     * ���Դӵ�ǰ�����뿨���������Χ��һ�����Ӳ����ڿ���״̬�������ƶ���������򣩡�
     * �����Χ2Ȧ�ڵĸ��Ӷ������ƶ����򷵻�null��
     * @param x ��ʼλ��
     * @param y ����λ��
     * @return �����Χ2Ȧ�ڵ�ĳ�����Ӳ��������򷵻��Ǹ����ӵ��м�λ�ã����򷵻�null
     */
    public int[] tryOutPrison(int x, int y) {
        int[] ret = tryOutPrison(x, y, SEARCH_GRIDS1);
        if (ret == null) {
            ret = tryOutPrison(x, y, SEARCH_GRIDS2);
        }
        return ret;
    }
    
    /*
     * ��ָ������Χλ�����������ߵ�λ�á�
     */
    private int[] tryOutPrison(int x, int y, int[][] search) {
        int t = search.length;
        int maxPassLevel = 0;
        int maxPassIndex = -1;
        for (int i = 0; i < t; i++) {
            int tx = x + search[i][0];
            int ty = y + search[i][1];
            int lvl = getPassLevel(tx, ty);
            if (lvl > maxPassLevel) {
                maxPassIndex = i;
                maxPassLevel = lvl;
            }
        }
        if (maxPassIndex != -1 && maxPassLevel >= 6) {
            int tx = x + search[maxPassIndex][0];
            int ty = y + search[maxPassIndex][1];
            tx = (tx / GRID_SIZE) * GRID_SIZE + GRID_SIZE / 2;
            ty = (ty / GRID_SIZE) * GRID_SIZE + GRID_SIZE / 2;
            return new int[] { tx, ty };
        } else {
            return null;
        }
    }
    
    /**
     * �жϴ�ָ��������ܷ񵽴�ָ�����ڣ�16x16���ȣ���
     * @param x ��������
     * @param y ��������
     * @param exitIndex �����ڳ��������г����б��е�����
     * @return
     */
    public boolean canReach(int x, int y, int exitIndex) {
        try {
            byte[][] rinfo = reachable.get(exitIndex);
            if (rinfo[y / GRID_SIZE][x / GRID_SIZE] == 1) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }
    
    /**
    * ��鳡����ĳһ���Ƿ�ɵ����������ڣ�4x4���ȣ���
    * @param x ��������
    * @param y ��������
    * @return
    */
   public boolean canReach(int x, int y) {
       try {
           int gy = (y << 2) / GRID_SIZE;
           int gx = (x << 2) / GRID_SIZE;
           return reachable3[gy][gx] == 1;
       } catch (Exception e) {
       }
       return false;
   }
}
