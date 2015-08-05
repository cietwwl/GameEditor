package com.pip.game.data;

/**
 * 用于监视项目数据的保存，以更新文件时间。
 * @author lighthu
 */
public interface IGameDataListener {
    void saveStart(Class cls);
    void saveEnd(Class cls);
}
