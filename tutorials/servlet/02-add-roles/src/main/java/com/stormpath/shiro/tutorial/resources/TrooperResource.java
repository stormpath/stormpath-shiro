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
package com.stormpath.shiro.tutorial.resources;

import com.stormpath.shiro.tutorial.dao.StormtrooperDao;
import com.stormpath.shiro.tutorial.models.Stormtrooper;
import org.glassfish.jersey.server.mvc.Template;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Path("/")
public class TrooperResource {

    final private StormtrooperDao trooperDao;

    public TrooperResource()
    {
        super();
        this.trooperDao = StormtrooperDao.getInstance();
    }

    @GET
    @Path("/")
    @Produces({ MediaType.APPLICATION_JSON})
    @Template(name = "troopers")
    public Collection<Stormtrooper> listTroopers() {
        return trooperDao.listStormtroopers();
    }

    @GET
    @Path("/{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    @Template(name = "trooper")
    public Stormtrooper getTrooper(@PathParam("id") String id) {

        Stormtrooper stormtrooper = trooperDao.getStormtrooper(id);
        if (stormtrooper == null) {
            throw new NotFoundException();
        }
        return stormtrooper;
    }

    @POST
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @Template(name = "trooper")
    public Stormtrooper updateTrooper(@PathParam("id") String id,
                                      @FormParam("type") String type,
                                      @FormParam("species") String species,
                                      @FormParam("planetOfOrigin") String planetOfOrigin) {

        Stormtrooper stormtrooper = getTrooper(id);
        stormtrooper.setType(type);
        stormtrooper.setSpecies(species);
        stormtrooper.setPlanetOfOrigin(planetOfOrigin);

        trooperDao.updateStormtrooper(stormtrooper);
        return stormtrooper;
    }

    @POST
    @Path("/")
    @Produces({MediaType.APPLICATION_JSON})
    @Template(name = "trooper")
    public Stormtrooper createTrooper(@FormParam("id") String id,
                                      @FormParam("type") String type,
                                      @FormParam("species") String species,
                                      @FormParam("planetOfOrigin") String planetOfOrigin) {

        Stormtrooper stormtrooper = new Stormtrooper(id, planetOfOrigin, species, type);
        trooperDao.addStormtrooper(stormtrooper);
        return stormtrooper;
    }
}