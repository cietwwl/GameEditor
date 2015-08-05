package com.pip.game.data.map;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * ʱ��Σ���¼һ����ʼʱ���Լ�����ʱ��,֮��¼Сʱ�Լ�����,֧�ֿ����ʱ�䣬Ҳ���ǿ�ʼʱ����ڽ���ʱ��
 * @author Jeffrey
 *
 */


public class Period {
    public int startHour,endHour,startMinute,endMinute;
    public static final int DAY_START = 0; //ÿ��Ŀ�ʼʱ��(����Ϊ��λ)
    public static final int DAY_END = 24 * 60; //ÿ��Ľ���ʱ��(����Ϊ��λ)
    
    public Period(int startHour,int startMinute,int endHour,int endMinute){
        if (startHour < 0 || startHour > 23 || endHour < 0 || endHour > 23)
            throw new IllegalArgumentException();
        if(startMinute < 0 || startMinute > 59 || endMinute <0 || endMinute >59)
            throw new IllegalArgumentException();
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
    }
    
    public int getStartTimeMinute(){
        return startHour * 60 + startMinute;
    }
    
    public int getEndTimeMinute(){
        return endHour * 60 + endMinute;
    }
    
    public boolean in(Date time){
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        return in(cal);
    }
    
    public boolean in(Calendar time){
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int minu = time.get(Calendar.MINUTE);
        return in(hour,minu);

    }
    
    public boolean in(int hour,int minu){
        int start = startHour * 60 + startMinute;
        int end = endHour * 60 + endMinute;
        int v = hour * 60 + minu;
        if(start<=end){
            return v>=start&&v<=end;
        }else{
            return (v>=start&&v<=DAY_END)||(v>=DAY_START&&v<=end);
        }
    }
    
    public static Period[] parse(String s){
        if(s.length()==0)
            return new Period[0];
        String[] ss = s.split(",");
        if(ss.length%2 != 0) //����ɶ�
            throw new NumberFormatException();
        Period[] ret = new Period[ss.length/2];
        for(int i = 0;i < ss.length;i += 2){
            int start = Integer.parseInt(ss[i]);
            int end = Integer.parseInt(ss[i+1]);
            int[] startTime = getTime(start);
            int[] endTime = getTime(end);
            ret[i/2] = new Period(startTime[0],startTime[1],endTime[0],endTime[1]);
        }
        return ret;
    }
    
    public static int[] getTime(int v){
        int hour = v / 60;
        int minu = v % 60;
        if(hour < 0 || hour > 23)
            throw new NumberFormatException();
        if(minu < 0 || minu > 59)
            throw new NumberFormatException();
        return new int[]{hour,minu};
    }
    
    public String toString() {
        return (startHour * 60 + startMinute) + "," + (endHour * 60 + endMinute);
    }
    
    
    public static Calendar getNextTimeInPeriods(Calendar cal,List<Period> refreshPeriods){
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minu = cal.get(Calendar.MINUTE);
        int v = hour * 60 + minu;
        int min = Integer.MAX_VALUE; //����Period������Ŀ�ʼʱ��
        int min1 = Integer.MAX_VALUE;//����cal������cal�����ʱ��
        for(Period p:refreshPeriods){
            if(p.in(hour,minu)){
                return cal;
            }else{
                int m = p.getStartTimeMinute();
                if(m < min){
                    min = m;
                }
                if(m > v && m < min1){
                    min1 = m;
                }
            }
        }
        if(min1 != Integer.MAX_VALUE){
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, min1/60);
            c.set(Calendar.MINUTE, min1%60);
            return c;
        }
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, min/60);
        c.set(Calendar.MINUTE, min%60);
        return c;
    }
    
    public static String getString(Period[] periods){
        StringBuilder sb = new StringBuilder(200);
        for(int i=0;i<periods.length;i++){
            if(i !=0 ) sb.append(',');
            sb.append(periods[i].toString());
        }
        return sb.toString();
    }
}
