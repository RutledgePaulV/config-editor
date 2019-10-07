#!/usr/bin/env bash
set -e

export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-10.0.1.jdk/Contents/Home
lein uberjar
