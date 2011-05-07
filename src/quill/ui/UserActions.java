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

import quill.client.Quill;
import quill.textbase.Common;
import quill.textbase.Special;

/**
 * Actions that the user can take. There is one of these per PrimaryWindow.
 * 
 * @author Andrew Cowie
 */
class UserActions
{
    private final PrimaryWindow primary;

    /**
     * The current editor widget, whichever it is.
     */
    private EditorTextView editor;

    private final AcceleratorGroup group;

    final Manuscript manuscript;

    final Chapter chapter;

    final Edit edit;

    final View view;

    final Insert insert;

    final Format format;

    final Help help;

    private static int count;

    private static final ModifierType both;

    static {
        count = 0;
        both = ModifierType.or(ModifierType.CONTROL_MASK, ModifierType.SHIFT_MASK);
    }

    /*
     * Throw something together for the action name.
     */
    private static String generateName() {
        count++;
        return "quill-" + count;
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

        NormalAction(String label, Stock stock, Keyval keyval, ModifierType modifier) {
            super(generateName(), label, null, stock);
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

        this.manuscript = new Manuscript();
        this.chapter = new Chapter();
        this.edit = new Edit();
        this.view = new View();
        this.insert = new Insert();
        this.format = new Format();
        this.help = new Help();
    }

    class Manuscript
    {
        final Action create;

        final Action open;

        final Action save;

        final Action print;

        final Action export;

        final Action quit;

        private Manuscript() {
            create = new Create();
            open = new Open();
            save = new Save();
            print = new Print();
            export = new Export();
            quit = new Quit();
        }

        private class Create extends NormalAction
        {
            private Create() {
                super("New Document");
            }

            public void onActivate(Action source) {
                throw new UnsupportedOperationException("Not yet implemented");
            }
        }

        private class Open extends NormalAction
        {
            private Open() {
                super(Stock.OPEN, Keyval.o, ModifierType.CONTROL_MASK);
            }

            public void onActivate(Action source) {
                try {
                    primary.saveIfModified();
                    primary.openDocument();
                } catch (SaveCancelledException e) {
                    // ok
                }
            }
        }

        private class Save extends NormalAction
        {
            private Save() {
                super(Stock.SAVE, Keyval.s, ModifierType.CONTROL_MASK);
            }

            public void onActivate(Action source) {
                try {
                    primary.saveDocument();
                } catch (SaveCancelledException e) {
                    // ok
                }
            }
        }

        private class Print extends NormalAction
        {
            private Print() {
                super(Stock.PRINT, Keyval.p, ModifierType.CONTROL_MASK);
            }

            public void onActivate(Action source) {
                primary.printDocument();
            }
        }

        private class Export extends NormalAction
        {
            private Export() {
                super("_Export", Keyval.e, ModifierType.CONTROL_MASK);
            }

            public void onActivate(Action source) {
            // TODO
            }
        }

        private class Quit extends NormalAction
        {
            private Quit() {
                super(Stock.QUIT, Keyval.q, ModifierType.CONTROL_MASK);
            }

            public void onActivate(Action source) {
                final UserInterface ui;

                try {
                    ui = Quill.getUserInterface();

                    primary.saveIfModified();
                    ui.shutdown();
                } catch (SaveCancelledException sce) {
                    // ok
                }
            }
        }
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

        private View() {
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

        private Edit() {
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
                if (editor == null) {
                    return;
                }
                editor.handleCutText();
            }
        }

        private class Copy extends NormalAction
        {
            private Copy() {
                super(Stock.COPY, Keyval.c, ModifierType.CONTROL_MASK);
            }

            public void onActivate(Action source) {
                if (editor == null) {
                    return;
                }
                editor.handleCopyText();
            }
        }

        private class Paste extends NormalAction
        {
            private Paste() {
                super(Stock.PASTE, Keyval.v, ModifierType.CONTROL_MASK);
            }

            public void onActivate(Action source) {
                if (editor == null) {
                    return;
                }
                editor.handlePasteText();
            }
        }
    }

    class Chapter
    {
        final Action create;

        final Action previous;

        final Action next;

        private Chapter() {
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
                super("Previous", Stock.GO_BACK, Keyval.PageUp, ModifierType.CONTROL_MASK);
            }

            public void onActivate(Action source) {
                primary.handleComponentPrevious();
            }
        }

        private class Next extends NormalAction
        {
            private Next() {
                super("Next", Stock.GO_FORWARD, Keyval.PageDown, ModifierType.CONTROL_MASK);
            }

            public void onActivate(Action source) {
                primary.handleComponentNext();
            }
        }
    }

