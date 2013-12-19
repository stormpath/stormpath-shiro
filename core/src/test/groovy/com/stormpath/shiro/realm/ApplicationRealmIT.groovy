package com.stormpath.shiro.realm

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.resource.Deletable
import com.stormpath.shiro.authz.CustomDataPermissionsEditor
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.cache.MemoryConstrainedCacheManager
import org.apache.shiro.mgt.DefaultSecurityManager
import org.apache.shiro.subject.Subject

import static com.stormpath.sdk.application.Applications.*
import com.stormpath.shiro.ClientIT
import org.junit.Test
import static org.junit.Assert.*


class ApplicationRealmIT extends ClientIT {

    @Test
    void testDoGetAuthorizationInfo() {

        //create a test app:
        def app = client.instantiate(Application)
        app.name = uniquify('Stormpath-Shiro-Test-App')
        app = client.currentTenant.createApplication(newCreateRequestFor(app).createDirectory().build())
        deleteOnTeardown(app.getDefaultAccountStore() as Deletable)
        deleteOnTeardown(app)

        //create a user group:
        def group = client.instantiate(Group)
        group.name = uniquify('Users')
        //add some permissions to the group:
        new CustomDataPermissionsEditor(group.getCustomData()).append('user:login')
        group = app.createGroup(group)
        deleteOnTeardown(group)

        //create a test account:
        def acct = client.instantiate(Account)
        def password = 'Changeme1!'
        acct.username = uniquify('Stormpath-Shiro-Test-App-Acct1')
        acct.password = password
        acct.email = acct.username + '@nowhere.com'
        acct.givenName = 'Joe'
        acct.surname = 'Smith'
        //add some permissions to the account:
        new CustomDataPermissionsEditor(acct.getCustomData())
                .append('user:1234:edit')
                .append('report:create')
        acct = app.createAccount(Accounts.newCreateRequestFor(acct).setRegistrationWorkflowEnabled(false).build())
        deleteOnTeardown(acct)

        //add the account to the group:
        group.addAccount(acct);

        //setup a quick Shiro SecurityManager using the ApplicationRealm:
        def realm = new ApplicationRealm(client: client, applicationRestUrl: app.getHref())
        def securityManager = new DefaultSecurityManager(realm)
        securityManager.cacheManager = new MemoryConstrainedCacheManager()

        //simulate a currently executing user:
        Subject subject = new Subject.Builder(securityManager).buildSubject()

        //login this user via the above account credentials:
        subject.login(new UsernamePasswordToken(acct.getUsername(), password))

        //assert that the account is transitively granted the group's permissions:
        assertTrue subject.isPermitted('user:login')

        //assert that the account is granted directly assigned permissions:
        assertTrue subject.isPermitted('user:1234:edit')
        assertTrue subject.isPermitted('report:create')
    }
}
