/*
 * EditorWidget.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package markerpen.ui;

import java.io.IOException;

import markerpen.converter.DocBookConverter;
import markerpen.docbook.Document;
import markerpen.textbase.Change;
import markerpen.textbase.CharacterSpan;
import markerpen.textbase.Common;
import markerpen.textbase.DeleteChange;
import markerpen.textbase.Extract;
import markerpen.textbase.FormatChange;
import markerpen.textbase.InsertChange;
import markerpen.textbase.Markup;
import markerpen.textbase.Span;
import markerpen.textbase.TextStack;
import markerpen.textbase.TextualChange;

import org.gnome.gdk.EventKey;
import org.gnome.gdk.Keyval;
import org.gnome.gdk.ModifierType;
import org.gnome.gtk.TextBuffer;
import org.gnome.gtk.TextIter;
import org.gnome.gtk.TextMark;
import org.gnome.gtk.TextView;
import org.gnome.gtk.Widget;
import org.gnome.pango.FontDescription;

import static markerpen.ui.Format.tagForMarkup;
import static markerpen.ui.Format.tagsForMarkup;

class EditorWidget extends TextView
{
    private final TextView view;

    private TextBuffer buffer;

    private TextMark selectionBound, insertBound;

    private TextStack stack;

    /**
     * Cache of the offset into the TextBuffer of the insertBound TextMark.
     */
    private int insertOffset;

    private Markup[] insertMarkup;

    EditorWidget() {
        super();
        view = this;

        setupTextView();
        setupInternalStack();

        hookupKeybindings();
        hookupFormatManagement();
    }

    private void setupTextView() {
        final FontDescription desc;

        desc = new FontDescription("DejaVu Sans, Book 11");
        buffer = new TextBuffer();

        selectionBound = buffer.getSelectionBound();
        insertBound = buffer.getInsert();

        view.setBuffer(buffer);
        view.modifyFont(desc);
    }

    private void setupInternalStack() {
        stack = new TextStack();
        clipboard = Extract.EMPTY;
    }

    private void hookupKeybindings() {
        view.connect(new Widget.KeyPressEvent() {
            public boolean onKeyPressEvent(Widget source, EventKey event) {
                final Keyval key;
                final ModifierType mod;
                final char ch;

                key = event.getKeyval();

                /*
                 * Let default keybindings handle cursor movement keys
                 */

                if ((key == Keyval.Up) || (key == Keyval.Down) || (key == Keyval.Left)
                        || (key == Keyval.Right) || (key == Keyval.Home) || (key == Keyval.End)
                        || (key == Keyval.PageUp) || (key == Keyval.PageDown)) {
                    return false;
                }

                /*
                 * Other special keys that we DO handle.
                 */

                if ((key == Keyval.Escape) || (key == Keyval.Insert)) {
                    // deliberate no-op
                    return true;
                } else if (key == Keyval.Return) {
                    // rather likely that special things are going to happen
                    // here.
                    insert('\n');
                    return true;
                } else if (key == Keyval.Delete) {
                    deleteAt();
                    return true;
                } else if (key == Keyval.BackSpace) {
                    deleteBack();
                    return true;
                }

                /*
                 * Ignore initial press of modifier keys (for now)
                 */

                if ((key == Keyval.ShiftLeft) || (key == Keyval.ShiftRight) || (key == Keyval.AltLeft)
                        || (key == Keyval.AltRight) || (key == Keyval.ControlLeft)
                        || (key == Keyval.ControlRight) || (key == Keyval.SuperLeft)
                        || (key == Keyval.SuperRight)) {
                    // deliberate no-op
                    return true;
                }

                /*
                 * Tab is a strange one. At first glance it is tempting to set
                 * the TextView to not accept them and to have Tab change
                 * focus, but there is the case of program code in a
                 * preformatted block which might need indent support.
                 */

                if (key == Keyval.Tab) {
                    insert('\t');
                    return true;
                }

                /*
                 * Now on to processing normal keystrokes.
                 */

                mod = event.getState();

                if ((mod == ModifierType.NONE) || (mod == ModifierType.SHIFT_MASK)) {
                    ch = key.toUnicode();

                    if (ch == 0) {
                        /*
                         * Don't know what this is. If it's a modifier, we
                         * ought to have skipped it explicitly above. If it
                         * results in a character being inserted into the
                         * TextBuffer things will break. So, needs fixing!
                         */
                        throw new UnsupportedOperationException();
                    }

                    insert(ch);
                    return true;
                }
                if (mod == ModifierType.CONTROL_MASK) {
                    if (key == Keyval.a) {
                        // select all; pass through
                        return false;
                    } else if (key == Keyval.b) {
                        toggleMarkup(Common.BOLD);
                        return true;
                    } else if (key == Keyval.c) {
                        copyText();
                        return true;
                    } else if (key == Keyval.g) {
                        insertImage();
                        return true;
                    } else if (key == Keyval.i) {
                        toggleMarkup(Common.ITALICS);
                        return true;
                    } else if (key == Keyval.s) {
                        exportContents();
                        return true;
                    } else if (key == Keyval.v) {
                        pasteText();
                        return true;
                    } else if (key == Keyval.x) {
                        cutText();
                        return true;
                    } else if (key == Keyval.y) {
                        redo();
                        return true;
                    } else if (key == Keyval.z) {
                        undo();
                        return true;
                    } else {
                        /*
                         * No keybinding
                         */
                        return true;
                    }
                } else if (mod.contains(ModifierType.CONTROL_MASK)
                        && mod.contains(ModifierType.SHIFT_MASK)) {
                    if (key == Keyval.Space) {
                        clearFormat();
                        return true;
                    } else if (key == Keyval.C) {
                        toggleMarkup(Common.CLASSNAME);
                        return true;
                    } else if (key == Keyval.F) {
                        toggleMarkup(Common.FILENAME);
                        return true;
                    } else {
                        /*
                         * No keybinding
                         */
                        return true;
                    }
                }

                /*
                 * We didn't handle it, and are assuming we're capable of
                 * handing all keyboard input. Boom :(
                 */

                throw new IllegalStateException("\n" + "Unhandled " + key + " with " + mod);
            }
        });
    }

    private void insert(char ch) {
        final Extract removed;
        final Span span;
        final Change change;
        final TextIter other;
        final int width;

        /*
         * Create a Span and insert into our internal representation. Then
         * send the character to the TextBuffer, updating the TextView to
         * reflect the user's input.
         */

        span = new CharacterSpan(ch, insertMarkup);

        if (buffer.getHasSelection()) {
            other = buffer.getIter(selectionBound);
            width = other.getOffset() - insertOffset;

            removed = stack.extractRange(insertOffset, width);
            change = new TextualChange(insertOffset, removed, span);
        } else {
            change = new InsertChange(insertOffset, span);
        }

        stack.apply(change);
        this.affect(change);
    }

    private void insert(Extract range) {
        final TextIter pointer;
        final TextIter start;
        final int offset;
        Span span;
        int i;

        /*
         * Where, in TextBuffer terms?
         */

        pointer = buffer.getIter(insertBound);
        offset = pointer.getOffset();

        /*
         * FIXME: this will cause a whole set of Changes to be added to the
         * stack, when really we want this to be a single undoable operation.
         * The same fix for inserting when a selection is active will apply
         * here.
         */

        if (buffer.getHasSelection()) {
            start = buffer.getIter(selectionBound);
            // where is insertBound

            deleteRange(start, pointer);
        }

        stack.apply(new InsertChange(offset, range));

        for (i = 0; i < range.size(); i++) {
            span = range.get(i);
            buffer.insert(pointer, span.getText(), tagsForMarkup(span.getMarkup()));
        }
    }

    private void deleteBack() {
        final TextIter start, end;

        end = buffer.getIter(insertBound);

        if (buffer.getHasSelection()) {
            start = buffer.getIter(selectionBound);
        } else {
            if (end.isStart()) {
                return;
            }
            start = end.copy();
            start.backwardChar();
        }

        deleteRange(start, end);
    }

    private void deleteAt() {
        final TextIter start, end;

        start = buffer.getIter(insertBound);

        if (buffer.getHasSelection()) {
            end = buffer.getIter(selectionBound);
        } else {
            if (start.isEnd()) {
                return;
            }
            end = start.copy();
            end.forwardChar();
        }

        deleteRange(start, end);
    }

    /**
     * Effect a deletion from start to end.
     */
    private void deleteRange(TextIter start, TextIter end) {
        int alpha, omega, width;
        final Extract range;
        final Change change;

        alpha = start.getOffset();
        omega = end.getOffset();

        width = omega - alpha;

        range = stack.extractRange(alpha, width);
        change = new DeleteChange(alpha, range);
        stack.apply(change);
        this.affect(change);
    }

    private void toggleMarkup(Markup format) {
        final TextIter start, end;
        int alpha, omega, width;
        Extract r;
        int i;
        Span s;
        final Markup[] replacement;

        /*
         * If there is a selection then toggle the markup applied there.
         * Otherwise, change the current insertion point formats.
         */

        if (buffer.getHasSelection()) {
            start = selectionBound.getIter();
            end = insertBound.getIter();

            alpha = start.getOffset();
            omega = end.getOffset();

            width = omega - alpha;

            // FIXME what about toggling off?
            stack.apply(new FormatChange(alpha, width, format));

            r = stack.extractRange(alpha, width);
            for (i = 0; i < r.size(); i++) {
                s = r.get(i);
                buffer.applyTag(tagForMarkup(format), start, end);
            }
        } else {
            replacement = Markup.applyMarkup(insertMarkup, format);
            if (replacement == insertMarkup) {
                insertMarkup = Markup.removeMarkup(insertMarkup, format);
            } else {
                insertMarkup = replacement;
            }
        }
    }

    private void insertImage() {}

    private void undo() {
        final Change change;

        change = stack.undo();
        if (change == null) {
            return;
        }

        reverse(change);
    }

    private void redo() {
        final Change change;

        change = stack.redo();
        if (change == null) {
            return;
        }

        affect(change);
    }

    /**
     * Cause the given Change to be reflected in the TextView. The assumption
     * is made that the backing TextBuffer is in a state where applying this
     * Change makes sense.
     */
    private void affect(Change change) {
        TextIter start, end;
        Extract r;
        int i;
        Span s;

        start = buffer.getIter(change.getOffset());

        if (change instanceof InsertChange) {
            r = change.getAdded();
            for (i = 0; i < r.size(); i++) {
                s = r.get(i);
                buffer.insert(start, s.getText(), tagsForMarkup(s.getMarkup()));
            }
        } else if (change instanceof DeleteChange) {
            r = change.getRemoved();
            end = buffer.getIter(change.getOffset() + r.getWidth());
            buffer.delete(start, end);
        } else if (change instanceof TextualChange) {
            // FIXME
        }
    }

    /**
     * Revert this Change, removing it's affect on the view. A DeleteChange
     * will cause an insertion, etc.
     */
    private void reverse(Change change) {
        final TextIter start, end;
        Extract r;
        int i;
        Span s;

        start = buffer.getIter(change.getOffset());

        if (change instanceof InsertChange) {
            r = change.getAdded();
            end = buffer.getIter(change.getOffset() + r.getWidth());
            buffer.delete(start, end);
        } else if (change instanceof DeleteChange) {
            r = change.getRemoved();
            for (i = 0; i < r.size(); i++) {
                s = r.get(i);
                buffer.insert(start, s.getText(), tagsForMarkup(s.getMarkup()));
            }
        }
    }

    private Extract clipboard;

    private void copyText() {
        extractText(true);
    }

    private void cutText() {
        extractText(false);
    }

    private void extractText(boolean copy) {
        final TextIter start, end;
        int alpha, omega, width;
        final Change change;

        /*
         * If there's no selection, we can't "Copy" or "Cut"
         */

        if (!buffer.getHasSelection()) {
            return;
        }

        start = buffer.getIter(selectionBound);
        end = buffer.getIter(insertBound);

        alpha = start.getOffset();
        omega = end.getOffset();

        width = omega - alpha;

        /*
         * Copy the range to clipboard, being the "Copy" behviour.
         */

        clipboard = stack.extractRange(alpha, width);

        if (copy) {
            return;
        }

        /*
         * And now delete the selected range, which makes this the "Cut"
         * behaviour.
         */

        change = new DeleteChange(alpha, clipboard);
        stack.apply(change);
        buffer.delete(start, end);
    }

    private void pasteText() {
        final TextIter pointer, start;

        /*
         * Where, in TextBuffer terms?
         */

        pointer = buffer.getIter(insertBound);

        /*
         * FIXME this needs to be one operation, not two.
         */

        if (buffer.getHasSelection()) {
            start = buffer.getIter(selectionBound);
            // where is insertBound

            deleteRange(start, pointer);
        }

        insert(clipboard);
    }

    private void exportContents() {
        final Document book;

        book = DocBookConverter.buildTree(stack);
        try {
            book.toXML(System.out);
        } catch (IOException ioe) {
            throw new Error(ioe);
        }
    }

    /**
     * Hookup signals to aggregate formats to be used on a subsequent
     * insertion. The insertMarkup array starts empty, and builds up as
     * formats are toggled by the user. When the cursor moves, the Set is
     * changed to the formatting applying on character back.
     */
    private void hookupFormatManagement() {
        insertMarkup = null;

        buffer.connect(new TextBuffer.NotifyCursorPosition() {
            public void onNotifyCursorPosition(TextBuffer source) {
                final TextIter pointer;
                int offset;

                pointer = buffer.getIter(insertBound);
                offset = pointer.getOffset();

                insertOffset = offset;

                insertMarkup = stack.getMarkupAt(offset);
            }
        });
    }

    private void clearFormat() {
        final TextIter start, end;
        int alpha, omega, width;

        /*
         * If there is a selection then clear the markup applied there. This
         * may not be the correct implementation; there could be Markups which
         * are structural and not block or inline.
         */

        if (buffer.getHasSelection()) {
            start = selectionBound.getIter();
            end = insertBound.getIter();

            alpha = start.getOffset();
            omega = end.getOffset();

            width = omega - alpha;

            // FIXME
            // TODO replace with a ClearFormattingChange?
            stack.apply(new FormatChange(alpha, width));
            buffer.removeAllTags(start, end);
        }

        /*
         * Deactivate the any insert formatting.
         */

        insertMarkup = null;
    }
}
