#!/usr/bin/env bash
docker run --rm -v "$(pwd)":/documents/ asciidoctor/docker-asciidoctor asciidoctor-pdf \
  -r asciidoctor-diagram \
  --section-numbers \
  --destination-dir=ad-output \
  --doctype=book \
  --out-file=iforium-casino-provider.pdf \
  --trace "$1"
