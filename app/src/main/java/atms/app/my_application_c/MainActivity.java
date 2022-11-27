package atms.app.my_application_c;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.topjohnwu.superuser.Shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import atms.app.my_application_c.Tools.GlobalMsg;
import atms.app.my_application_c.Tools.globalMsgOnAddCallback;
import atms.app.my_application_c.adapter.ViewPageAdapter;
import atms.app.my_application_c.databinding.ActivityMainBinding;
import atms.app.my_application_c.fragments.SystemInfoMap;
import atms.app.my_application_c.fragments.aboutFragment;
import atms.app.my_application_c.fragments.homeFragment;
import atms.app.my_application_c.fragments.installFragment;
import devlight.io.library.ntb.NavigationTabBar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //setup jni environment
    static {
        System.loadLibrary("agisk-cli");
        System.loadLibrary("gpt");
        System.loadLibrary("uuid");
        System.loadLibrary("c++_shared");
    }


    //////////////////////////////////////////////////////////////////////////
    /*
    libsu init main shell
     */
    static {

        // Set settings before the main shell can be created
        Shell.enableVerboseLogging = BuildConfig.DEBUG;
        Shell.setDefaultBuilder(Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(10)
        );
    }
    //

    private ActivityMainBinding binding;
    LinearLayout worning_box;
    TextView worning_tv;
    boolean result = false;
    ViewPager2 viewpage2;
    NavigationTabBar navigationTabBar;

    String[] colors;
    JNI jniObj;
    boolean isEnablePageScrollCallBack = true;

    static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Preheat the main root shell in the splash screen
        // so the app can use it afterwards without interrupting
        // application flow (e.g. root permission prompt)
        Shell.getShell(new Shell.GetShellCallback() {
            @Override
            public void onShell(@NonNull Shell shell) {
                //Toast.makeText(this, "Root access", Toast.LENGTH_SHORT).show();
                if (shell.isRoot()) {
                    Settings.setRootAccess(true);
                }

                checkRootAccess(shell.isRoot());
            }
        });

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        // Example of a call to a native method
        worning_box = binding.worningBox;
        worning_tv = binding.worningTv;
        navigationTabBar = binding.navigateBar;
        colors = getResources().getStringArray(R.array.full_wite);
        viewpage2 = binding.viewpageContainer;
        //

        jniObj = new JNI();

        setupLogDir();
        //setup ui
        setupNavBar();
        setupViewPage();

    }


    private void setupLogDir() {
        File log_dir = getExternalFilesDir("log");
    }

    private void checkRootAccess(boolean isroot) {
        if (!isroot) {
            MessageDialog.show("Root Access", "Permission denied. Root required.\n" +
                            "Unavailable functions without root:\n" +
                            "Do all actions\n" +
                            "Check certain information\n" +
                            "Check metadata\n" +
                            "Backup configurations to shared space\n" +
                            "Read reserved disk block"
                    , "Ignore");
            showWorningMsg("No root permission.");
        }
    }

    private void setupViewPage() {
        List<Fragment> data = new ArrayList<>();

        data.add(new homeFragment());
        data.add(new installFragment());
        data.add(new SystemInfoMap());
        data.add(new aboutFragment());
        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(this, data);
        viewpage2.setAdapter(viewPageAdapter);
        viewpage2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                // Log.d("MainActivity", String.valueOf(position));
                if (isEnablePageScrollCallBack) {
                    navigationTabBar.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    isEnablePageScrollCallBack = true;
                }
            }
        });

    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            /////////////////////////////////////////
