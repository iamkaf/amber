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

publish-mod-version version *args:
  @if [ ! -f "versions/{{version}}/gradle.properties" ]; then echo "Unknown version: {{version}}"; exit 1; fi
  @tasks=(); for loader in $(just list-loaders "{{version}}"); do suffix=$(printf '%s-%s\n' "{{version}}" "$loader" | sed -E 's/[^A-Za-z0-9]+/ /g' | awk '{ out=""; for (i=1; i<=NF; i++) out = out toupper(substr($i,1,1)) substr($i,2); print out }'); tasks+=("publishCurseforge$suffix" "publishModrinth$suffix"); done; ./gradlew "${tasks[@]}" {{args}} --console=plain

publish-all *args:
  @test -n "${MAVEN_PUBLISH_USERNAME:-}" || (echo "MAVEN_PUBLISH_USERNAME is required" >&2; exit 1)
  @test -n "${MAVEN_PUBLISH_PASSWORD:-}" || (echo "MAVEN_PUBLISH_PASSWORD is required" >&2; exit 1)
  @for version in $(just list-versions); do echo "==> publish $version"; just publish-version "$version" {{args}}; done

compile-all:
  @tasks=(); for version in $(just list-versions); do tasks+=(":common:$version:compileJava"); for loader in $(just list-loaders "$version"); do tasks+=(":$loader:$version:compileJava"); done; done; ./gradlew --configure-on-demand "${tasks[@]}" --console=plain

run-client node:
  @if ! just list-nodes | grep -Fxq "{{node}}"; then echo "Unknown node: {{node}}"; exit 1; fi
  @version="{{node}}"; loader="${version##*-}"; version="${version%-*}"; ./gradlew --configure-on-demand ":$loader:$version:runClient" --console=plain

boot-check node timeout="120":
  @if ! just list-nodes | grep -Fxq "{{node}}"; then echo "Unknown node: {{node}}"; exit 1; fi
  @node="{{node}}"; version="${node%-*}"; loader="${node##*-}"; \
    task=":$loader:$version:runClient"; \
    if [ "$node" = "1.16.5-forge" ]; then task=":forge:1.16.5:runLegacyClient"; fi; \
    log="/tmp/amber-$node.boot.log"; \
    status=0; timeout --kill-after=10s "{{timeout}}s" ./gradlew --configure-on-demand --no-daemon "$task" --console=plain \
      -Damber.withTeaKit=true -Dteakit.autoExitTitle=true -Dteakit.autoExitTitleDelayMs=2500 > "$log" 2>&1 || status=$?; \
    if [ "$status" -ne 0 ] || ! grep -q 'TeaKit scheduling clean shutdown from title screen' "$log" \
      || grep -q 'Mods loaded with .* warning' "$log"; then \
      pkill -f "$PWD/$loader/versions/$version/" 2>/dev/null || true; \
      tail -n 100 "$log"; echo "Startup failed: $node (status=$status)"; exit 1; \
    fi; \
    echo "Title screen and clean shutdown OK: $node"

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
