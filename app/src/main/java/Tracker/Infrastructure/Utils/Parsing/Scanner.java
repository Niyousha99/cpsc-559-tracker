package Tracker.Infrastructure.Utils.Parsing;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import Tracker.Infrastructure.Utils.FailureException;

public class Scanner {
    public static final int MAX_BUFFER_SIZE = 1048576;
    public static final String DEFAULT_CHARSET = "utf-8";

    public static final String NL = "\n";
    public static final String CR = "\r";

    private final HashMap<Character, Boolean> isSpace;

    private final DataInputStream input;
    private final Queue<Token> queue;
    private final byte[] buffer;
    
    private int iterator;
    private int limit;
    private String context;
    private String tmp;
    private boolean inputDone;

    public Scanner(DataInputStream input, Queue<Token> queue) {
        this.input = input;
        this.queue = queue;
        this.buffer = new byte[MAX_BUFFER_SIZE];
        this.iterator = 0;
        this.limit = 0;
        this.context = "";
        this.inputDone = false;

        this.isSpace = new HashMap<Character, Boolean>();
        this.isSpace.put((char) 32, true);
        this.isSpace.put((char) 9, true);
        this.isSpace.put((char) 10, true);
        this.isSpace.put((char) 13, true);

        this.tmp = "";
    }

    public void scan() throws FailureException {
        scanForTokens();
    }

    public String consumeContext() {
        if (context == null) {
            return "";
        }
        String res = new String(context);
        context = null;
        return res;
    }

    public int getContext() {
        return iterator;
    }

    public DataInputStream releaseStream() {
        return input;
    }

    private void scanForTokens() throws FailureException {
        int c;
        while (((c = get()) != -1) && (this.isSpace.getOrDefault((char) c, false))) {
            if ((char) c == '\r') {
                if ((char) get() == '\n') {
                    this.queue.add(new Token(TokenIdentifier.CRLF, "\r\n"));
                    return;
                }

                else {
                    unget();
                }
            }

            if (c == 32) {
                this.queue.add(new Token(TokenIdentifier.SP, "" + (char) c));
                return;
            }

            else if (c == 9) {
                this.queue.add(new Token(TokenIdentifier.HTAB, "" + (char) c));
                return;
            }
        }

        if (c == -1) {
            this.queue.add(new Token(TokenIdentifier.EOF, ""));
            return;
        }

        if ((char) c == ':') {
            this.queue.add(new Token(TokenIdentifier.COLON, ":"));
            return;
        }

        while (((c > 32) && (c <= 126)) && ((char) c != ':')) {
            tmp = tmp + (char) c;
            c = get();
        }
        if (tmp.length() > 0) {
            this.queue.add(new Token(TokenIdentifier.WORD, tmp));
            resetTmp();
            unget();
            return;
        }

        throw new FailureException();
    }


    private int get() {
        if (this.iterator >= this.limit) {
            nextContext();
        }

        if (this.iterator >= this.limit) {
            return -1;
        }

        char c = context.charAt(iterator);
        iterator++;
        return (int) c;
    }

    private void unget()  {
        if (this.iterator > 0) {
            this.iterator--;
        }
    }

    private void resetTmp() {
        this.tmp = "";
    }

    public void nextContext() {
        if (inputDone) {
            return;
        }
        int readBytes = -1;
        try {
            if ((readBytes = input.read(buffer)) != -1) {
                String tm = context;
                context = new String(buffer, 0, readBytes, DEFAULT_CHARSET);
                iterator = 0;

                if (tm.length() > 0) {
                    context = tm.charAt(tm.length()-1) + context;
                    iterator++;
                }

                limit = context.length();                
            }

            else {
                inputDone = true;
                context = "";
            }
        }

        catch (Exception e) {
            inputDone = true;
        }
    }
}
