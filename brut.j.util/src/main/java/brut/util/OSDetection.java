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
package brut.util;

public class OSDetection {
    private static String OS = System.getProperty("os.name").toLowerCase();
    private static String arch = System.getProperty("os.arch").toLowerCase();

    public static boolean isWindows() {
        return false;
    }

    public static boolean isMacOSX() {
        return false;
    }

    public static boolean isUnix() {
        return true;
    }

    public static boolean is64Bit() {
        return arch.contains("64");
    }

    public static String returnOS() {
        return "Android";
    }
}
