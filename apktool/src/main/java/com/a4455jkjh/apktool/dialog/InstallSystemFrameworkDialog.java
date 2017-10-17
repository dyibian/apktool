package com.a4455jkjh.apktool.dialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import brut.androlib.res.decoder.ARSCDecoder;
import brut.util.Log;
import com.a4455jkjh.apktool.utils.Settings;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.IOUtils;

public class InstallSystemFrameworkDialog extends ProcessDialog<PackageManager> {
	public InstallSystemFrameworkDialog (Context c) {
		super(c, "正在处理系统应用");
	}

	@Override
	protected boolean appendInfo () {
		return true;
	}

	@Override
	protected void start () throws Exception {
		String framework = Settings.getFrameworkDir(context);
		Log.info("框架目录：" + framework);
		List<ApplicationInfo> apps = data.getInstalledApplications(0);
		for (ApplicationInfo app:apps) {
			if (app.sourceDir.startsWith("/data/"))
				continue;
			File apk = new File(app.sourceDir);
			try {
				ZipFile zip = new ZipFile(apk);
				ZipEntry entry = zip.getEntry("resources.arsc");

				if (entry == null) {
					throw new Exception("找不到resources.arsc");
				}
				InputStream in = zip.getInputStream(entry);
				byte[] data = IOUtils.toByteArray(in);
				in.close();
				ARSCDecoder.ARSCData arsc = ARSCDecoder.decode(
					new ByteArrayInputStream(data),
					true, true);
				publicizeResources(data, arsc.getFlagsOffsets());
				int id = arsc.getOnePackage().getId();
				if (id != 127) {
					Log.info("正在安装：" + apk);
					File outFile = new File(framework, id + ".apk");
					ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFile));
					out.setMethod(ZipOutputStream.STORED);
					CRC32 crc = new CRC32();
					crc.update(data);
					entry = new ZipEntry("resources.arsc");
					entry.setSize(data.length);
					entry.setCrc(crc.getValue());
					out.putNextEntry(entry);
					out.write(data);
					out.closeEntry();

					//Write fake AndroidManifest.xml file to support original aapt
					entry = zip.getEntry("AndroidManifest.xml");
					if (entry != null) {
						in = zip.getInputStream(entry);
						byte[] manifest = IOUtils.toByteArray(in);
						in.close();
						CRC32 manifestCrc = new CRC32();
						manifestCrc.update(manifest);
						entry.setSize(manifest.length);
						entry.setCompressedSize(-1);
						entry.setCrc(manifestCrc.getValue());
						out.putNextEntry(entry);
						out.write(manifest);
						out.closeEntry();
					}
					out.close();
					Log.info("已安装到：" + outFile);
				}
				zip.close();
			} catch (Exception e) {
				Log.log(Log.LogLevel.WARN,"处理文件：" + apk + "失败",e);
			}
		}
		Log.info("安装完成");
	}
	public void publicizeResources (byte[] arsc, ARSCDecoder. FlagsOffset[] flagsOffsets) {
        for (ARSCDecoder. FlagsOffset flags : flagsOffsets) {
            int offset = flags.offset + 3;
            int end = offset + 4 * flags.count;
            while (offset < end) {
                arsc[offset] |= (byte) 0x40;
                offset += 4;
            }
        }
    }
}
