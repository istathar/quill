/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2011 Operational Dynamics Consulting, Pty Ltd
 *
 * The code in this file, and the program it is a part of, is made available
 * to you by its authors as open source software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License version
 * 2 ("GPL") as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GPL for more details.
 *
 * You should have received a copy of the GPL along with this program. If not,
 * see http://www.gnu.org/licenses/. The authors of this program may be
 * contacted through http://research.operationaldynamics.com/projects/quill/.
 */
package quill.ui;

import org.gnome.gdk.Keyval;
import org.gnome.gdk.ModifierType;
import org.gnome.gtk.AcceleratorGroup;
import org.gnome.gtk.CheckMenuItem;
import org.gnome.gtk.ImageMenuItem;
import org.gnome.gtk.Menu;
import org.gnome.gtk.MenuBar;
import org.gnome.gtk.MenuItem;
import org.gnome.gtk.SeparatorMenuItem;
import org.gnome.gtk.Stock;

class OptionalMenuBar extends MenuBar
{
    private final MenuBar bar;

    private final PrimaryWindow primary;

    private final AcceleratorGroup group;

    OptionalMenuBar(PrimaryWindow primary) {
        super();
        this.bar = this;
        this.primary = primary;

        this.group = new AcceleratorGroup();
        this.primary.addAcceleratorGroup(group);

        setupManuscriptMenu();
        setupChapterMenu();
        setupEditMenu();
        setupViewMenu();
        setupFormatMenu();
        setupHelpMenu();
    }

    private void setupManuscriptMenu() {
        bar.append(new MenuItem("_Manuscript"));
    }

    private void setupChapterMenu() {
        bar.append(new MenuItem("_Chapter"));
    }

    private void setupEditMenu() {
        final Menu menu;
        final MenuItem edit;
        final ImageMenuItem undo, redo, cut, copy, paste;

        menu = new Menu();
        menu.setAcceleratorGroup(group);

        undo = new ImageMenuItem(Stock.UNDO);
        undo.setAccelerator(group, Keyval.z, ModifierType.CONTROL_MASK);
        menu.append(undo);

        redo = new ImageMenuItem(Stock.REDO);
        redo.setAccelerator(group, Keyval.y, ModifierType.CONTROL_MASK);
        menu.append(redo);

        menu.append(new SeparatorMenuItem());

        cut = new ImageMenuItem(Stock.CUT);
        cut.setAccelerator(group, Keyval.x, ModifierType.CONTROL_MASK);
        menu.append(cut);

        copy = new ImageMenuItem(Stock.COPY);
        copy.setAccelerator(group, Keyval.c, ModifierType.CONTROL_MASK);
        menu.append(copy);

        paste = new ImageMenuItem(Stock.PASTE);
        paste.setAccelerator(group, Keyval.v, ModifierType.CONTROL_MASK);
        menu.append(paste);

        /*
         * Build the actual top level item for the menu bar.
         */

        edit = new MenuItem("_Edit");
        edit.setSubmenu(menu);

        bar.append(edit);
    }

    private void setupViewMenu() {
        final Menu menu;
        final MenuItem view;
        final MenuItem editor, stylist, metaditor, preview, outline, endnotes, references;
        final CheckMenuItem fullscreen, single, optional;

        menu = new Menu();
        menu.setAcceleratorGroup(group);

        editor = new MenuItem("_Editor");
        editor.setAccelerator(group, Keyval.F1, ModifierType.NONE);
        menu.append(editor);

        stylist = new MenuItem("_Stylesheet");
        stylist.setAccelerator(group, Keyval.F2, ModifierType.NONE);
        menu.append(stylist);

        metaditor = new MenuItem("_Metadata");
        metaditor.setAccelerator(group, Keyval.F3, ModifierType.NONE);
        menu.append(metaditor);

        preview = new MenuItem("_Preview");
        preview.setAccelerator(group, Keyval.F5, ModifierType.NONE);
        menu.append(preview);

        outline = new MenuItem("_Outline");
        outline.setAccelerator(group, Keyval.F6, ModifierType.NONE);
        menu.append(outline);

        endnotes = new MenuItem("End_notes");
        endnotes.setAccelerator(group, Keyval.F7, ModifierType.NONE);
        menu.append(endnotes);

        references = new MenuItem("_References");
        references.setAccelerator(group, Keyval.F8, ModifierType.NONE);
        menu.append(references);

        menu.append(new SeparatorMenuItem());

        fullscreen = new CheckMenuItem("Fullscreen");
        fullscreen.setActive(false);
        fullscreen.setAccelerator(group, Keyval.F11, ModifierType.NONE);
        menu.append(fullscreen);

        optional = new CheckMenuItem("Menubar");
        optional.setActive(true);
        optional.setAccelerator(group, Keyval.F12, ModifierType.NONE);
        menu.append(optional);

        single = new CheckMenuItem("Right-hand side");
        single.setActive(true);
        single.setAccelerator(group, Keyval.F12, ModifierType.SHIFT_MASK);
        menu.append(single);

        /*
         * Build the actual top level item for the menu bar.
         */

        view = new MenuItem("_View");
        view.setSubmenu(menu);

        bar.append(view);
    }

