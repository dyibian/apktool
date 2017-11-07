package com.a4455jkjh.apktool.views;

import android.content.Context;
import android.graphics.Typeface;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.SearchView;
import android.widget.Toast;
import brut.androlib.mod.SmaliMod;
import com.a4455jkjh.apktool.R;
import com.myopicmobile.textwarrior.android.AutoCompletePanel;
import com.myopicmobile.textwarrior.android.FreeScrollingTextField;
import com.myopicmobile.textwarrior.android.YoyoNavigationMethod;
import com.myopicmobile.textwarrior.common.Document;
import com.myopicmobile.textwarrior.common.DocumentProvider;
import com.myopicmobile.textwarrior.common.Lexer;
import com.myopicmobile.textwarrior.common.LexerThread;
import com.myopicmobile.textwarrior.common.LinearSearchStrategy;
import com.myopicmobile.textwarrior.common.SmaliLexer;
import jadx.api.JadxArgs;
import jadx.api.JadxDecompiler;
import jadx.api.JavaClass;
import jadx.core.utils.exceptions.JadxException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.io.IOUtils;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.jf.dexlib2.writer.io.FileDataStore;
import com.myopicmobile.textwarrior.common.JavaLexerThread;
import com.myopicmobile.textwarrior.common.CharSeqReader;
import com.myopicmobile.textwarrior.common.XMLFlexerThread;

public class SmaliEditor extends FreeScrollingTextField {
	private float size;
	private String path;
	public int type;
	private int line=0;
	private CharSequence tmp=null;
	public SmaliEditor (Context c, AttributeSet a) {
		super(c, a);
		type = 0;
		AutoCompletePanel.setLanguage(LanguageSmali.getInstance());
		Lexer.setLanguage(LanguageSmali.getInstance());
		setTypeface(Typeface.MONOSPACE);
		size = c.getResources().
			getDimension(R.dimen.size);
		setShowLineNumbers(true);
		setWordWrap(false);
		setTabSpaces(4);
		setAutoIndentWidth(4);
		setTextSize(12 * size);
		setHighlightCurrentRow(true);
		setWordWrap(false);
		//Lexer.setLanguage(LanguageNonProg.getInstance());
		setNavigationMethod(new YoyoNavigationMethod(this));
	}
	private void setTextSize (float size) {
		setTextSize((int)size);
	}
	public void setText (CharSequence text) {
		Document doc = new Document(this);
		doc.setWordWrap(false);
		doc.setText(text);
		DocumentProvider pro = new DocumentProvider(doc);
		setDocumentProvider(pro);
	}
	public void read (String path) throws IOException {
		this.path = path;
		if (path.endsWith(".xml"))
			type = 2;
		else
			type = 0;
		InputStream is=new FileInputStream(path);
		String text = IOUtils.toString(is);
		setText(text);
	}
	public boolean save () {
		DocumentProvider doc = createDocumentProvider();
		try {
			OutputStream os = new FileOutputStream(path);
			IOUtils.write(doc.toString(), os);
			changed=false;
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	public void undo () {
		DocumentProvider doc = createDocumentProvider();
		int newPosition = doc.undo();

		if (newPosition >= 0) {
			//TODO editor.setEdited(false); if reached original condition of file
			setEdited(true);

			respan();
			selectText(false);
			moveCaret(newPosition);
			invalidate();
		}

	}

	public void redo () {
		DocumentProvider doc = createDocumentProvider();
		int newPosition = doc.redo();

		if (newPosition >= 0) {
			setEdited(true);

			respan();
			selectText(false);
			moveCaret(newPosition);
			invalidate();
		}

	}
	public void find (SearchView searchView) {
		SearchView.OnQueryTextListener query = new SearchView.OnQueryTextListener(){
			int idx=0;
			@Override
			public boolean onQueryTextSubmit (String p1) {
				idx = findNext(p1, idx);
				if (idx == 0)
					return false;
				return true;
			}
			@Override
			public boolean onQueryTextChange (String p1) {
				idx = 0;
				return onQueryTextSubmit(p1);
			}
		};
		searchView.setOnQueryTextListener(query);
	}
	private int findNext (String kw, int idx) {
		LinearSearchStrategy finder = new LinearSearchStrategy();
		if (kw.isEmpty()) {
			selectText(false);
			return 0;
		}
		int len = kw.length();
		idx = finder.find(createDocumentProvider(), kw, idx, createDocumentProvider().length(), false, false);
		if (idx == -1) {
			selectText(false);
			Toast.makeText(getContext(), "未找到", 500).show();
			return 0;
		}
		setSelection(idx, len);
		idx += len;
		moveCaret(idx);
		return idx;
	}
	public void gotoLine (SearchView searchView) {
		SearchView.OnQueryTextListener query = new SearchView.OnQueryTextListener(){
			@Override
			public boolean onQueryTextSubmit (String p1) {
				return false;
			}
			@Override
			public boolean onQueryTextChange (String p1) {
				if (p1.equals(""))
					return false;
				int line = Integer.parseInt(p1);
				gotoLine(line);
				return true;
			}
		};
		searchView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
		searchView.setOnQueryTextListener(query);
	}
	public void gotoLine (int line) {
		if (line > _hDoc.getRowCount()) {
			line = _hDoc.getRowCount();
		}
		if (line <= 0)
			line = 1;
		int i=createDocumentProvider().getLineOffset(line - 1);
		setSelection(i);
	}
	private int _index = 0;
	public void setSelection (int index) {
		selectText(false);
		if (!hasLayout())
			moveCaret(index);
		else
			_index = index;
	}
	@Override
	protected void onLayout (boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (_index != 0 && right > 0) {
			moveCaret(_index);
			_index = 0;
		}
	}

	@Override
	protected LexerThread getlexer () {
		if (type == 0)
			return new SmaliLexer(_hDoc);
		if (type == 2)
			return new XMLFlexerThread(_hDoc);
		return new JavaLexerThread(_hDoc);
	}

	public boolean translate () throws IOException, RecognitionException, JadxException {
		if (type == 2)
			return false;
		if (type == 1) {
			type = 0;
			setText(tmp);
			setSelection(line + 10);
			line = 0;
			tmp = null;
			return true;
		}
		tmp = _hDoc;
		line = getCaretPosition();
		Opcodes code = Opcodes.forApi(15);
		DexBuilder builder = new DexBuilder(code);
		File out = File.createTempFile("Apktool", "out.dex");
		FileOutputStream fo = new FileOutputStream(out);
		IOUtils.write(tmp, fo);
		fo.close();
		SmaliMod.assembleSmaliFile(out, builder, false, false);
		out.delete();
		builder.writeTo(new FileDataStore(out));
		JadxArgs arg = new JadxArgs();
		arg.setSkipResources(true);
		JadxDecompiler dec = new JadxDecompiler(arg);
		dec.loadFile(out);
		JavaClass clz = dec.getClasses().get(0);
		clz.decompile();
		type = 1;
		setText(clz.getCode());
		out.delete();
		return true;
	}
}
