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
	mkdir -p tmp/i18n
fi

if [ ! -f tmp/stamp/build-core ] ; then
	touch -d "2001-01-01" tmp/stamp/build-core
fi
find src -type f -name '*.java' -newer tmp/stamp/build-core > tmp/stamp/list-core

if [ -s tmp/stamp/list-core ] ; then
	echo -n "${JAVAC_CMD}"
	sed -e 's/^/\t/' < tmp/stamp/list-core
	${JAVAC} \
		-classpath ${GNOME_JARS}:${XOM_JARS} \
		-d tmp/classes \
		-sourcepath src \
		`cat tmp/stamp/list-core`
	if [ $? -ne 0 ] ; then
		exit $?
	fi
	touch tmp/stamp/build-core
	rm tmp/stamp/list-core
fi

# strictly speaking, not necessary to generate the .pot file, but this has to
# go somewhere and might as well get it done

if [ ! -f tmp/i18n/quill.pot ] ; then
	touch -d "2001-01-01" tmp/i18n/quill.pot
fi
find src -type f -name '*.java' -newer tmp/i18n/quill.pot > tmp/stamp/list-i18n
if [ -s tmp/stamp/list-i18n ] ; then
	rm tmp/stamp/list-i18n
	echo -e "EXTRACT\ttmp/i18n/quill.pot"
	find src -type f -name '*.java' > tmp/stamp/list-core
	xgettext -o tmp/i18n/quill.pot --omit-header --from-code=UTF-8 --keyword=_ --keyword=N_ `cat tmp/stamp/list-core`
	rm tmp/stamp/list-core
fi

#
# Compile translations
#

for i in po/*.po
do
	lang=`basename $i .po`
	if [ ! -d share/locale/$lang/LC_MESSAGES ] ; then
		echo -e "MKDIR\tshare/locale/$lang/LC_MESSAGES"
		mkdir -p share/locale/$lang/LC_MESSAGES
	fi
	if [ $i -nt share/locale/$lang/LC_MESSAGES/quill.mo ] ; then
		echo -e "MSGFMT\tshare/locale/$lang/LC_MESSAGES/quill.mo"
		msgfmt -o share/locale/$lang/LC_MESSAGES/quill.mo $i
	fi
done

if [ tmp/launcher/quill-local -nt quill ] ; then
	echo -e "CP\tquill"
	cp -f tmp/launcher/quill-local quill
	echo -e "CHMOD\tquill"
	chmod +x quill
fi