    private void setupFormatMenu() {
        final Menu menu;
        final MenuItem format;
        final ImageMenuItem clear;
        final MenuItem italics, bold, filename, type, function, project, command, literal, highlight, publication, acronym, keyboard;
        final ModifierType both;

        both = ModifierType.or(ModifierType.CONTROL_MASK, ModifierType.SHIFT_MASK);

        menu = new Menu();
        menu.setAcceleratorGroup(group);

        clear = new ImageMenuItem(Stock.CLEAR);
        clear.setAlwaysShowImage(true);
        clear.setAccelerator(group, Keyval.Space, both);
        menu.append(clear);

        menu.append(new SeparatorMenuItem());

        italics = new ImageMenuItem(Stock.ITALIC);
        italics.setAccelerator(group, Keyval.i, ModifierType.CONTROL_MASK);
        menu.append(italics);

        bold = new ImageMenuItem(Stock.BOLD);
        bold.setAccelerator(group, Keyval.b, ModifierType.CONTROL_MASK);
        menu.append(bold);

        filename = new MenuItem("_File");
        filename.setAccelerator(group, Keyval.f, both);
        filename.connect(new MenuItem.Activate() {
            public void onActivate(MenuItem source) {
                System.out.println("DEBUG: Filename!");
            }
        });
        menu.append(filename);

        type = new MenuItem("_Class or Type");
        type.setAccelerator(group, Keyval.c, both);
        menu.append(type);

        function = new MenuItem("_Method or Function");
        function.setAccelerator(group, Keyval.m, both);
        menu.append(function);

        project = new MenuItem("_Project");
        project.setAccelerator(group, Keyval.p, both);
        menu.append(project);

        command = new MenuItem("C_ommand");
        command.setAccelerator(group, Keyval.o, both);
        menu.append(command);

        literal = new MenuItem("Code _Literal");
        literal.setAccelerator(group, Keyval.l, both);
        menu.append(literal);

        highlight = new MenuItem("_Highlight");
        highlight.setAccelerator(group, Keyval.h, both);
        menu.append(highlight);

        publication = new MenuItem("Publication _Title");
        publication.setAccelerator(group, Keyval.t, both);
        menu.append(publication);

        acronym = new MenuItem("_Acronym");
        acronym.setAccelerator(group, Keyval.a, both);
        menu.append(acronym);

        keyboard = new MenuItem("_Keystroke");
        keyboard.setAccelerator(group, Keyval.k, both);
        menu.append(keyboard);

        /*
         * Build the actual top level item for the menu bar.
         */

        format = new MenuItem("_Format");
        format.setSubmenu(menu);

        bar.append(format);
    }

    private void setupHelpMenu() {

        final Menu menu;
        final MenuItem help;
        final MenuItem about;

        menu = new Menu();
        menu.setAcceleratorGroup(group);

        about = new ImageMenuItem(Stock.ABOUT);
        menu.append(about);

        /*
         * Build the actual top level item for the menu bar.
         */

        help = new MenuItem("_Help");
        help.setSubmenu(menu);

        bar.append(help);
    }
}
