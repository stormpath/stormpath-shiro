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
package com.stormpath.shiro.tutorial.dao;

import com.stormpath.shiro.tutorial.models.Stormtrooper;

import java.security.SecureRandom;
import java.util.*;


/**
 * Dummy DAO that will generate 50 random Stormtroopers upon creation.
 */
public final class StormtrooperDao {

    final static private String[] trooperTypes = {"Basic", "Space", "Aquatic", "Marine", "Jump", "Sand"};
    final static private String[] planetsList = {"Coruscant", "Tatooine", "Felucia", "Hoth", "Naboo", "Serenno"};
    final static private String[] speciesList = {"Human", "Kel Dor", "Nikto", "Twi'lek", "Unidentified"};
    final static private Random RANDOM = new SecureRandom();

    final private Map<String, Stormtrooper> trooperMap = Collections.synchronizedSortedMap(new TreeMap<String, Stormtrooper>());

    public StormtrooperDao() {
        for (int i = 0; i < 50; i++) {
            addStormtrooper(randomTrooper());
        }
    }

    private static class SingletonHolder {
        static StormtrooperDao instance = new StormtrooperDao();
    }

    public static StormtrooperDao getInstance() {
        return SingletonHolder.instance;
    }

    public Collection<Stormtrooper> listStormtroopers() {
        return Collections.unmodifiableCollection(trooperMap.values());
    }


    public Stormtrooper getStormtrooper(String id) {
        return trooperMap.get(id);
    }

    public void addStormtrooper(Stormtrooper stormtrooper) {
        trooperMap.put(stormtrooper.getId(), stormtrooper);
    }

    public void updateStormtrooper(Stormtrooper stormtrooper) {
        // we are just backing with a map, so just call add.
        addStormtrooper(stormtrooper);
    }


    ///////////////////////////////////
    //  Dummy data generating below  //
    ///////////////////////////////////


    private static Stormtrooper randomTrooper(String id) {
        String planet = planetsList[RANDOM.nextInt(planetsList.length)];
        String species = speciesList[RANDOM.nextInt(speciesList.length)];
        String type = trooperTypes[RANDOM.nextInt(trooperTypes.length)];

        return new Stormtrooper(id, planet, species, type);
    }

    private static Stormtrooper randomTrooper() {
        String id = "FN-"  + String.format("%04d", RANDOM.nextInt(9999));
        return randomTrooper(id);
    }


}
