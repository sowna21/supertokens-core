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

package io.supertokens.test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.supertokens.ProcessState;
import io.supertokens.pluginInterface.RECIPE_ID;
import io.supertokens.pluginInterface.STORAGE_TYPE;
import io.supertokens.storageLayer.StorageLayer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.*;

import static org.junit.Assert.*;

public class GetUsersByEmailAPITest2_8 {
    @Rule
    public TestRule watchman = Utils.getOnFailure();

    @AfterClass
    public static void afterTesting() {
        Utils.afterTesting();
    }

    @Before
    public void beforeEach() {
        Utils.reset();
    }

    @Test
    public void testReturnTwoUsersWithSameEmail() throws Exception {
        // setup
        String[] args = {"../"};

        TestingProcessManager.TestingProcess process = TestingProcessManager.start(args);
        assertNotNull(process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.STARTED));

        if (StorageLayer.getStorage(process.getProcess()).getType() != STORAGE_TYPE.SQL) {
            return;
        }

        // given
        {
            JsonObject signUpResponse = Utils.signInUpRequest_2_7(process, "john.doe@example.com", true, "mockThirdParty", "mockThirdPartyUserId");
            assertEquals(signUpResponse.get("status").getAsString(), "OK");

            JsonObject signUpUser = signUpResponse.get("user").getAsJsonObject();
            assertEquals(signUpUser.get("email").getAsString(), "john.doe@example.com");
            assertNotNull(signUpUser.get("id"));
        }
        {
            JsonObject signUpResponse = Utils.signInUpRequest_2_7(process, "john.doe@example.com", true, "mockThirdParty2", "mockThirdParty2UserId");
            assertEquals(signUpResponse.get("status").getAsString(), "OK");

            JsonObject signUpUser = signUpResponse.get("user").getAsJsonObject();
            assertEquals(signUpUser.get("email").getAsString(), "john.doe@example.com");
            assertNotNull(signUpUser.get("id"));
        }

        // when
        HashMap<String, String> query = new HashMap<>();
        query.put("email", "john.doe@example.com");

        JsonObject response = io.supertokens.test.httpRequest.HttpRequest
                .sendGETRequest(process.getProcess(), "",
                        "http://localhost:3567/recipe/users/by-email", query, 1000,
                        1000,
                        null, "2.8", RECIPE_ID.THIRD_PARTY.toString());

        // then
        JsonArray jsonUsers = response.get("users").getAsJsonArray();

        jsonUsers.forEach(jsonUser -> assertEquals("john.doe@example.com", jsonUser.getAsJsonObject().get("email").getAsString()));

        assertEquals("OK", response.get("status").getAsString());
        assertEquals(2, response.entrySet().size());

        process.kill();
        assertNotNull(process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.STOPPED));
    }

    @Test
    public void testShouldThrowOnBadInput() throws Exception {
        // setup
        String[] args = {"../"};

        TestingProcessManager.TestingProcess process = TestingProcessManager.start(args);
        assertNotNull(process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.STARTED));

        if (StorageLayer.getStorage(process.getProcess()).getType() != STORAGE_TYPE.SQL) {
            return;
        }

        // when
        try {
            Map<String, String> emptyQuery = new HashMap<>();

            testBadInput(process, emptyQuery);
        // then
        } catch (io.supertokens.test.httpRequest.HttpResponseException e) {
            assertEquals(400, e.statusCode);
            assertEquals("Http error. Status Code: 400. Message: email cannot be empty", e.getMessage());
        }

        // when
        try {
            Map<String, String> invalidQuery = new HashMap<>();

            invalidQuery.put("email", "");

            testBadInput(process, invalidQuery);
        // then
        } catch (io.supertokens.test.httpRequest.HttpResponseException e) {
            assertEquals(400, e.statusCode);
            assertEquals("Http error. Status Code: 400. Message: email cannot be empty", e.getMessage());
        }

        process.kill();
        assertNotNull(process.checkOrWaitForEvent(ProcessState.PROCESS_STATE.STOPPED));
    }

    private void testBadInput(TestingProcessManager.TestingProcess process, Map<String, String> query) throws Exception {
        io.supertokens.test.httpRequest.HttpRequest
                .sendGETRequest(process.getProcess(), "",
                        "http://localhost:3567/recipe/users/by-email", query, 1000,
                        1000,
                        null, "2.8", RECIPE_ID.THIRD_PARTY.toString());

        throw new Exception("Request didn't throw as expected");
    }
}
