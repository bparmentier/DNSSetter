package be.brunoparmentier.dnssetter;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class MainActivity extends Activity {
    private final String TAG = "MainActivity";
    private EditText editdns1;
    private EditText editdns2;
    private Button applyButton;
    private TextView currentDNS1;
    private TextView currentDNS2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentDNS1 = (TextView) findViewById(R.id.currentdns1);
        currentDNS2 = (TextView) findViewById(R.id.currentdns2);
        editdns1 = (EditText) findViewById(R.id.editdns1);
        editdns2 = (EditText) findViewById(R.id.editdns2);
        applyButton = (Button) findViewById(R.id.applydns);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dns1 = editdns1.getText().toString();
                String dns2 = editdns2.getText().toString();
                (new SetDNSTask()).execute(dns1, dns2);
            }
        });
        (new SetCurrentDNSTask()).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class SetCurrentDNSTask extends AsyncTask<Void, Void, List<String>> {
        List<String> dns1 = new ArrayList<String>();
        List<String> dns2 = new ArrayList<String>();

        @Override
        protected List<String> doInBackground(Void... voids) {
            dns1 = Shell.SH.run("getprop net.dns1");
            dns2 = Shell.SH.run("getprop net.dns2");
            return null;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            currentDNS1.setText(dns1.get(0));
            currentDNS2.setText(dns2.get(0));
        }
    }

    private class SetDNSTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            if (Shell.SU.available()) {
                String dns1 = strings[0];
                String dns2 = strings[1];
                String cmd1 = "setprop net.dns1 " + dns1;
                String cmd2 = "setprop net.dns2 " + dns2;

                List<String> retcmd1 = Shell.SU.run(cmd1);
                List<String> retcmd2 = Shell.SU.run(cmd2);

                if (retcmd1 == null || retcmd2 == null) {
                    Log.e(TAG, "Root command failed");
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            (new SetCurrentDNSTask()).execute();
        }
    }
}
