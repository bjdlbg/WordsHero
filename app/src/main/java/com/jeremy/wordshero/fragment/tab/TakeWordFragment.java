package com.jeremy.wordshero.fragment.tab;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.jeremy.wordshero.R;
import com.jeremy.wordshero.Util.FileUtil;
import com.jeremy.wordshero.activity.MainActivity;
import com.jeremy.wordshero.activity.SelectWordsActivity;
import com.jeremy.wordshero.service.RecognizeService;

import java.util.ArrayList;
import java.util.List;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

/**
 * @author jixiang
 * @date 2019/3/31
 */
public class TakeWordFragment extends Fragment {

    //判断是否授权
    private boolean hasGotToken = false;
    private MainActivity mActivity;
    private FloatingTextButton openCamera;
    private AlertDialog.Builder alertDialog;
    private List<String> wordsList;
    private StringBuilder wordTextSb;
    private View rootView;
    private ProgressBar progressBar;
    private RelativeLayout relativeLayout;
    private ImageView appLogo;

    private static final String TAG = "TakeWordFragment";
    private static final int REQUEST_CODE_GENERAL_BASIC = 106;
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL = 457;

    /**
     * 当fragment与activity建立联系时执行此函数
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.fragment_take_words, container, false);
        initView(rootView);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alertDialog = new AlertDialog.Builder(mActivity);
        wordsList = new ArrayList<>();

        //根据我在百度开发者平台上建立的文字识别应用的license,初始化OCR。
        //此license文件位于main/assets文件夹
        initAccessTokenLicenseFile();

        //向用户请求权限
        if (ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.
                PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL);
        }
    }

    /**
     * 初始化控件
     *
     * @param v
     */
    public void initView(View v) {
        openCamera = (FloatingTextButton) v.findViewById(R.id.take_words_bt);
        progressBar=(ProgressBar)v.findViewById(R.id.progress);
        relativeLayout=(RelativeLayout)v.findViewById(R.id.take_words_fragment_layout) ;
        appLogo = (ImageView)v.findViewById(R.id.app_logo);
        appLogo.setVisibility(View.VISIBLE);
        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("cyyyyd", "i am onclicked open camera");
                openCameraForResult();
            }
        });


    }

    /**
     * 自定义license的文件路径和文件名称，以license文件方式初始化
     */
    private void initAccessTokenLicenseFile() {
        OCR.getInstance(mActivity.getApplicationContext()).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {
                String token = accessToken.getAccessToken();
                Log.d(TAG, token);
                hasGotToken = true;
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                alertText("自定义文件路径licence方式获取token失败", error.getMessage());
            }
        }, "aip.license", mActivity.getApplicationContext());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(mActivity, getString(R.string.no_permission), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }


    /**
     * 打开相机，进入的相机页面是借用百度OCR 官方DEMO中的相机页面
     * 能够在相机中裁剪图片，和进入图库
     *
     * @author cyd
     */
    private void openCameraForResult() {
        if (!checkTokenStatus()) {
            return;
        }
        Intent intent = new Intent(mActivity, CameraActivity.class);
        intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                FileUtil.getSaveFile(getActivity()).getAbsolutePath());
        intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_GENERAL);
        startActivityForResult(intent, REQUEST_CODE_GENERAL_BASIC);
    }


    /**
     * 1，从CameraActivity得到图片后返回
     * 2，用RecognizeService.recAccurateBasic(高精度版)对图片进行文字识别。
     * 3，返回json格式的结果 result
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * @author cyd
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_GENERAL_BASIC:
                if (resultCode == Activity.RESULT_OK) {
                    appLogo.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    relativeLayout.setBackgroundColor(Color.WHITE);
                    if(progressBar.isShown()){
                        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                    RecognizeService.recAccurateBasic(mActivity, FileUtil.getSaveFile(mActivity.getApplicationContext()).getAbsolutePath(),
                            new RecognizeService.ServiceListener() {
                                @Override
                                public void onResult(String result) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("wordResultJson", result);
                                    Intent intent = new Intent(mActivity, SelectWordsActivity.class);
                                    intent.putExtra("wordResultBundle", bundle);
                                    startActivity(intent);
                                    progressBar.setVisibility(View.GONE);
                                    relativeLayout.setBackgroundColor(Color.WHITE);
                                }
                            });
                }

                break;
            default:
                Log.d(TAG, "onActivityResult: " + "run in default");
                break;
        }
    }

    private void infoPopText(final String result) {
        alertText("", result);
    }

    private void alertText(final String title, final String message) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("确定", null)
                        .show();
            }
        });
    }

    private boolean checkTokenStatus() {
        if (!hasGotToken) {
            Toast.makeText(getActivity(), "token还未成功获取", Toast.LENGTH_LONG).show();
        }
        return hasGotToken;
    }

}
