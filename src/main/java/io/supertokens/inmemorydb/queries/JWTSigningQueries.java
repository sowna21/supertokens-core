/*
 *    Copyright (c) 2021, VRAI Labs and/or its affiliates. All rights reserved.
 *
 *    This software is licensed under the Apache License, Version 2.0 (the
 *    "License") as published by the Apache Software Foundation.
 *
 *    You may not use this file except in compliance with the License. You may
 *    obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *    License for the specific language governing permissions and limitations
 *    under the License.
 */

package io.supertokens.inmemorydb.queries;

import io.supertokens.inmemorydb.Start;
import io.supertokens.inmemorydb.config.Config;

public class JWTSigningQueries {
    static String getQueryToCreateJWTSigningTable(Start start) {
        return "CREATE TABLE IF NOT EXISTS " + Config.getConfig(start).getJWTSigningKeysTable() + " ("
                + "key_id VARCHAR(255) NOT NULL," + "public_key TEXT NOT NULL,"
                + "private_key TEXT NOT NULL," + "algorithm VARCHAR(10) NOT NULL,"
                + "algorithm_type VARCHAR(10) NOT NULL," + "created_at BIGINT UNSIGNED,"
                + "PRIMARY KEY(key_id));";
    }

    static String getQueryToFetchLatestKey(Start start) {
        return "SELECT * FROM " + Config.getConfig(start).getJWTSigningKeysTable()
                + "ORDER BY created_at DESC";
    }
}