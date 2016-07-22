package com.stormpath.shiro.servlet.config.filter;

import com.stormpath.sdk.servlet.config.filter.LoginFilterFactory;
import com.stormpath.sdk.servlet.mvc.LoginController;
import com.stormpath.shiro.servlet.mvc.ShiroLoginController;

public class ShiroLoginFilterFactory extends LoginFilterFactory {

    @Override
    protected LoginController newController() {
        return new ShiroLoginController();
    }
}
