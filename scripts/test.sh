#!/usr/bin/env bash
#
# Runs the scripts in tests/ on every loader and reports which pass.
#
#   ./scripts/test.sh                   fabric, neoforge and paper, one after the other
#   ./scripts/test.sh fabric paper      only these
#
# For each loader it empties that loader's dev scripts folder, copies the tests in, starts the game (or the
# server), waits for the tests to finish, stops it and reads the log. The folders it owns:
#
#   fabric    src/fabric/run/tums/scripts
#   neoforge  src/neoforge/run/tums/scripts
#   paper     src/paper/run/plugins/Tums/scripts
#
# so scripts of your own in there are deleted. Logs end up in build/tums-tests/<loader>.log.
#
# A test is a .tsn file in tests/ named NN_<name>.tsn. It prints "[T] <name> start" first and
# "[T] <name> ok" once everything it checks has worked. Two comment lines change how it is run:
#
#   # fabric neoforge   a line of nothing but loader names: only copy the test to those loaders
#   # expect error      the test passes by stopping with an error, and must not get to its ok line
#
# Any line of output starting with "wrong" fails the test it appears in, so a test can print one from a
# branch that must not run.
#
# Settings, as environment variables:
#   TUMS_TEST_TIMEOUT  seconds to wait for a loader before giving up (600)
#   TUMS_TEST_IDLE     seconds without new test output before a loader counts as done (8)
#
set -uo pipefail

HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT="$(cd "$HERE/.." && pwd)"
TESTS="$ROOT/tests"
LOGS="$ROOT/build/tums-tests"
TIMEOUT="${TUMS_TEST_TIMEOUT:-600}"
IDLE="${TUMS_TEST_IDLE:-8}"
LOADERS_ALL="fabric neoforge paper"

run_dir(){ echo "$ROOT/src/$1/run"; }
scripts_dir(){ case "$1" in
    paper) echo "$(run_dir paper)/plugins/Tums/scripts";;
    *)     echo "$(run_dir "$1")/tums/scripts";;
esac; }
gradle_task(){ case "$1" in
    paper) echo ":paper:runServer";;
    *)     echo ":$1:runClient";;
esac; }

# the name a test prints: its file name without the number in front
test_name(){ basename "$1" .tsn | sed 's/^[0-9]*_//'; }

# the loaders a test is for: those on a line of nothing but loader names, or all of them
test_loaders(){
    local line
    line="$(grep -E '^[[:space:]]*#[[:space:]]*((fabric|neoforge|paper)[[:space:]]*)+$' "$1" | head -1)"
    if [ -n "$line" ]; then echo "$line" | tr -d '#' | xargs; else echo "$LOADERS_ALL"; fi
}

expects_error(){ grep -qE '^[[:space:]]*#[[:space:]]*expect error[[:space:]]*$' "$1"; }

# The lines the scripts printed, without the log's own prefix. Fabric and NeoForge log them as
# "... [STDOUT]: text", Paper as "[12:00:00 INFO]: text" (the first one with "[Tums] [STDOUT] " in front)
script_output(){
    sed 's/\x1b\[[0-9;]*m//g' "$1" | awk '
        index($0, "[STDOUT]: ") { print substr($0, index($0, "[STDOUT]: ") + 10); next }
        /^\[[0-9:]+ INFO\]: / { line = $0; sub(/^\[[0-9:]+ INFO\]: /, "", line); sub(/^\[Tums\] \[STDOUT\] /, "", line); print line }
    ' | sed 's/^"\(\[T\] .*\)"$/\1/'
}

# Stops a loader's game or server: Gradle first, then the java process running in its run folder, which
# a Gradle daemon leaves behind
stop_loader(){
    local pid="$1" dir p i
    dir="$(run_dir "$2")"
    kill -INT "$pid" 2>/dev/null
    for p in $(pgrep java); do
        [ "$(readlink "/proc/$p/cwd" 2>/dev/null)" = "$dir" ] && kill "$p" 2>/dev/null
    done
    for i in $(seq 1 20); do
        local left=0
        for p in $(pgrep java); do [ "$(readlink "/proc/$p/cwd" 2>/dev/null)" = "$dir" ] && left=1; done
        [ "$left" = 0 ] && break
        sleep 1
    done
    for p in $(pgrep java); do
        [ "$(readlink "/proc/$p/cwd" 2>/dev/null)" = "$dir" ] && kill -9 "$p" 2>/dev/null
    done
    wait "$pid" 2>/dev/null
}

CURRENT_PID=""; CURRENT_LOADER=""
trap '[ -n "$CURRENT_PID" ] && stop_loader "$CURRENT_PID" "$CURRENT_LOADER"; exit 130' INT TERM

# ---------------------------------------------------------------------------- run

