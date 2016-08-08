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
