#!/usr/bin/env bash
set -e

# this will generate resolve-ip-host
./resolve-ips.sh

sudo cp /etc/hosts /etc/host-backup
sudo cp "./resolved-ip-host" /etc/hosts
sudo cat /etc/host-backup >> /etc/hosts
