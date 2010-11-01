#
# Makefile
#
# Copyright © 2009 Operational Dynamics Consulting, Pty Ltd
# 
# The code in this file, and the library it is a part of, are made available
# to you by the authors under the terms of the "GNU General Public Licence
# version 2". See the LICENCE file for the terms governing usage and
# redistribution.
#

ifdef V
MAKEFLAGS=-R
else
MAKEFLAGS=-s -R
REDIRECT=>/dev/null
endif

.PHONY: all dirs compile test install clean distclean

all: .config dirs compile 

.config: src/quill/client/Version.java
	/bin/echo
	/bin/echo "You need to run ./configure to check prerequisites"
	/bin/echo "and setup preferences before you can build Quill and Parchment."
	( if [ ! -x configure ] ; then chmod +x configure ; /bin/echo "I just made it executable for you." ; fi )
	/bin/echo
	exit 1

-include .config

dirs: tmp/stamp

tmp/stamp:
	@/bin/echo -e "MKDIR\t$@"
	mkdir $@

# --------------------------------------------------------------------
# Source compilation
# --------------------------------------------------------------------

#
# build the sources (that are part of the distributed app)
#


compile: dirs
	build/compile.sh

test: compile
	build/tests.sh




# --------------------------------------------------------------------
# Installation
# --------------------------------------------------------------------

install: compile
	build/install.sh


# --------------------------------------------------------------------
# House keeping
# --------------------------------------------------------------------

# [note that we don't remove .config here, as a) darcs doesn't pick it up
# so if it's hanging around it won't cause problems, and b) if it is removed 
# here, then `make clean all` fails]
clean:
	@/bin/echo -e "RM\ttemporary build directories"
	-rm -rf tmp/classes
	-rm -rf tmp/stamp
	-rm -rf hs_err_*
	@/bin/echo -e "RM\texecutables and wrappers"
	-rm -f tmp/quill.jar
	-rm -f quill
	@/bin/echo -e "RM\tgenerated message files"
	-rm -rf share/locale
	-rm -f tmp/i18n/quill.pot

distclean: clean
	@/bin/echo -e "RM\tbuild configuration information"
	-rm -f .config .config.tmp
	-rm -rf tmp/
#	@/bin/echo "RM        generated documentation"
#	-rm -f doc/api/*

# --------------------------------------------------------------------
# Distribution target
# --------------------------------------------------------------------

#
# Remember that if you bump the version number you need to commit the change
# and re-./configure before being able to run this! On the other hand, we
# don't have to distclean before calling this.
#
dist: all
	@/bin/echo -e "CHECK\tfully committed state"
	bzr diff > /dev/null || ( /bin/echo -e "\nYou need to commit all changes before running make dist\n" ; exit 4 )
	@/bin/echo -e "EXPORT\ttmp/quill-$(VERSION)"
	-rm -rf tmp/quill-$(VERSION)
	bzr export --format=dir tmp/quill-$(VERSION)
	@/bin/echo -e "TAR\tquill-$(VERSION).tar.bz2"
	tar cjf quill-$(VERSION).tar.bz2 -C tmp quill-$(VERSION)
	rm -r tmp/quill-$(VERSION)


