package com.pip.game.data.i18n;

import java.util.ArrayList;
import java.util.List;

import com.pip.game.data.DataObject;

/**
 * 记录I18N过程中发生的错误。
 * @author lighthu
 */
public class I18NError {
    public DataObject source;
    public String message;
    public Throwable exception;
    
    public static List<I18NError> errorList = new ArrayList<I18NError>();
    
    public I18NError(DataObject s, String m, Throwable e) {
        source = s;
        message = m;
        exception = e;
    }
    
    public static void clear() {
        errorList.clear();
    }
    
    public static void error(DataObject s, String m, Throwable e) {
        errorList.add(new I18NError(s, m, e));
    }
    
    public static boolean hasError() {
        return errorList.size() != 0;
    }
}
