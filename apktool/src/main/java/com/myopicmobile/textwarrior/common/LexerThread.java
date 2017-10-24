package com.myopicmobile.textwarrior.common;

import java.util.ArrayList;

public abstract class LexerThread extends Thread 
{
	protected CharSequence doc;
	private boolean rescan = false;
	protected Lexer _lexManager;
	/** can be set by another thread to stop the scan immediately */
	protected final Flag _abort;
	/** A collection of Pairs, where Pair.first is the start
	 *  position of the token, and Pair.second is the type of the token.*/
	protected ArrayList<Pair> _tokens;
	protected Language lan;

	public LexerThread (CharSequence doc) {
		this.doc = doc;
		_abort = new Flag();
	}
	public final void setLexer(Lexer p) {
		_lexManager = p;
		lan = p.getLanguage();
	}


	@Override
	public void run() {
		do{
			rescan = false;
			_abort.clear();
			tokenize();
		}
		while(rescan);

		if (!_abort.isSet()) {
			// lex complete
			_lexManager.tokenizeDone(_tokens);
		}
	}

	public void restart() {
		rescan = true;
		_abort.set();
	}

	public void abort() {
		_abort.set();
	}
	protected abstract void tokenize();
}
