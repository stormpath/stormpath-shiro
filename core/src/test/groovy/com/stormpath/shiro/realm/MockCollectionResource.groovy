package com.stormpath.shiro.realm

import com.stormpath.sdk.resource.CollectionResource
import com.stormpath.sdk.resource.Resource


class MockCollectionResource<T> implements CollectionResource {

    int offset, limit, size
    String href
    Collection items
    T single;

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
        return size
    }

    @Override
    Resource single() {
        return single
    }

}
