package com.a4455jkjh.apktool.dialog;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.URLSpan;
import brut.androlib.Androlib;
import brut.androlib.R;
import com.a4455jkjh.apktool.ApktoolActivity;
import org.jf.util.Log;

public class HelpDialog extends ProcessDialog {
	public HelpDialog(ApktoolActivity a, CharSequence t) {
		super(a, t);
	}
	@Override
	protected boolean appendInfo() {
		return false;
	}

	@Override
	protected void start() throws Exception {
		String apktool_url = "https://github.com/iBotPeaches/Apktool";
		String apksig_url="https://android.googlesource.com/platform/tools/apksig";
		String editor_url="https://github.com/nirenr/AndroLua_pro";
		Log.infoResources(R.string.help_msg_1);
		Log.infoResources(R.string.apktool_version, Androlib.getVersion());
		SpannableString apktool = new SpannableString("    " + apktool_url);
		URLSpan apktool_span = new URLSpan(apktool_url);
		apktool.setSpan(apktool_span, 4, apktool.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		Log.info(apktool);
		Log.infoResources(R.string.apksig_version);
		SpannableString apksig = new SpannableString("    " + apksig_url);
		URLSpan apksig_span = new URLSpan(apksig_url);
		apksig.setSpan(apksig_span, 4, apksig.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		Log.info(apksig);
		Log.infoResources(R.string.editor_version);
		SpannableString editor = new SpannableString("    " + editor_url);
		URLSpan editor_span = new URLSpan(editor_url);
		editor.setSpan(editor_span, 4, editor.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		Log.info(editor);
		Log.infoResources(R.string.help_msg);
	}

	@Override
	protected CharSequence getTitle(boolean success) {
		return "帮助";
	}



}
