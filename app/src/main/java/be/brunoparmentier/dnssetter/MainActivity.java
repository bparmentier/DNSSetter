/*
 * Copyright (c) 2014 Bruno Parmentier. This file is part of DNSSetter.
 *
 * DNSSetter is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DNSSetter is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DNSSetter.  If not, see <http://www.gnu.org/licenses/>.
 */

package be.brunoparmentier.dnssetter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
    private static final String TAG = "MainActivity";
    private static final String PREF_KEY_IS_FIRST_RUN = "is_first_run";
    private static final String PREF_KEY_DNS1 = "pref_dns1";
    private static final String PREF_KEY_DNS2 = "pref_dns2";
    private EditText editdns1;
    private EditText editdns2;
    private Button applyButton;
    private TextView currentDNS1;
    private TextView currentDNS2;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
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

        String prefDNS1 = settings.getString(PREF_KEY_DNS1, "");
        String prefDNS2 = settings.getString(PREF_KEY_DNS2, "");
        editdns1.setText(prefDNS1);
        editdns2.setText(prefDNS2);

        (new SetCurrentDNSTask()).execute();

        boolean firstRun = settings.getBoolean(PREF_KEY_IS_FIRST_RUN, true);
        if (firstRun) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.firstrun_dialog_message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.firstrun_dialog_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
            settings.edit().putBoolean(PREF_KEY_IS_FIRST_RUN, false).apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            Intent aboutIntent = new Intent(this, AboutActivity.class);
            startActivity(aboutIntent);
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

                settings.edit().putString(PREF_KEY_DNS1, dns1).apply();
                settings.edit().putString(PREF_KEY_DNS2, dns2).apply();

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
