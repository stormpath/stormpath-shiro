<%--
  ~ Copyright (c) 2012 Stormpath, Inc. and contributors
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<%@ include file="include.jsp" %>
<%
    //These next constants below would ordinarily be defined in your application's configuration somewhere.  The URLs
    //for your Groups are unique to your Stormpath tenant/application.
%>
<c:set var="GOOD_GUYS" value="https://api.stormpath.com/v1/groups/upXiVIrPQ7yfA5L1G5ZaSQ" scope="application"/>
<c:set var="BAD_GUYS" value="https://api.stormpath.com/v1/groups/01L6Fj7ATwKg8XrcpF1Lww" scope="application"/>
<c:set var="SCHWARTZ_MASTERS" value="https://api.stormpath.com/v1/groups/hyXDGl2oT1GDL8b_B7WG3A" scope="application"/>
<c:set var="subject" value=""/>

<html>
<head>
    <link type="text/css" rel="stylesheet" href="<c:url value="/style.css"/>"/>
    <title>Stormpath + Apache Shiro Quickstart</title>
</head>

<body>

<h1>Stormpath + Apache Shiro Quickstart</h1>

<p>Hi <shiro:guest>Guest</shiro:guest><shiro:user>
    <%
        //This should never be done in a normal page and should exist in a proper MVC controller of some sort, but for this
        //demo, we'll just pull out Stormpath Account data from Shiro's PrincipalCollection to reference in the
        //<c:out/> tag next:

        pageContext.setAttribute("account", org.apache.shiro.SecurityUtils.getSubject().getPrincipals().oneByType(java.util.Map.class));

    %>

    <c:out value="${account.givenName}"/></shiro:user>!
    ( <shiro:user>
        <a href="<c:url value="/logout"/>" onclick="document.getElementById('logout_form').submit();return false;">logout</a>
    </shiro:user>
    <shiro:guest><a href="<c:url value="/login"/>">Log in</a> (sample accounts provided)</shiro:guest> )
</p>

<p>Welcome to the Stormpath + Apache Shiro Quickstart sample application.
    This page represents the home page of any web application.</p>

<shiro:user><p>Visit your <a href="<c:url value="/account"/>">account page</a>.</p></shiro:user>
<shiro:guest><p>If you want to access the user-only <a href="<c:url value="/account"/>">account page</a>,
    you will need to log-in first.</p></shiro:guest>

<h2>Roles</h2>

<p>To show some taglibs, here are the roles you have and don't have. Log out and log back in under different user
    accounts to see different roles.</p>

<h3>Roles you have</h3>

<p>
    <shiro:hasRole name="${GOOD_GUYS}">Good Guys<br/></shiro:hasRole>
    <shiro:hasRole name="${BAD_GUYS}">Bad Guys<br/></shiro:hasRole>
    <shiro:hasRole name="${SCHWARTZ_MASTERS}">Schwartz Masters<br/></shiro:hasRole>
</p>

<h3>Roles you DON'T have</h3>

<p>
    <shiro:lacksRole name="${GOOD_GUYS}">Good Guys<br/></shiro:lacksRole>
    <shiro:lacksRole name="${BAD_GUYS}">Bad Guys<br/></shiro:lacksRole>
    <shiro:lacksRole name="${SCHWARTZ_MASTERS}">Schwartz Masters<br/></shiro:lacksRole>
</p>

<form id="logout_form" action="<c:url value="/logout"/>" method="post"/>

</body>
</html>
