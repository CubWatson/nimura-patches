#!/usr/bin/env bash
# Builds build/nimurapatches-<version>.jar with only a JDK 21 (no Gradle, no network).
#
# The mixins target other mods by class *name* (strings), so the only things we compile against are a few
# Minecraft / NeoForge / Mixin signatures. Those live in stubs/ as tiny compile-only copies: they are
# never packaged, and the real classes are used at runtime. Every stub signature was checked against the
# real game/mod bytecode (javap) before use.
set -euo pipefail
cd "$(dirname "$0")"
VERSION=$(sed -n 's/^version="\(.*\)"/\1/p' src/main/resources/META-INF/neoforge.mods.toml)
rm -rf build && mkdir -p build/stubs build/classes
javac --release 21 -nowarn -d build/stubs $(find stubs -name '*.java')
javac --release 21 -Xlint:-options -cp build/stubs -d build/classes $(find src/main/java -name '*.java')
jar --create --file "build/nimurapatches-$VERSION.jar" -C build/classes . -C src/main/resources .
echo "Built build/nimurapatches-$VERSION.jar"
