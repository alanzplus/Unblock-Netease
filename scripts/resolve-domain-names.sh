#!/usr/bin/env bash
set -e

readonly script_dir=$(cd "$(dirname $0)"; pwd)
readonly project_root=$(cd "${script_dir}"/../; pwd)

if [[ $1 == "rebuild" ]]; then
  echo "rebuild..."
  "${project_root}"/gradlew clean install
fi

readonly output="${script_dir}/resolved"

readonly domainsToLookup='p1.music.126.net p2.music.126.net p3.music.126.net p4.music.126.net m10.music.126.net img3.126.net'

JAVA_OPTS="
-Dmusic163DomainName=music.163.com
-DunblockYouKuIp=158.69.209.100
-Ddns.query.concurrency=10
-Ddns.query.timeoutIntSeconds=10
-Dping.concurrency=30
-Dping.timeoutInSeconds=10
-Dout.hostfile=${output}
" ./build/install/Unblock-Netease/bin/Unblock-Netease ${domainsToLookup}
