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

.PHONY: all dirs compile test translation install clean distclean

all: .config dirs compile translation quill

.config: src/quill/client/Version.java
	/bin/echo
	/bin/echo "You need to run ./configure to check prerequisites"
	/bin/echo "and setup preferences before you can build Quill and Parchment."
	( if [ ! -x configure ] ; then chmod +x configure ; /bin/echo "I just made it executable for you." ; fi )
	/bin/echo
	exit 1

-include .config

SOURCES_DIST=$(shell find src -name '*.java' | sort)
TRANSLATIONS=$(shell find po/ -name '*.po' | sed -e 's/po\/\(.*\)\.po/share\/locale\/\1\/LC_MESSAGES\/quill\.mo/g')

dirs: tmp/classes tmp/stamp tmp/i18n

tmp/classes:
	@/bin/echo -e "MKDIR\t$@"
	mkdir $@

tmp/stamp:
	@/bin/echo -e "MKDIR\t$@"
	mkdir $@

tmp/i18n:
	@/bin/echo -e "MKDIR\t$@"
	mkdir $@

# --------------------------------------------------------------------
# Source compilation
# --------------------------------------------------------------------

#
# build the sources (that are part of the distributed app)
#


compile:
	build/compile.sh

test: compile
	build/tests.sh


translation: tmp/i18n/quill.pot $(TRANSLATIONS)

# strictly speaking, not necessary to generate the .pot file, but this has to
# go somewhere and might as well get it done

tmp/i18n/quill.pot: $(SOURCES_DIST)
	@/bin/echo -e "EXTRACT\t$@"
	xgettext -o $@ --omit-header --from-code=UTF-8 --keyword=_ --keyword=N_ $^

share/locale/%/LC_MESSAGES/quill.mo: po/%.po
	mkdir -p $(dir $@)
	@/bin/echo -e "MSGFMT\t$@"
	msgfmt -o $@ $<


quill: tmp/launcher/quill-local
	@/bin/echo -e "CP\t$@"
	cp -f $< $@
	chmod +x $@


# --------------------------------------------------------------------
# Installation
# --------------------------------------------------------------------

install: all \
		$(DESTDIR)$(JARDIR)/quill-$(APIVERSION).jar \
	 	tmp/stamp/install-pixmaps \
	 	tmp/stamp/install-translations \
		$(DESTDIR)$(PREFIX)/share/applications/quill.desktop \
		$(DESTDIR)$(PREFIX)/bin/quill

$(DESTDIR)$(PREFIX):
	@/bin/echo -e "MKDIR\t$(DESTDIR)$(PREFIX)/"
	-mkdir -p $(DESTDIR)$(PREFIX)

$(DESTDIR)$(PREFIX)/bin:
	@/bin/echo -e "MKDIR\t$@/"
	-mkdir -p $@

$(DESTDIR)$(JARDIR):
	@/bin/echo -e "MKDIR\t$@/"
	-mkdir -p $@

$(DESTDIR)$(PREFIX)/share/applications:
	@/bin/echo -e "MKDIR\t$@/"
	-mkdir -p $@

$(DESTDIR)$(PREFIX)/bin/quill: \
		$(DESTDIR)$(PREFIX)/bin \
		tmp/launcher/quill-install
	@/bin/echo -e "INSTALL\t$@"
	cp -f tmp/launcher/quill-install $@
	chmod +x $@

$(DESTDIR)$(PREFIX)/share/applications/quill.desktop: \
		$(DESTDIR)$(PREFIX)/share/applications \
		tmp/launcher/quill.desktop
	@/bin/echo -e "INSTALL\t$@"
	cp -f tmp/launcher/quill.desktop $@

tmp/quill.jar: compile
	@/bin/echo -e "$(JAR_CMD)\t$@"
	$(JAR) -cf $@ -C tmp/classes .

$(DESTDIR)$(PREFIX)/share/pixmaps: 
	@/bin/echo -e "MKDIR\t$@/"
	-mkdir $@

$(DESTDIR)$(PREFIX)/share/locale: 
	@/bin/echo -e "MKDIR\t$@/"
	-mkdir $@

tmp/stamp/install-pixmaps: \
		$(DESTDIR)$(PREFIX)/share/pixmaps \
		share/pixmaps/*.png
	@/bin/echo -e "INSTALL\t$(DESTDIR)$(PREFIX)/share/pixmaps/*.png"
	cp -f share/pixmaps/*.png $(DESTDIR)$(PREFIX)/share/pixmaps
	touch $@

tmp/stamp/install-translations: \
		$(DESTDIR)$(PREFIX)/share/locale \
		share/locale/*/LC_MESSAGES/quill.mo
	@/bin/echo -e "INSTALL\t$(DESTDIR)$(PREFIX)/share/locale/*/LC_MESSAGES/quill.mo"
	cp -af share/locale/* $(DESTDIR)$(PREFIX)/share/locale
	touch $@

$(DESTDIR)$(JARDIR)/quill-$(APIVERSION).jar: \
		$(DESTDIR)$(JARDIR) \
		tmp/quill.jar
	@/bin/echo -e "INSTALL\t$@"
	cp -f tmp/quill.jar $@


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


