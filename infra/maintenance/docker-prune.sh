#!/usr/bin/env bash
set -euo pipefail
docker container prune -f --filter "until=24h"
docker image prune -f
