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
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static class ViewHolder {

        private TextView tvHourMinute;
        private TextView tvSecond;
        private TextView tvBatteryLevel;

        private CheckBox cbBateria;

        private ImageView ivConfig;
        private ImageView ivCloseConfig;

        private LinearLayout llPanelConfig;

        private void init(Activity act) {
            tvHourMinute = act.findViewById(R.id.tv_hour_minute);
            tvSecond = act.findViewById(R.id.tv_second);
            tvBatteryLevel = act.findViewById(R.id.tv_battery_level);

            cbBateria = act.findViewById(R.id.cb_bateria);

            ivConfig = act.findViewById(R.id.bt_config);
            ivCloseConfig = act.findViewById(R.id.bt_close_config);

            llPanelConfig = act.findViewById(R.id.ll_panel);
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

    /*
    -------------------
    Objeto AdMob - Publicidade
    -------------------
    */
    private InterstitialAd mInterstitialAd;

    /*
    ---------------
    Objetos Firebase
    ---------------
    -> Analytics
    -> Crashlytics
    */
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        viewHolder.init(this);

        viewHolder.ivConfig.setOnClickListener(btConfigListener);

        viewHolder.ivCloseConfig.setOnClickListener(btCloseConfigListener);

        viewHolder.cbBateria.setOnClickListener(cbBateriaListener);
        viewHolder.cbBateria.setChecked(true);

        // Configura o painel na posicao inicial -> Objetivo: realizar a primeira animação
        viewHolder.llPanelConfig.animate()
                .translationY(1000)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));

        adicaoFirebase();

        adicaoPublicidadeAdMob();
    }

    private void adicaoFirebase() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, null);
    }

    private void adicaoPublicidadeAdMob() {
        //Publicidade AdMob
        String AdMob_ID_APP = "ca-app-pub-1791259810056092~8426044344";
        MobileAds.initialize(this, AdMob_ID_APP);

        // AdMob ID Bloco Anuncio Prod: ca-app-pub-1791259810056092/6625877516
        // AdMob ID Bloco Anuncio Test: ca-app-pub-3940256099942544/1033173712
        String AdMob_ID_Anuncio = "ca-app-pub-3940256099942544/1033173712";
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(AdMob_ID_Anuncio);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        // Carregar Proximo Anuncio
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
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
            /*
            Firebase - Analytics - Inicio
            */
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id_bt_config");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "bt_config_click");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            /*
            Firebase - Analytics - Fim
            */

            //--------------------------------------

            /*
            Firebase - Crashlytics - Inicio
            --
            Log para erros tratatos
            */
            String msgErro = null;
            try {
                int x = msgErro.length();
            } catch (Exception E) {
                Log.i("CVB", "Erro -> Gerar Crashlytics");
                Crashlytics.log("Erro Tratado - Bt=Config");
                Crashlytics.logException(E);
            }
            /*
            Firebase - Crashlytics - Fim
            */

            //--------------------------------------

            viewHolder.ivConfig.setVisibility(View.GONE);

            viewHolder.llPanelConfig.setVisibility(View.VISIBLE);
            viewHolder.llPanelConfig.animate()
                    .translationY(0)
                    .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
        }
    };

    private View.OnClickListener btCloseConfigListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            /*
            -------------------
            AdMob - Publicidade -> Inicio Apresentacao
            -------------------
            */
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                Log.d("CVB", "AdMob -> The interstitial wasn't loaded yet.");
            }
            /*
            -------------------
            AdMob - Publicidade -> Fim Apresentacao
            -------------------
            */

            viewHolder.llPanelConfig.animate()
                    .translationY(viewHolder.llPanelConfig.getMeasuredHeight())
                    .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));

            viewHolder.ivConfig.setVisibility(View.VISIBLE);
        }
    };

    private View.OnClickListener cbBateriaListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            /*
            Firebase - Analytics - Inicio
            */
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id_cb_battery");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, viewHolder.cbBateria.isChecked() ? "Ck=True" : "Ck=False");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "cb_battery_click");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            /*
            Firebase - Analytics - Fim
            */

            viewHolder.tvBatteryLevel.setVisibility(viewHolder.cbBateria.isChecked() ?
                View.VISIBLE : View.GONE
            );
        }
    };
}
