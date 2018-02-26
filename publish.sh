#!/usr/bin/env bash
./gradlew :library:clean :library:assembleRelease :library:generatePomFileForReleasePublication :library:bintrayUpload