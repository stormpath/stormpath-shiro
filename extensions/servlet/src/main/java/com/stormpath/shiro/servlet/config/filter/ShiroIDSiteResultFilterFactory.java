package com.stormpath.shiro.servlet.config.filter;

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.filter.IDSiteResultFilterFactory;
import com.stormpath.sdk.servlet.mvc.IdSiteResultController;
import com.stormpath.shiro.servlet.mvc.ShiroIDSiteResultController;
import com.stormpath.shiro.servlet.mvc.ShiroLogoutController;


public class ShiroIDSiteResultFilterFactory extends IDSiteResultFilterFactory {

    @Override
    public void doConfigure(IdSiteResultController c, Config config) {

        ShiroLogoutController controller = new ShiroLogoutController();
        controller.setNextUri(config.getLogoutConfig().getNextUri());
        controller.setInvalidateHttpSession(config.isLogoutInvalidateHttpSession());
        controller.setProduces(config.getProducedMediaTypes());
        controller.init();

        c.setLogoutController(controller);

        super.doConfigure(c, config);
    }

    @Override
    protected IdSiteResultController newController() {
        return new ShiroIDSiteResultController();
    }
}
