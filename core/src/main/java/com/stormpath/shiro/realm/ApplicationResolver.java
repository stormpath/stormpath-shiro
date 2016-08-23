package com.stormpath.shiro.realm;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;

public interface ApplicationResolver {

    Application getApplication(Client client, String href);

}
