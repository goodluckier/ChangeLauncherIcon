package com.gss.changelaunchericon;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * @author gss
 */
public class MainActivity extends AppCompatActivity {

    private static final String PACKAGE_NAME = "com.gss.changelaunchericon";

    @BindView(R.id.ll_ic_launcher_rect)
    LinearLayout ll_ic_launcher_rect;
    @BindView(R.id.ll_ic_launcher_round)
    LinearLayout ll_ic_launcher_round;
    @BindView(R.id.ll_ic_launcher_default)
    LinearLayout ll_ic_launcher_default;

    @BindView(R.id.rb_ic_launcher_round)
    AppCompatRadioButton rb_ic_launcher_round;
    @BindView(R.id.rb_ic_launcher_rect)
    AppCompatRadioButton rb_ic_launcher_rect;
    @BindView(R.id.rb_ic_launcher_default)
    AppCompatRadioButton rb_ic_launcher_default;

    Launcher current;
    Launcher select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        createShortcut();
        initView();
    }

    private void initView() {
        if (isEnabledLauncher(Launcher.Launcher_Rect)) {
            current = select = Launcher.Launcher_Rect;
            rb_ic_launcher_rect.setChecked(true);
        } else if (isEnabledLauncher(Launcher.Launcher_Round)) {
            current = select = Launcher.Launcher_Round;
            rb_ic_launcher_round.setChecked(true);
        } else {
            current = select = Launcher.Launcher_Default;
            rb_ic_launcher_default.setChecked(true);
        }
    }

    public static String getLauncherComponentName(Context context) {
        try {
            int stateDefault = context.getPackageManager().getComponentEnabledSetting(new ComponentName(PACKAGE_NAME, Launcher.Launcher_Default.componentName));
            int stateRect = context.getPackageManager().getComponentEnabledSetting(new ComponentName(PACKAGE_NAME, Launcher.Launcher_Rect.componentName));
            int stateRound = context.getPackageManager().getComponentEnabledSetting(new ComponentName(PACKAGE_NAME, Launcher.Launcher_Round.componentName));

            if (stateDefault == PackageManager.COMPONENT_ENABLED_STATE_ENABLED || stateDefault == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT) {
                return Launcher.Launcher_Default.componentName;
            } else if (stateRect == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                return Launcher.Launcher_Rect.componentName;
            } else if (stateRound == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                return Launcher.Launcher_Round.componentName;
            } else {
                context.getPackageManager().setComponentEnabledSetting(new ComponentName(PACKAGE_NAME, Launcher.Launcher_Default.componentName),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);
                return Launcher.Launcher_Default.componentName;
            }
        } catch (Exception e) {
            return Launcher.Launcher_Default.componentName;
        }
    }

    private boolean isEnabledLauncher(Launcher launcher) {
        String name = getLauncherComponentName(this);
        return launcher.componentName.equals(name);
    }

    private void enableComponent(ComponentName componentName) {
        getPackageManager().setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

    }

    private void disableComponent(ComponentName componentName) {
        getPackageManager().setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

    }

    private void setLauncher(Launcher launcher) {
        for (Launcher name : Launcher.values()) {
            if (!name.componentName.equals(launcher.componentName)) {
                disableComponent(new ComponentName(PACKAGE_NAME, name.componentName));
            }
        }
        enableComponent(new ComponentName(PACKAGE_NAME, launcher.componentName));
        current = launcher;
    }

    public enum Launcher {
        Launcher_Default("系统默认图标", "com.gss.changelaunchericon.MainActivity"),
        Launcher_Round("圆形图标", "com.gss.changelaunchericon.MainActivity2"),
        Launcher_Rect("圆角矩形图标", "com.gss.changelaunchericon.MainActivity3");
        String name;
        String componentName;

        Launcher(String name, String componentName) {
            this.name = name;
            this.componentName = componentName;
        }
    }

    @OnClick({R.id.ll_ic_launcher_rect, R.id.ll_ic_launcher_round, R.id.ll_ic_launcher_default})
    public void onRBContainerClick(ViewGroup container) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View view = container.getChildAt(i);
            if (view instanceof AppCompatRadioButton) {
                ((AppCompatRadioButton) view).setChecked(true);
            }
        }
    }

    @OnCheckedChanged({R.id.rb_ic_launcher_round, R.id.rb_ic_launcher_rect, R.id.rb_ic_launcher_default})
    void onGenderSelected(CompoundButton button, boolean checked) {
        if (checked) {
            switch (button.getId()) {
                case R.id.rb_ic_launcher_round:
                    select = Launcher.Launcher_Round;
                    rb_ic_launcher_default.setChecked(false);
                    rb_ic_launcher_rect.setChecked(false);
                    break;
                case R.id.rb_ic_launcher_rect:
                    select = Launcher.Launcher_Rect;
                    rb_ic_launcher_default.setChecked(false);
                    rb_ic_launcher_round.setChecked(false);
                    break;
                case R.id.rb_ic_launcher_default:
                    select = Launcher.Launcher_Default;
                    rb_ic_launcher_round.setChecked(false);
                    rb_ic_launcher_rect.setChecked(false);
                    break;
                default:
                    break;
            }
        }
    }

    @OnClick(R.id.btn_apply)
    public void apply() {
        if (select == current) {
            Toast.makeText(this, "当前图标即为" + current.name, Toast.LENGTH_SHORT).show();
            return;
        }
        setLauncher(select);
        Toast.makeText(this, "切换为" + select.name + "，稍后生效~", Toast.LENGTH_SHORT).show();
    }

    /**
     * 创建快捷方式
     */
    private void createShortcut() {
        try {
            boolean isExit = ShortcutUtils.isShortCutExist(getApplicationContext(), "APPNAME");
            if (!isExit) {
                // 当前不存在桌面快捷方式，手动创建一个
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setComponent(new ComponentName(this, MainActivity.getLauncherComponentName(this)));
                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                ShortcutUtils.addShortcut(MainActivity.this, intent, "APPNAME", false, icon);
            }
        } catch (Exception e) {

        }
    }
}
