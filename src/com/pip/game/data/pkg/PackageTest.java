package com.pip.game.data.pkg;

import java.io.File;

public class PackageTest {
    public static void main(String[] args) throws Exception{
        String pkgFilePath = "E:/workspace/Xiyou-Editor1.0/data/Areas/1_1/client.pkg";
        testReadPkg(pkgFilePath);
    }

    private static void testReadPkg(String pkgFilePath) throws Exception {
        File pkgFile = new File(pkgFilePath);
        PackageFile packageFile = new PackageFile();
        packageFile.load(pkgFile);
        System.out.println(packageFile);
    }
}
