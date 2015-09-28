DNSSetter
=========

Set custom DNS servers on Android (requires root!).

Also works for mobile connection (3G, 4G, ...), only tested on KitKat
(CyanogenMod 11).

The app currently use the *setprop* method. It might not work for you depending
on your device/Android version.

I am aware of the four `ndc resolver ...` commands that seems to work for some people,
but `setprop` was working for me so I didn't bother with those ones. Please tell
me if you need it.

**Warning: this is currently alpha software, use at your own risk!

Please verify yourself that the app is working for you (using tcpdump, etc.).
That is not because the `net.dns[0-1]` properties are successfully set that
Android will really use them as DNS servers! It really depends on the Android
version.**
