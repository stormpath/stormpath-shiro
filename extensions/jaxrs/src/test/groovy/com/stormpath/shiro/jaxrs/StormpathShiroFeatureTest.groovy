package com.stormpath.shiro.jaxrs

import org.apache.shiro.web.jaxrs.ShiroAnnotationFilterFeature
import org.apache.shiro.web.jaxrs.SubjectPrincipalRequestFilter
import org.testng.annotations.Test

import javax.ws.rs.core.FeatureContext

import static org.easymock.EasyMock.*

/**
 * Tests for @{link StormpathShiroFeature}.
 */
class StormpathShiroFeatureTest {

    @Test
    void testFeature() {

        def featureContext = mock(FeatureContext);
        expect(featureContext.register(StormpathShiroExceptionMapper)).andReturn(null)
        expect(featureContext.register(SubjectPrincipalRequestFilter)).andReturn(null)
        expect(featureContext.register(ShiroAnnotationFilterFeature)).andReturn(null)

        def shiroFeature = new StormpathShiroFeature();

        replay featureContext

        shiroFeature.configure(featureContext)

        verify featureContext

    }
}
