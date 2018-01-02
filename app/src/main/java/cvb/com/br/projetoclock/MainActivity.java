package cvb.com.br.projetoclock;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static class ViewHolder {

        private TextView tvHourMinute;
        private TextView tvSecond;
        private TextView tvBatteryLevel;

        private CheckBox cbBateria;

        private Button btConfig;
        private Button btCloseConfig;

        private void init(Activity act) {
            tvHourMinute = act.findViewById(R.id.tv_hour_minute);
            tvSecond = act.findViewById(R.id.tv_second);
            tvBatteryLevel = act.findViewById(R.id.tv_battery_level);

            cbBateria = act.findViewById(R.id.cb_bateria);

            btConfig = act.findViewById(R.id.bt_config);
            btCloseConfig = act.findViewById(R.id.bt_close_config);
        }
    }

    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            viewHolder.tvBatteryLevel.setText(String.valueOf(level).concat(" %"));
        }
    };

    private ViewHolder viewHolder = new ViewHolder();

    private Handler handler = new Handler();
    private Runnable runnable;

    private boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        viewHolder.init(this);

        viewHolder.btConfig.setOnClickListener(btConfigListener);
        viewHolder.btCloseConfig.setOnClickListener(btCloseConfigListener);
        viewHolder.cbBateria.setOnClickListener(cbBateriaListener);
        viewHolder.cbBateria.setChecked(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        this.isRunning = true;
        configTime();
    }

    @Override
    protected void onStop() {
        super.onStop();

        this.unregisterReceiver(batteryReceiver);

        this.isRunning = false;
    }

    private void configTime() {
        final Calendar calendar = Calendar.getInstance();

        runnable = new Runnable() {
            @Override
            public void run() {
                if (!isRunning)
                    return;

                calendar.setTimeInMillis(System.currentTimeMillis());

                String hourMinute =
                        String.format(Locale.getDefault(), "%02d:%02d",
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE));
                viewHolder.tvHourMinute.setText(hourMinute);

                String second =
                        String.format(Locale.getDefault(),"%02d", calendar.get(Calendar.SECOND));
                viewHolder.tvSecond.setText(second);

                long now = SystemClock.uptimeMillis();
                final long nextUpdate = now + (1000 - (now % 1000));

                handler.postAtTime(runnable, nextUpdate);
            }
        };

        runnable.run();
    }

    private View.OnClickListener btConfigListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener btCloseConfigListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener cbBateriaListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            viewHolder.tvBatteryLevel.setVisibility(viewHolder.cbBateria.isChecked() ?
                View.VISIBLE : View.GONE
            );
        }
    };
}
