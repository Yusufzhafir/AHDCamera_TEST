package com.quectel.multicamera.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.quectel.multicamera.R;
import com.quectel.multicamera.utils.APKVersionCodeUtils;
import com.quectel.multicamera.utils.GUtilMain;
import com.quectel.multicamera.utils.LanguageType;
import com.quectel.multicamera.utils.PreviewParams;
import com.quectel.multicamera.utils.RecorderParams;
import com.quectel.multicamera.utils.SpUtil;
import com.quectel.qcarapi.QCarVersion;
import com.quectel.qcarapi.util.QCarLog;

import java.util.ArrayList;
import java.util.List;

public class SettingsDialog extends AlertDialog {
    private String TAG = "SettingsDialog";
    private Spinner preNumSpinner;
    private Spinner langSelect;
    private Spinner displayStyle;
    private Spinner cameraTypeSpinner1, cameraTypeSpinner2, cameraTypeSpinner3, cameraTypeSpinner4, cameraTypeSpinner5, cameraTypeSpinner6;
    private Spinner preResolutionSpinner1, preResolutionSpinner2, preResolutionSpinner3, preResolutionSpinner4, preResolutionSpinner5, preResolutionSpinner6;
    private Spinner videoResolutionSpinner1, videoResolutionSpinner2, videoResolutionSpinner3, videoResolutionSpinner4, videoResolutionSpinner5, videoResolutionSpinner6;

    private CheckBox recordChannel1, recordChannel2, recordChannel3, recordChannel4, recordChannel5, recordChannel6;

    private LinearLayout display_style, linearLayoutChannel1, linearLayoutChannel2, linearLayoutChannel3, linearLayoutChannel4, linearLayoutChannel5, linearLayoutChannel6;
    private LinearLayout rChannel1, rChannel2, rChannel3, rChannel4, rChannel5, rChannel6;

    private Switch switch_mir;
    private Switch switch_record;

    //Video encoder
    private Spinner childRecorderSizeSpinner;
    private Spinner recorderSSP;  //分段大小选项
    private Spinner codecSpinner; //编码格式选项
    private Spinner containerSpinner; //录像格式
    private Spinner mainstreamerSpinner; //主码率
    private Spinner substreamerSpinner; //次码率

    private Switch switch_cr; //是否开启录像子码流
    private Switch switch_audio; //是否开启录像音频

    private Switch debugEnableSwitch;

    private Button mCancel = null;
    private Button mOk = null;

    private List<Integer> noList;
    private ArrayAdapter<Integer> noAdapter;

    private RecorderParams rParams = GUtilMain.getRecorderParams();
    private PreviewParams pParams;

    private Context mContext = null;
    private Handler mHandler;

    public SettingsDialog(Context context, int width, int height, Handler handler) {
        super(context);
        mContext = context;
        this.width = width;
        this.height = height;
        this.mHandler = handler;
    }

    private int width = 0;
    private int height = 0;

    private TextView Text1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = width;
        params.height = height;
        getWindow().setAttributes(params);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        setCancelable(false);

