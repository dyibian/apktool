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
package brut.androlib;

import java.io.File;
import java.util.Collection;
import org.jf.util.Log;

public class ApkOptions {
    public boolean forceBuildAll = false;
    public boolean forceDeleteFramework = false;
    public boolean debugMode = false;
    public boolean verbose = false;
    public boolean copyOriginalFiles = false;
    public boolean updateFiles = false;
    public boolean isFramework = false;
    public boolean resourcesAreCompressed = false;
    public Collection<String> doNotCompress;
	public int api;

	public File in,out,tmp;
	
    public String frameworkFolderLocation = null;
    public String frameworkTag = null;
    public String aaptPath = "";
	public SignTool signTool;


	public void sign () throws Exception {
		if(signTool==null){
			tmp.renameTo(out);
			return;
		}
		Log.info("正在签名");
		signTool.sign(tmp,out);
		tmp.delete();
	}
}
