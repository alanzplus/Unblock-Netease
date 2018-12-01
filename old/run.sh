#!/usr/bin/env bash
_JAVA=$(which java)
_HOSTS='p1.music.126.net p2.music.126.net p3.music.126.net p4.music.126.net m10.music.126.net img3.126.net'
_OPTS="-Dunblock=158.69.209.100 -DreplaceHost=true -Ddns.output=dns.output -Ddns.timeout=10 -Ddns.retry=1 -Ddns.concurrency=5 -Dping.timeout=3 -Dping.count=3 -Dping.retry=1 -Dping.concurrency=16"
mvn clean package && clear && sudo $_JAVA $_OPTS -jar target/dns-1.0.0-SNAPSHOT-jar-with-dependencies.jar $_HOSTS