        pParams = GUtilMain.getPreviewParams();
        String sdkVersion = QCarVersion.qcarGetVersion(); // query the sdk version number
        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "sdkVersion = " + sdkVersion);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.dialog_set);

        recordChannel1 = findViewById(R.id.recordChannel1);
        recordChannel2 = findViewById(R.id.recordChannel2);
        recordChannel3 = findViewById(R.id.recordChannel3);
        recordChannel4 = findViewById(R.id.recordChannel4);
        recordChannel5 = findViewById(R.id.recordChannel5);
        recordChannel6 = findViewById(R.id.recordChannel6);

        Text1 = findViewById(R.id.Text1);
        display_style = findViewById(R.id.display_style);
        linearLayoutChannel1 = findViewById(R.id.channel1);
        linearLayoutChannel2 = findViewById(R.id.channel2);
        linearLayoutChannel3 = findViewById(R.id.channel3);
        linearLayoutChannel4 = findViewById(R.id.channel4);
        linearLayoutChannel5 = findViewById(R.id.channel5);
        linearLayoutChannel6 = findViewById(R.id.channel6);

        rChannel1 = findViewById(R.id.rChannel1);
        rChannel2 = findViewById(R.id.rChannel2);
        rChannel3 = findViewById(R.id.rChannel3);
        rChannel4 = findViewById(R.id.rChannel4);
        rChannel5 = findViewById(R.id.rChannel5);
        rChannel6 = findViewById(R.id.rChannel6);
        if (!pParams.isN41Exist())
            rChannel4.setVisibility(View.GONE);

        Text1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!pParams.isN41Exist())
                    return true;
                display_style.setVisibility(View.VISIBLE);
                return true;
            }
        });

        TextView version = (TextView) findViewById(R.id.version);
        version.setText("V " + APKVersionCodeUtils.getVerName(mContext));

        switch_mir = (Switch) findViewById(R.id.video_mir_switch);
        switch_mir.setChecked(rParams.getVideoMirror());
        switch_mir.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rParams.setVidoeMirror(isChecked);
            }
        });

        switch_record = (Switch) findViewById(R.id.record_video_switch);
        switch_record.setChecked(rParams.getRecordState());
        switch_record.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rParams.setRecordState(isChecked);
            }
        });

        displayStyle = (Spinner) findViewById(R.id.spinner0_1);
        displayStyle.setSelection(pParams.getDisplayStyle());
        displayStyle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pParams.setDisplayStyle(position);
                if (position == 0) {
                    rChannel5.setVisibility(View.GONE);
                    rChannel6.setVisibility(View.GONE);
                } else {
                    rChannel5.setVisibility(View.VISIBLE);
                    rChannel6.setVisibility(View.VISIBLE);
                }
                pParams.setCsiNum(position == 0 ? 0 : 2);
                rParams.setCsiNum(position == 0 ? 0 : 2);
                setPreviewVideoByCsiNum(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        langSelect = findViewById(R.id.spinner0);
        langSelect.setSelection(SpUtil.getInstance(mContext).getString(SpUtil.LANGUAGE).equals("ch") ? 1 : 0, true);
        langSelect.post(new Runnable() {
            @Override
            public void run() {
                langSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String language;
                        if (position == 0) {
                            //切换为英语
                            language = LanguageType.ENGLISH.getLanguage();
                        } else {
                            //切换为中文
                            language = LanguageType.CHINESE.getLanguage();
                        }
                        SpUtil.getInstance(mContext).putString(SpUtil.LANGUAGE, language);
                        SpUtil.getInstance(mContext).setChange(true);

                        Message msg = mHandler.obtainMessage();
                        msg.what = 0;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });

        mCancel = findViewById(R.id.cancel);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsDialog.this.hide();
            }
        });

        mOk = findViewById(R.id.ok);
        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = mHandler.obtainMessage();
                msg.what = 0;
                mHandler.sendMessage(msg);

            }
        });

        cameraTypeSpinner1 = findViewById(R.id.cameraTypeSpinner1);
        cameraTypeSpinner2 = findViewById(R.id.cameraTypeSpinner2);
        cameraTypeSpinner3 = findViewById(R.id.cameraTypeSpinner3);
        cameraTypeSpinner4 = findViewById(R.id.cameraTypeSpinner4);
        cameraTypeSpinner5 = findViewById(R.id.cameraTypeSpinner5);
        cameraTypeSpinner6 = findViewById(R.id.cameraTypeSpinner6);

        cameraTypeSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pParams.setCameraType(position, 1);
                if (preResolutionSpinner1.getSelectedItemPosition() == 2) {
                    videoResolutionSpinner1.setSelection(position + 2);
                    rParams.setVsPosition(position, 1);
                    pParams.setPsPosition1(2);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cameraTypeSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pParams.setCameraType(position, 2);
                if (preResolutionSpinner2.getSelectedItemPosition() == 2) {
                    videoResolutionSpinner2.setSelection(position + 2);
                    rParams.setVsPosition(position, 2);
                    pParams.setPsPosition2(2);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cameraTypeSpinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int rpChannel = pParams.isN41Exist() ? (pParams.getDisplayStyle() == 0 ? 4 : 3) : 3;
                pParams.setCameraType(position, rpChannel);
                if (preResolutionSpinner3.getSelectedItemPosition() == 2) {
                    videoResolutionSpinner3.setSelection(position + 2);
                    rParams.setVsPosition(position, rpChannel);
                    if (rpChannel == 3) {
                        pParams.setPsPosition3(2);
                    } else {
                        pParams.setPsPosition4(2);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cameraTypeSpinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int rpChannel = pParams.isN41Exist() ? (pParams.getDisplayStyle() == 1 ? 4 : 5) : 4;
                pParams.setCameraType(position, rpChannel);
                if (preResolutionSpinner4.getSelectedItemPosition() == 2) {
                    videoResolutionSpinner4.setSelection(position + 2);
                    rParams.setVsPosition(position, rpChannel);
                    if (rpChannel == 4)
                        pParams.setPsPosition4(2);
                    else
                        pParams.setPsPosition5(2);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cameraTypeSpinner5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pParams.setCameraType(position, 5);
                if (preResolutionSpinner5.getSelectedItemPosition() == 2) {
                    videoResolutionSpinner5.setSelection(position + 2);
                    rParams.setVsPosition(position, 5);
                    pParams.setPsPosition5(2);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cameraTypeSpinner6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pParams.setCameraType(position, 6);
                if (preResolutionSpinner6.getSelectedItemPosition() == 2) {
                    videoResolutionSpinner6.setSelection(position + 2);
                    rParams.setVsPosition(position, 6);
                    pParams.setPsPosition6(2);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cameraTypeSpinner1.setSelection(pParams.getCameraType(1));
        cameraTypeSpinner2.setSelection(pParams.getCameraType(2));
        cameraTypeSpinner3.setSelection(pParams.getCameraType(pParams.isN41Exist() ? (pParams.getDisplayStyle() == 1 ? 3 : 4) : 3));
        cameraTypeSpinner4.setSelection(pParams.getCameraType(pParams.getDisplayStyle() == 1 ? 4 : 5));
        cameraTypeSpinner5.setSelection(pParams.getCameraType(5));
        cameraTypeSpinner6.setSelection(pParams.getCameraType(6));

        preResolutionSpinner1 = findViewById(R.id.spinnerChannel1);
        preResolutionSpinner2 = findViewById(R.id.spinnerChannel2);
        preResolutionSpinner3 = findViewById(R.id.spinnerChannel3);
        preResolutionSpinner4 = findViewById(R.id.spinnerChannel4);
        preResolutionSpinner5 = findViewById(R.id.spinnerChannel5);
        preResolutionSpinner6 = findViewById(R.id.spinnerChannel6);
        videoResolutionSpinner1 = findViewById(R.id.rSpinnerChannel1);
        videoResolutionSpinner2 = findViewById(R.id.rSpinnerChannel2);
        videoResolutionSpinner3 = findViewById(R.id.rSpinnerChannel3);
        videoResolutionSpinner4 = findViewById(R.id.rSpinnerChannel4);
        videoResolutionSpinner5 = findViewById(R.id.rSpinnerChannel5);
        videoResolutionSpinner6 = findViewById(R.id.rSpinnerChannel6);
        preNumSpinner = (Spinner) findViewById(R.id.spinner3);

        debugEnableSwitch = (Switch) findViewById(R.id.global_debug_switch);
        noList = new ArrayList<>();

        recordChannel1.setOnCheckedChangeListener(new OnCheckedChangeListener());
        recordChannel2.setOnCheckedChangeListener(new OnCheckedChangeListener());
        recordChannel3.setOnCheckedChangeListener(new OnCheckedChangeListener());
        recordChannel4.setOnCheckedChangeListener(new OnCheckedChangeListener());
        recordChannel5.setOnCheckedChangeListener(new OnCheckedChangeListener());
        recordChannel6.setOnCheckedChangeListener(new OnCheckedChangeListener());

        recordChannel1.setChecked(rParams.getRecordState(1));
        recordChannel2.setChecked(rParams.getRecordState(2));
        recordChannel3.setChecked(rParams.getRecordState(3));
        recordChannel4.setChecked(rParams.getRecordState(4));
        recordChannel5.setChecked(rParams.getRecordState(5));
        recordChannel6.setChecked(rParams.getRecordState(6));

        preResolutionSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "通道1，选择了=" + position);
//
//                if (position==0 && pParams.getCameraType(1)==1) {
//                    videoResolutionSpinner1.setSelection(1);
//                    rParams.setVsPosition(1, 1);
//                }else {
//                    videoResolutionSpinner1.setSelection(position);
//                    rParams.setVsPosition(position, 1);
//                }
                if (position == 2) {
                    if (cameraTypeSpinner1.getSelectedItemPosition() == 0) {
                        if (videoResolutionSpinner1.getSelectedItemPosition() != 2) {
                            videoResolutionSpinner1.setSelection(2);
                            rParams.setVsPosition(2, 1);
                        }
                    } else {
                        if (videoResolutionSpinner1.getSelectedItemPosition() != 3) {
                            videoResolutionSpinner1.setSelection(3);
                            rParams.setVsPosition(3, 1);
                        }
                    }
                } else if (position == 0) {
                    if (videoResolutionSpinner1.getSelectedItemPosition() != 0) {
                        videoResolutionSpinner1.setSelection(0);
                        rParams.setVsPosition(0, 1);
                    }
                } else if (position == 1) {
                    if (videoResolutionSpinner1.getSelectedItemPosition() == 2 || videoResolutionSpinner1.getSelectedItemPosition() == 3) {
                        videoResolutionSpinner1.setSelection(1);
                        rParams.setVsPosition(1, 1);
                    }
                }
//                recordChannel1.setChecked(false);
//                recordChannel2.setChecked(false);
//                recordChannel3.setChecked(false);
//                recordChannel4.setChecked(false);
//                recordChannel5.setChecked(false);
//                recordChannel6.setChecked(false);
                pParams.setPsPosition1(position);
                rParams.adjustResolutionWidthReValue(parent.getSelectedItem().toString(), 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        preResolutionSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "通道2，选择了=" + position);
                if (position == 2) {
                    if (cameraTypeSpinner2.getSelectedItemPosition() == 0) {
                        if (videoResolutionSpinner2.getSelectedItemPosition() != 2) {
                            videoResolutionSpinner2.setSelection(2);
                            rParams.setVsPosition(2, 2);
                        }
                    } else {
                        if (videoResolutionSpinner2.getSelectedItemPosition() != 3) {
                            videoResolutionSpinner2.setSelection(3);
                            rParams.setVsPosition(3, 2);
                        }
                    }
                } else if (position == 0) {
                    if (videoResolutionSpinner2.getSelectedItemPosition() != 0) {
                        videoResolutionSpinner2.setSelection(0);
                        rParams.setVsPosition(0, 2);
                    }
                } else if (position == 1) {
                    if (videoResolutionSpinner2.getSelectedItemPosition() == 2 || videoResolutionSpinner2.getSelectedItemPosition() == 3) {
                        videoResolutionSpinner2.setSelection(1);
                        rParams.setVsPosition(1, 2);
                    }
                }
//                recordChannel1.setChecked(false);
//                recordChannel2.setChecked(false);
//                recordChannel3.setChecked(false);
//                recordChannel4.setChecked(false);
//                recordChannel5.setChecked(false);
//                recordChannel6.setChecked(false);
                pParams.setPsPosition2(position);
                rParams.adjustResolutionWidthReValue(parent.getSelectedItem().toString(), 2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        preResolutionSpinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "通道3，选择了=" + position);
                int rpChannel = pParams.isN41Exist() ? (pParams.getDisplayStyle() == 1 ? 3 : 4) : 3;
                if (position == 2) {
                    if (cameraTypeSpinner3.getSelectedItemPosition() == 0) {
                        if (videoResolutionSpinner3.getSelectedItemPosition() != 2) {
                            videoResolutionSpinner3.setSelection(2);
                            rParams.setVsPosition(2, rpChannel);
                        }
                    } else {
                        if (videoResolutionSpinner3.getSelectedItemPosition() != 3) {
                            videoResolutionSpinner3.setSelection(3);
                            rParams.setVsPosition(3, rpChannel);
                        }
                    }
                } else if (position == 0) {
                    if (videoResolutionSpinner3.getSelectedItemPosition() != 0) {
                        videoResolutionSpinner3.setSelection(0);
                        rParams.setVsPosition(0, rpChannel);
                    }
                } else if (position == 1) {
                    if (videoResolutionSpinner3.getSelectedItemPosition() == 2 || videoResolutionSpinner3.getSelectedItemPosition() == 3) {
                        videoResolutionSpinner3.setSelection(1);
                        rParams.setVsPosition(1, rpChannel);
                    }
                }
//                recordChannel1.setChecked(false);
//                recordChannel2.setChecked(false);
//                recordChannel3.setChecked(false);
//                recordChannel4.setChecked(false);
//                recordChannel5.setChecked(false);
//                recordChannel6.setChecked(false);
                if (pParams.isN41Exist()) {
                    if (pParams.getDisplayStyle() == 0) {
                        pParams.setPsPosition4(position);
                    }
                } else {
                    pParams.setPsPosition3(position);
                }
//                pParams.setPsPosition3(position);
                rParams.adjustResolutionWidthReValue(parent.getSelectedItem().toString(), rpChannel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        preResolutionSpinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "通道4，选择了=" + position);
                int rpChannel = pParams.isN41Exist() ? (pParams.getDisplayStyle() == 1 ? 4 : 5) : 4;
                if (position == 2) {
                    if (cameraTypeSpinner4.getSelectedItemPosition() == 0) {
                        if (videoResolutionSpinner4.getSelectedItemPosition() != 2) {
                            videoResolutionSpinner4.setSelection(2);
                            rParams.setVsPosition(2, rpChannel);
                        }
                    } else {
                        if (videoResolutionSpinner4.getSelectedItemPosition() != 3) {
                            videoResolutionSpinner4.setSelection(3);
                            rParams.setVsPosition(3, rpChannel);
                        }
                    }
                } else if (position == 0) {
                    if (videoResolutionSpinner4.getSelectedItemPosition() != 0) {
                        videoResolutionSpinner4.setSelection(0);
                        rParams.setVsPosition(0, rpChannel);
                    }
                } else if (position == 1) {
                    if (videoResolutionSpinner4.getSelectedItemPosition() == 2 || videoResolutionSpinner4.getSelectedItemPosition() == 3) {
                        videoResolutionSpinner4.setSelection(1);
                        rParams.setVsPosition(1, rpChannel);
                    }
                }
//                recordChannel1.setChecked(false);
//                recordChannel2.setChecked(false);
//                recordChannel3.setChecked(false);
//                recordChannel4.setChecked(false);
//                recordChannel5.setChecked(false);
//                recordChannel6.setChecked(false);
                if (pParams.getDisplayStyle() == 0) {
                    pParams.setPsPosition5(position);
                } else {
                    pParams.setPsPosition4(position);
                }
                rParams.adjustResolutionWidthReValue(parent.getSelectedItem().toString(), rpChannel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        preResolutionSpinner5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "通道5，选择了=" + position);
                if (position == 2) {
                    if (cameraTypeSpinner5.getSelectedItemPosition() == 0) {
                        if (videoResolutionSpinner5.getSelectedItemPosition() != 2) {
                            videoResolutionSpinner5.setSelection(2);
                            rParams.setVsPosition(2, 5);
                        }
                    } else {
                        if (videoResolutionSpinner5.getSelectedItemPosition() != 3) {
                            videoResolutionSpinner5.setSelection(3);
                            rParams.setVsPosition(3, 5);
                        }
                    }
                } else if (position == 0) {
                    if (videoResolutionSpinner5.getSelectedItemPosition() != 0) {
                        videoResolutionSpinner5.setSelection(0);
                        rParams.setVsPosition(0, 5);
                    }
                } else if (position == 1) {
                    if (videoResolutionSpinner5.getSelectedItemPosition() == 2 || videoResolutionSpinner5.getSelectedItemPosition() == 3) {
                        videoResolutionSpinner5.setSelection(1);
                        rParams.setVsPosition(1, 5);
                    }
                }
//                recordChannel1.setChecked(false);
//                recordChannel2.setChecked(false);
//                recordChannel3.setChecked(false);
//                recordChannel4.setChecked(false);
//                recordChannel5.setChecked(false);
//                recordChannel6.setChecked(false);
                pParams.setPsPosition5(position);
                rParams.adjustResolutionWidthReValue(parent.getSelectedItem().toString(), 5);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        preResolutionSpinner6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "通道6，选择了=" + position);
                if (position == 2) {
                    if (cameraTypeSpinner6.getSelectedItemPosition() == 0) {
                        if (videoResolutionSpinner6.getSelectedItemPosition() != 2) {
                            videoResolutionSpinner6.setSelection(2);
                            rParams.setVsPosition(2, 6);
                        }
                    } else {
                        if (videoResolutionSpinner6.getSelectedItemPosition() != 3) {
                            videoResolutionSpinner6.setSelection(3);
                            rParams.setVsPosition(3, 6);
                        }
                    }
                } else if (position == 0) {
                    if (videoResolutionSpinner6.getSelectedItemPosition() != 0) {
                        videoResolutionSpinner6.setSelection(0);
                        rParams.setVsPosition(0, 6);
                    }
                } else if (position == 1) {
                    if (videoResolutionSpinner6.getSelectedItemPosition() == 2 || videoResolutionSpinner6.getSelectedItemPosition() == 3) {
                        videoResolutionSpinner6.setSelection(1);
                        rParams.setVsPosition(1, 6);
                    }
                }
//                recordChannel1.setChecked(false);
//                recordChannel2.setChecked(false);
//                recordChannel3.setChecked(false);
//                recordChannel4.setChecked(false);
//                recordChannel5.setChecked(false);
//                recordChannel6.setChecked(false);
                pParams.setPsPosition6(position);
                rParams.adjustResolutionWidthReValue(parent.getSelectedItem().toString(), 6);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        preResolutionSpinner1.setSelection(pParams.getPsPosition(1));
        preResolutionSpinner2.setSelection(pParams.getPsPosition(2));
        preResolutionSpinner3.setSelection(pParams.getPsPosition(pParams.isN41Exist() ? (pParams.getDisplayStyle() == 1 ? 3 : 4) : 3));
        preResolutionSpinner4.setSelection(pParams.getPsPosition(pParams.getDisplayStyle() == 1 ? 4 : 5));
        preResolutionSpinner5.setSelection(pParams.getPsPosition(5));
        preResolutionSpinner6.setSelection(pParams.getPsPosition(6));


        videoResolutionSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "Channel 1 选择了=" + position);
                if (preResolutionSpinner1.getSelectedItemPosition() == 2) {
                    if (cameraTypeSpinner1.getSelectedItemPosition() == 0) {
                        if (position != 2) {
                            videoResolutionSpinner1.setSelection(rParams.getVsPosition(1));
                            Toast.makeText(mContext, "CVBS PAL can only choose the resolution of CVBS PAL for recording!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        if (position != 3) {
                            videoResolutionSpinner1.setSelection(rParams.getVsPosition(1));
                            Toast.makeText(mContext, "CVBS NTSC can only choose the resolution of CVBS NTSC for recording!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                } else if (preResolutionSpinner1.getSelectedItemPosition() == 0) {
                    if (position != 0) {
                        videoResolutionSpinner1.setSelection(rParams.getVsPosition(1));
                        Toast.makeText(mContext, "720P can only choose the resolution of 720P for recording!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else if (preResolutionSpinner1.getSelectedItemPosition() == 1) {
                    if (position == 2 || position == 3) {
                        videoResolutionSpinner1.setSelection(rParams.getVsPosition(1));
                        Toast.makeText(mContext, "There are channels for 1080p recording, and recording can only record up to 4 channels!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
//                recordChannel1.setChecked(false);
//                recordChannel2.setChecked(false);
//                recordChannel3.setChecked(false);
//                recordChannel4.setChecked(false);
//                recordChannel5.setChecked(false);
//                recordChannel6.setChecked(false);
                rParams.setVsPosition(position, 1);
                rParams.adjustResolutionWidthReValue(parent.getSelectedItem().toString(), 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        videoResolutionSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "Channel 2 选择了=" + position);
                if (preResolutionSpinner2.getSelectedItemPosition() == 2) {
                    if (cameraTypeSpinner2.getSelectedItemPosition() == 0) {
                        if (position != 2) {
                            videoResolutionSpinner2.setSelection(rParams.getVsPosition(2));
                            Toast.makeText(mContext, "CVBS PAL can only choose the resolution of CVBS PAL for recording!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        if (position != 3) {
                            videoResolutionSpinner2.setSelection(rParams.getVsPosition(2));
                            Toast.makeText(mContext, "CVBS NTSC can only choose the resolution of CVBS NTSC for recording!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                } else if (preResolutionSpinner2.getSelectedItemPosition() == 0) {
                    if (position != 0) {
                        videoResolutionSpinner2.setSelection(rParams.getVsPosition(2));
                        Toast.makeText(mContext, "720P can only choose the resolution of 720P for recording!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else if (preResolutionSpinner2.getSelectedItemPosition() == 1) {
                    if (position == 2 || position == 3) {
                        videoResolutionSpinner2.setSelection(rParams.getVsPosition(2));
                        Toast.makeText(mContext, "There are channels for 1080p recording, and recording can only record up to 4 channels!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
//                recordChannel1.setChecked(false);
//                recordChannel2.setChecked(false);
//                recordChannel3.setChecked(false);
//                recordChannel4.setChecked(false);
//                recordChannel5.setChecked(false);
//                recordChannel6.setChecked(false);
                rParams.setVsPosition(position, 2);
                rParams.adjustResolutionWidthReValue(parent.getSelectedItem().toString(), 2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        videoResolutionSpinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "Channel 3 选择了=" + position);
                int rpChannel = pParams.isN41Exist() ? (pParams.getDisplayStyle() == 1 ? 3 : 4) : 3;
                if (preResolutionSpinner3.getSelectedItemPosition() == 2) {
                    if (cameraTypeSpinner3.getSelectedItemPosition() == 0) {
                        if (position != 2) {
                            videoResolutionSpinner3.setSelection(rParams.getVsPosition(rpChannel));
                            Toast.makeText(mContext, "CVBS PAL can only choose the resolution of CVBS PAL for recording!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        if (position != 3) {
                            videoResolutionSpinner3.setSelection(rParams.getVsPosition(rpChannel));
                            Toast.makeText(mContext, "CVBS NTSC can only choose the resolution of CVBS NTSC for recording!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                } else if (preResolutionSpinner3.getSelectedItemPosition() == 0) {
                    if (position != 0) {
                        videoResolutionSpinner3.setSelection(rParams.getVsPosition(rpChannel));
                        Toast.makeText(mContext, "720P can only choose the resolution of 720P for recording!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else if (preResolutionSpinner3.getSelectedItemPosition() == 1) {
                    if (position == 2 || position == 3) {
                        videoResolutionSpinner3.setSelection(rParams.getVsPosition(rpChannel));
                        Toast.makeText(mContext, "There are channels for 1080p recording, and recording can only record up to 4 channels!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
//                recordChannel1.setChecked(false);
//                recordChannel2.setChecked(false);
//                recordChannel3.setChecked(false);
//                recordChannel4.setChecked(false);
//                recordChannel5.setChecked(false);
//                recordChannel6.setChecked(false);
                rParams.setVsPosition(position, rpChannel);
                rParams.adjustResolutionWidthReValue(parent.getSelectedItem().toString(), rpChannel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        videoResolutionSpinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "Channel 4 选择了=" + position);
                int rpChannel = pParams.isN41Exist() ? (pParams.getDisplayStyle() == 1 ? 4 : 5) : 4;
                if (preResolutionSpinner4.getSelectedItemPosition() == 2) {
                    if (cameraTypeSpinner4.getSelectedItemPosition() == 0) {
                        if (position != 2) {
                            videoResolutionSpinner4.setSelection(rParams.getVsPosition(rpChannel));
                            Toast.makeText(mContext, "CVBS PAL can only choose the resolution of CVBS PAL for recording!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        if (position != 3) {
                            videoResolutionSpinner4.setSelection(rParams.getVsPosition(rpChannel));
                            Toast.makeText(mContext, "CVBS NTSC can only choose the resolution of CVBS NTSC for recording!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                } else if (preResolutionSpinner4.getSelectedItemPosition() == 0) {
                    if (position != 0) {
                        videoResolutionSpinner4.setSelection(rParams.getVsPosition(rpChannel));
                        Toast.makeText(mContext, "720P can only choose the resolution of 720P for recording!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else if (preResolutionSpinner4.getSelectedItemPosition() == 1) {
                    if (position == 2 || position == 3) {
                        videoResolutionSpinner4.setSelection(rParams.getVsPosition(rpChannel));
                        Toast.makeText(mContext, "There are channels for 1080p recording, and recording can only record up to 4 channels!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
//                recordChannel1.setChecked(false);
//                recordChannel2.setChecked(false);
//                recordChannel3.setChecked(false);
//                recordChannel4.setChecked(false);
//                recordChannel5.setChecked(false);
//                recordChannel6.setChecked(false);
                rParams.setVsPosition(position, rpChannel);
                rParams.adjustResolutionWidthReValue(parent.getSelectedItem().toString(), rpChannel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        videoResolutionSpinner5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "Channel 5 选择了=" + position);
                if (preResolutionSpinner5.getSelectedItemPosition() == 2) {
                    if (cameraTypeSpinner5.getSelectedItemPosition() == 0) {
                        if (position != 2) {
                            videoResolutionSpinner5.setSelection(rParams.getVsPosition(5));
                            Toast.makeText(mContext, "CVBS PAL can only choose the resolution of CVBS PAL for recording!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        if (position != 3) {
                            videoResolutionSpinner5.setSelection(rParams.getVsPosition(5));
                            Toast.makeText(mContext, "CVBS NTSC can only choose the resolution of CVBS NTSC for recording!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                } else if (preResolutionSpinner5.getSelectedItemPosition() == 0) {
                    if (position != 0) {
                        videoResolutionSpinner5.setSelection(rParams.getVsPosition(5));
                        Toast.makeText(mContext, "720P can only choose the resolution of 720P for recording!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else if (preResolutionSpinner5.getSelectedItemPosition() == 1) {
                    if (position == 2 || position == 3) {
                        videoResolutionSpinner5.setSelection(rParams.getVsPosition(5));
                        Toast.makeText(mContext, "There are channels for 1080p recording, and recording can only record up to 4 channels!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
//                recordChannel1.setChecked(false);
//                recordChannel2.setChecked(false);
//                recordChannel3.setChecked(false);
//                recordChannel4.setChecked(false);
//                recordChannel5.setChecked(false);
//                recordChannel6.setChecked(false);
                rParams.setVsPosition(position, 5);
                rParams.adjustResolutionWidthReValue(parent.getSelectedItem().toString(), 5);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        videoResolutionSpinner6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "Channel 6 选择了=" + position);
                if (preResolutionSpinner6.getSelectedItemPosition() == 2) {
                    if (cameraTypeSpinner6.getSelectedItemPosition() == 0) {
                        if (position != 2) {
                            videoResolutionSpinner6.setSelection(rParams.getVsPosition(6));
                            Toast.makeText(mContext, "CVBS PAL can only choose the resolution of CVBS PAL for recording!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        if (position != 3) {
                            videoResolutionSpinner6.setSelection(rParams.getVsPosition(6));
                            Toast.makeText(mContext, "CVBS NTSC can only choose the resolution of CVBS NTSC for recording!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                } else if (preResolutionSpinner6.getSelectedItemPosition() == 0) {
                    if (position != 0) {
                        videoResolutionSpinner6.setSelection(rParams.getVsPosition(6));
                        Toast.makeText(mContext, "720P can only choose the resolution of 720P for recording!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else if (preResolutionSpinner6.getSelectedItemPosition() == 1) {
                    if (position == 2 || position == 3) {
                        videoResolutionSpinner6.setSelection(rParams.getVsPosition(6));
                        Toast.makeText(mContext, "There are channels for 1080p recording, and recording can only record up to 4 channels!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
//                recordChannel1.setChecked(false);
//                recordChannel2.setChecked(false);
//                recordChannel3.setChecked(false);
//                recordChannel4.setChecked(false);
//                recordChannel5.setChecked(false);
//                recordChannel6.setChecked(false);
                rParams.setVsPosition(position, 6);
                rParams.adjustResolutionWidthReValue(parent.getSelectedItem().toString(), 6);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        preNumSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "选择了=" + position);
                pParams.setPreviewNum(position);
                if (position >= 5) {
                    pParams.setDisplayStyle(1);
                    displayStyle.setSelection(1);
                }

                linearLayoutChannel1.setVisibility(position >= 1 ? View.VISIBLE : View.GONE);
                linearLayoutChannel2.setVisibility(position >= 2 ? View.VISIBLE : View.GONE);
                linearLayoutChannel3.setVisibility(position >= 3 ? View.VISIBLE : View.GONE);
                linearLayoutChannel4.setVisibility(position >= 4 ? View.VISIBLE : View.GONE);
                linearLayoutChannel5.setVisibility(position >= 5 ? View.VISIBLE : View.GONE);
                linearLayoutChannel6.setVisibility(position >= 6 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        debugEnableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId()) {
                    case R.id.global_debug_switch:
                        if (isChecked) {
                            QCarLog.setTagLogLevel(Log.DEBUG);
                        } else {
                            QCarLog.setTagLogLevel(Log.INFO);
                        }
                }
            }
        });

        // Video encoder
        childRecorderSizeSpinner = (Spinner) findViewById(R.id.child_spinner);
        codecSpinner = (Spinner) findViewById(R.id.codecSpinner);
        containerSpinner = (Spinner) findViewById(R.id.containerSpinner);
        recorderSSP = (Spinner) findViewById(R.id.recorderSSpinner);
        mainstreamerSpinner = (Spinner) findViewById(R.id.mainkps); //主码率
        substreamerSpinner = (Spinner) findViewById(R.id.subkps);   //次码率
        switch_cr = (Switch) findViewById(R.id.switch_1);
        switch_audio = (Switch) findViewById(R.id.audiosw);

        switch_cr.setChecked(rParams.isChildRecordEnable());
        switch_audio.setChecked(rParams.isAudioRecordEnable());
        childRecorderSizeSpinner.setSelection(rParams.getChild_size());
        recorderSSP.setSelection(rParams.getSegmentSizePosition());
        mainstreamerSpinner.setSelection(rParams.getMainRatePosition());
        substreamerSpinner.setSelection(rParams.getSubRatePosition());
        codecSpinner.setSelection(rParams.getCodecTypePosition());
        containerSpinner.setSelection(rParams.getCtPosition());

        childRecorderSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rParams.setChild_size(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        recorderSSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rParams.setSegmentSizePosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mainstreamerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rParams.setMainRatePosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        substreamerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rParams.setSubRatePosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        codecSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rParams.setCodecTypePosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        switch_cr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId()) {
                    case R.id.switch_1:
                        if (isChecked) {
                            rParams.setChildRecordEnable(true);
                        } else {
                            rParams.setChildRecordEnable(false);
                        }
                }
            }
        });
        switch_audio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId()) {
                    case R.id.audiosw:
                        if (isChecked) {
                            rParams.setAudioRecordEnable(true);
                        } else {
                            rParams.setAudioRecordEnable(false);
                        }
                }
            }
        });

        containerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rParams.setCtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (SpUtil.getInstance(mContext).getChange()) {
            SpUtil.getInstance(mContext).setChange(false);
        }
    }

    private class OnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int numOf1080 = (recordChannel1.isChecked() ? (videoResolutionSpinner1.getSelectedItemPosition() == 3 ? 1 : 0) : 0)
                    + (recordChannel2.isChecked() ? (videoResolutionSpinner2.getSelectedItemPosition() == 3 ? 1 : 0) : 0)
                    + (recordChannel3.isChecked() ? (videoResolutionSpinner3.getSelectedItemPosition() == 3 ? 1 : 0) : 0)
                    + (recordChannel4.isChecked() ? (videoResolutionSpinner4.getSelectedItemPosition() == 3 ? 1 : 0) : 0)
                    + (recordChannel5.isChecked() ? (videoResolutionSpinner5.getSelectedItemPosition() == 3 ? 1 : 0) : 0)
                    + (recordChannel6.isChecked() ? (videoResolutionSpinner6.getSelectedItemPosition() == 3 ? 1 : 0) : 0);
            if (isChecked) {
                if (numOf1080 >= 4) {
                    buttonView.setChecked(false);
                    QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "zyz --> numOf1080 = " + numOf1080);
                    Toast.makeText(mContext, "There are channels for 1080p recording, and recording can only record up to 4 channels!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            if (buttonView == recordChannel1) {
                rParams.setRecordState(1, isChecked);
            } else if (buttonView == recordChannel2) {
                rParams.setRecordState(2, isChecked);
            } else if (buttonView == recordChannel3) {
                rParams.setRecordState(3, isChecked);
            } else if (buttonView == recordChannel4) {
                rParams.setRecordState(4, isChecked);
            } else if (buttonView == recordChannel5) {
                rParams.setRecordState(5, isChecked);
            } else if (buttonView == recordChannel6) {
                rParams.setRecordState(6, isChecked);
            }
        }
    }

    private void setPreviewVideoByCsiNum(int csiNum) {
        noList.clear();

        if (pParams.isN41Exist()) {
            for (int i = 0; i <= 4; i++) {
                noList.add(i);
            }
            if (csiNum == 1) {
                for (int i = 5; i <= 6; i++) {
                    noList.add(i);
                }
            } else {
                if (pParams.getPreviewNum() > 4)
                    pParams.setPreviewNum(4);
            }
        } else {
            noList.add(0);
            noList.add(1);
            noList.add(2);
            noList.add(3);
            pParams.setPreviewNum(Math.min(pParams.getPreviewNum(), 3));
        }

        noAdapter = new ArrayAdapter<Integer>(mContext, R.layout.support_simple_spinner_dropdown_item, noList);

        preNumSpinner.setAdapter(noAdapter);

        QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "pParams.getPreviewNum = " + pParams.getPreviewNum() + preNumSpinner.getAdapter().getCount());
        for (int i = 0; i < 6; i++)
            QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "pParams.getPsPosition(" + (i + 1) + ") = " + pParams.getPsPosition(i + 1));
        for (int i = 0; i < 6; i++)
            QCarLog.i(QCarLog.LOG_MODULE_APP, TAG, "rParams.getVsPosition(" + (i + 1) + ") = " + rParams.getVsPosition(i + 1));
        preNumSpinner.setSelection(pParams.getPreviewNum());

        preResolutionSpinner1.setSelection(pParams.getPsPosition(1));
        preResolutionSpinner2.setSelection(pParams.getPsPosition(2));
        preResolutionSpinner3.setSelection(pParams.getPsPosition(pParams.isN41Exist() ? (pParams.getDisplayStyle() == 1 ? 3 : 4) : 3));
        preResolutionSpinner4.setSelection(pParams.getPsPosition(pParams.isN41Exist() ? (pParams.getDisplayStyle() == 1 ? 4 : 5) : 4));
        preResolutionSpinner5.setSelection(pParams.getPsPosition(5));
        preResolutionSpinner6.setSelection(pParams.getPsPosition(6));

        cameraTypeSpinner1.setSelection(pParams.getCameraType(1));
        cameraTypeSpinner2.setSelection(pParams.getCameraType(2));
        cameraTypeSpinner3.setSelection(pParams.getCameraType(pParams.isN41Exist() ? (pParams.getDisplayStyle() == 1 ? 3 : 4) : 3));
        cameraTypeSpinner4.setSelection(pParams.getCameraType(pParams.isN41Exist() ? (pParams.getDisplayStyle() == 1 ? 4 : 5) : 4));
        cameraTypeSpinner5.setSelection(pParams.getCameraType(5));
        cameraTypeSpinner6.setSelection(pParams.getCameraType(6));

        videoResolutionSpinner1.setSelection(rParams.getVsPosition(1));
        videoResolutionSpinner2.setSelection(rParams.getVsPosition(2));
        videoResolutionSpinner3.setSelection(rParams.getVsPosition(pParams.isN41Exist() ? (pParams.getDisplayStyle() == 1 ? 3 : 4) : 3));
        videoResolutionSpinner4.setSelection(rParams.getVsPosition(pParams.isN41Exist() ? (pParams.getDisplayStyle() == 1 ? 4 : 5) : 4));
        videoResolutionSpinner5.setSelection(rParams.getVsPosition(5));
        videoResolutionSpinner6.setSelection(rParams.getVsPosition(6));
        display_style.setVisibility(View.GONE);
    }
}