//            case R.id.button1:
//
//                //debug worning box
//                if (worning_box.getVisibility()==View.VISIBLE){
//                    YoYo.with(Techniques.SlideOutDown)
//                            .duration(500)
//                            .onEnd(new YoYo.AnimatorCallback() {
//                                @Override
//                                public void call(Animator animator) {
//                                    worning_box.setVisibility(View.GONE);
//                                }
//                            })
//                            .playOn(worning_box)
//
//                    ;
//                }else{
//                    YoYo.with(Techniques.SlideInUp)
//                            .duration(500)
//                            .onStart(new YoYo.AnimatorCallback() {
//                                @Override
//                                public void call(Animator animator) {
//                                    worning_box.setVisibility(View.VISIBLE);
//                                }
//                            })
//                            .playOn(worning_box);
//
//                }
//                //
//                boolean result=jniObj.checkPasswd(edit_pass.getText().toString());
//                if (result) {
//                    tv.setText("Password correct.");
//                    edit_pass.setText("");
//                }else {
//                    tv.setText("Password incorrect.");
//                    edit_pass.setText("");
//                }
//                break;
            //////////////////////////////////////
            default:
                break;


        }

    }

    public void setupNavBar() {

        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_home_foreground),
                        Color.parseColor(colors[0])
                ).title("Preview")
                        .badgeTitle("1")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_install_foreground),
                        Color.parseColor(colors[1])
                ).title("Install")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_map_foreground),
                        Color.parseColor(colors[2])
                ).title("Map")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_contract_foreground),
                        Color.parseColor(colors[3])
                ).title("About")
                        .build()
        );
        navigationTabBar.setModels(models);
        navigationTabBar.setIsTitled(true);
        navigationTabBar.setIsScaled(true);
        navigationTabBar.setTitleSize(40);
        navigationTabBar.setIsBadged(true);
        //////////////////////////////////
        navigationTabBar.setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
            @Override
            public void onStartTabSelected(NavigationTabBar.Model model, int index) {
                //isEnablePageScrollCallBack=false;
            }

            @Override
            public void onEndTabSelected(NavigationTabBar.Model model, int index) {
                isEnablePageScrollCallBack = false;
                viewpage2.setCurrentItem(index, true);
            }
        });
//        navigationTabBar.setIconSizeFraction();
        navigationTabBar.onPageScrolled(0, 0, 0);


