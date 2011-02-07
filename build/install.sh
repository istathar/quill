#!/bin/bash
#
# install.sh
# Install the source code
#
# Copyright © 2010-2011 Operational Dynamics Consulting, Pty Ltd
# 
# The code in this file, and the library it is a part of, are made available
# to you by the authors under the terms of the "GNU General Public Licence
# version 2". See the LICENCE file for the terms governing usage and
# redistribution.
#

source .config


#
# Build .jar file if necessary
#

if [ -f tmp/quill.jar ] ; then
	find tmp/classes -type f -name '*.class' -newer tmp/quill.jar > tmp/stamp/list-classes
else
	echo "ALL" > tmp/stamp/list-classes
fi

if [ -s tmp/stamp/list-classes ] ; then
	echo -e "${JAR_CMD}\ttmp/quill.jar"
	${JAR} -cf tmp/quill.jar -C tmp/classes .
fi
rm -f tmp/stamp/list-classes

#
# Install to prefix
#

install_mkdir () {
	if [ ! -d $1 ] ; then
		echo -e "MKDIR\t$1"
		mkdir -p $1
	fi
}

install_file () {
	if [ $1 -nt $2 ] ; then
		echo -e "INSTALL\t$2"
		cp -f $1 $2
	fi
}

install_chmod () {
	if [ ! -x $1 ] ; then
		echo -e "CHMOD\t$1"
		chmod +x $1
	fi
}

install_mkdir ${DESTDIR}${PREFIX}/bin
install_file tmp/launcher/quill-install ${DESTDIR}${PREFIX}/bin/quill
install_chmod ${DESTDIR}${PREFIX}/bin/quill

install_mkdir ${DESTDIR}${PREFIX}/share/applications
install_file tmp/launcher/quill.desktop ${DESTDIR}${PREFIX}/share/applications/quill.desktop

install_mkdir ${DESTDIR}${PREFIX}/share/mime/packages
install_file tmp/launcher/quill.xml ${DESTDIR}${PREFIX}/share/mime/packages/quill.xml
install_mkdir ${DESTDIR}${PREFIX}/share/icons/hicolor/48x48/mimetypes
install_file share/icons/hicolor/48x48/apps/quill-and-parchment.png ${DESTDIR}${PREFIX}/share/icons/hicolor/48x48/mimetypes/application-x-parchment.png

install_mkdir ${DESTDIR}${PREFIX}/share/icons/hicolor/48x48/apps
install_file share/icons/hicolor/48x48/apps/quill-and-parchment.png ${DESTDIR}${PREFIX}/share/icons/hicolor/48x48/apps/quill-and-parchment.png

install_mkdir ${DESTDIR}${PREFIX}/share/quill/images
for i in share/quill/images/*.png
do
	install_file $i ${DESTDIR}${PREFIX}/$i
done

for i in share/locale/*/LC_MESSAGES/quill.mo
do
	install_mkdir `dirname ${DESTDIR}${PREFIX}/$i`
	install_file $i ${DESTDIR}${PREFIX}/$i
done

install_mkdir ${DESTDIR}${JARDIR}
install_file tmp/quill.jar ${DESTDIR}${JARDIR}/quill-${APIVERSION}.jar

exit 0;
