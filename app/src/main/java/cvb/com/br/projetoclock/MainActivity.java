package cvb.com.br.projetoclock;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static class ViewHolder {

        private TextView tvHourMinute;
        private TextView tvSecond;

        private Button btConfig;

        private void init(Activity act) {
            tvHourMinute = act.findViewById(R.id.tvHourMinute);
            tvHourMinute = act.findViewById(R.id.tvHourMinute);

            btConfig = act.findViewById(R.id.btConfig);
        }
    }

    private ViewHolder viewHolder = new ViewHolder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewHolder.init(this);
    }
}
