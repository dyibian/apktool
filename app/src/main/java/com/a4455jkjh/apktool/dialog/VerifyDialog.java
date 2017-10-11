package com.a4455jkjh.apktool.dialog;
import brut.util.Log;
import com.a4455jkjh.apktool.MainActivity;
import com.a4455jkjh.apktool.util.Verify;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;

public class VerifyDialog extends DialogCommon
{

	public VerifyDialog(MainActivity main,int style){
		super(main,style);
	}

	@Override
	public void show() {
		setTitle("校验签名中……");
		super.show();
	}
	
	
	@Override
	protected void start() throws BuildException {
		if(!(input instanceof File))
			throw new BuildException("不是一个文件");
		File jar = (File)input;
		try {
			Verify verify = new Verify(jar);
			if(!verify.check()){
				throw new BuildException("校验签名失败");
			}
			Log.info("验证成功");
		} catch (IOException|NoSuchAlgorithmException
		|SignatureException|InvalidKeyException
		|CertificateEncodingException e) {
			throw new BuildException("校验签名失败",e);
		}
	}
	
}
