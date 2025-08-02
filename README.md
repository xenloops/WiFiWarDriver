# wifiWarDriver

Tries to get rough location of hotspots while wandering the neighborhood.

## Vows

* This app does _not_ send data from your device to an external server. The data it collects is for your eyes only.
* This app requests only the minimal required permissions:
  * ACCESS_COARSE_LOCATION - Required for GPS location
  * ACCESS_FINE_LOCATION - Required for GPS location
  * ACCESS_WIFI_STATE - Required to scan for hotspots
  * CHANGE_WIFI_STATE - Required to turn on radio for scanning
* This app is free for noncommercial use. If for some odd reason you represent a company that wants to license the code, make me an offer.
  * See [the license file](LICENSE) for details.

## Suggestions

Have a feature idea? Fork me and try crafting it. Pull requests are always welcome!

This code will never be perfect. Find a bug? File an issue.

## FAQ

### How do I get to the WiFi file?

As of 0.2.0-alpha, the app writes it to its own directory. To see the file (and copy/download/etc it), connect the device to your computer and use this command from a terminal:

```adb shell run-as net.xenloops.wifiWarDriver cat files/wifi_log.txt```

It's a CSV file, so pastes nicely into your choice of spreadsheet application. I've tested it in LibreOffice Calc; if you have trouble using certain other apps, send your ire that vendor's way.

