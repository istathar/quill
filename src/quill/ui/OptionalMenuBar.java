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
import org.gnome.gtk.Action;
import org.gnome.gtk.CheckMenuItem;
import org.gnome.gtk.IconSize;
import org.gnome.gtk.Image;
import org.gnome.gtk.ImageMenuItem;
import org.gnome.gtk.Menu;
import org.gnome.gtk.MenuBar;
import org.gnome.gtk.MenuItem;
import org.gnome.gtk.SeparatorMenuItem;
import org.gnome.gtk.Stock;
import org.gnome.gtk.ToggleAction;

/**
 * The code for the optional MenuBar. It's "optional" because you're supposed
 * to be able to use the entire application without using the menu, thereby
 * allowing you to get your 24 vertical pixels back. The menu is here
 * primarily for accessibiltiy reasons, and secondarily to help people learn
 * the UI.
 * 
 * @author Andrew Cowie
 */
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
        setupInsertMenu();
        setupFormatMenu();
        setupHelpMenu();
    }

    private void setupManuscriptMenu() {
        final Menu menu;
        final MenuItem manuscript;
        final MenuItem create, open, save, print, export, quit;

        menu = new Menu();
        menu.setAcceleratorGroup(group);

        create = new ImageMenuItem(new Image(Stock.NEW, IconSize.MENU), "New Document...");
        menu.append(create);

        open = new ImageMenuItem(Stock.OPEN);
        open.setAccelerator(group, Keyval.o, ModifierType.CONTROL_MASK);
        menu.append(open);

        save = new ImageMenuItem(Stock.SAVE);
        save.setAccelerator(group, Keyval.s, ModifierType.CONTROL_MASK);
        menu.append(save);

        menu.append(new SeparatorMenuItem());

        print = new ImageMenuItem(Stock.PRINT);
        print.setAccelerator(group, Keyval.p, ModifierType.CONTROL_MASK);
        menu.append(print);

        export = new MenuItem("_Export...");
        export.setAccelerator(group, Keyval.e, ModifierType.CONTROL_MASK);
        menu.append(export);

        menu.append(new SeparatorMenuItem());

        quit = new ImageMenuItem(Stock.QUIT);
        quit.setAccelerator(group, Keyval.q, ModifierType.CONTROL_MASK);
        menu.append(quit);

        /*
         * Build the actual top level item for the menu bar.
         */

        manuscript = new MenuItem("_Manuscript");
        manuscript.setSubmenu(menu);

        bar.append(manuscript);
    }

    private void setupChapterMenu() {
        final Menu menu;
        final MenuItem chapter;
        Action action;
        MenuItem item;
        ImageMenuItem image;
        SeparatorMenuItem separator;

        menu = new Menu();
        menu.setAcceleratorGroup(group);

        /*
         * New Chapter
         */

        action = new Action("chapter-new", "New Chapter...", null, Stock.NEW);

        item = action.createMenuItem();
        menu.append(item);

        action.connect(new Action.Activate() {
            public void onActivate(Action source) {
            // TODO
            }
        });

        separator = new SeparatorMenuItem();
        menu.append(separator);

        /*
         * Previous chapter
         */

        action = new Action("chapter-previous", "Previous", null, Stock.GO_BACK);
        action.setAccelerator(group, Keyval.PageUp, ModifierType.CONTROL_MASK);

        image = (ImageMenuItem) action.createMenuItem();
        image.setAlwaysShowImage(true);
        menu.append(image);

        action.connect(new Action.Activate() {
            public void onActivate(Action source) {
                primary.handleComponentPrevious();
            }
        });

        /*
         * Next chapter
         */

        action = new Action("chapter-next", "Next", null, Stock.GO_FORWARD);
        action.setAccelerator(group, Keyval.PageDown, ModifierType.CONTROL_MASK);

        image = (ImageMenuItem) action.createMenuItem();
        image.setAlwaysShowImage(true);
        menu.append(image);

        action.connect(new Action.Activate() {
            public void onActivate(Action source) {
                primary.handleComponentNext();
            }
        });

        /*
         * Build the actual top level item for the menu bar.
         */

        chapter = new MenuItem("_Chapter");
        chapter.setSubmenu(menu);

        bar.append(chapter);
    }

    private void setupInsertMenu() {
        final Menu menu;
        final MenuItem edit;
        final MenuItem note, cite, block;

        menu = new Menu();
        menu.setAcceleratorGroup(group);

        note = new MenuItem("End_note Anchor");
        menu.append(note);

        cite = new MenuItem("_Reference Citation");
        menu.append(cite);

        block = new MenuItem("Block...");
        block.setAccelerator(group, Keyval.Insert, ModifierType.NONE);
        menu.append(block);

        /*
         * Build the actual top level item for the menu bar.
         */

        edit = new MenuItem("_Insert");
        edit.setSubmenu(menu);

        bar.append(edit);
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
        Action action;
        MenuItem item;
        CheckMenuItem check;
        SeparatorMenuItem separator;

        menu = new Menu();
        menu.setAcceleratorGroup(group);

        /*
         * Switch to Editor
         */

        action = new Action("switch-to-editor", "_Editor");
        action.setAccelerator(group, Keyval.F1, ModifierType.NONE);

        item = action.createMenuItem();
        menu.append(item);

        action.connect(new Action.Activate() {
            public void onActivate(Action source) {
                primary.switchToEditor();
            }
        });

        /*
         * Switch to Stylesheet
         */

        action = new Action("switch-to-stylesheet", "_Stylesheet");
        action.setAccelerator(group, Keyval.F2, ModifierType.NONE);

        item = action.createMenuItem();
        menu.append(item);

        action.connect(new Action.Activate() {
            public void onActivate(Action source) {
                primary.switchToStylesheet();
            }
        });

        /*
         * Switch to Metadata
         */

        action = new Action("switch-to-metadata", "_Metadata");
        action.setAccelerator(group, Keyval.F3, ModifierType.NONE);

        item = action.createMenuItem();
        menu.append(item);

        action.connect(new Action.Activate() {
            public void onActivate(Action source) {
                primary.switchToMetadata();
            }
        });

        /*
         * Switch to Preview
         */

        action = new Action("switch-to-preview", "_Preview");
        action.setAccelerator(group, Keyval.F5, ModifierType.NONE);

        item = action.createMenuItem();
        menu.append(item);

        action.connect(new Action.Activate() {
            public void onActivate(Action source) {
                primary.switchToPreview();
            }
        });

        /*
         * Switch to Outline
         */

        action = new Action("switch-to-outline", "_Outline");
        action.setAccelerator(group, Keyval.F6, ModifierType.NONE);

        item = action.createMenuItem();
        menu.append(item);

        action.connect(new Action.Activate() {
            public void onActivate(Action source) {
                primary.switchToOutline();
            }
        });

        /*
         * Switch to Endnotes
         */

        action = new Action("switch-to-endnotes", "End_notes");
        action.setAccelerator(group, Keyval.F7, ModifierType.NONE);

        item = action.createMenuItem();
        menu.append(item);

        action.connect(new Action.Activate() {
            public void onActivate(Action source) {
                primary.switchToEndnotes();
            }
        });

        /*
         * Switch to References
         */

        action = new Action("switch-to-references", "_References");
        action.setAccelerator(group, Keyval.F8, ModifierType.NONE);

        item = action.createMenuItem();
        menu.append(item);

        action.connect(new Action.Activate() {
            public void onActivate(Action source) {
                primary.switchToReferences();
            }
        });

        separator = new SeparatorMenuItem();
        menu.append(separator);

        /*
         * Toggle Fullscreen
         */

        action = new Action("toggle-fullscreen", Stock.FULLSCREEN);
        action.setAccelerator(group, Keyval.F11, ModifierType.NONE);

        item = action.createMenuItem();
        menu.append(item);

        action.connect(new Action.Activate() {
            public void onActivate(Action source) {
                primary.toggleFullscreen();
            }
        });

        /*
         * Toggle Menubar
         */

        action = new ToggleAction("toggle-menubar", "Menubar");
        action.setAccelerator(group, Keyval.F12, ModifierType.NONE);

        check = (CheckMenuItem) action.createMenuItem();
        check.setActive(true);
        menu.append(check);

        action.connect(new Action.Activate() {
            public void onActivate(Action source) {
                primary.toggleOptionalMenu();
            }
        });

        /*
         * Toggle Right Side
         */

        action = new ToggleAction("toggle-right-side", "Right-Hand Side");
        action.setAccelerator(group, Keyval.F12, ModifierType.SHIFT_MASK);

        check = (CheckMenuItem) action.createMenuItem();
        check.setActive(true);
        menu.append(check);

        action.connect(new Action.Activate() {
            public void onActivate(Action source) {
                primary.toggleRightSide();
            }
        });

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
        final ImageMenuItem clear, italics, bold;
        final MenuItem filename, type, function, project, command, literal, highlight, publication, acronym, keyboard;
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
        italics.setAlwaysShowImage(true);
        italics.setAccelerator(group, Keyval.i, ModifierType.CONTROL_MASK);
        menu.append(italics);

        bold = new ImageMenuItem(Stock.BOLD);
        bold.setAlwaysShowImage(true);
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
        Action action;
        MenuItem item;
        SeparatorMenuItem separator;

        menu = new Menu();
        menu.setAcceleratorGroup(group);

        /*
         * Help for Editor
         */

        action = new Action("help-for-editor", "Using the _Editor");
        action.setAccelerator(group, Keyval.F1, ModifierType.SHIFT_MASK);

        item = action.createMenuItem();
        menu.append(item);

        action.connect(new Action.Activate() {
            public void onActivate(Action source) {
                primary.switchToHelp();
            }
        });

        /*
         * Help for Stylist
         */

        action = new Action("help-for-stylist", "Configuring _Stylesheet");
        action.setAccelerator(group, Keyval.F2, ModifierType.SHIFT_MASK);

        item = action.createMenuItem();
        menu.append(item);

        action.connect(new Action.Activate() {
            public void onActivate(Action source) {
            // TODO
            }
        });

        /*
         * Help for Metadata
         */

        action = new Action("help-for-metadata", "Editing _Metadata");
        action.setAccelerator(group, Keyval.F3, ModifierType.SHIFT_MASK);

        item = action.createMenuItem();
        menu.append(item);

        action.connect(new Action.Activate() {
            public void onActivate(Action source) {
            // TODO
            }
        });

        /*
         * Help for Preview
         */

        action = new Action("help-for-preview", "Using the _Preview");
        action.setAccelerator(group, Keyval.F5, ModifierType.SHIFT_MASK);

        item = action.createMenuItem();
        menu.append(item);

        action.connect(new Action.Activate() {
            public void onActivate(Action source) {
            // TODO
            }
        });

        /*
         * Help for Outline
         */

        action = new Action("help-for-preview", "Using the _Outline");
        action.setAccelerator(group, Keyval.F6, ModifierType.SHIFT_MASK);

        item = action.createMenuItem();
        menu.append(item);

        action.connect(new Action.Activate() {
            public void onActivate(Action source) {
            // TODO
            }
        });

        /*
         * Help for Endnotes
         */

        action = new Action("help-for-endnotes", "Editing End_notes");
        action.setAccelerator(group, Keyval.F7, ModifierType.SHIFT_MASK);

        item = action.createMenuItem();
        menu.append(item);

        action.connect(new Action.Activate() {
            public void onActivate(Action source) {
            // TODO
            }
        });

        /*
         * Help for References
         */

        action = new Action("help-for-references", "Editing _References");
        action.setAccelerator(group, Keyval.F8, ModifierType.SHIFT_MASK);

        item = action.createMenuItem();
        menu.append(item);

        action.connect(new Action.Activate() {
            public void onActivate(Action source) {
            // TODO
            }
        });

        separator = new SeparatorMenuItem();
        menu.append(separator);

        /*
         * About
         */

        action = new Action("help-about", Stock.ABOUT);

        item = action.createMenuItem();
        menu.append(item);

        action.connect(new Action.Activate() {
            public void onActivate(Action source) {
            // TODO
            }
        });

        /*
         * Build the actual top level item for the menu bar.
         */

        help = new MenuItem("_Help");
        help.setSubmenu(menu);

        bar.append(help);
    }
}
