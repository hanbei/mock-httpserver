/* Copyright 2011 Florian Schulz

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */
package de.hanbei.httpserver;

import de.hanbei.httpserver.request.Request;
import de.hanbei.httpserver.response.Response;

/**
 * Created by IntelliJ IDEA.
 * User: hanbei
 * Date: 28.05.11
 * Time: 17:53
 * To change this template use File | Settings | File Templates.
 */
public interface RequestProcessor {

    Response process(Request request);

}
