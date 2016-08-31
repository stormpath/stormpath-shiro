<%--

  ~ Copyright 2012 Stormpath, Inc.
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

<%@ page import="org.apache.shiro.SecurityUtils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<%
    //These next constants below would ordinarily be defined in your application's configuration somewhere.  The URLs
    //for your Groups are unique to your Stormpath tenant/application.
%>
<c:set var="GOOD_GUYS" value="GOOD_GUYS" scope="application"/>
<c:set var="BAD_GUYS" value="BAD_GUYS" scope="application"/>
<c:set var="SCHWARTZ_MASTERS" value="SCHWARTZ_MASTERS" scope="application"/>
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

<p>Welcome to the Stormpath + Apache Shiro Quickstart sample application.</p>

<form id="logout_form" action="<c:url value="/logout"/>" method="post">
</form>

</body>
</html>
