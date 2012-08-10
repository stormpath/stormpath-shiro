/*
 * Copyright 2012 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.shiro.realm;

import com.stormpath.sdk.group.Group;
import org.apache.shiro.util.CollectionUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Default implementation of the {@code GroupRoleResolver} interface that allows a Stormpath
 * {@link Group} to be translated into Shiro role names based on custom preferences.
 * <h2>Overview</h2>
 * This implementation converts a Group into one or more role names based on one or more configured
 * {@link Mode Mode}s:
 * <table>
 *     <thead>
 *         <tr>
 *             <th>Mode (case insensitive)</th>
 *             <th>Behavior</th>
 *             <th>Example</th>
 *         </tr>
 *     </thead>
 *     <tbody>
 *         <tr>
 *             <td>HREF</td>
 *             <td>Returns the Group's fully qualified HREF as a role name</td>
 *             <td>{@code https://api.stormpath.com/v1/groups/upXiExAmPlEfA5L1G5ZaSQ}</td>
 *         </tr>
 *         <tr>
 *             <td>ID</td>
 *             <td>Returns Group's globally unique identifier as a role name</td>
 *             <td>{@code upXiExAmPlEfA5L1G5ZaSQ}</td>
 *         </tr>
 *         <tr>
 *             <td>NAME</td>
 *             <td>Returns Group's name a role name</td>
 *             <td>{@code administrators}</td>
 *         </tr>
 *     </tbody>
 * </table>
 * <h2>Usage</h2>
 * You can choose one or more modes either by referencing the {@link Mode Mode} enum values directly, or by using
 * the mode string names.
 * <p/>
 * For example, in code:
 * <pre>
 * Set&lt;DefaultGroupRoleResolver.Mode&gt; modes = CollectionUtils.asSet(
 *     DefaultGroupRoleResolver.Mode.HREF, DefaultGroupRoleResolver.Mode.ID
 * );
 * groupRoleResolver.setModes(modes);
 * </pre>
 * <p/>
 * Or maybe in shiro .ini:
 * <pre>
 * applicationRealm.groupRoleResolver.modeNames = href, id
 * </pre>
 * In the above configuration, each Group translates into two Shiro role names: one role name is the raw href itself,
 * the other role name is the Group ID.  You can specify one or more modes to translate into one or more role names
 * respectively.
 * <i>modeNames are case-insensitive</i>.
 * <p/>
 * <b>WARNING:</b> Group Names, while easier to read in code, can change at any time via a REST API call or by using
 * the Stormpath UI Console.  It is <em>strongly</em> recommended to use only the HREF or ID modes as these values
 * will never change.  Acquiring group names might also incur a performance penalty beyond the HREF which is guaranteed
 * to be present.
 *
 * @since 0.2
 */
public class DefaultGroupRoleResolver implements GroupRoleResolver {

    private Set<Mode> modes;

    public DefaultGroupRoleResolver() {
        this.modes = CollectionUtils.asSet(Mode.HREF);
    }

    public Set<Mode> getModes() {
        return modes;
    }

    public void setModes(Set<Mode> modes) {
        this.modes = modes;
    }

    public Set<String> getModeNames() {
        if (CollectionUtils.isEmpty(modes)) {
            return Collections.emptySet();
        }
        Set<String> names = new HashSet<String>(modes.size());
        for (Mode mode : modes) {
            names.add(mode.name());
        }
        return names;
    }

    public void setModeNames(Set<String> modeNames) {
        Set<Mode> modes = new HashSet<Mode>(CollectionUtils.size(modeNames));
        if (modeNames != null) {
            for (String name : modeNames) {
                modes.add(Mode.fromString(name));
            }
        }
        this.modes = modes;
    }

    @Override
    public Set<String> resolveRoles(Group group) {

        Set<String> set = new HashSet<String>();

        Set<Mode> modes = getModes();

        String groupHref = group.getHref();

        if (groupHref != null) {
            if (modes.contains(Mode.HREF)) {
                set.add(groupHref);
            }
            if (modes.contains(Mode.ID)) {
                String instanceId = getInstanceId(groupHref);
                if (instanceId != null) {
                    set.add(instanceId);
                }
            }
        }

        if (modes.contains(Mode.NAME)) {
            String name = group.getName();
            if (name != null) {
                set.add(name);
            }

        }

        return set;
    }

    private String getInstanceId(String href) {
        if (href != null) {
            int i = href.lastIndexOf('/');
            if (i >= 0) {
                return href.substring(i);
            }
        }
        return null;
    }

    public enum Mode {

        HREF,
        ID,
        NAME;

        public static Mode fromString(final String name) {
            String upper = name.toUpperCase();
            for (Mode mode : values()) {
                if (mode.name().equals(upper)) {
                    return mode;
                }
            }
            throw new IllegalArgumentException("There is no Mode name '" + name + "'");
        }
    }
}