    class Insert
    {
        final Action note;

        final Action cite;

        final Action block;

        private Insert() {
            note = new EndnoteAnchor();
            cite = new ReferenceAnchor();
            block = new InsertBlock();
        }

        private class EndnoteAnchor extends NormalAction
        {
            private EndnoteAnchor() {
                super("End_note Anchor");
            }

            public void onActivate(Action source) {
                editor.handleInsertMarker(Special.NOTE);
            }
        }

        private class ReferenceAnchor extends NormalAction
        {
            private ReferenceAnchor() {
                super("_Reference Citation");
            }

            public void onActivate(Action source) {
                editor.handleInsertMarker(Special.CITE);
            }
        }

        private class InsertBlock extends NormalAction
        {
            private InsertBlock() {
                super("Insert Block...", Keyval.Insert, ModifierType.NONE);
            }

            public void onActivate(Action source) {
                if (editor == null) {
                    return;
                }
                editor.popupInsertMenu();
            }
        }
    }

    class Format
    {
        final Action clear;

        final Action italics;

        final Action bold;

        final Action filename;

        final Action type;

        final Action function;

        final Action project;

        final Action command;

        final Action literal;

        final Action highlight;

        final Action publication;

        final Action acronym;

        final Action keyboard;

        private Format() {
            clear = new ClearFormat();
            italics = new ApplyItalics();
            bold = new ApplyBold();
            filename = new ApplyFilename();
            type = new ApplyType();
            function = new ApplyFunction();
            project = new ApplyProject();
            command = new ApplyCommand();
            literal = new ApplyLiteral();
            highlight = new ApplyHighlight();
            publication = new ApplyTitle();
            acronym = new ApplyAcronym();
            keyboard = new ApplyKeystroke();
        }

        private class ClearFormat extends NormalAction
        {
            private ClearFormat() {
                super(Stock.NEW, Keyval.Space, both);
            }

            public void onActivate(Action source) {
                if (editor == null) {
                    return;
                }
                editor.handleClearFormat();
            }
        }

        private class ApplyItalics extends NormalAction
        {
            private ApplyItalics() {
                super(Stock.ITALIC, Keyval.i, ModifierType.CONTROL_MASK);
            }

            public void onActivate(Action source) {
                if (editor == null) {
                    return;
                }
                editor.handleToggleMarkup(Common.ITALICS);
            }
        }

        private class ApplyBold extends NormalAction
        {
            private ApplyBold() {
                super(Stock.BOLD, Keyval.b, ModifierType.CONTROL_MASK);
            }

            public void onActivate(Action source) {
                if (editor == null) {
                    return;
                }
                editor.handleToggleMarkup(Common.BOLD);
            }
        }

        private class ApplyFilename extends NormalAction
        {
            private ApplyFilename() {
                super("_Filename", Keyval.f, both);
            }

            public void onActivate(Action source) {
                if (editor == null) {
                    return;
                }
                editor.handleToggleMarkup(Common.FILENAME);
            }
        }

        private class ApplyType extends NormalAction
        {
            private ApplyType() {
                super("_Class or Type", Keyval.c, both);
            }

            public void onActivate(Action source) {
                if (editor == null) {
                    return;
                }
                editor.handleToggleMarkup(Common.TYPE);
            }
        }

        private class ApplyFunction extends NormalAction
        {
            private ApplyFunction() {
                super("_Method or Function", Keyval.m, both);
            }

            public void onActivate(Action source) {
                if (editor == null) {
                    return;
                }
                editor.handleToggleMarkup(Common.FUNCTION);
            }
        }

        private class ApplyProject extends NormalAction
        {
            private ApplyProject() {
                super("_Project", Keyval.p, both);
            }

            public void onActivate(Action source) {
                if (editor == null) {
                    return;
                }
                editor.handleToggleMarkup(Common.PROJECT);
            }
        }

        private class ApplyCommand extends NormalAction
        {
            private ApplyCommand() {
                super("C_ommand", Keyval.o, both);
            }

            public void onActivate(Action source) {
                if (editor == null) {
                    return;
                }
                editor.handleToggleMarkup(Common.COMMAND);
            }
        }

