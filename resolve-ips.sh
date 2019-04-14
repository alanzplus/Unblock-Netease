#!/usr/bin/env bash
set -e

if [[ $1 == "rebuild" ]]; then
  echo "rebuild..."
  ./gradlew clean install
fi

readonly domainsToLookup="p1.music.126.net p2.music.126.net p3.music.126.net p4.music.126.net m10.music.126.net img3.126.net"
JAVA_OPTS="
-Dmusic163DomainName=music.163.com
-DunblockYouKuIp=158.69.209.100
-Ddns.query.concurrency=10
-Ddns.query.timeoutIntSeconds=10
-Dping.concurrency=30
-Dping.timeoutInSeconds=10
-Dout.hostfile=./resolved-ip-host
" ./build/install/Unblock-Netease/bin/Unblock-Netease ${domainsToLookup}
