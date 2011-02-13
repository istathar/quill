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

    final Edit edit;

    final View view;

    final Chapter chapter;

    final Help help;

    private static int count;

    static {
        count = 0;
    }

    /*
     * Throw something together for the action name.
     */
    private static String generateName() {
        count++;
        return "NormalAction-" + count;
    }

    /**
     * Base class to facilitate easily coding Actions with their Activate
     * callbacks, while accessing our reference to the top level
     * PrimaryWindow.
     */
    private abstract class NormalAction extends org.gnome.gtk.Action implements Action.Activate
    {
        NormalAction(Stock stock) {
            super(generateName(), stock);
            super.connect(this);
        }

        NormalAction(String label) {
            super(generateName(), label);
            super.connect(this);
        }

        NormalAction(String label, Stock stock) {
            super(generateName(), label, null, stock);
            super.connect(this);
        }

        NormalAction(String label, Keyval keyval, ModifierType modifier) {
            super(generateName(), label);
            super.setAccelerator(group, keyval, modifier);
            super.connect(this);
        }

        NormalAction(Stock stock, Keyval keyval, ModifierType modifier) {
            super(generateName(), stock);
            super.setAccelerator(group, keyval, modifier);
            super.connect(this);
        }
    }

    private abstract class ToggleAction extends org.gnome.gtk.ToggleAction implements Action.Activate
    {
        ToggleAction(String label, Keyval keyval, ModifierType modifier, boolean initial) {
            super(generateName(), label);
            super.setAccelerator(group, keyval, modifier);
            super.setActive(initial);
            super.connect(this);

        }

        ToggleAction(Stock stock, Keyval keyval, ModifierType modifier, boolean initial) {
            super(generateName(), stock);
            super.setAccelerator(group, keyval, modifier);
            super.setActive(initial);
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

        this.edit = new Edit();
        this.view = new View();
        this.chapter = new Chapter();
        this.help = new Help();
    }

    class View
    {
        final Action editor;

        final Action stylist;

        final Action metaditor;

        final Action preview;

        final Action outline;

        final Action endnotes;

        final Action references;

        final Action fullscreen;

        final Action menubar;

        final Action rightside;

        View() {
            editor = new SwitchEditor();
            stylist = new SwitchStylist();
            metaditor = new SwitchMetaditor();
            preview = new SwitchPreview();
            outline = new SwitchOutline();
            endnotes = new SwitchEndnotes();
            references = new SwitchReferences();
            fullscreen = new ToggleFullscreen();
            menubar = new ToggleMenubar();
            rightside = new ToggleRightSide();
        }

        private class SwitchEditor extends NormalAction
        {
            private SwitchEditor() {
                super("_Editor", Keyval.F1, ModifierType.NONE);
            }

            public void onActivate(Action source) {
                primary.switchToEditor();
            }
        }

        private class SwitchStylist extends NormalAction
        {
            private SwitchStylist() {
                super("_Stylesheet", Keyval.F2, ModifierType.NONE);
            }

            public void onActivate(Action source) {
                primary.switchToStylesheet();
            }
        }

        private class SwitchMetaditor extends NormalAction
        {
            private SwitchMetaditor() {
                super("_Metadata", Keyval.F3, ModifierType.NONE);
            }

            public void onActivate(Action source) {
                primary.switchToMetadata();
            }
        }

        private class SwitchPreview extends NormalAction
        {
            private SwitchPreview() {
                super("_Preview", Keyval.F5, ModifierType.NONE);
            }

            public void onActivate(Action source) {
                primary.switchToPreview();
            }
        }

        private class SwitchOutline extends NormalAction
        {
            private SwitchOutline() {
                super("_Outline", Keyval.F6, ModifierType.NONE);
            }

            public void onActivate(Action source) {
                primary.switchToOutline();
            }
        }

        private class SwitchEndnotes extends NormalAction
        {
            private SwitchEndnotes() {
                super("End_notes", Keyval.F7, ModifierType.NONE);
            }

            public void onActivate(Action source) {
                primary.switchToEndnotes();
            }
        }

        private class SwitchReferences extends NormalAction
        {
            private SwitchReferences() {
                super("_References", Keyval.F8, ModifierType.NONE);
            }

            public void onActivate(Action source) {
                primary.switchToReferences();
            }
        }

        private class ToggleFullscreen extends ToggleAction
        {
            private ToggleFullscreen() {
                super(Stock.FULLSCREEN, Keyval.F11, ModifierType.NONE, false);
            }

            public void onActivate(Action source) {
                primary.toggleFullscreen();
            }
        }

        private class ToggleMenubar extends ToggleAction
        {
            private ToggleMenubar() {
                super("Menubar", Keyval.F12, ModifierType.NONE, false);
            }

            public void onActivate(Action source) {
                primary.toggleOptionalMenu();
            }
        }

        private class ToggleRightSide extends ToggleAction
        {
            private ToggleRightSide() {
                super("Right-Hand Side", Keyval.F12, ModifierType.SHIFT_MASK, true);
            }

            public void onActivate(Action source) {
                primary.toggleRightSide();
            }
        }
    }

    class Edit
    {
        final Action undo;

        final Action redo;

        final Action cut;

        final Action copy;

        final Action paste;

        Edit() {
            undo = new Undo();
            redo = new Redo();
            cut = new Cut();
            copy = new Copy();
            paste = new Paste();
        }

        private class Undo extends NormalAction
        {
            private Undo() {
                super(Stock.UNDO, Keyval.z, ModifierType.CONTROL_MASK);
            }

            public void onActivate(Action source) {
                primary.handleUndo();
            }
        }

        private class Redo extends NormalAction
        {
            private Redo() {
                super(Stock.REDO, Keyval.y, ModifierType.CONTROL_MASK);
            }

            public void onActivate(Action source) {
                primary.handleRedo();
            }
        }

        private class Cut extends NormalAction
        {
            private Cut() {
                super(Stock.CUT, Keyval.x, ModifierType.CONTROL_MASK);
            }

            public void onActivate(Action source) {
            // TODO
            }
        }

        private class Copy extends NormalAction
        {
            private Copy() {
                super(Stock.COPY, Keyval.c, ModifierType.CONTROL_MASK);
            }

            public void onActivate(Action source) {
            // TODO
            }
        }

        private class Paste extends NormalAction
        {
            private Paste() {
                super(Stock.PASTE, Keyval.v, ModifierType.CONTROL_MASK);
            }

            public void onActivate(Action source) {
            // TODO
            }
        }
    }

    class Chapter
    {
        final Action create;

        final Action previous;

        final Action next;

        Chapter() {
            create = new Create();
            previous = new Previous();
            next = new Next();
        }

        private class Create extends NormalAction
        {
            private Create() {
                super("New Chapter", Stock.NEW);
            }

            public void onActivate(Action source) {
                primary.handleComponentPrevious();
            }
        }

        private class Previous extends NormalAction
        {
            private Previous() {
                super("Previous", Keyval.PageUp, ModifierType.CONTROL_MASK);
            }

            public void onActivate(Action source) {
                primary.handleComponentPrevious();
            }
        }

        private class Next extends NormalAction
        {
            private Next() {
                super("Next", Keyval.PageDown, ModifierType.CONTROL_MASK);
            }

            public void onActivate(Action source) {
                primary.handleComponentNext();
            }
        }
    }

    class Help
    {
        Action editor;
    }
}
