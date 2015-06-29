package com.mycompany.apktool;
import android.graphics.*;
import brut.androlib.*;
import brut.androlib.err.*;
import brut.androlib.res.decoder.*;
import brut.util.*;
import java.io.*;
import org.apache.commons.io.*;
import out.*;

public class r9s extends Res9patchStreamDecoder implements ResStreamDecoder {
    @Override
    public void decode(InputStream in, OutputStream out)
	throws AndrolibException {
        try {
            byte[] data = IOUtils.toByteArray(in);
            Bitmap im = BitmapFactory.decodeByteArray(data,0,data.length);// ImageIO.read(new ByteArrayInputStream(data));
            int w = im.getWidth(), h = im.getHeight();

           // ImageTypeSpecifier its = ImageTypeSpecifier.createFromRenderedImage( im );
            Bitmap im2 = Bitmap.createBitmap( w+2, h+2 ,im.getConfig());

          //  im2.getRaster().setRect(1, 1, im.getRaster());
            NinePatch np = getNinePatch(data);
            drawHLine(im2, h + 1, np.padLeft + 1, w - np.padRight);
            drawVLine(im2, w + 1, np.padTop + 1, h - np.padBottom);

            int[] xDivs = np.xDivs;
            for (int i = 0; i < xDivs.length; i += 2) {
                drawHLine(im2, 0, xDivs[i] + 1, xDivs[i + 1]);
            }

            int[] yDivs = np.yDivs;
            for (int i = 0; i < yDivs.length; i += 2) {
                drawVLine(im2, 0, yDivs[i] + 1, yDivs[i + 1]);
            }

           // ImageIO.write(im2, "png", out);
		   im2.compress(Bitmap.CompressFormat.PNG,100,out);
        } catch (IOException ex) {
            throw new AndrolibException(ex);
        } catch (NullPointerException ex) {
            // In my case this was triggered because a .png file was
            // containing a html document instead of an image.
            // This could be more verbose and try to MIME ?
            throw new AndrolibException(ex);
        }
    }

    private NinePatch getNinePatch(byte[] data) throws AndrolibException,
	IOException {
        ExtDataInput di = new ExtDataInput(new ByteArrayInputStream(data));
        find9patchChunk(di);
        return NinePatch.decode(di);
    }

    private void find9patchChunk(DataInput di) throws AndrolibException,
	IOException {
        di.skipBytes(8);
        while (true) {
            int size;
            try {
                size = di.readInt();
            } catch (IOException ex) {
                throw new CantFind9PatchChunk("Cant find nine patch chunk", ex);
            }
            if (di.readInt() == NP_CHUNK_TYPE) {
                return;
            }
            di.skipBytes(size + 4);
        }
    }

    private void drawHLine(Bitmap im, int y, int x1, int x2) {
        for (int x = x1; x <= x2; x++) {
            im.setPixel(x, y, NP_COLOR);
			
        }
    }

    private void drawVLine(Bitmap im, int x, int y1, int y2) {
        for (int y = y1; y <= y2; y++) {
            im.setPixel(x, y, NP_COLOR);
        }
    }

    private static final int NP_CHUNK_TYPE = 0x6e705463; // npTc
    private static final int NP_COLOR = 0xff000000;

    public static class NinePatch {
        public final int padLeft, padRight, padTop, padBottom;
        public final int[] xDivs, yDivs;

        public NinePatch(int padLeft, int padRight, int padTop, int padBottom,
                         int[] xDivs, int[] yDivs) {
            this.padLeft = padLeft;
            this.padRight = padRight;
            this.padTop = padTop;
            this.padBottom = padBottom;
            this.xDivs = xDivs;
            this.yDivs = yDivs;
        }

        public static NinePatch decode(ExtDataInput di) throws IOException {
            di.skipBytes(1);
            byte numXDivs = di.readByte();
            byte numYDivs = di.readByte();
            di.skipBytes(1);
            di.skipBytes(8);
            int padLeft = di.readInt();
            int padRight = di.readInt();
            int padTop = di.readInt();
            int padBottom = di.readInt();
            di.skipBytes(4);
            int[] xDivs = di.readIntArray(numXDivs);
            int[] yDivs = di.readIntArray(numYDivs);

            return new NinePatch(padLeft, padRight, padTop, padBottom, xDivs,
								 yDivs);
        }
    }
}
