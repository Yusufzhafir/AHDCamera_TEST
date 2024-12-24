package com.quectel.multicamera.dialog;

//import android.ai.SystemAlg;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.quectel.multicamera.R;
import com.quectel.multicamera.utils.GUtilMain;
import com.quectel.multicamera.utils.PreviewParams;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class IdentityEntryDialog extends AlertDialog {
    private Context mContext;
    private Handler mHandler;
    private PreviewParams pParams ;
    private static String mPath;
    private int width, height;
    private final int MAX_UNIT_NUM = 8;
    private final String UNNAMED = "unnamed";
    public IdentityEntryDialog(Context context, int width, int height, Handler handler) {
        super(context);
        pParams = GUtilMain.getPreviewParams();
        mContext = context;
        mHandler = handler;
        this.width = width;
        this.height = height;
    }

    private LinearLayout[] layout = new LinearLayout[MAX_UNIT_NUM+1];
    private ImageView[] imageView = new ImageView[MAX_UNIT_NUM];
    private EditText[] editTexts = new EditText[MAX_UNIT_NUM];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = width;
        params.height = height;
        getWindow().setAttributes(params);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        setCancelable(false);
        setContentView(R.layout.identity_config);

        layout[0] = findViewById(R.id.layout1);
        layout[1] = findViewById(R.id.layout2);
        layout[2] = findViewById(R.id.layout3);
        layout[3] = findViewById(R.id.layout4);
        layout[4] = findViewById(R.id.layout5);
        layout[5] = findViewById(R.id.layout6);
        layout[6] = findViewById(R.id.layout7);
        layout[7] = findViewById(R.id.layout8);
        layout[8] = findViewById(R.id.layout_add);

        imageView[0] = findViewById(R.id.image1);
        imageView[1] = findViewById(R.id.image2);
        imageView[2] = findViewById(R.id.image3);
        imageView[3] = findViewById(R.id.image4);
        imageView[4] = findViewById(R.id.image5);
        imageView[5] = findViewById(R.id.image6);
        imageView[6] = findViewById(R.id.image7);
        imageView[7] = findViewById(R.id.image8);

        editTexts[0] = findViewById(R.id.image_text1);
        editTexts[1] = findViewById(R.id.image_text2);
        editTexts[2] = findViewById(R.id.image_text3);
        editTexts[3] = findViewById(R.id.image_text4);
        editTexts[4] = findViewById(R.id.image_text5);
        editTexts[5] = findViewById(R.id.image_text6);
        editTexts[6] = findViewById(R.id.image_text7);
        editTexts[7] = findViewById(R.id.image_text8);

        for (int i=0; i<MAX_UNIT_NUM; i++){
            layout[i].setOnLongClickListener(new LongClickEvent());
        }
        File file;
        String path;
        Bitmap bitmap;
        for (int i=0; i< pParams.getFaceNumber(); i++){
            path = mContext.getFilesDir().getAbsolutePath()+File.separator+pParams.getIdentityPictureName()+i;
            file = new File(path);
            if (file.exists()){
                bitmap = getLoacalBitmap(path);
                imageView[i].setImageBitmap(bitmap);
                editTexts[i].setText(pParams.getImageName(i));
                layout[i].setVisibility(View.VISIBLE);
                if (i == 7)
                    layout[8].setVisibility(View.GONE);
            }else {
                System.out.println("zyz --> file "+file.getName()+" is lost !!!");
            }
        }

        layout[8].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = mHandler.obtainMessage();
                msg.what = 29;
                mHandler.sendMessage(msg);
                IdentityEntryDialog.this.hide();
            }
        });

        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str;
                for (int i=0; i<pParams.getFaceNumber(); i++){
                    str = editTexts[i].getText().toString();
                    if (str.equals("")){
                        Toast.makeText(mContext, mContext.getString(R.string.identity_name_null_warn),Toast.LENGTH_SHORT).show();
                        return;
                    }
                    pParams.setImageName(i, str);
                }
                Message msg = mHandler.obtainMessage();
                msg.what = 31;
                mHandler.sendMessage(msg);
                IdentityEntryDialog.this.hide();
            }
        });
    }

    private class LongClickEvent implements View.OnLongClickListener{
        @Override
        public boolean onLongClick(View v) {
            if (v == layout[0]){
                showWarnDialog(0, pParams.getImageName(0));
            }else if (v == layout[1]){
                showWarnDialog(1, pParams.getImageName(1));
            }else if (v == layout[2]){
                showWarnDialog(2, pParams.getImageName(2));
            }else if (v == layout[3]){
                showWarnDialog(3, pParams.getImageName(3));
            }else if (v == layout[4]){
                showWarnDialog(4, pParams.getImageName(4));
            }else if (v == layout[5]){
                showWarnDialog(5, pParams.getImageName(5));
            }else if (v == layout[6]){
                showWarnDialog(6, pParams.getImageName(6));
            }else if (v == layout[7]){
                showWarnDialog(7, pParams.getImageName(7));
            }
//            System.out.println("zyz --> long click !!!");
            return true;
        }
    }

    private void showWarnDialog(final int num, String name){
        new Builder(mContext)
                .setTitle(mContext.getString(R.string.identity_attention_title))
                .setMessage(mContext.getString(R.string.identity_delete_conform)+" '"+name+"' ?")
                .setCancelable(false)
                .setNegativeButton(mContext.getString(R.string.cancel), null)
                .setPositiveButton(mContext.getString(R.string.ok), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        System.out.println("zyz --> ok !!!!!!");
                        //直接删除目标文件
                        File origin = new File(mContext.getFilesDir().getAbsolutePath()+File.separator+pParams.getIdentityPictureName()+num);
                        if (!origin.delete()){
                            Toast.makeText(mContext, pParams.getIdentityPictureName()+num+" "+mContext.getString(R.string.identity_delete_error), Toast.LENGTH_SHORT).show();
                        }

                        for (int i=num; i<pParams.getFaceNumber()-1; i++){
                            //文件操作
                            File file = new File(mContext.getFilesDir().getAbsolutePath()+File.separator+pParams.getIdentityPictureName()+(i+1));
                            String newPath = mContext.getFilesDir().getAbsolutePath()+File.separator+pParams.getIdentityPictureName()+i;
                            if (!file.renameTo(new File(newPath))){
                                Toast.makeText(mContext, pParams.getIdentityPictureName()+(i+1)+" "+mContext.getString(R.string.identity_rename_error),Toast.LENGTH_SHORT).show();
                            }
                            Bitmap bitmap = getLoacalBitmap(newPath);
                            imageView[i].setImageBitmap(bitmap);
                            pParams.setImageName(i, pParams.getImageName(i+1));
                            editTexts[i].setText(pParams.getImageName(i));
                        }
                        //删除算法中的特征点
//                        SystemAlg.deleteFaceFeature(num, pParams.getFaceNumber());

                        //后续考虑界面切换
                        layout[pParams.getFaceNumber()-1].setVisibility(View.GONE);
                        //自减1
                        pParams.setFaceNumber(pParams.getFaceNumber()-1);
                        //如果目标数为0，则设置未录入
                        pParams.setDMSIsFaceEntry(pParams.getFaceNumber()!=0);
                        layout[8].setVisibility(pParams.getFaceNumber()<=7?View.VISIBLE:View.GONE);


                    }
                }).show();
    }

    /**
     * 加载本地图片
     * @param url
     * @return
     */
    private Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setFaceInfo(){
        String path = mContext.getFilesDir().getAbsolutePath()+File.separator+pParams.getIdentityPictureName()+(pParams.getFaceNumber()-1);
        File file = new File(path);
        if (file.exists()){
            Bitmap bitmap = getLoacalBitmap(path);
            imageView[pParams.getFaceNumber()-1].setImageBitmap(bitmap);
            layout[pParams.getFaceNumber()-1].setVisibility(View.VISIBLE);
            editTexts[pParams.getFaceNumber()-1].setText(UNNAMED+pParams.getFaceNumber());
            pParams.setImageName(pParams.getFaceNumber()-1, UNNAMED+pParams.getFaceNumber());
            if (pParams.getFaceNumber() == MAX_UNIT_NUM){
                layout[8].setVisibility(View.GONE);
            }
        }
        pParams.setDMSIsFaceEntry(true);
    }
}
