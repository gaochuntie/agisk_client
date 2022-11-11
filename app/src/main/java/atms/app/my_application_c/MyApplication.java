package atms.app.my_application_c;

import android.app.Application;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;

import com.kongzue.dialogx.DialogX;
import com.kongzue.dialogx.style.MaterialStyle;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import atms.app.my_application_c.Tools.Worker;


public class MyApplication extends Application {
    public static boolean isAppExited = false;
    ExecutorService executorService = Executors.newSingleThreadExecutor();

    Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());

    public Handler getMainThreadHandler() {
        return mainThreadHandler;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Worker.setExecutorService(executorService);
        setupDialogX();

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Worker.unsetExecutorService();
        executorService.shutdown();
        isAppExited = true;
    }

    private void setupDialogX() {
        DialogX.init(this);
        //设置主题样式
        DialogX.globalStyle = MaterialStyle.style();

//设置亮色/暗色（在启动下一个对话框时生效）
        DialogX.globalTheme = DialogX.THEME.LIGHT;

//设置对话框最大宽度（单位为像素）
        DialogX.dialogMaxWidth = 1920;

//设置 InputDialog 自动弹出键盘
        DialogX.autoShowInputKeyboard = false;

//限制 PopTip 一次只显示一个实例（关闭后可以同时弹出多个 PopTip）
        DialogX.onlyOnePopTip = true;


//设置默认对话框背景颜色（值为ColorInt，为-1不生效）
        DialogX.backgroundColor = Color.WHITE;

//设置默认对话框默认是否可以点击外围遮罩区域或返回键关闭，此开关不影响提示框（TipDialog）以及等待框（TipDialog）
        DialogX.cancelable = true;

//设置默认提示框及等待框（WaitDialog、TipDialog）默认是否可以关闭
        DialogX.cancelableTipDialog = false;

//设置默认取消按钮文本文字，影响 BottomDialog
        DialogX.cancelButtonText = "Cancel";


//设置全局 Dialog 生命周期监听器
//        DialogX.dialogLifeCycleListener = (DialogLifecycleCallback);


/**
 * 设置 BottomDialog 导航栏背景颜色
 */
        DialogX.bottomDialogNavbarColor = Color.TRANSPARENT;

//是否自动在主线程执行
        DialogX.autoRunOnUIThread = true;
    }
}
