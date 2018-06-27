/*
   Copyright (c) 2016-2017 Slamtec Co., Ltd. All Rights Reserved.
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.samton.ibenrobotdemo.map;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorUtil {

    //
    // singleton
    //

    private static ExecutorUtil instance;

    private ExecutorUtil() {
        executorService = Executors.newFixedThreadPool(10);
    }

    public static ExecutorUtil getInstance() {
        if (instance == null) {
            synchronized (ExecutorUtil.class) {
                if (instance == null) {
                    instance = new ExecutorUtil();
                }
            }
        }
        return instance;
    }

    //
    // ExecutorService
    //
    private ExecutorService executorService;

    public void execute(Runnable runnable) {
        executorService.execute(runnable);
    }
}
