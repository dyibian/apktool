package brut.androlib.res.decoder;

import android.util.Log;
import brut.androlib.res.data.ResTable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.util.HashMap;
import java.util.Iterator;

public class XMLWrite {
	ResTable resTable;
	Tag root;
	boolean showSdkInfo;
	Map<String,String> nameSpaces;
	class Attribute {
		public String name;
		public String namespace;
		public String value;
		public boolean read (XmlPullParser xpp, int i) {
			name = xpp.getAttributeName(i);
			namespace = nameSpaces.get(xpp.getAttributeNamespace(i));
			value = xpp.getAttributeValue(i);
			String n = xpp.getName();
			if (n.equals("manifest")) {
				if (name.equalsIgnoreCase(("package"))) {
					resTable.setPackageRenamed(value);
					return true;
				} else if (name.equalsIgnoreCase("versionCode")) {
					resTable.setVersionCode(value);
					return false;
				} else if (name.equalsIgnoreCase("versionName")) {
					resTable.setVersionName(value);
					return false;
				}
			}
			if (n.equals("uses-sdk")) {
				String a_ns = "http://schemas.android.com/apk/res/android";
				String ns = xpp.getAttributeNamespace(i);

				if (a_ns.equalsIgnoreCase(ns)) {
					if (name.equalsIgnoreCase("minSdkVersion")
						|| name.equalsIgnoreCase("targetSdkVersion")
						|| name.equalsIgnoreCase("maxSdkVersion")) {
						resTable.addSdkInfo(name, value);
						return showSdkInfo;
					} 
				} 
			}
			return true;
		}
	}

	class Tag {
		private String name;
		private List<Attribute> attributes;
		private List<Tag> tags;
		private String text;
		public Tag () {
			attributes = new ArrayList<Attribute>();
			tags = new ArrayList<Tag>();
		}
		public void setName (String name) {
			this.name = name;
		}
		public void setText (String text) {
			this.text = text;
		}
		public void read (XmlPullParser xpp, int event) throws IOException, XmlPullParserException {
			if (event == XmlPullParser.START_TAG) {
				name = xpp.getName();
				putNameSpace(xpp);
				int c = xpp.getNamespaceCount(xpp.getDepth());
				Log.i("NAMESPACE", name + ":" + c);
				c = xpp.getAttributeCount();
				for (int i=0; i < c;i++) {
					Attribute a=new Attribute();
					if (a.read(xpp, i))
						attributes.add(a);
				}
			}
			event = xpp.next();
			while (event != XmlPullParser.END_DOCUMENT) {
				if (event == XmlPullParser.START_TAG) {
					Tag tag = new Tag();
					tag.read(xpp, event);
					tags.add(tag);
				}
				if (event == XmlPullParser.TEXT)
					text = xpp.getText();
				if (event == XmlPullParser.END_TAG)
					if (xpp.getName().equals(name))
						break;
				event = xpp.next();
			}
		}
		public void save (StringBuffer os, int depth) throws IllegalArgumentException, IllegalStateException, IOException {
			if (!showSdkInfo) {
				if (name.equals("uses-sdk"))
					return;
			}
			newLine(os, depth);
			os.append('<').
				append(name);
			if (attributes.size() == 1) {
				os.append(' ');
				if (attributes.get(0).namespace != null)
					os.append(attributes.get(0).namespace).
						append(':');
				os.append(attributes.get(0).name).
					append("=\"").
					append(attributes.get(0).value).
					append("\"");
			} else if (attributes.size() >= 2)
				for (Attribute a:attributes) {
					newLine(os, depth + 1);
					if (a.namespace != null)
						os.
							append(a.namespace).
							append(':');
					os.
						append(a.name).
						append("=\"").
						append(a.value).
						append("\"");
				}
			if (this.tags.size() > 0 || text != null)
				os.append(">");
			for (Tag t:tags)
				t.save(os, depth + 1);
			if (text != null)
				os.append(text);
			if (tags.size() > 0 || text != null) {
				newLine(os, depth);
				os.append("</").
					append(name).
					append(">");
			} else
				os.append("/>");
		}
		private void newLine (StringBuffer xms, int depth) throws IllegalStateException, IOException, IllegalArgumentException {
			xms.append("\n");
			for (int i=0;i < depth;i++)
				xms.append("\t");
		}
		private void putNameSpace (XmlPullParser xpp) throws XmlPullParserException {
			int c=xpp.getNamespaceCount(xpp.getDepth());
			for (int i=0;i < c;i++) {
				String prefix=xpp.getNamespacePrefix(i);
				String uri=xpp.getNamespaceUri(i);
				if(prefix!=null)
				nameSpaces.put(uri, prefix);
			}
		}
		public void insertNameSpace () {
			Iterator<String> keys= nameSpaces.keySet().iterator();
			while (keys.hasNext()) {
				String uri=keys.next();
				String prefix=nameSpaces.get(uri);
				Attribute a=new Attribute();
				a.name = prefix;
				a.namespace = "xmlns";
				a.value = uri;
				attributes.add(a);
			}
		}
	}
	public XMLWrite (ResTable resTable) {
		this.resTable = resTable;
		root = new Tag();
		showSdkInfo = resTable.getAnalysisMode();
		nameSpaces = new HashMap<String,String>();
	}
	public void parse (XmlPullParser xpp) throws XmlPullParserException, IOException {
		int state = xpp.getEventType();
		while (state != XmlPullParser.END_DOCUMENT) {
			if (state == XmlPullParser.START_TAG)
				root.read(xpp, state);
			if (state == XmlPullParser.END_DOCUMENT)
				break;
			state = xpp.next();
		}
		root.insertNameSpace();
	}
	public void write (OutputStream out) throws IOException, IllegalStateException, IllegalArgumentException {
		StringBuffer sb = new StringBuffer("<?xml version='1.0' encoding='utf-8'?>");
		root.save(sb, 0);
		out.write(sb.toString().
				  getBytes());
	}
}
