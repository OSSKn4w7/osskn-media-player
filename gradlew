#!/bin/sh

# Copyright (C) 2015 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# For backwards compatibility, if the GRADLE_OPTS environment variable is set, prepend it
# to JAVA_OPTS. Only do this if GRADLE_OPTS is set and JAVA_OPTS isn't, to avoid duplication.
if [ -n "$GRADLE_OPTS" ] && [ -z "$JAVA_OPTS" ]; then
    JAVA_OPTS="$GRADLE_OPTS"
    export JAVA_OPTS
fi

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD="maximum"

warn () {
    echo "$*"
} >&2

die () {
    echo
    echo "$*"
    echo
    exit 1
} >&2

# OS specific support (must be 'true' or 'false').
cygwin=false
msys=false
darwin=false
nonstop=false
case "$(uname)" in
  CYGWIN* )
    cygwin=true
    ;;
  Darwin* )
    darwin=true
    ;;
  MINGW* )
    msys=true
    ;;
  NONSTOP* )
    nonstop=true
    ;;
esac

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        # IBM's JDK on AIX uses strange locations for the executables
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
fi

# Increase the maximum file descriptors if we can.
if ! "$cygwin" && ! "$darwin" && ! "$nonstop" ; then
    case $MAX_FD in
      max*)
        # In POSIX sh, ulimit -H is undefined. That's why the result is checked to see if
        # it is valid prior to being used.
        MAX_FD=$(ulimit -H -n) ||
            warn "Could not query maximum file descriptor limit"
    esac
    case $MAX_FD in
      '' | soft | unlimited | unknown | -1)
        # Fall back to a simpler calculation
        MAX_FD=65536
        ;;
      *)
        # In POSIX sh, ulimit -n is undefined. That's why the result is checked to see if
        # it is valid prior to being used.
        ulimit -n $MAX_FD ||
            warn "Could not set maximum file descriptor limit to $MAX_FD"
        ;;
    esac
fi

# For Darwin, add options to specify how the application appears in the dock
if "$darwin"; then
    GRADLE_OPTS="$GRADLE_OPTS -Xdock:name=Gradle -Xdock:icon=$APP_HOME/media/gradle.icns"
fi

# For Cygwin or MSYS, switch paths to Windows format before running java
if "$cygwin" || "$msys" ; then
    APP_HOME=$(cygpath --path --mixed "$APP_HOME")
    CLASSPATH=$(cygpath --path --mixed "$CLASSPATH")

    # Now convert the arguments - kludge to limit ourselves to /bin/sh
    for arg do
        if
            case $arg in                                #(
              -*)   false ;;                           # don't mess with options #(
              /?*)  t=${arg#/} t=/${t%%/*}            # looks like a POSIX filepath
                    [ -e "$t" ] ;;                    #(
              *)    false ;;
            esac ; then
            eval $echo_arg=\$\(cygpath --path --mixed "$arg"\)
        fi
    done
fi

# Split up the JVM_OPTS And GRADLE_OPTS values into an array, following the shell rules
IFS=' ' read -ra jvm_opts_array <<< "$JVM_OPTS"
IFS=' ' read -ra gradle_opts_array <<< "$GRADLE_OPTS"

exec "$JAVACMD" "${jvm_opts_array[@]}" "${gradle_opts_array[@]}" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"