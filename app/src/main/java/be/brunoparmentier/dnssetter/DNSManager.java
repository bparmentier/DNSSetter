/*
 * Copyright (c) 2015 Bruno Parmentier. This file is part of DNSSetter.
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

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class DNSManager {
    private static final String TAG = DNSManager.class.getSimpleName();

    public static void setDNS(String dns[]) {
        if (Shell.SU.available()) {
            List<String> cmds = new ArrayList<>();
            for (int i = 1; i <= 2; i++) {
                cmds.add("setprop net.dns" + i + " " + dns[i - 1]);
            }

            if (Shell.SU.run(cmds) == null) {
                Log.e(TAG, "Root command failed");
            }
        }
    }

    public static List<String> getDNS() {
        List<String> cmds = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            cmds.add("getprop net.dns" + i);
        }
        return Shell.SH.run(cmds);
    }
}
