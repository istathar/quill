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
import org.gnome.gtk.ImageMenuItem;
import org.gnome.gtk.Menu;
import org.gnome.gtk.MenuBar;
import org.gnome.gtk.MenuItem;
import org.gnome.gtk.SeparatorMenuItem;
import org.gnome.gtk.Stock;

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

    private final UserActions actions;

    OptionalMenuBar(PrimaryWindow primary, UserActions actions) {
        super();
        this.bar = this;
        this.primary = primary;

        this.group = new AcceleratorGroup();
        this.primary.addAcceleratorGroup(group);
        this.actions = actions;

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
        Action action;
        MenuItem item;

        menu = new Menu();
        menu.setAcceleratorGroup(group);

        /*
         * New Document
         */

        action = actions.manuscript.create;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Open Document
         */

        action = actions.manuscript.open;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Save Document
         */

        action = actions.manuscript.save;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Separator
         */

        item = new SeparatorMenuItem();
        menu.append(item);

        /*
         * Print Document
         */

        action = actions.manuscript.print;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Export Document
         */

        action = actions.manuscript.export;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Separator
         */

        item = new SeparatorMenuItem();
        menu.append(item);

        /*
         * Quit
         */

        action = actions.manuscript.quit;
        item = action.createMenuItem();
        menu.append(item);

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

        action = actions.chapter.create;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Separator
         */

        separator = new SeparatorMenuItem();
        menu.append(separator);

        /*
         * Previous chapter
         */

        action = actions.chapter.previous;
        image = (ImageMenuItem) action.createMenuItem();
        image.setAlwaysShowImage(true);
        menu.append(image);

        /*
         * Next chapter
         */

        action = actions.chapter.next;
        image = (ImageMenuItem) action.createMenuItem();
        image.setAlwaysShowImage(true);
        menu.append(image);

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
        Action action;
        MenuItem item;

        menu = new Menu();
        menu.setAcceleratorGroup(group);

        /*
         * Insert Endnote Anchor
         */

        action = actions.insert.note;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Insert Reference Anchor
         */

        action = actions.insert.cite;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Insert Block
         */

        action = actions.insert.block;
        item = action.createMenuItem();
        menu.append(item);

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
        Action action;
        MenuItem item;

        menu = new Menu();
        menu.setAcceleratorGroup(group);

        /*
         * Undo
         */

        action = actions.edit.undo;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Redo
         */

        action = actions.edit.redo;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Separator
         */

        item = new SeparatorMenuItem();
        menu.append(item);

        /*
         * Cut
         */

        action = actions.edit.cut;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Copy
         */

        action = actions.edit.copy;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Paste
         */

        action = actions.edit.paste;
        item = action.createMenuItem();
        menu.append(item);

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

        menu = new Menu();
        menu.setAcceleratorGroup(group);

        /*
         * Switch to Editor
         */

        action = actions.view.editor;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Switch to Stylesheet
         */

        action = actions.view.stylist;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Switch to Metadata
         */

        action = actions.view.metaditor;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Switch to Preview
         */

        action = actions.view.preview;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Switch to Outline
         */

        action = actions.view.outline;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Switch to Endnotes
         */

        action = actions.view.endnotes;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Switch to References
         */

        action = actions.view.references;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Separator
         */

        item = new SeparatorMenuItem();
        menu.append(item);

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

        action = actions.view.menubar;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Toggle Right Side
         */

        action = actions.view.rightside;
        check = (CheckMenuItem) action.createMenuItem();
        menu.append(check);

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
        Action action;
        MenuItem item;
        ImageMenuItem icon;

        menu = new Menu();
        menu.setAcceleratorGroup(group);

        /*
         * Clear Formatting
         */

        action = actions.format.clear;
        item = action.createMenuItem();
        icon = (ImageMenuItem) item;
        icon.setAlwaysShowImage(true);
        menu.append(item);

        /*
         * Separator
         */

        item = new SeparatorMenuItem();
        menu.append(item);

        /*
         * Apply Italic Formatting
         */

        action = actions.format.italics;
        item = action.createMenuItem();
        icon = (ImageMenuItem) item;
        icon.setAlwaysShowImage(true);
        menu.append(item);

        /*
         * Apply Bold Formatting
         */

        action = actions.format.bold;
        item = action.createMenuItem();
        icon = (ImageMenuItem) item;
        icon.setAlwaysShowImage(true);
        menu.append(item);

        /*
         * Apply Filename Formatting
         */

        action = actions.format.filename;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Apply Class/Type Formatting
         */

        action = actions.format.type;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Apply Method/Function Formatting
         */

        action = actions.format.function;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Apply Project Formatting
         */

        action = actions.format.project;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Apply Command Formatting
         */

        action = actions.format.command;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Apply Literal Formatting
         */

        action = actions.format.literal;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Apply Highlight Formatting
         */

        action = actions.format.highlight;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Apply Title Formatting
         */

        action = actions.format.publication;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Apply Acronym Formatting
         */

        action = actions.format.acronym;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Apply Keystroke Formatting
         */

        action = actions.format.keyboard;
        item = action.createMenuItem();
        menu.append(item);

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

        menu = new Menu();
        menu.setAcceleratorGroup(group);

        /*
         * Help for Editor
         */

        action = actions.help.editor;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Help for Stylist
         */

        action = actions.help.stylist;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Help for Metadata
         */

        action = actions.help.metaditor;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Help for Preview
         */

        action = actions.help.preview;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Help for Outline
         */

        action = actions.help.outline;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Help for Endnotes
         */

        action = actions.help.endnotes;
        item = action.createMenuItem();
        menu.append(item);

        /*
         * Help for References
         */

        action = actions.help.references;
        item = action.createMenuItem();
        menu.append(item);

        item = new SeparatorMenuItem();
        menu.append(item);

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
