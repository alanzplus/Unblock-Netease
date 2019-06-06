#!/usr/bin/env bash
set -e

readonly script_dir=$(cd "$(dirname $0)"; pwd)
readonly project_root=$(cd "${script_dir}"/../; pwd)

"${script_dir}"/resolve-domain-names.sh "$@"

sudo cp /etc/hosts /etc/host-backup
cp "${script_dir}/resolved" /tmp/hosts
cat /etc/host-backup >> /tmp/hosts
sudo cp /tmp/hosts /etc/hosts
