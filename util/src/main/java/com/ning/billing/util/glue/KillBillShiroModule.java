/*
 * Copyright 2010-2013 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.ning.billing.util.glue;

import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.guice.ShiroModule;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SessionManager;

import com.ning.billing.util.security.shiro.dao.JDBCSessionDao;
import com.ning.billing.util.security.shiro.realm.KillBillJndiLdapRealm;

import com.google.inject.binder.AnnotatedBindingBuilder;

// For Kill Bill library only.
// See com.ning.billing.server.modules.KillBillShiroWebModule for Kill Bill server.
public class KillBillShiroModule extends ShiroModule {

    public static final String KILLBILL_LDAP_PROPERTY = "killbill.server.ldap";

    protected void configureShiro() {
        bindRealm().toProvider(IniRealmProvider.class).asEagerSingleton();

        final boolean ldapEnabled = Boolean.parseBoolean(System.getProperty(KILLBILL_LDAP_PROPERTY, "false"));
        if (ldapEnabled) {
            bindRealm().to(KillBillJndiLdapRealm.class).asEagerSingleton();
        }
    }

    @Override
    protected void bindSecurityManager(final AnnotatedBindingBuilder<? super SecurityManager> bind) {
        super.bindSecurityManager(bind);

        // Magic provider to configure the cache manager
        bind(CacheManager.class).toProvider(EhCacheManagerProvider.class).asEagerSingleton();
    }

    @Override
    protected void bindSessionManager(final AnnotatedBindingBuilder<SessionManager> bind) {
        bind.to(DefaultSessionManager.class).asEagerSingleton();

        // Magic provider to configure the session DAO
        bind(JDBCSessionDao.class).toProvider(JDBCSessionDaoProvider.class).asEagerSingleton();
    }
}