LOADERS="${*:-$LOADERS_ALL}"
for loader in $LOADERS; do
    case " $LOADERS_ALL " in *" $loader "*) ;; *) echo "unknown loader: $loader (use: $LOADERS_ALL)" >&2; exit 2;; esac
done
[ -d "$TESTS" ] || { echo "no tests folder at $TESTS" >&2; exit 2; }
mkdir -p "$LOGS"
cd "$ROOT"

for loader in $LOADERS; do
    dir="$(scripts_dir "$loader")"
    mkdir -p "$dir"
    rm -f "$dir"/*.tsn
    names=()
    for file in "$TESTS"/*.tsn; do
        case " $(test_loaders "$file") " in *" $loader "*)
            cp "$file" "$dir/"
            names+=("$(test_name "$file")");;
        esac
    done
    printf '%s\n' "${names[@]}" > "$LOGS/$loader.tests"
    log="$LOGS/$loader.log"
    echo "== $loader: ${#names[@]} tests, $(gradle_task "$loader")"

    ./gradlew "$(gradle_task "$loader")" > "$log" 2>&1 < /dev/null &
    CURRENT_PID=$!; CURRENT_LOADER="$loader"

    # done once every test has started and nothing new has been printed for a while
    start=$SECONDS; last_count=-1; last_change=$SECONDS; outcome="timed out"
    while true; do
        sleep 1
        if ! kill -0 "$CURRENT_PID" 2>/dev/null; then outcome="stopped by itself"; break; fi
        if grep -q "BUILD FAILED" "$log"; then outcome="build failed"; break; fi
        output="$(script_output "$log")"
        started=$(grep -c '^\[T\] [a-z_0-9]* start$' <<< "$output")
        count=$(wc -l <<< "$output")
        if [ "$count" != "$last_count" ]; then last_count=$count; last_change=$SECONDS; fi
        if [ "$started" -ge "${#names[@]}" ] && [ $((SECONDS - last_change)) -ge "$IDLE" ]; then outcome="done"; break; fi
        if [ $((SECONDS - start)) -ge "$TIMEOUT" ]; then break; fi
    done
    stop_loader "$CURRENT_PID" "$loader"
    CURRENT_PID=""
    echo "   $outcome after $((SECONDS - start))s, log: ${log#"$ROOT"/}"
done

# ---------------------------------------------------------------------------- report

# PASS, FAIL or -- (not run on that loader) for one test on one loader
verdict(){
    local name="$1" loader="$2" file="$3" output block
    grep -qx "$name" "$LOGS/$loader.tests" 2>/dev/null || { echo "--"; return; }
    output="$(script_output "$LOGS/$loader.log")"
    grep -qx "\[T\] $name start" <<< "$output" || { echo "MISSING"; return; }
    # the test's own output: from its start line to the next test's
    block="$(awk -v s="[T] $name start" '$0 == s { on = 1; print; next } on && /^\[T\] [a-z_0-9]+ start$/ { exit } on' <<< "$output")"
    local ok=0 error=0 wrong=0
    grep -qx "\[T\] $name ok" <<< "$block" && ok=1
    grep -q '^================ ERROR ================$' <<< "$block" && error=1
    grep -q '^wrong' <<< "$block" && wrong=1
    if expects_error "$file"; then
        [ "$error" = 1 ] && [ "$ok" = 0 ] && [ "$wrong" = 0 ] && echo PASS || echo FAIL
    else
        [ "$ok" = 1 ] && [ "$error" = 0 ] && [ "$wrong" = 0 ] && echo PASS || echo FAIL
    fi
}

echo
printf '%-34s' "test"; for loader in $LOADERS; do printf '%-10s' "$loader"; done; echo
failed=0; passed=0; failures=""
for file in "$TESTS"/*.tsn; do
    name="$(test_name "$file")"
    printf '%-34s' "$name"
    for loader in $LOADERS; do
        v="$(verdict "$name" "$loader" "$file")"
        printf '%-10s' "$v"
        case "$v" in
            PASS) passed=$((passed + 1));;
            FAIL|MISSING) failed=$((failed + 1)); failures="$failures$loader $name
";;
        esac
    done
    echo
done
echo
echo "$passed passed, $failed failed"

if [ -n "$failures" ]; then
    echo
    echo "failures, with what each printed:"
    while read -r loader name; do
        [ -n "$name" ] || continue
        echo "--- $name on $loader"
        script_output "$LOGS/$loader.log" \
            | awk -v s="[T] $name start" '$0 == s { on = 1; print; next } on && /^\[T\] [a-z_0-9]+ start$/ { exit } on' \
            | head -15 | sed 's/^/    /'
    done <<< "$failures"
    exit 1
fi