        private class ApplyLiteral extends NormalAction
        {
            private ApplyLiteral() {
                super("Code _Literal", Keyval.l, both);
            }

            public void onActivate(Action source) {
                if (editor == null) {
                    return;
                }
                editor.handleToggleMarkup(Common.LITERAL);
            }
        }

        private class ApplyHighlight extends NormalAction
        {
            private ApplyHighlight() {
                super("_Highlight", Keyval.h, both);
            }

            public void onActivate(Action source) {
                if (editor == null) {
                    return;
                }
                editor.handleToggleMarkup(Common.HIGHLIGHT);
            }
        }

        private class ApplyTitle extends NormalAction
        {
            private ApplyTitle() {
                super("Publication _Title", Keyval.t, both);
            }

            public void onActivate(Action source) {
                if (editor == null) {
                    return;
                }
                editor.handleToggleMarkup(Common.TITLE);
            }
        }

        private class ApplyAcronym extends NormalAction
        {
            private ApplyAcronym() {
                super("_Acronym", Keyval.a, both);
            }

            public void onActivate(Action source) {
                if (editor == null) {
                    return;
                }
                editor.handleToggleMarkup(Common.ACRONYM);
            }
        }

        private class ApplyKeystroke extends NormalAction
        {
            private ApplyKeystroke() {
                super("_Keystroke", Keyval.k, both);
            }

            public void onActivate(Action source) {
                if (editor == null) {
                    return;
                }
                editor.handleToggleMarkup(Common.KEYBOARD);
            }
        }
    }

    class Help
    {
        final Action editor;

        final Action stylist;

        final Action metaditor;

        final Action preview;

        final Action outline;

        final Action endnotes;

        final Action references;

        final Action intro;

        private Help() {
            editor = new SwitchEditor();
            stylist = new SwitchStylist();
            metaditor = new SwitchMetaditor();
            preview = new SwitchPreview();
            outline = new SwitchOutline();
            endnotes = new SwitchEndnotes();
            references = new SwitchReferences();
            intro = new SwitchIntro(); // testing
        }

        private class SwitchEditor extends NormalAction
        {
            private SwitchEditor() {
                super("Using the _Editor", Keyval.F1, ModifierType.SHIFT_MASK);
            }

            public void onActivate(Action source) {
                primary.switchToHelp();
            }
        }

        private class SwitchStylist extends NormalAction
        {
            private SwitchStylist() {
                super("Configuring _Stylesheet", Keyval.F2, ModifierType.SHIFT_MASK);
            }

            public void onActivate(Action source) {
            // TODO
            }
        }

        private class SwitchMetaditor extends NormalAction
        {
            private SwitchMetaditor() {
                super("Editing _Metadata", Keyval.F3, ModifierType.SHIFT_MASK);
            }

            public void onActivate(Action source) {
            // TODO
            }
        }

        private class SwitchPreview extends NormalAction
        {
            private SwitchPreview() {
                super("Using the _Preview", Keyval.F5, ModifierType.SHIFT_MASK);
            }

            public void onActivate(Action source) {
            // TODO
            }
        }

        private class SwitchOutline extends NormalAction
        {
            private SwitchOutline() {
                super("Using the _Outline", Keyval.F6, ModifierType.SHIFT_MASK);
            }

            public void onActivate(Action source) {
            // TODO
            }
        }

        private class SwitchEndnotes extends NormalAction
        {
            private SwitchEndnotes() {
                super("Editing End_notes", Keyval.F7, ModifierType.SHIFT_MASK);
            }

            public void onActivate(Action source) {
            // TODO
            }
        }

        private class SwitchReferences extends NormalAction
        {
            private SwitchReferences() {
                super("Editing _References", Keyval.F8, ModifierType.SHIFT_MASK);
            }

            public void onActivate(Action source) {
            // TODO
            }
        }

        private class SwitchIntro extends NormalAction
        {
            private SwitchIntro() {
                super("Introduction", Keyval.F9, ModifierType.SHIFT_MASK);
            }

            public void onActivate(Action source) {
                primary.switchToIntro();
            }
        }
    }

    /**
     * Inform UserActions which EditorTextView the editing actions
     * (formatting, etc) should be directed to. Pass <code>null</code> to
     * clear the setting.
     */
    void setCurrentEditor(EditorTextView view) {
        this.editor = view;
    }
}
