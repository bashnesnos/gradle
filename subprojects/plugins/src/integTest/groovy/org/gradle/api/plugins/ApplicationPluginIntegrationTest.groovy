/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.plugins

import org.gradle.integtests.fixtures.WellBehavedPluginTest

class ApplicationPluginIntegrationTest extends WellBehavedPluginTest {

    @Override
    String getPluginId() {
        "application"
    }

    String getMainTask() {
        return "build"
    }

    def setup() {
        file("settings.gradle").text = "rootProject.name='AppPluginTestProject'"
        file("src/dist/read.me").createFile()
        file("src/main/resources/config.xml").createFile()
        
        file("src/main/java/Ok.java").with {
            createFile()
            withWriter { itWriter ->
                itWriter.println """
                public class Ok {
                    public static void main(String[] args) {
                        System.out.println("Ok!");
                    }
                }
                """
            }
        }
    }

//    def checkDefaultDistribution() {
//        when:
//        buildFile << """
//            apply plugin:'application'
//
//            applicationDistribution.with {
//                into('config') {
//                    from(processResources) {
//                        includeEmptyDirs = false
//                    }
//                }
//            }
//
//            startScripts {
//                mainClassName = 'Ok'
//            }
//            """
//        then:
//        succeeds('distZip')
//        and:
//        file('build/distributions/AppPluginTestProject.zip').usingNativeTools().unzipTo(file("unzip"))
//        file("unzip/AppPluginTestProject/bin/AppPluginTestProject.bat").assertIsFile()
//        //file("unzip/AppPluginTestProject/bin/AppPluginTestProject").assertIsFile()
//        file("unzip/AppPluginTestProject/lib/AppPluginTestProject.jar").assertIsFile()
//        file("unzip/AppPluginTestProject/config/config.xml").assertIsFile()
//        file("unzip/AppPluginTestProject/read.me").assertIsFile()
//    }

    def checkAlteredDistribution() {
        when:
        buildFile << """
            apply plugin:'application'

            mainClassName = 'Ok'

            configureDist {
                applicationBinDir = '.'
                applicationDistribution.getClasspathSpec().with {
                    into('config') {
                        from(processResources) {
                            includeEmptyDirs = false
                        }
                    }
                }
            }
            """
        then:
        succeeds('distZip')
        and:
        file('build/distributions/AppPluginTestProject.zip').usingNativeTools().unzipTo(file("unzip"))
        file("unzip/AppPluginTestProject/AppPluginTestProject.bat").assertIsFile()
            
//            text =~ /(?m)CLASSPATH=.*?%APP_HOME%\\config/
//        }
//        file("unzip/AppPluginTestProject/AppPluginTestProject").with {
//            assertIsFile()
//            text =~ /(?m)CLASSPATH=.*?APP_HOME\/config/
//        }
        file("unzip/AppPluginTestProject/lib/AppPluginTestProject.jar").assertIsFile()
        file("unzip/AppPluginTestProject/config/config.xml").assertIsFile()
        file("unzip/AppPluginTestProject/read.me").assertIsFile()
    }

}
