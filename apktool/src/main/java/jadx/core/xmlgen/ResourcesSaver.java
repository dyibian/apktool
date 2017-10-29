package jadx.core.xmlgen;

import jadx.api.ResourceFile;
import jadx.api.ResourceType;
import jadx.core.codegen.CodeWriter;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static jadx.core.utils.files.FileUtils.prepareFile;
import android.graphics.Bitmap;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ResourcesSaver implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(ResourcesSaver.class);

	private final ResourceFile resourceFile;
	private File outDir;

	public ResourcesSaver (File outDir, ResourceFile resourceFile) {
		this.resourceFile = resourceFile;
		this.outDir = outDir;
	}

	@Override
	public void run () {
		if (!ResourceType.isSupportedForUnpack(resourceFile.getType())) {
			return;
		}
		ResContainer rc = resourceFile.loadContent();
		if (rc != null) {
			saveResources(rc);
		}
	}

	private void saveResources (ResContainer rc) {
		if (rc == null) {
			return;
		}
		List<ResContainer> subFiles = rc.getSubFiles();
		if (subFiles.isEmpty()) {
			save(rc, outDir);
		} else {
			for (ResContainer subFile : subFiles) {
				saveResources(subFile);
			}
		}
	}

	private void save (ResContainer rc, File outDir) {
		File outFile = new File(outDir, rc.getFileName());
		Bitmap image = rc.getImage();
		if (image != null) {
			String ext = FilenameUtils.getExtension(outFile.getName()).toUpperCase();
			try {
				outFile = prepareFile(outFile);
				OutputStream o = new FileOutputStream(outFile);
				image.compress(Bitmap.CompressFormat.valueOf(ext),
							   100, o);
				o.close();
			} catch (IOException e) {
				LOG.error("Failed to save image: {}", rc.getName(), e);
			}
			return;
		}
		CodeWriter cw = rc.getContent();
		if (cw != null) {
			cw.save(outFile);
			return;
		}
		LOG.warn("Resource '{}' not saved, unknown type", rc.getName());
	}
}
