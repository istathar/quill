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

import markerpen.textbase.Change;
import markerpen.textbase.CharacterSpan;
import markerpen.textbase.Common;
import markerpen.textbase.DeleteChange;
import markerpen.textbase.FormatChange;
import markerpen.textbase.InsertChange;
import markerpen.textbase.Markup;
import markerpen.textbase.Span;
import markerpen.textbase.TextStack;

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
        clipboard = new Span[0];
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
        final TextIter start, pointer;
        final int offset;
        final Span span;

        /*
         * Where, in TextBuffer terms?
         */

        pointer = buffer.getIter(insertBound);
        offset = pointer.getOffset();

        /*
         * Create a Span and insert into our internal stack.
         */

        span = new CharacterSpan(ch, insertMarkup);
        stack.apply(new InsertChange(offset, span));

        /*
         * And now send the character to the TextBuffer, updating the TextView
         * to reflect the user's input.
         */

        if (buffer.getHasSelection()) {
            start = buffer.getIter(selectionBound);
            deleteRange(start, pointer);
        }

        buffer.insert(pointer, span.getText(), tagsForMarkup(insertMarkup));
    }

    private void insert(Span[] range) {
        final TextIter pointer;
        final TextIter start;
        final int offset;

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
        for (Span span : range) {
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

        alpha = start.getOffset();
        omega = end.getOffset();

        width = omega - alpha;

        /*
         * There is a subtle bug that if you have selected moving backwards,
         * selectionBound will be at a point where the range ends. We need to
         * work in increasing order in our internal Text representation.
         */

        if (width < 0) {
            width = -width;
            alpha = omega;
        }

        stack.apply(new DeleteChange(alpha, width));

        buffer.delete(start, end);
    }

    private void toggleMarkup(Markup format) {
        final TextIter start, end;
        int alpha, omega, width;
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

            if (width < 0) {
                width = -width;
                alpha = omega;
            }

            // FIXME what about toggling off?
            stack.apply(new FormatChange(alpha, width, format));

            for (Span s : stack.copyRange(alpha, width)) {
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
     * Cause the given Change to be reflected in the view.
     */
    private void affect(Change change) {
        final TextIter start, end;

        start = buffer.getIter(change.getOffset());

        if (change instanceof InsertChange) {
            buffer.insert(start, change.getText());
        } else {
            end = buffer.getIter(change.getOffset() + change.getLength());
            buffer.delete(start, end);
        }
    }

    /**
     * Revert this Change, removing it's affect on the view. A DeleteChange
     * will cause an insertion, etc.
     */
    private void reverse(Change change) {
        final TextIter start, end;

        start = buffer.getIter(change.getOffset());

        if (change instanceof InsertChange) {
            end = buffer.getIter(change.getOffset() + change.getLength());
            buffer.delete(start, end);
        } else if (change instanceof DeleteChange) {
            buffer.insert(start, change.getText());
        }
    }

    private Span[] clipboard;

    private void copyText() {
        extractText(true);
    }

    private void cutText() {
        extractText(false);
    }

    private void extractText(boolean copy) {
        final TextIter start, end;
        int alpha, omega, width;

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

        if (width < 0) {
            width = -width;
            alpha = omega;
        }

        /*
         * Copy the range to clipboard, being the "Copy" behviour.
         */

        clipboard = stack.copyRange(alpha, width);

        if (copy) {
            return;
        }

        /*
         * And now delete the selected range, which makes this the "Cut"
         * behaviour.
         */

        stack.apply(new DeleteChange(alpha, width));
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

    private void exportContents() {}

    /**
     * Hookup signals to aggregate formats to be used on a subsequent
     * insertion. The insertTags Set starts empty, and builds up as formats
     * are toggled by the user. When the cursor moves, the Set is changed to
     * the formatting applying on character back.
     */
    private void hookupFormatManagement() {
        insertMarkup = null;

        buffer.connect(new TextBuffer.NotifyCursorPosition() {
            public void onNotifyCursorPosition(TextBuffer source) {
                final int offset;

                offset = buffer.getCursorPosition();
                System.out.println(offset + "\t" + (insertMarkup != null ? insertMarkup[0] : ""));
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

            if (width < 0) {
                width = -width;
                alpha = omega;
            }

            stack.apply(new FormatChange(alpha, width));
            buffer.removeAllTags(start, end);
        }

        /*
         * Deactivate the any insert formatting.
         */

        insertMarkup = null;
    }
}