//        navigationTabBar.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < navigationTabBar.getModels().size(); i++) {
//                    final NavigationTabBar.Model model = navigationTabBar.getModels().get(i);
//                    navigationTabBar.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            model.showBadge();
//                        }
//                    }, i * 100);
//                }
//            }
//        }, 500);
    }


    //worning box
    public void showWorningMsg(String msg) {
        worning_box.setVisibility(View.VISIBLE);
        worning_tv.setText(msg);
    }

    public void hideWorningBox() {
        worning_box.setVisibility(View.GONE);
    }

    public LinearLayout getWorning_box() {
        return worning_box;
    }

    public TextView getWorning_tv() {
        return worning_tv;
    }

    synchronized public void updateBadge(int module_index, String content) {
        Log.d(TAG, "Update badge : " + content);

        if (navigationTabBar != null & module_index < navigationTabBar.getModels().size()) {
            if (!navigationTabBar.getModels().get(module_index)
                    .isBadgeShowed()) {
                navigationTabBar.getModels().get(module_index).setBadgeTitle(content);
                navigationTabBar.getModels().get(module_index).showBadge();
                Log.d(TAG, "Show badge again");
            }
            navigationTabBar.getModels().get(module_index)
                    .updateBadgeTitle(content);


            if (content.equals("0")) {
                navigationTabBar.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideBadge(0);
                        getWorning_box().setBackgroundColor(Color.GREEN);
                        getWorning_tv().setText("All client exited");
                    }
                }, 600);

            }
        } else {
            Log.d(TAG, "UpdateBadge failed");
        }

    }

    public void hideBadge(int index) {
        if (navigationTabBar != null & index < navigationTabBar.getModels().size()) {
            navigationTabBar.getModels().get(index).hideBadge();
        }
    }


    PopupWindow logPopWin = null;

    public void setupLogPopWindows(View v) {
        View view = LayoutInflater.from(this).inflate(R.layout.logviewer, null, false);
        Button cancek = view.findViewById(R.id.log_cancel);
        Button savelog = view.findViewById(R.id.log_save_bt);
        TextView logcontent = view.findViewById(R.id.log_content);
        TabLayout tabLayout = view.findViewById(R.id.log_type);
        NestedScrollView nestedScrollView = view.findViewById(R.id.log_scrollview);

        //1.构造一个PopupWindow，参数依次是加载的View，宽高
        final PopupWindow popWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);

        popWindow.setAnimationStyle(R.style.anim_logviewer);  //设置加载动画
        //这些为了点击非PopupWindow区域，PopupWindow会消失的，如果没有下面的
        //代码的话，你会发现，当你把PopupWindow显示出来了，无论你按多少次后退键
        //PopupWindow并不会关闭，而且退不出程序，加上下述代码可以解决这个问题
        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));    //要为popWindow设置一个背景才有效


        //设置popupWindow里的按钮的事件
        cancek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dismiss();
            }
        });
        savelog.setEnabled(false);
        savelog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GlobalMsg.appendLog(logcontent.getText().toString(), getExternalFilesDir("log").getPath()
                        + "/"
                        + GlobalMsg.getContinusDataString()
                        + ".log");
                popWindow.dismiss();
            }
        });
        GlobalMsg.setCallback(new globalMsgOnAddCallback() {
            @Override
            public void onMsgAdd(String newmsg) {
                logcontent.append(newmsg);
            }
        });


        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
        logPopWin = popWindow;

        //tab layout
        tabLayout.removeAllTabs();
        tabLayout.addTab(tabLayout.newTab().setText("Main"));
        tabLayout.addTab(tabLayout.newTab().setText("DiskAction"));
        tabLayout.addTab(tabLayout.newTab().setText("PartitionAction"));
        tabLayout.addTab(tabLayout.newTab().setText("PartitionDump"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getText().toString()) {
                    case "Main":
                        readOutLog(GlobalMsg.GLOBAL_LOG,logcontent,nestedScrollView);
                        break;
                    case "DiskAction":
                        readOutLog(GlobalMsg.DISKACTION_LOG,logcontent,nestedScrollView);
                        break;
                    case "PartitionAction":
                        readOutLog(GlobalMsg.PARTITION_LOG,logcontent,nestedScrollView);
                        break;
                    case "PartitionDump":
                        readOutLog(GlobalMsg.PARTITION_DUMP_LOG,logcontent,nestedScrollView);
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                switch (tab.getText().toString()) {
                    case "Main":
                        readOutLog(GlobalMsg.GLOBAL_LOG,logcontent,nestedScrollView);
                        break;
                    case "DiskAction":
                        readOutLog(GlobalMsg.DISKACTION_LOG,logcontent,nestedScrollView);
                        break;
                    case "PartitionAction":
                        readOutLog(GlobalMsg.PARTITION_LOG,logcontent,nestedScrollView);
                        break;
                    case "PartitionDump":
                        readOutLog(GlobalMsg.PARTITION_DUMP_LOG,logcontent,nestedScrollView);
                        break;
                    default:
                        break;
                }
            }
        });

    }

    private void readOutLog(String path, TextView out,NestedScrollView container) {
        out.setText("");
        out.append("Please wait...\n");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File myObj = new File(path);
                    if (!myObj.exists()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                out.append("No such file");
                            }
                        });

                        return;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            out.setText(path+"\n");
                        }
                    });
                    Scanner myReader = new Scanner(myObj);
                    while (myReader.hasNextLine()) {
                        String data = myReader.nextLine();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                out.append(data+"\n");}
                        });

                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            container.post(new Runnable() {
                                @Override
                                public void run() {
                                    container.fullScroll(View.FOCUS_DOWN);

                                }
                            });
                        }
                    });

                    myReader.close();
                } catch (FileNotFoundException e) {
                    System.out.println("An error occurred.");
                    out.append(e.getMessage());
                    e.printStackTrace();

                }
            }
        }).start();

    }

    public void showLogViewer() {
        if (logPopWin != null) {
            logPopWin.showAtLocation(logPopWin.getContentView(), Gravity.CENTER, 0, 0);
            View view = logPopWin.getContentView();
            TabLayout tabLayout = view.findViewById(R.id.log_type);

            tabLayout.selectTab(tabLayout.getTabAt(0));
        }
    }

    public void showLogViewer(int index) {
        if (logPopWin != null) {
            logPopWin.showAtLocation(logPopWin.getContentView(), Gravity.CENTER, 0, 0);
            View view = logPopWin.getContentView();
            TabLayout tabLayout = view.findViewById(R.id.log_type);
            if (index < tabLayout.getTabCount()) {
                tabLayout.selectTab(tabLayout.getTabAt(index));
            } else {
                tabLayout.selectTab(tabLayout.getTabAt(0));
            }
        }
    }

    public void dismissLogViewer() {
        if (logPopWin != null) {
            logPopWin.dismiss();
        }
    }

    public void appendLog(String log) {
        if (logPopWin != null) {
            TextView textView = logPopWin.getContentView().findViewById(R.id.log_content);
            textView.append(log);
        }
    }
}