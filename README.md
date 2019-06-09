# Unblock Netease Music (Unblock 网易云音乐）

Unblock oversea access to [Netease Music (网易云音乐)](https://music.163.com/), a freemium music streaming service in China.

## How to Use

### Resolve Domain Names and Append to `/etc/host`

`./scripts/resolve-and-append.sh` backups your original `/etc/hosts` to `/etc/host-backup` and appends content of `./scripts/resolved` to `/etc/hosts`

Then you should be able to access the restricted contents using Netease Music application or browser.

### Resolve Domain Names Only
Run `./scripts/resolve-domain-names.sh` to resolve the domains names and outputs resolved domains `./scripts/resolved`.

For more configuration, please check the script.

### Rebuild Artifacts
This repo comes with pre-build artifacts in directory `build/install`. You can rebuild it by

```bash
./gradlew clean install
```

## Under the Hood
To access Netease Music we need to resolve the following domain names

```
music.163.com
p1.music.126.net
p2.music.126.net
p3.music.126.net
p4.music.126.net
m10.music.126.net
img3.126.net
```

The program automatically resolves the rest of domain names by looking up ChinaZ website

Then it generates a file `resolved`` of the same format as `/etc/hosts`

for example,

```
210.66.47.82 img3.126.net
112.123.33.135 m10.music.126.net
223.252.199.67 music.163.com
114.112.160.234 p2.music.126.net
110.53.75.73 p1.music.126.net
110.53.75.73 p3.music.126.net
110.53.75.73 p4.music.126.net
```

and we can append these entry into out system's `/etc/hosts`.

Then after restarting the Netease Music Application, it should be able to access the restricted content.
