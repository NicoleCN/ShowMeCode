package cn.shanghai.nicole.utils;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * @Description: 屏幕工具类
 * @author wuchangbin
 * @date 2016-5-13
 */
public class ScreenUtil {
	private static int sScreenWidth;
	private static int sScreenHeight;


	/**
	 * 返回值就是导航栏的高度,得到的值单位px
	 */
	public static int getNavigationBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}
	/**
	 * 获取状态栏高度
	 */
	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	/**
	 * 获取屏幕宽度
	 */
    public static int getScreenWidth(Context context) {
        if (sScreenWidth <= 0) {
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(dm);
            sScreenWidth = dm.widthPixels;
            sScreenHeight = dm.heightPixels;
        }
        return sScreenWidth;
    }

	/**
	 * 获取屏幕高度
	 */
    public static int getScreenHeight(Context context) {
        if (sScreenHeight <= 0) {
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(dm);
            sScreenWidth = dm.widthPixels;
            sScreenHeight = dm.heightPixels;
        }
        return sScreenHeight;
    }

	/**
	 * 隐藏软键盘
	 *
	 * @param activity
	 */
	public static void hideSoftKeyboard(Activity activity) {
		try {
			InputMethodManager inputMethodManager = (InputMethodManager) activity
					.getSystemService(Activity.INPUT_METHOD_SERVICE);
			if (activity.getCurrentFocus() == null || activity.getCurrentFocus().getWindowToken() == null)
				return;
			inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isSoftShowing(Activity activity) {
		//获取当前屏幕内容的高度
		int screenHeight = activity.getWindow().getDecorView().getHeight();
		//获取View可见区域的bottom
		Rect rect = new Rect();
		//DecorView即为activity的顶级view
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		//考虑到虚拟导航栏的情况（虚拟导航栏情况下：screenHeight = rect.bottom + 虚拟导航栏高度）
		//选取screenHeight*2/3进行判断
		return screenHeight * 2 / 3 > rect.bottom;
	}

	/**
	 * 设置标题栏MarginTop来作为状态栏展示高度
	 * @param context
	 * @param view
	 */
	public static void showStatusBarByLayoutMargins(Context context, View view){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			ViewGroup.MarginLayoutParams parmas = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
			parmas.setMargins(0, getStatusBarHeight(context), 0, 0);
			view.setLayoutParams(parmas);
		}

	}

	public static void hideStatusBarByLayoutMargins(Context context, View view){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			ViewGroup.MarginLayoutParams parmas = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
			parmas.setMargins(0, 0, 0, 0);
			view.setLayoutParams(parmas);
		}
	}

	/**
	 * 全屏隐藏系统UI
	 */
	public static void fullScreenHideSystemUI(Activity activity){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			View decorView = activity.getWindow().getDecorView();
			decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}

	}
	/**
	 * 全屏显示系统UI
	 */
	public static void fullScreenShowSystemUI(Activity activity){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			View decorView = activity.getWindow().getDecorView();
			decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
		}
	}

}
