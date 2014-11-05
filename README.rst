TabKeeper
=========

TabKeeper is an application that helps you keep track of that you have been
eating and drinking on a restaurant. This can be especially useful if you're
out with several people, and don't pay for your drinks until you're about to
leave. When you need to remember what your share of the bill was, you can
simply look it up in TabKeeper.

Cool! How does it work?
-----------------------

The current version (v0.7) of TabKeeper is an unofficial branch for a pub in
Kristiansand, Norway, (Christiansand Brygghus) and has been branded as such. It
has been implemented in Android/Java and in its current state populates the
"menu" via a CSV file that is compiled into the Android .APK file. In future
versions, this CSV file can be sent to the device remotely (e.g. due to updates
on the menu), or the user can poll for changes from a remote server.

The user can also store custom items on the menu. All custom items, and
historic data, are stored on the local device as JSON using the great Google
GSON library.

What are the future plans with TabKeeper?
-----------------------------------------

I am aiming to make TabKeeper a "generic" open source tabkeeping application.
As such, it is currently licensed under LGPL 2.1. If there is any interest for
making commercial branches (e.g. for specific restaurants or pubs, like the
current version is branded for "Christiansand Brygghus"), this is totally fine.

Can I try it out without compiling it myself?
---------------------------------------------

Yes you can! A link to the latest compiled version can be found at:
http://x.0r.no/f/tabkeeper/tabkeeper-nightly.apk
