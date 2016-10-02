DNS
--

A quick-and-dirty DNS tool written in Scala. Motivation of this project is to unlock the access to Netease Music.

# Current Feature
1. lookup a list of hosts and for each host returns the fast resolved IP
2. automatically backup your `/etc/host` and replace it with the generated one (for unblocking Netease Music)

# Unblock Netease Music
Execute `./run.sh`. It will takes some time to process. After processing it should be able to use both the web version and desktop version. For iPhone , simply setup a HTTP proxy on your desktop and configure your iPhone to use the proxy.

Sometimes, it may fail to resolve the host due to network issue and just try to run the script another time.
