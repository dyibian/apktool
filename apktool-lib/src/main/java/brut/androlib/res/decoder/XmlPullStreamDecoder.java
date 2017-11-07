/**
 *  Copyright (C) 2017 Ryszard Wiśniewski <brut.alll@gmail.com>
 *  Copyright (C) 2017 Connor Tumbleson <connor.tumbleson@gmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package brut.androlib.res.decoder;

import brut.androlib.AndrolibException;
import brut.androlib.res.data.ResTable;
import brut.androlib.res.util.ExtXmlSerializer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.jf.util.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * @author Ryszard Wiśniewski <brut.alll@gmail.com>
 */
public class XmlPullStreamDecoder implements ResStreamDecoder {
    public XmlPullStreamDecoder (XmlPullParser parser,
								 ExtXmlSerializer serializer) {
        this.mParser = parser;
        this.mSerial = serializer;
    }
	
	ResTable resTable;
    @Override
    public void decode (InputStream in, OutputStream out)
	throws AndrolibException {
        try {
            resTable = ((AXmlResourceParser) mParser).getAttrDecoder().getCurrentPackage().getResTable();
			mParser.setInput(in, "utf-8");
			XMLWrite writer = new XMLWrite(resTable);
			writer.parse(mParser);
			writer.write(out);
        } catch (XmlPullParserException ex) {
			
            throw new AndrolibException("Could not decode XML", ex);
        } catch (IOException ex) {
			Log.log(Log.LogLevel.WARN,null,ex);
            throw new AndrolibException("Could not decode XML", ex);
        }
    }
    public void decodeManifest (InputStream in, OutputStream out)
	throws AndrolibException {
		decode(in, out);
    }

    private final XmlPullParser mParser;
    private final ExtXmlSerializer mSerial;

}
