/*
 * EditorWindow.java
 *
 * Copyright (c) 2008 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package com.operationaldynamics.markerpen;

import java.util.HashSet;
import java.util.LinkedList;

import org.gnome.gdk.Event;
import org.gnome.gdk.EventKey;
import org.gnome.gdk.Keyval;
import org.gnome.gdk.ModifierType;
import org.gnome.gtk.Gtk;
import org.gnome.gtk.PolicyType;
import org.gnome.gtk.ScrolledWindow;
import org.gnome.gtk.TextBuffer;
import org.gnome.gtk.TextIter;
import org.gnome.gtk.TextMark;
import org.gnome.gtk.TextTag;
import org.gnome.gtk.TextView;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;
import org.gnome.gtk.Window;
import org.gnome.gtk.WrapMode;

class EditorWindow extends Window
{
    private Window window;

    private VBox top;

    private TextBuffer buffer;

    private TextMark selectionBound, insertBound;

    private TextView view;

    EditorWindow() {
        super();

        setupWindow();
        setupEditor();

        setupUndoRedo();
        installKeybindings();
        hookupFormatManagement();

        completeWindow();
    }

    private void setupWindow() {
        window = this;
        window.setDefaultSize(200, 300);

        top = new VBox(false, 6);
        window.add(top);

        window.connect(new DeleteEvent() {
            public boolean onDeleteEvent(Widget source, Event event) {
                Gtk.mainQuit();
                return false;
            }
        });
    }

    private void completeWindow() {
        window.showAll();
    }

    private void setupEditor() {
        final ScrolledWindow scroll;

        buffer = new TextBuffer();

        selectionBound = buffer.getSelectionBound();
        insertBound = buffer.getInsert();

        view = new TextView(buffer);

        /*
         * word wrap
         */

        scroll = new ScrolledWindow();
        scroll.setPolicy(PolicyType.NEVER, PolicyType.ALWAYS);
        scroll.add(view);
        view.setWrapMode(WrapMode.WORD);

        /*
         * Paragraph spacing
         */

        view.setPaddingBelowParagraph(10);

        top.packStart(scroll);
    }

    private LinkedList<Change> undoStack;

    private int undoPointer;

    private boolean undoInProgress;

    private void setupUndoRedo() {
        undoStack = new LinkedList<Change>();
        undoPointer = 0;

        buffer.connect(new TextBuffer.InsertText() {
            public void onInsertText(TextBuffer source, TextIter pointer, String text) {
                if (undoInProgress) {
                    return;
                }

                undoStack.add(undoPointer, new Change(pointer, pointer, text));
                undoPointer++;
            }
        });

        buffer.connect(new TextBuffer.DeleteRange() {
            public void onDeleteRange(TextBuffer source, TextIter start, TextIter end) {
                String text;

                if (undoInProgress) {
                    return;
                }

                text = buffer.getText(start, end, false);

                undoStack.add(undoPointer, new Change(start, end, text));
                undoPointer++;

            }
        });
    }

    private void undo() {
        final Change change;
        final TextIter start, end;

        if (undoStack.size() == 0) {
            return;
        }
        if (undoPointer == 0) {
            return;
        }
        undoInProgress = true;
        undoPointer--;

        change = undoStack.get(undoPointer);

        start = buffer.getIter(change.offset);

        if (change.add) {
            end = buffer.getIter(change.offset + change.length);
            buffer.delete(start, end);
        } else {
            buffer.insert(start, change.text);
        }

        undoInProgress = false;
    }

    private void redo() {
        final Change change;
        final TextIter start, end;

        if (undoStack.size() == 0) {
            return;
        }
        if (undoPointer == undoStack.size()) {
            return;
        }
        undoInProgress = true;

        change = undoStack.get(undoPointer);

        start = buffer.getIter(change.offset);

        if (change.add) {
            buffer.insert(start, change.text);
        } else {
            end = buffer.getIter(change.offset + change.length);

            buffer.delete(start, end);
        }

        undoPointer++;
        undoInProgress = false;
    }

    private void installKeybindings() {
        view.connect(new Widget.KeyPressEvent() {
            public boolean onKeyPressEvent(Widget source, EventKey event) {
                final Keyval key;
                final ModifierType mod;

                key = event.getKeyval();
                mod = event.getState();

                if (mod == ModifierType.CONTROL_MASK) {
                    if (key == Keyval.i) {
                        toggleFormat(Format.italics);
                        return true;
                    } else if (key == Keyval.b) {
                        toggleFormat(Format.bold);
                        return true;
                    } else if (key == Keyval.m) {
                        toggleFormat(Format.mono);
                        return true;
                    } else if (key == Keyval.s) {
                        System.out.println(Serializer.extractToFile(buffer));
                    } else if (key == Keyval.y) {
                        redo();
                    } else if (key == Keyval.z) {
                        undo();
                    }
                } else if (mod.contains(ModifierType.CONTROL_MASK)
                        && mod.contains(ModifierType.SHIFT_MASK)) {
                    if (key == Keyval.Space) {
                        clearFormat();
                    }
                }
                return false;
            }
        });
    }

    private HashSet<TextTag> insertTags;

    /**
     * Hookup signals to aggregate formats to be used on a subsequent
     * insertion. The insertTags Set starts empty, and builds up as formats
     * are toggled by the user. When the cursor moves, the Set is changed to
     * the formatting applying on character back.
     */
    private void hookupFormatManagement() {
        insertTags = new HashSet<TextTag>(4);

        buffer.connectAfter(new TextBuffer.InsertText() {
            public void onInsertText(TextBuffer source, TextIter end, String text) {
                final TextIter start;

                start = end.copy();
                start.backwardChars(text.length());

                for (TextTag tag : insertTags) {
                    buffer.applyTag(tag, start, end);
                }
            }
        });

        buffer.connect(new TextBuffer.MarkSet() {
            public void onMarkSet(TextBuffer source, TextIter location, TextMark mark) {
                if (mark != insertBound) {
                    return;
                }

                /*
                 * Forget previous insert formatting.
                 */
                insertTags.clear();

                /*
                 * But having moved, pickup the formatting of the character
                 * preceeding the cursor.
                 */

                location.backwardChar();

                for (TextTag tag : location.getTags()) {
                    insertTags.add(tag);
                }
            };
        });
    }

    private void toggleFormat(TextTag format) {
        final TextIter start, end, iter;

        if (buffer.getHasSelection()) {
            start = selectionBound.getIter();
            end = insertBound.getIter();

            /*
             * There is a subtle bug that if you have selected moving
             * backwards, selectionBound will be at a point where the
             * formatting ends, with the result that the second toggling will
             * fail. Work around this by ensuring we work from the earlier of
             * the two TextIters.
             */
            if (start.getOffset() > end.getOffset()) {
                iter = end;
            } else {
                iter = start;
            }

            if (iter.hasTag(format)) {
                buffer.removeTag(format, start, end);
            } else {
                buffer.applyTag(format, start, end);
            }
        } else {
            if (insertTags.contains(format)) {
                insertTags.remove(format);
            } else {
                insertTags.add(format);
            }
        }
    }

    private void clearFormat() {
        final TextIter start, end;

        start = selectionBound.getIter();
        end = insertBound.getIter();

        buffer.removeAllTags(start, end);

        /*
         * and regardless, deactivate the any insert formatting.
         */

        insertTags.clear();
    }

    /**
     * Enable unit tests to get to the underlying TextBuffer
     */
    TextBuffer getBuffer() {
        return buffer;
    }
}

class Change
{
    final int offset;

    final int length;

    final String text;

    final boolean add;

    final TextTag tag;

    Change(TextIter start, TextIter end, String text) {
        this.offset = start.getOffset();

        if (end == start) {
            this.length = text.length();
            this.add = true;
        } else {
            this.length = end.getOffset() - this.offset;
            this.add = false;
        }
        this.text = text;

        this.tag = null;
    }

    Change(TextIter start, TextIter end, TextTag tag, boolean apply) {
        this.offset = start.getOffset();
        this.length = end.getOffset() - this.offset;
        this.tag = tag;
        this.add = apply;

        this.text = null;
    }
}
