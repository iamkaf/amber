set shell := ["bash", "-euo", "pipefail", "-c"]

default:
  @just --list

list-versions:
  @find versions -mindepth 2 -maxdepth 2 -type f -name 'gradle.properties' -printf '%h\n' | xargs -r -n1 basename | sort -V

list-loaders version:
  @grep '^project.enabled-loaders=' "versions/{{version}}/gradle.properties" | head -n1 | cut -d= -f2- | tr ',' '\n' | sed 's/^[[:space:]]*//; s/[[:space:]]*$//' | sed '/^$/d'

list-nodes:
  @for props in versions/*/gradle.properties; do version=$(basename "$(dirname "$props")"); loaders=$(sed -nE 's/^project\.enabled-loaders=(.*)$/\1/p' "$props" | head -n1); for loader in $(printf '%s\n' "$loaders" | tr ',' '\n' | sed 's/^[[:space:]]*//; s/[[:space:]]*$//' | sed '/^$/d'); do echo "$version-$loader"; done; done | sort -V

projects:
  @./gradlew projects --console=plain

clean-generated:
  @rm -rf build versions/*/build common/versions fabric/versions forge/versions neoforge/versions

run version loader *args:
  @if ! just list-loaders "{{version}}" | grep -Fxq "{{loader}}"; then echo "Loader {{loader}} is not enabled for {{version}}"; exit 1; fi
  @./gradlew --configure-on-demand ":{{loader}}:{{version}}:{{args}}" --console=plain

build node:
  @if ! just list-nodes | grep -Fxq "{{node}}"; then echo "Unknown node: {{node}}"; exit 1; fi
  @version="{{node}}"; loader="${version##*-}"; version="${version%-*}"; ./gradlew --configure-on-demand ":$loader:$version:build" --console=plain

build-all:
  @./gradlew build --console=plain

publish-version version *args:
  @tasks=(":common:{{version}}:publishAllPublicationsToKafMavenRepository"); for loader in $(just list-loaders "{{version}}"); do tasks+=(":$loader:{{version}}:publishAllPublicationsToKafMavenRepository"); done; ./gradlew --configure-on-demand "${tasks[@]}" {{args}} --console=plain

compile-all:
  @tasks=(); for version in $(just list-versions); do tasks+=(":common:$version:compileJava"); for loader in $(just list-loaders "$version"); do tasks+=(":$loader:$version:compileJava"); done; done; ./gradlew --configure-on-demand "${tasks[@]}" --console=plain

run-client node:
  @if ! just list-nodes | grep -Fxq "{{node}}"; then echo "Unknown node: {{node}}"; exit 1; fi
  @version="{{node}}"; loader="${version##*-}"; version="${version%-*}"; ./gradlew --configure-on-demand ":$loader:$version:runClient" --console=plain

boot-check node timeout="80":
  @if ! just list-nodes | grep -Fxq "{{node}}"; then echo "Unknown node: {{node}}"; exit 1; fi
  @node="{{node}}"; version="${node%-*}"; loader="${node##*-}"; log="/tmp/amber-$node.run.log"; boot_marker='Initializing Everlasting Amber Dreams'; set +e; ./gradlew --configure-on-demand ":$loader:$version:runClient" -Pamber.withTeaKit=true --console=plain > "$log" 2>&1 & gradle_pid=$!; deadline=$(( $(date +%s) + {{timeout}} )); status=124; while [ "$(date +%s)" -lt "$deadline" ]; do if grep -q "$boot_marker" "$log"; then status=124; break; fi; if ! kill -0 "$gradle_pid" 2>/dev/null; then wait "$gradle_pid"; status=$?; break; fi; sleep 1; done; if kill -0 "$gradle_pid" 2>/dev/null; then kill "$gradle_pid" 2>/dev/null || true; wait "$gradle_pid" || true; fi; pkill -f "$PWD/$loader/versions/$version/" 2>/dev/null || true; set -e; if [ "$status" -ne 0 ] && [ "$status" -ne 124 ]; then tail -n 160 "$log"; exit "$status"; fi; grep -q "$boot_marker" "$log"; echo "Boot OK: $node (status=$status)"

boot-check-all timeout="80":
  @for node in $(just list-nodes); do echo "==> $node"; just boot-check "$node" "{{timeout}}"; done

scenario-check node timeout="180":
  @scripts/doctor-scenario-node.sh "{{node}}" "{{timeout}}"

scenario-check-all timeout="180":
  @for node in $(just list-nodes); do echo "==> $node"; just scenario-check "$node" "{{timeout}}"; done

scenario-log node:
  @log="/tmp/amber-{{node}}.doctor.run.log"; if [ -f "$log" ]; then less "$log"; else echo "No scenario log found at $log"; exit 1; fi

scenario-result node:
  @result="/tmp/amber-{{node}}.doctor.result.json"; if [ -f "$result" ]; then jq . "$result"; else echo "No scenario result found at $result"; exit 1; fi
