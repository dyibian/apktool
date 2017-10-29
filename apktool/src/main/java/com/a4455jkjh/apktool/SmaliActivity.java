package com.a4455jkjh.apktool;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.a4455jkjh.apktool.views.SmaliEditor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import android.view.Menu;
import com.myopicmobile.textwarrior.common.ColorScheme;
import com.myopicmobile.textwarrior.common.ColorSchemeLight;
import com.myopicmobile.textwarrior.common.ColorSchemeDark;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.SearchView;
import jadx.core.utils.exceptions.JadxException;
import org.antlr.runtime.RecognitionException;
import android.view.View;

public class SmaliActivity extends ApktoolActivity {
	private SmaliEditor editor;
	private boolean changed;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editor);
		changed=true;
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().
			inflate(R.menu.smali, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		int type = editor.type;
		MenuItem save = menu.findItem(R.id.save);
		MenuItem tran = menu.findItem(R.id.translate);
		if(type==0){
			save.setEnabled(changed);
			tran.setTitle("转为Java");
			save.setVisible(true);
		}else{
			save.setVisible(false);
			tran.setTitle("返回Smali");
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.save:
				if (editor.save())
					Toast.makeText(this, "保存成功", 0).show();
				else
					Toast.makeText(this, "保存失败", 0).show();
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
					if(!editor.translate())
						Toast.makeText(this,"转换失败",0).show();
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
			if (editor.type == 1)
				editor.translate();
			else
				super.onBackPressed();
		} catch (JadxException e) {} catch (IOException e) {} catch (RecognitionException e) {}
	}

}
