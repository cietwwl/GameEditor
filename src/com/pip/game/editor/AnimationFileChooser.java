package com.pip.game.editor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.pip.game.data.Animation;
import com.pip.game.data.AnimationFormat;
import com.pip.game.editor.util.AnimateHeadIconPreviewer;
import com.pip.game.editor.util.AnimatePreviewer;
import com.pip.util.EFSUtil;
import com.pip.util.Utils;
import com.pipimage.image.PipAnimateSet;
import com.pipimage.image.PipImage;

public class AnimationFileChooser extends Composite {
    private AnimationEditor owner;
    private int format;
    
    private Text textAttackAnimateFile;
    private Text textAnimateFile;
    private AnimatePreviewer previewer;
    private AnimatePreviewer previewerAttack;
    private AnimateHeadIconPreviewer headIconPrev;

    /**
     * Create the composite
     * @param parent
     * @param style
     */
    public AnimationFileChooser(Composite parent, AnimationEditor owner, int format) {
        super(parent, SWT.NONE);
        this.owner = owner;
        this.format = format;
        
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 6;
        setLayout(gridLayout);

        final Label label = new Label(this, SWT.NONE);
        label.setText("动画文件：");

        textAnimateFile = new Text(this, SWT.BORDER);
        textAnimateFile.setEditable(false);
        final GridData gd_textAnimateFile = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textAnimateFile.setLayoutData(gd_textAnimateFile);

        final Button buttonImport = new Button(this, SWT.NONE);
        buttonImport.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                onBrowse();
            }
        });
        buttonImport.setText("导入...");

        final Label label_1 = new Label(this, SWT.NONE);
        label_1.setText("攻击动画：");

        textAttackAnimateFile = new Text(this, SWT.BORDER);
        textAttackAnimateFile.setEditable(false);
        final GridData gd_textAttackAnimateFile = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textAttackAnimateFile.setLayoutData(gd_textAttackAnimateFile);

        final Button buttonImportAttack = new Button(this, SWT.NONE);
        buttonImportAttack.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                onBrowseAttack();
            }
        });
        buttonImportAttack.setText("导入...");
        
        // 自动放大功能
        Animation aniObj = (Animation)owner.getEditObject();
        AnimationFormat af0 = aniObj.owner.config.animationFormats.get(0);
        AnimationFormat af = aniObj.owner.config.animationFormats.get(format);
        if (af0.scale == 1.0 && af.scale == 2.0) {
            final Button buttonEnlarge = new Button(this, SWT.NONE);
            buttonEnlarge.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 6, 1));
            buttonEnlarge.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(final SelectionEvent e) {
                    onEnlarge();
                }
            });
            buttonEnlarge.setText("自动放大...");
        }

        final Label label_2 = new Label(this, SWT.NONE);
        label_2.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 6, 1));
        label_2.setText("预览：");

        final Composite composite = new Composite(this, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 6, 1));
        final GridLayout gridLayout_1 = new GridLayout();
        gridLayout_1.numColumns = 2;
        composite.setLayout(gridLayout_1);

        previewer = new AnimatePreviewer(composite, SWT.NONE);
        final GridData gd_previewer = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        previewer.setLayoutData(gd_previewer);
        
        headIconPrev = new AnimateHeadIconPreviewer(composite, owner, format);
        final GridData gd_headpreviewer = new GridData(SWT.FILL, SWT.FILL, false, true);
        gd_headpreviewer.widthHint = 200;
        headIconPrev.setLayoutData(gd_headpreviewer);
        
        final Label label_3 = new Label(this, SWT.NONE);
        label_3.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 6, 1));
        label_3.setText("攻击动画预览：");

        previewerAttack = new AnimatePreviewer(this, SWT.NONE);
        final GridData gd_previewerAttack = new GridData(SWT.FILL, SWT.FILL, true, true, 6, 1);
        previewerAttack.setLayoutData(gd_previewerAttack);
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }
    
    public void setupFile() {
        Animation aniObj = (Animation)owner.getEditObject();
        previewer.setAnimateFile(aniObj.getAnimateFile(format));
        headIconPrev.setupFile();
        previewerAttack.setAnimateFile(aniObj.getAttackAnimateFile(format));
        textAnimateFile.setText(aniObj.animateFiles[format] == null ? "<未导入>" : aniObj.animateFiles[format]);
        textAttackAnimateFile.setText(aniObj.attackAnimateFiles[format] == null ? "<未导入>" : aniObj.attackAnimateFiles[format]);
    }
    
    public void setupHeadArea() {
        headIconPrev.setupHeadArea();
    }
    
    // 显示对话框选择行走动画文件。
    private void onBrowse() {
        FileDialog dlg = new FileDialog(getShell(), SWT.OPEN);
        dlg.setFilterExtensions(new String[] { "*.cts", "*.*" });
        dlg.setFilterNames(new String[] { "动画文件(*.cts)", "所有文件(*.*)" });
        Animation dataDef = (Animation)owner.getEditObject();
        dlg.setFilterPath(dataDef.getAnimateDir(format).getAbsolutePath());
        String file = dlg.open();
        if (file != null) {
            updateFile(file);
        }
    }
    
    private void updateFile(String file) {
        // 把动画文件和相关的ctn文件和pip文件都拷贝到项目目录中
        Animation dataDef = (Animation)owner.getEditObject();
        File newFile;
        try {
            newFile = copyAnimateFile(new File(file), dataDef.getAnimateDir(format), false);
        } catch (Exception e) {
            MessageDialog.openError(getShell(), "错误", e.toString());
            e.printStackTrace();
            return;
        }
        dataDef.setAnimateFile(format, newFile);
        textAnimateFile.setText(newFile.getName());
        previewer.setAnimateFile(newFile);
        headIconPrev.setupFile();
        owner.setDirty(true);
    }
    
    // 显示对话框选择攻击动画文件。
    private void onBrowseAttack() {
        FileDialog dlg = new FileDialog(getShell(), SWT.OPEN);
        dlg.setFilterExtensions(new String[] { "*.cts", "*.*" });
        dlg.setFilterNames(new String[] { "动画文件(*.cts)", "所有文件(*.*)" });
        Animation dataDef = (Animation)owner.getEditObject();
        dlg.setFilterPath(dataDef.getAnimateDir(format).getAbsolutePath());
        String file = dlg.open();
        if (file != null) {
            updateAttackFile(file);
        }
    }
    
    private void updateAttackFile(String file) {
        // 把动画文件和相关的ctn文件和pip文件都拷贝到项目目录中
        Animation dataDef = (Animation)owner.getEditObject();
        File newFile;
        try {
            newFile = copyAnimateFile(new File(file), dataDef.getAnimateDir(format), false);
        } catch (Exception e) {
            MessageDialog.openError(getShell(), "错误", e.toString());
            return;
        }
        dataDef.setAttackAnimateFile(format, newFile);
        textAttackAnimateFile.setText(newFile.getName());
        previewerAttack.setAnimateFile(newFile);
        owner.setDirty(true);
    }
    
    // 把动画文件和相关的ctn文件和pip文件都拷贝到Animations目录中
    private File copyAnimateFile(File f, File targetDir, boolean keepName) throws Exception {
        // 尝试载入动画文件
        PipAnimateSet tempAnimate = new PipAnimateSet();
        tempAnimate.load(f);
        
        // 如果动画文件已经在Animations目录里，则不用拷贝了
        if (targetDir.equals(f.getParentFile())) {
            return f;
        }
        
        // 载入已有的所有pip图片内容
        List<File> existImageFiles = new ArrayList<File>();
        List<PipImage> existImages = new ArrayList<PipImage>();
        File[] fs = targetDir.listFiles();
        for (File ff : fs) {
            if (ff.isFile() && ff.getName().toLowerCase().endsWith(".pip")) {
                PipImage img = new PipImage();
                try {
                    img.load(ff.getAbsolutePath());
                    if (Character.isDigit(ff.getName().charAt(0))) {
                        existImageFiles.add(0, ff);
                        existImages.add(0, img);
                    } else {
                        existImageFiles.add(ff);
                        existImages.add(img);
                    }
                } catch (Exception e) {
                }
            }
        }
        
        // 拷贝所有pip文件，并按规则重命名
        int objID = owner.getEditObject().id;
        HashMap<String, String> fileNameMap = new HashMap<String, String>();
        int fc = tempAnimate.getFileCount();
        List<File[]> needCopyFiles = new ArrayList<File[]>();   // 延迟拷贝
        Set<String> usedNames = new HashSet<String>();
        for (int i = 0; i < fc; i++) {
            String name = tempAnimate.getFileName(i);
            if (fileNameMap.containsKey(name)) {
                continue;
            }
            
            // 载入需要导入的PIP文件，和已有的所有图片进行比较
            File src = tempAnimate.getSourceFile(i);
            PipImage srcImg = new PipImage();
            srcImg.load(src.getAbsolutePath());
            ArrayList<String> matchFileNames = new ArrayList<String>();
            ArrayList<Integer> matchFileIndices = new ArrayList<Integer>();
            ArrayList<Double> matchFileRates = new ArrayList<Double>();
            int exactMatch = -1;
            for (int j = existImages.size() - 1; j >= 0; j--) {
                double rate = srcImg.compare(existImages.get(j));
                if (rate > 0.9999) {
                    exactMatch = j;
                    break;
                } else if (rate > 0.95) {
                    matchFileNames.add(existImageFiles.get(j).getName());
                    matchFileIndices.add(j);
                    matchFileRates.add(rate);
                } else if (existImageFiles.get(j).getName().equals(name)) {
                    matchFileNames.add(existImageFiles.get(j).getName());
                    matchFileIndices.add(j);
                    matchFileRates.add(rate);
                }
            }
            int matchIndex = -1;
            if (exactMatch != -1) {
                matchIndex = exactMatch;
            } else if (matchFileNames.size() > 0) {
                // 询问是否使用旧文件
                ChooseMatchFileDialog dlg = new ChooseMatchFileDialog(getShell());
                dlg.newFileName = src.getName();
                dlg.matchFileNames = new String[matchFileNames.size()];
                matchFileNames.toArray(dlg.matchFileNames);
                dlg.matchRate = new double[matchFileNames.size()];
                for (int j = 0; j < matchFileNames.size(); j++) {
                    dlg.matchRate[j] = matchFileRates.get(j);
                }
                if (dlg.open() == Dialog.OK) {
                    if (dlg.chosenIndex != -1) {
                        matchIndex = matchFileIndices.get(dlg.chosenIndex);
                    }
                } else {
                    throw new Exception("操作已取消");
                }
            }
            
            // 如果有相同文件，则使用旧文件，否则创建新文件
            if (matchIndex == -1) {
                int index = 0;
                String newName = src.getName();
                if (!keepName) {
                    newName = objID + "_" + index + ".pip";
                }
                while (new File(targetDir, newName).exists() || usedNames.contains(newName)) {
                    index++;
                    newName = objID + "_" + index + ".pip";
                }
                needCopyFiles.add(new File[] { src, new File(targetDir, newName) });
                usedNames.add(newName);
                fileNameMap.put(name, newName);
            } else {
                fileNameMap.put(name, existImageFiles.get(matchIndex).getName());
            }
        }
        
        // 所有文件确认完毕，执行文件拷贝
        for (File[] arr : needCopyFiles) {
            EFSUtil.copyFile(arr[0], arr[1]);
        }

        // 修改引用文件表中的文件名
        for (int i = 0; i < tempAnimate.getFileCount(); i++) {
            tempAnimate.setFileName(i, fileNameMap.get(tempAnimate.getFileName(i)));
        }
        
        // 保存修改后的CTS文件和CTN文件
        int index = 0;
        String newName = objID + ".cts";
        while (new File(targetDir, newName).exists()) {
            index++;
            newName = objID + "_" + index + ".cts";
        }
        tempAnimate.save(new File(targetDir, newName), true);
        tempAnimate.save(new File(targetDir, newName.substring(0, newName.length() - 1) + "n"), false);
        return new File(targetDir, newName);
    }
    
    // 放大小版本图片到大版本
    private void onEnlarge() {
        try {
            Animation dataDef = (Animation)owner.getEditObject();
            if (dataDef.getAnimateFile(0) != null) {
                File aniFile = enlargeOneAnimation(dataDef.getAnimateFile(0));
                updateFile(aniFile.getAbsolutePath());
                Utils.deleteDir(aniFile.getParentFile());
            }
            if (dataDef.getAttackAnimateFile(0) != null) {
                File aniFile = enlargeOneAnimation(dataDef.getAttackAnimateFile(0));
                updateAttackFile(aniFile.getAbsolutePath());
                Utils.deleteDir(aniFile.getParentFile());
            }
        } catch (IOException e) {
            MessageDialog.openError(getShell(), "错误", e.toString());
        }
    }
    
    private File enlargeOneAnimation(File src) throws IOException {
        // 先把小版本的动画文件拷贝到临时目录
        File tmpDir = File.createTempFile("_ani", ".temp");
        tmpDir.delete();
        tmpDir.mkdirs();
        PipAnimateSet ani = new PipAnimateSet();
        ani.load(src);
        ani.enlarge(true);
        ani.setOriginalFile(new File(tmpDir, ani.getOriginalFile().getName()));
        ani.save(ani.getOriginalFile(), true);
        
        return ani.getOriginalFile();
    }
}

