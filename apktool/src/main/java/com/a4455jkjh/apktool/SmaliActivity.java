package com.a4455jkjh.apktool;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;
import com.a4455jkjh.apktool.views.SmaliEditor;
import com.myopicmobile.textwarrior.common.ColorSchemeDark;
import com.myopicmobile.textwarrior.common.ColorSchemeLight;
import jadx.core.utils.exceptions.JadxException;
import java.io.File;
import java.io.IOException;
import org.antlr.runtime.RecognitionException;

public class SmaliActivity extends ApktoolActivity
implements DialogInterface.OnClickListener {
	private SmaliEditor editor;
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editor);
		editor = findViewById1(R.id.editor);
		if (theme.equals("light"))
			editor.setColorScheme(new ColorSchemeLight());
		else
			editor.setColorScheme(new ColorSchemeDark());
		Intent i = getIntent();
		Uri data = i.getData();
		String path = data.getPath();
		setTitle(new File(path).
				 getName());
		try {
			editor.read(path);
		} catch (IOException e) {}
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		getMenuInflater().
			inflate(R.menu.smali, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu (Menu menu) {
		int type = editor.type;
		MenuItem save = menu.findItem(R.id.save);
		MenuItem tran = menu.findItem(R.id.translate);
		if (type == 0) {
			save.setEnabled(editor.changed);
			tran.setTitle("转为Java");
			save.setVisible(true);
		} else if (type == 2) {
			save.setEnabled(editor.changed);
			tran.setVisible(false);
			save.setEnabled(true);
		} else {
			save.setVisible(false);
			tran.setTitle("返回Smali");
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		switch (item.getItemId()) {
			case R.id.save:
				if (editor.save())
					Toast.makeText(this, "保存成功", 0).show();
				else
					Toast.makeText(this, "保存失败", 0).show();
				invalidateOptionsMenu();
				break;
			case R.id.undo:
				editor.undo();
				break;
			case R.id.redo:
				editor.redo();
				break;
			case R.id.find:
				SearchView view = (SearchView)item.getActionView();
				editor.find(view);
				break;
			case R.id.goto_line:
				SearchView view1 = (SearchView)item.getActionView();
				editor.gotoLine(view1);
				break;
			case R.id.translate:
				try {
					if (!editor.translate())
						Toast.makeText(this, "转换失败", 0).show();
				} catch (IOException | JadxException | RecognitionException e) {

				}
				invalidateOptionsMenu();
				break;
		}
		return true;
	}

	@Override
	public void onBackPressed () {
		try {
			if (editor.type == 1) {
				editor.translate();
				invalidateOptionsMenu();
			} else if (editor.changed) {
				showSaveDialog();
			} else
				super.onBackPressed();
		} catch (JadxException e) {} catch (IOException e) {} catch (RecognitionException e) {}
	}

	private void showSaveDialog () {
		new AlertDialog.Builder(this).
			setTitle("保存").
			setMessage("是否保存？").
			setPositiveButton("保存", this).
			setNegativeButton("取消", null).
			setNeutralButton("不保存", this).
			create().show();
	}

	@Override
	public void onClick (DialogInterface p1, int p2) {
		if(p2==DialogInterface.BUTTON_POSITIVE)
			editor.save();
		finish();
	}


}
