package rprocessing.mode;

import java.awt.event.InputEvent;

import processing.app.ui.Editor;
import processing.app.ui.EditorToolbar;

/**
 * RLangToolbar is toolbar in R mode, which takes care of run and stop
 * buttons. 
 * 
 * @author github.com/gaocegege
 */
public class RLangToolbar extends EditorToolbar {

    private static final long    serialVersionUID = -4227659009839101912L;

    private static final boolean VERBOSE          = Boolean
        .parseBoolean(System.getenv("VERBOSE_RLANG_MODE"));

    private static void log(String msg) {
        if (!VERBOSE) {
            return;
        }
        System.err.println(RLangToolbar.class.getSimpleName() + ": " + msg);
    }

    public RLangToolbar(final Editor editor) {
        super(editor);
    }

    @Override
    public void handleRun(final int modifiers) {
        log("The toolbar is handling start-event.");
        final RLangEditor peditor = (RLangEditor) editor;
        final boolean shift = (modifiers & InputEvent.SHIFT_MASK) != 0;
        if (shift) {
            peditor.handlePresent();
        } else {
            peditor.handleRun();
        }
    }

    @Override
    public void handleStop() {
        log("The toolbar is handling stop-event.");
        final RLangEditor peditor = (RLangEditor) editor;
        peditor.handleStop();
    }
}
