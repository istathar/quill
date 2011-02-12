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
import org.gnome.gtk.Stock;

/**
 * Actions that the user can take. There is one of these per PrimaryWindow.
 * 
 * @author Andrew Cowie
 */
class UserActions
{
    private final PrimaryWindow primary;

    private final AcceleratorGroup group;

    final EditActions edit;

    final ViewActions view;

    final ChapterActions chapter;

    final HelpActions help;

    private static int count;

    static {
        count = 0;
    }

    /*
     * Throw something together for the action name.
     */
    private static String generateName() {
        count++;
        return "PrimaryAction-" + count;
    }

    abstract class PrimaryAction extends Action implements Action.Activate
    {
        PrimaryAction(Stock stock) {
            super(generateName(), stock);
            super.connect(this);
        }

        PrimaryAction(String label) {
            super(generateName(), label);
            super.connect(this);
        }

        PrimaryAction(String label, Stock stock) {
            super(generateName(), label, null, stock);
            super.connect(this);
        }

        PrimaryAction(String label, Keyval key, ModifierType mod) {
            super(generateName(), label);
            super.setAccelerator(group, key, mod);
            super.connect(this);
        }
    }

    /**
     * Set up the Actions for the given PrimaryWindow.
     */
    UserActions(final PrimaryWindow primary) {
        this.primary = primary;
        this.group = new AcceleratorGroup();
        this.primary.addAcceleratorGroup(group);

        this.edit = new EditActions();
        this.view = new ViewActions();
        this.chapter = new ChapterActions();
        this.help = new HelpActions();
    }

    class ViewActions
    {
        Action editor;

        Action stylist;

        Action metadata;
    }

    class EditActions
    {
        Action undo;

        Action redo;
    }

    class ChapterActions
    {
        final Action create;

        final Action previous;

        final Action next;

        ChapterActions() {
            create = new ChapterCreate();
            previous = new ChapterPrevious();
            next = new ChapterNext();
        }

        private class ChapterCreate extends PrimaryAction
        {
            private ChapterCreate() {
                super("New Chapter", Stock.NEW);
            }

            public void onActivate(Action source) {
                primary.handleComponentPrevious();
            }
        }

        private class ChapterPrevious extends PrimaryAction
        {
            private ChapterPrevious() {
                super("Previous", Keyval.PageUp, ModifierType.CONTROL_MASK);
            }

            public void onActivate(Action source) {
                primary.handleComponentPrevious();
            }
        }

        private class ChapterNext extends PrimaryAction
        {
            private ChapterNext() {
                super("Next", Keyval.PageDown, ModifierType.CONTROL_MASK);
            }

            public void onActivate(Action source) {
                primary.handleComponentNext();
            }
        }
    }

    class HelpActions
    {
        Action editor;
    }
}
