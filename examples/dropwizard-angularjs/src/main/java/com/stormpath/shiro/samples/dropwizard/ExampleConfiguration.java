package com.stormpath.shiro.samples.dropwizard;

import com.stormpath.shiro.samples.dropwizard.bundle.shiro.ShiroConfiguration;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.dropwizard.Configuration;

import java.util.List;


@SuppressFBWarnings("UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD")
public class ExampleConfiguration extends Configuration {

    public ShiroConfiguration shiro;

    public List<String> angularjsRoutes;
}
