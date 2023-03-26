# Glowsquid

Glowsquid intercepts and redirects system Minecraft network traffic through itself, acting as a proxy. While online-mode Minecraft traffic is normally encrypted, Glowsquid hijacks the login key exchange to intercept the encryption key and decrypt the traffic.

Glowsquid provides an interactive packet view UI, where packets can be easily analyzed and broken down. There is additionally a basic packet filtering system, which supports blocking and modifying packets based on their fields.

![Glowsquid UI](https://i.imgur.com/rXdMmcG.png)

![Glowsquid Filters UI](https://i.imgur.com/waZqJ5N.png)

## Usage
Download the latest version from releases or clone and build it yourself with gradle.

``./gradlew build``

Administrator permission is required for WinDivert to work correctly.

Currently, only Windows is supported. Access tokens are required to intercept the login process and are loaded automatically from the default Minecraft and Lunar Client directories. If tokens fail to load, you can still add them manually via the menu bar. Access tokens are never saved.

## NOTE
This project is still a bit of a mess and I've only recently cleaned it up a bit. I plan to slowly improve it in my free time, but for now expect bugs and bad performance. 