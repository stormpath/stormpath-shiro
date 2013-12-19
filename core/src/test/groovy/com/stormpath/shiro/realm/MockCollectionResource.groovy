package com.stormpath.shiro.realm

import com.stormpath.sdk.resource.CollectionResource


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
}
