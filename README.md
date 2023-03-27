# Glowsquid

Glowsquid intercepts and redirects system Minecraft network traffic through itself, acting as a proxy. While online-mode Minecraft traffic is normally encrypted, Glowsquid hijacks the login key exchange to intercept the encryption key and decrypt the traffic.

Glowsquid provides an interactive packet view UI, where packets can be easily analyzed and broken down. There is additionally a basic packet filtering system, which supports blocking and modifying packets based on their fields.

![Glowsquid UI](https://i.imgur.com/rXdMmcG.png)

![Glowsquid Filters UI](https://i.imgur.com/waZqJ5N.png)

## Usage
Download the latest version from releases or clone and build it yourself with gradle.

``./gradlew build``

Administrator permission is required for WinDivert to work correctly.

Currently, only Windows and Minecraft 1.7.10 / 1.8.9 are supported. Access tokens are required to intercept the login process and are loaded automatically from the default Minecraft and Lunar Client directories. If tokens fail to load, you can still add them manually via the menu bar. Access tokens are never saved.

Open the program before connecting to a server from any game client. Glowsquid will immediately begin intercepting the connection.

## Modules
Modules are plugins that can be used to capture and modify packets intercepted by Glowsquid.
You can load modules on startup by placing them in the ``modules`` folder, or load them on the spot through the UI.

### Developing Modules
Clone the repository, build it to your maven cache, and add the following to your build.gradle:

```groovy
dependencies {
    implementation "me.javlin:glowsquid:1.1-SNAPSHOT"
}
```
Modules can primarily utilize a packet event system.

```java
public class ExampleModule extends Module {
    @PacketEvent
    public void onPluginMessage(PacketSendEvent<PacketPluginMessage> event) {
        PacketPluginMessage packet = event.getPacket();
        
        if (packet.getChannel().equals("MC|Brand")) {
            packet.setMessage("glowsquid".getBytes());
        }
    }
}
```
The ``@PacketEvent`` annotation is used to register a method as a packet event handler.
The method must take a single parameter of type ``PacketSendEvent<? extends Packet>``. 
The event type is determined by the generic type of the parameter.

The parent ProxySession of each module is provided via the Module class and can be used to queue packets to be sent to the client or server.
It also can be used to schedule repeating tasks, which are cancelled when the module is unloaded.

One or more Module classes can be present in a module JAR. No manifest is required. Some packets do not yet have implementations and will not be processed by the event system.

An example module is provided in the ``modules/example`` folder.
## NOTE
This project is still a bit of a mess, and I've only recently cleaned it up a bit. I plan to slowly improve it in my free time, but for now expect bugs and bad performance. 