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
import org.gnome.gtk.ImageMenuItem;
import org.gnome.gtk.Menu;
import org.gnome.gtk.MenuBar;
import org.gnome.gtk.MenuItem;
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

        group = new AcceleratorGroup();
        primary.addAcceleratorGroup(group);

        setupManuscriptMenu();
        bar.append(new MenuItem("_Chapter"));
        bar.append(new MenuItem("_Edit"));
        bar.append(new MenuItem("_View"));
        setupFormatMenu();
    }

    private void setupManuscriptMenu() {
        bar.append(new MenuItem("_Manuscript"));
    }

    private void setupFormatMenu() {
        final Menu menu;
        final MenuItem format;
        final MenuItem clear, italics, bold, filename, type, function, project, command, literal, highlight, publication, acronym, keyboard;
        final ModifierType CTRL, CTRL_SHIFT;

        CTRL = ModifierType.CONTROL_MASK;
        CTRL_SHIFT = ModifierType.or(ModifierType.CONTROL_MASK, ModifierType.SHIFT_MASK);

        menu = new Menu();
        menu.setAcceleratorGroup(group);

        clear = new MenuItem("_Clear");
        clear.setAccelerator(group, Keyval.Space, CTRL_SHIFT);
        menu.append(clear);

        italics = new ImageMenuItem(Stock.ITALIC);
        italics.setAccelerator(group, Keyval.i, CTRL);
        menu.append(italics);

        bold = new ImageMenuItem(Stock.BOLD);
        bold.setAccelerator(group, Keyval.b, CTRL);
        menu.append(bold);

        filename = new MenuItem("_File");
        filename.setAccelerator(group, Keyval.f, CTRL_SHIFT);
        filename.connect(new MenuItem.Activate() {
            public void onActivate(MenuItem source) {
                System.out.println("DEBUG: Filename!");
            }
        });
        menu.append(filename);

        type = new MenuItem("_Class or Type");
        type.setAccelerator(group, Keyval.c, CTRL_SHIFT);
        menu.append(type);

        function = new MenuItem("_Method or Function");
        function.setAccelerator(group, Keyval.m, CTRL_SHIFT);
        menu.append(function);

        project = new MenuItem("_Project");
        project.setAccelerator(group, Keyval.p, CTRL_SHIFT);
        menu.append(project);

        command = new MenuItem("C_ommand");
        command.setAccelerator(group, Keyval.o, CTRL_SHIFT);
        menu.append(command);

        literal = new MenuItem("Code _Literal");
        literal.setAccelerator(group, Keyval.l, CTRL_SHIFT);
        menu.append(literal);

        highlight = new MenuItem("_Highlight");
        highlight.setAccelerator(group, Keyval.h, CTRL_SHIFT);
        menu.append(highlight);

        publication = new MenuItem("_Publication Title");
        publication.setAccelerator(group, Keyval.t, CTRL_SHIFT);
        menu.append(publication);

        acronym = new MenuItem("_Acronym");
        acronym.setAccelerator(group, Keyval.a, CTRL_SHIFT);
        menu.append(acronym);

        keyboard = new MenuItem("_Keystroke");
        keyboard.setAccelerator(group, Keyval.k, CTRL_SHIFT);
        menu.append(keyboard);

        /*
         * Build the actual top level item for the menu bar.
         */

        format = new MenuItem("_Format");
        format.setSubmenu(menu);

        bar.append(format);
    }
}
