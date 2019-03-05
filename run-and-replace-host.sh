#!/usr/bin/env bash
set -e

# this will generate resolve-ip-host
./resolve-ips.sh "$@"

sudo cp /etc/hosts /etc/host-backup
cp "./resolved-ip-host" /tmp/hosts
cat /etc/host-backup >> /tmp/hosts
sudo cp /tmp/hosts /etc/hosts
