/*
 * Copyright 2012 Stormpath, Inc.
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
package com.stormpath.shiro.realm

import com.stormpath.sdk.resource.CollectionResource
import com.stormpath.sdk.resource.Resource


class MockCollectionResource<T> implements CollectionResource {

    int offset, limit
    String href
    Collection items

    @Override
    int getOffset() {
        return offset
    }

    @Override
    int getLimit() {
        return limit
    }

    @Override
    Iterator<T> iterator() {
        return items?.iterator()
    }

    @Override
    String getHref() {
        return href
    }

    @Override
    int getSize() {
        return items.size()
    }

    @Override
    Resource single() {
        Iterator iterator = this.iterator();
        if(!iterator.hasNext()) {
            throw new IllegalStateException("This list is empty while it was expected to contain one (and only one) element.");
        } else {
            Resource itemToReturn = (Resource)iterator.next();
            if(iterator.hasNext()) {
                throw new IllegalStateException("Only a single resource was expected, but this list contains more than one item.");
            } else {
                return itemToReturn;
            }
        }
    }
}
