# Unblock Netease Music (Unblock 网易云音乐）

Unblock the access to Netease(网易云音乐).

This repo resolves the domain names needed for accessing Netease Music outside China.

And it is tested with Netease Music Application (Mac Version).

## Mechanism
To access Netease we need to resolve the following domain names

```
music.163.com
p1.music.126.net
p2.music.126.net
p3.music.126.net
p4.music.126.net
m10.music.126.net
img3.126.net
```

For `music.163.com` we can simply use the ip provided by UnblockYouku, which is `158.69.209.100`

The program will automatically resolve the rest of domain names by looking up ChinaZ website and finally generate a `/etc/hosts` like file,

for example,

```
158.69.209.100 music.163.com
58.20.164.88 img3.126.net
183.62.114.251 m10.music.126.net
14.215.100.233 p2.music.126.net
14.215.100.233 p1.music.126.net
14.215.100.233 p3.music.126.net
14.215.100.233 p4.music.126.net
```

and we can append these entry into out system's `/etc/hosts`.

Then after restarting the Netease Music Application, it should be able to access the restricted content.

## How to Use

### Resolve IPS
Run `resolve-ips.sh` to resolve the domains names and it will generate a file called `resolved-ip-host`.

For more configuration, please check the script.

### Resolve IPs and Append them to `/etc/host`

Running `./run-and-replace-host.sh` will backup your original `/etc/hosts` to `/etc/host-backup` and replace it
with the resolved one.

Note that you should run the script with `sudo`
