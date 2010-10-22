#!/bin/bash
#
# compile.sh
# Build the source code
#
# Copyright © 2009-2010 Operational Dynamics Consulting, Pty Ltd
# 
# The code in this file, and the library it is a part of, are made available
# to you by the authors under the terms of the "GNU General Public Licence
# version 2". See the LICENCE file for the terms governing usage and
# redistribution.
#

source .config

if [ ! -d  tmp/classes ] ; then
	echo -e "MKDIR\ttmp/"
	mkdir -p tmp/classes
	mkdir -p tmp/unittests
fi

if [ ! -f tmp/stamp/build-core ] ; then
	touch -d "2001-01-01" tmp/stamp/build-core
fi
find src -type f -name '*.java' -newer tmp/stamp/build-core > tmp/list-core

if [ -s tmp/list-core ] ; then
	echo -n "${JAVAC_CMD}"
	sed -e 's/^/\t/' < tmp/list-core
	${JAVAC} \
		-classpath ${GNOME_JARS}:${XOM_JARS} \
		-d tmp/classes \
		-sourcepath src \
		`cat tmp/list-core`
	if [ $? -ne 0 ] ; then
		exit $?
	fi
	touch tmp/stamp/build-core
fi
