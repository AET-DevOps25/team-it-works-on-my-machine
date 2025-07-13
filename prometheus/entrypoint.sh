#!/bin/sh
# Expand environment variables into prometheus.yml
envsubst < /etc/prometheus/prometheus.yml.template > /etc/prometheus/prometheus.yml

# Run Prometheus
exec /bin/prometheus --config.file=/etc/prometheus/prometheus.yml
