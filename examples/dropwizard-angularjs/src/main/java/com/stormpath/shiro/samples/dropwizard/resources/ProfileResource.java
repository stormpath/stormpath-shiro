/*
 * Copyright 2016 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.shiro.samples.dropwizard.resources;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.shiro.samples.dropwizard.model.Profile;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Example JAX-RS resource that requires authentication via the {@link RequiresAuthentication} annotation.  This
 * resource will update a Stormpath account's (currently logged in user).
 */
@RequiresAuthentication
@Path("/profile")
public class ProfileResource {

    /**
     * Saves a profile information to a Stormpath account. Sets a Users first and last name, as well as updating a
     * custom metadata field <code>favoriteColor</code>.
     * @param request The current request.
     * @param profile The profile to be saved.
     * @return Always returns a Response 200.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response profile(@Context HttpServletRequest request, Profile profile) {

        Account account = AccountResolver.INSTANCE.getAccount(request);

        if (account != null) {
            account.setGivenName(profile.getGivenName());
            account.setSurname(profile.getSurname());
            account.getCustomData().put("favoriteColor", profile.getFavoriteColor());
            account.save();
        }

        return Response
                .status(200)
                .build();
    }
}
